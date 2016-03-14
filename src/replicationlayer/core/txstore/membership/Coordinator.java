package replicationlayer.core.txstore.membership;
import replicationlayer.core.util.Debug;

import java.net.InetAddress;

public class Coordinator extends Principal{
    private int dcId;
    public Coordinator(int dcId, String host, int port){
	super(host, port);
	this.dcId = dcId;
    }

    public Coordinator(int dcId, InetAddress host, int port){
	super(host, port);
	this.dcId = dcId;
    }

    public int getDatacenterId(){
	return this.dcId;
    }
    

    public String toString(){
	return "++ COORDINATOR " + super.toString();
    }

}