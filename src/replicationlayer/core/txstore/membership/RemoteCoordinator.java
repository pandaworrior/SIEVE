package replicationlayer.core.txstore.membership;

import replicationlayer.core.util.Debug;

import java.net.InetAddress;

public class RemoteCoordinator extends Principal{
    private int dcId;
    public RemoteCoordinator(int dcId, String host, int port){
	super(host, port);
	this.dcId = dcId;
    }

    public RemoteCoordinator(int dcId, InetAddress host, int port){
	super(host, port);
	this.dcId = dcId;
    }

    public int getDatacenterId(){
	return this.dcId;
    }
    

    public String toString(){
	return "++ REMOTECOORDINATOR " + super.toString();
    }

}
