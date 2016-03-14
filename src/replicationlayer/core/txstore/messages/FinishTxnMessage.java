package replicationlayer.core.txstore.messages;
import replicationlayer.core.util.Debug;

import replicationlayer.core.txstore.util.ProxyTxnId;
import replicationlayer.core.txstore.util.StorageList;
import replicationlayer.core.txstore.util.LogicalClock;

public class FinishTxnMessage extends MessageBase{

    protected ProxyTxnId proxyTxnId;
    protected StorageList storageList;
    public FinishTxnMessage(ProxyTxnId txid, StorageList slist){
	super(MessageTags.FINISHTXN, computeByteSize(txid, slist));
	proxyTxnId = txid;
	storageList = slist;
	
	int offset = getOffset();
	proxyTxnId.getBytes(getBytes(), offset);
	offset += proxyTxnId.getByteSize();
	storageList.getBytes(getBytes(), offset);
	offset += slist.getByteSize();
	if (offset != getBytes().length)
	    throw new RuntimeException("did not fill up the byte array!");
    }

    public FinishTxnMessage(byte[] b){
	super(b);
	if (getTag() != MessageTags.FINISHTXN)
	    throw new RuntimeException("Invalid message tag.  looking for "+
				       MessageTags.FINISHTXN+ " found "+getTag());
	int offset = getOffset();
	proxyTxnId = new ProxyTxnId(b, offset);
	offset += proxyTxnId.getByteSize();
	storageList = new StorageList(b, offset);
	offset += storageList.getByteSize();
	if (offset != b.length)
	    throw new RuntimeException("did not consume the entire byte array!");
    }

    public ProxyTxnId getTxnId(){
	return proxyTxnId;
    }


    public StorageList getStorageList(){
	return storageList;
    }

    static int computeByteSize(ProxyTxnId proxyTxnId, StorageList slist){
	return proxyTxnId.getByteSize() + slist.getByteSize();
    }

    public String toString(){
	return "<"+getTagString()+", "+proxyTxnId+", "+storageList+">";
    }


}