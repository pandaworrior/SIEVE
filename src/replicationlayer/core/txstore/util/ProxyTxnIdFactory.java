package replicationlayer.core.txstore.util;
import replicationlayer.core.util.Debug;


public class ProxyTxnIdFactory{

    int dcId;
    int proxyId;
    int count;

    public ProxyTxnIdFactory(int dcId, int proxy){
	this.dcId = dcId;
	this.proxyId = proxy;
	count = 0;
    }

    public synchronized ProxyTxnId nextTxnId(){
	return new ProxyTxnId(dcId, proxyId, ++count);
    }

}