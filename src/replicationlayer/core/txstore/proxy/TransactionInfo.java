package replicationlayer.core.txstore.proxy;
import replicationlayer.core.util.Debug;

import replicationlayer.core.txstore.util.ReadWriteSet;
import replicationlayer.core.txstore.util.StorageList;
import replicationlayer.core.txstore.util.ProxyTxnId;
import replicationlayer.core.txstore.util.TimeStamp;
import replicationlayer.core.txstore.util.LogicalClock;
import replicationlayer.core.txstore.util.Operation;
import replicationlayer.core.txstore.util.Result;

import replicationlayer.core.txstore.messages.AckCommitTxnMessage;
import replicationlayer.core.txstore.messages.BeginTxnMessage;
import replicationlayer.core.txstore.messages.CommitTxnMessage;
import replicationlayer.core.txstore.messages.MessageBase;
import replicationlayer.core.txstore.messages.MessageTags;
import replicationlayer.core.txstore.messages.OperationMessage;
import replicationlayer.core.txstore.messages.ProxyCommitMessage;
import replicationlayer.core.txstore.messages.ResultMessage;
import replicationlayer.core.txstore.messages.AbortTxnMessage;
import replicationlayer.core.txstore.messages.StorageCommitTxnMessage;

import java.util.Vector;

/**
   accumulates information on a transaction as it is processed
 **/
public class TransactionInfo{

    StorageList slist;
    ProxyTxnId txnId;
    TimeStamp ts;
    LogicalClock lc;

    TimeStamp finish_ts;
    LogicalClock finish_lc;
    MessageBase commitMsg;
    AckCommitTxnMessage ackMsg;
    BeginTxnMessage bMsg;
    ProxyCommitMessage pcMsg;
    Operation shadowOp; //shadowOp
    int color;
    int count;
    ReadWriteSet rws;
    long startTime;
    

    Vector<Operation> operations;
    Vector<Result> results;

    public TransactionInfo(ProxyTxnId id){
	txnId = id;
	slist = new StorageList();
	count = 0;
	results = new Vector<Result>();
	operations = new Vector<Operation>();
	shadowOp = null;
	color = 0;
	startTime = 0;
    }
    
    public void reset(){
    	txnId = null;
    	slist.reset();
    	count = 0;
    	results.clear();
    	operations.clear();
    	shadowOp = null;
    	color = 0;
    	startTime = 0;
        ackMsg = null;
        ts = null;
        bMsg = null;
        pcMsg = null;
    }
    
    public void setTxnId(ProxyTxnId id){
    	txnId = id;
    }
    
    public void setBeginTxnMessage(BeginTxnMessage msg){
    	bMsg = msg;
    }
    
    public void setProxyCommitMessage(ProxyCommitMessage msg){
    	pcMsg = msg;
    }
    
    Object appId;
    public TransactionInfo(ProxyTxnId id, Object o){
        this(id);
        appId = o;
    }

    public Object getApplicationTxnId(){
        return appId;
    }
    
    /**
       Add a storage service to the transaction
     **/
    public void addStorage(int i){
	slist.addStorage(i);
    }

    /**
       get the list of storage services
     **/
    public StorageList getStorageList(){
	return slist;
    }

    /**
       get the next operation id for this transaction
     **/
    public  int nextOperationId(){
    	//Debug.printf("get next operation id %d \n", count);
	operations.add(count, null);
	results.add(count, null);
	return count++;
    }
    
    
    /**
       Set the index operation of the transaction to happen on service storageid
     **/
    public  void setOperation(int index, Operation op, int storageId){
	operations.add(index, op);
	slist.addStorage(storageId);
    }

    /**
       get the indexth operation in the transaction
     **/
    public Operation getOperation(int index){
	return operations.get(index);
    }

    Operation[] op = new Operation[1];
    /**
       Get the full set of operations in the transaction
     **/
    public Operation[] getOperations(){
	return operations.toArray(op);
    }

    /**
       Get the indexth result of the transaction
     **/
    public Result getResult(int index){
	return results.get(index);
    }

    Result[] res = new Result[1];
    /**
       Get all results
     **/
    public Result[] getResults(){
	return results.toArray(res);
    }
    
    /**
     * 
     * @param set shadow operation for a transaction
     */
    public void setShadowOp(Operation op, int color){	
    	shadowOp = op;
    	this.color = color;
    }
    
    public Operation getShadowOp(){
    	return shadowOp;
    }
    
    public int getColor(){
    	return color;
    }
    
    public boolean isBlue(){
    	if(color == 1)
    		return true;
    	else
    		return false;
    }
    
    public void setStartTime(){
    	startTime = System.nanoTime();
    }
    
    public long getLatency(){
    	return (System.nanoTime() - startTime);
    }
    /**
     * set readwrite set for this transaction
     */
    public void setReadwriteSet(ReadWriteSet rws){
    	this.rws = rws;
    }
    
    public ReadWriteSet getRws(){
    	return rws;
    }


    /**
       Wait for the start timestamp (i.e. the response to txn begin)
     **/
    public synchronized void waitForTimeStamp(){
	while (ts == null)
	    try{
		wait(5000);
		//		Debug.println("\t\tTransactionInfo.waitForTimeStamp() 5000 wait");
	    }catch(InterruptedException e){}
    }
    
    /**
       Wait for a commit or abort message
     **/
    public synchronized boolean waitForCommit(){
	/*while (commitMsg == null || !slist.isEmpty()){
	    try{
		wait(5000);
		//		Debug.println("\t\tTransactionInfo.waitForCommit() 5000 wait");
				
	    }catch(InterruptedException e){}
	}
	return commitMsg instanceof CommitTxnMessage;*/
    	while (ackMsg == null){
    	 try{
    			wait(5000);
    			//		Debug.println("\t\tTransactionInfo.waitForCommit() 5000 wait");
    					
    		    }catch(InterruptedException e){}
    	}
    		    
    	return ackMsg.getOutcome();
    }

    /**
       Set the timestamp followign receipt of beginack
     **/
    public synchronized void setTimeStamp(TimeStamp t){
	if (ts != null)
	    throw new RuntimeException("timestamp is already set");
	ts = t;
	notify();
    }

    /**
       Set the commit message
     **/
    public synchronized void setCommitMessage(CommitTxnMessage m){
	finish_ts = m.getTimeStamp();
	finish_lc = m.getLogicalClock();
	commitMsg = m;
        if (isCommitted())
            notify();
    }
    /**
     * 
     * @param set the commit ack message
     */
    
    public synchronized void setAckCommitMessage(AckCommitTxnMessage m){
    	ackMsg= m;
         notify();
    }
    
    public synchronized void setStorageCommitTxnMessage(StorageCommitTxnMessage m){
        
        slist.remove(m.getStorageId());
        if (isCommitted())
            notify();
    }

    
    public synchronized boolean isCommitted(){
        return slist.isEmpty() && commitMsg != null;
    }
    /**
       Set the abort message
     **/
    public synchronized void setAbortMessage(AbortTxnMessage m){
	commitMsg = m;
	notify();
    }

    /**
       Wait for the result of operation id
     **/
    public synchronized Result waitForResult(int id){
	while(results.get(id) == null)
	    try{
		wait(5000);
		//		Debug.println("\t\tTransactionInfo.waitForResult() 5000 wait");
	    }catch(InterruptedException e){Debug.println("interrupted!");}
	return results.get(id);
    }

    /**
       Set the result of operation id to msg
     **/
    public synchronized void setResult(Result msg, int id){
	results.setElementAt(msg, id);
	notify();
    }
    
    public ProxyTxnId getTxnId(){
    	return txnId;
    }
}