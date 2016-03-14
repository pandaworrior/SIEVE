package replicationlayer.core.txstore.proxy;
import replicationlayer.core.util.Debug;

import replicationlayer.core.txstore.util.Operation;

public interface ApplicationInterface{

    /** returns the integer identifier for the storage server responsible for the operation **/
    public int selectStorageServer(Operation op);
    public int selectStorageServer(byte[] op);
}