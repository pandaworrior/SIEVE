package replicationlayer.core.txstore.coordinator;

import replicationlayer.core.txstore.BaseNode;
import replicationlayer.core.txstore.membership.Role;

import replicationlayer.core.txstore.messages.MessageFactory;
import replicationlayer.core.txstore.messages.MessageTags;
import replicationlayer.core.txstore.messages.MessageBase;

// receiving messages
import replicationlayer.core.txstore.messages.AckCommitTxnMessage;
import replicationlayer.core.txstore.messages.BeginTxnMessage;
import replicationlayer.core.txstore.messages.BlueTokenGrantMessage;
import replicationlayer.core.txstore.messages.CommitShadowOpMessage;
import replicationlayer.core.txstore.messages.FinishTxnMessage;
import replicationlayer.core.txstore.messages.OperationMessage;
import replicationlayer.core.txstore.messages.ProxyCommitMessage;
import replicationlayer.core.txstore.messages.ReadWriteSetMessage;
import replicationlayer.core.txstore.messages.RemoteShadowOpMessage;
import replicationlayer.core.txstore.messages.TxnReadyMessage;
import replicationlayer.core.txstore.messages.TxnMetaInformationMessage;

// sending messages
import replicationlayer.core.txstore.messages.AckTxnMessage;
import replicationlayer.core.txstore.messages.CommitTxnMessage;
import replicationlayer.core.txstore.messages.AbortTxnMessage;
import replicationlayer.core.txstore.messages.FinishRemoteMessage;
import replicationlayer.core.txstore.messages.GimmeTheBlueMessage;

import replicationlayer.core.txstore.storageshim.StorageShim;
import replicationlayer.core.txstore.util.Operation;
import replicationlayer.core.txstore.util.ProxyTxnId;
import replicationlayer.core.txstore.util.TimeStamp;
import replicationlayer.core.txstore.util.LogicalClock;
import replicationlayer.core.txstore.util.ReadWriteSet;
import replicationlayer.core.txstore.util.StorageList;
import replicationlayer.core.txstore.util.ReadSetEntry;
import replicationlayer.core.txstore.util.ReadSet;
import replicationlayer.core.txstore.util.WriteSet;
import replicationlayer.core.txstore.util.WriteSetEntry;

import replicationlayer.core.util.Counter;
import replicationlayer.core.util.Debug;

import replicationlayer.core.network.netty.NettyTCPSender;
import replicationlayer.core.network.netty.NettyTCPReceiver;
import replicationlayer.core.network.ParallelPassThroughNetworkQueue;
import replicationlayer.core.network.PassThroughNetworkQueue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;
import java.util.Vector;


public class RemoteCoordinator extends BaseNode {

	MessageFactory mf;
	NewCoordinator coord; 
	
	ObjectPool<TransactionRecord> txnPool;
	Hashtable<ProxyTxnId, TransactionRecord> records;

	public RemoteCoordinator(String file, int dc, int id) {
		super(file, dc, Role.REMOTECOORDINATOR, id);
		System.out.println("start remote coordinator acceptor");
		this.mf = new MessageFactory();
		records = new Hashtable<ProxyTxnId, TransactionRecord>();
		
		//initiate the txnPool
		txnPool = new ObjectPool<TransactionRecord>();
		for(int i = 0; i < 100; i++){
			TransactionRecord txn = new TransactionRecord();
			txnPool.addObject(txn);
		}
		Debug.println("RemoteCoordinator acceptor finished initialization and starts");
	}
	
	public void setCoordinator(NewCoordinator c){
		coord = c;
	}

	/***
	 * handle incoming messages. implements ByteHandler
	 ***/
	public void handle(byte[] b) {
		MessageBase msg = mf.fromBytes(b);
		if (msg == null) {
			throw new RuntimeException("Should never receive a null message");
		}
		
		 if (coord.messageCount.incrementAndGet() % 5000 == 0) { 
			 coord.messageCount.set(0);
			 System.out.println("beginTxn  |  gimetheblue |  abortxn | bluetokengrant | proxycommit | ackcommit | remoteshadow ");
			 for (int i = 0; i < coord.messages.length; i++) {
				 System.out.print(coord.messages[i] + "\t"); coord.messages[i] = 0; 
			 }
			 System.out.println();
		 }
		switch (msg.getTag()) {
		case MessageTags.ACKCOMMIT:
			coord.messages[5]++;
			process((AckCommitTxnMessage) msg);
			return;
		case MessageTags.REMOTESHADOW:
			coord.messages[6]++;
			process((RemoteShadowOpMessage) msg);
			return;
		default:
			throw new RuntimeException("invalid message tag: " + msg.getTag());
		}

	}

	private void process(AckCommitTxnMessage msg) {
		// TODO Auto-generated method stub
		Debug.println("receive ack commit " + msg);
		
		TransactionRecord tmpRec = records.get(msg.getTxnId());
		coord.updateLastCommittedLogicalClock(tmpRec.getMergeClock(), tmpRec.isBlue());
		coord.updateLastVisibleLogicalClock(tmpRec.getMergeClock());
		// insure that one dc doesnt "always win" because another is underloaded
		coord.setLocalTxn(tmpRec.getFinishTime().getCount());
		
		coord.updateObjectTable(tmpRec.getWriteSet().getWriteSet(), tmpRec.getMergeClock(), 
				tmpRec.getFinishTime(), tmpRec.getTxnId());
		
		coord.statisticOutput(tmpRec);
		records.remove(tmpRec.getTxnId());
		
		//clean datastructure
		mf.returnRemoteShadowOpMessage(tmpRec.rOpMsg);
		mf.returnCommitShadowOpMessage(tmpRec.cSMsg);
		tmpRec.reset();
		txnPool.returnObject(tmpRec);
		mf.returnAckCommitTxnMessage(msg);
	}

	private void process(RemoteShadowOpMessage msg) {
		// TODO Auto-generated method stub
		Debug.println("receive remote shadow " + msg);
		TransactionRecord txn  = txnPool.borrowObject();
		if(txn == null){
			txn = new TransactionRecord(msg.getTxnId(), msg.getTimeStamp(), msg.getLogicalClock());
		}else{
			txn.setTxnId(msg.getTxnId());
		}
		records.put(msg.getTxnId(), txn);
		txn.setWriteSet(msg.getWset());
		txn.setShadowOp(msg.getShadowOperation());
		txn.setColor(msg.getColor());
		txn.setMergeClock(msg.getLogicalClock());
		txn.setFinishTime(msg.getTimeStamp());
		txn.setRemote();
		txn.addStorage(0);
		txn.setRemoteShadowOpMessage(msg);
		
		CommitShadowOpMessage csm = mf.borrowCommitShadowOpMessage();
		if(csm == null){
			csm = new CommitShadowOpMessage(txn.getTxnId(), 
				txn.getShadowOp(), msg.getTimeStamp(), txn.getMergeClock());
		}else{
			csm.encodeMessage(txn.getTxnId(), 
				txn.getShadowOp(), msg.getTimeStamp(), txn.getMergeClock());
		}
		txn.setCommitShadowOpMessage(csm);
		Debug.println("commit remote to data writer" + csm);
		sendToStorage(csm, 0); //TODO: fix to more generic
	}


}

