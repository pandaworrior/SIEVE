package replicationlayer.core.txstore.membership;
import replicationlayer.core.util.Debug;

import java.net.InetAddress;

public class Proxy extends Principal{

    private int proxyId;
    private int dcId;

    public Proxy(int proxyId, int dcId, String host, int port){
	super(host, port);
	this.proxyId = proxyId;
	this.dcId = dcId;
    }

    public Proxy(int proxyId, int dcId, InetAddress host, int port){
	super(host, port);
	this.proxyId = proxyId;
	this.dcId = dcId;
    }

    public int getProxyId(){
	return proxyId;
    }

    public int getDatacenterId(){
	return dcId;
    }

    public String toString(){
	return "++ PROXY " + super.toString();
    }

}