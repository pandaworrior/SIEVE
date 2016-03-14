package replicationlayer.core.txstore.proxy;
import java.sql.ResultSet;

import replicationlayer.core.util.Debug;


import replicationlayer.core.txstore.scratchpad.rdbms.util.DBShadowOperation;
import replicationlayer.core.txstore.util.ProxyTxnId;
import replicationlayer.core.txstore.util.Operation;
import replicationlayer.core.txstore.util.Result;

public interface ClosedLoopProxyInterface{

    public byte[] execute(byte[] op, ProxyTxnId txn);
    public byte[] execute(byte[] op, ProxyTxnId txn, int storageId);

    public Result execute(Operation op, ProxyTxnId txn);
    public Result execute(Operation op, ProxyTxnId txn, int storageId);
    
    public ResultSet executeOrig(Operation op, ProxyTxnId txnid);
    public ResultSet executeOrig(Operation op, ProxyTxnId pr, int sid);

    public ProxyTxnId beginTxn();

    public void abort(ProxyTxnId txn);

    // returns true if the transaction commits, false otherwise
    public boolean commit(ProxyTxnId txn);

	public boolean commit(ProxyTxnId txId, DBShadowOperation op, int color);
}