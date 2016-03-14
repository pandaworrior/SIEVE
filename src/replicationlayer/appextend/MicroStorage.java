package replicationlayer.appextend;
import replicationlayer.core.util.Debug;

import replicationlayer.core.txstore.membership.Membership;
import replicationlayer.core.txstore.membership.Role;
import replicationlayer.core.txstore.storageshim.StorageShim;

import replicationlayer.core.network.netty.NettyTCPSender;
import replicationlayer.core.network.netty.NettyTCPReceiver;
import replicationlayer.core.network.ParallelPassThroughNetworkQueue;

public class MicroStorage {

	public static void main(String arg[]) {

		if (arg.length != 7) {
			System.out.println("usage: StubStorage config.xml db.xml dcId stroageId threadcount tcnnodelay scratchpadNum");
			System.exit(0);
		}
		
		
		Boolean tcpnodelay = Boolean.parseBoolean(arg[5]);
		
		Membership mem = new Membership(arg[0], Integer.parseInt(arg[2]),
				Role.STORAGE, Integer.parseInt(arg[3]));
		StorageShim imp = new StorageShim(arg[0], Integer.parseInt(arg[2]),
				Integer.parseInt(arg[3]), new CommitScratchpadFactory(mem
						.getDatacenterCount(), Integer.parseInt(arg[2]), Integer.parseInt(arg[3]),arg[1],Integer.parseInt(arg[6])));

		// set up the networking for outgoing messages
		NettyTCPSender sendNet = new NettyTCPSender();
		imp.setSender(sendNet);
		//sendNet.setTCPNoDelay(tcpnodelay);
		sendNet.setTCPNoDelay(false);
		sendNet.setKeepAlive(true);

		// set up the networking for incoming messages
		// first, create the pipe from the network to the proxy
		int threadcount = Integer.parseInt(arg[4]);
                ParallelPassThroughNetworkQueue ptnq = new ParallelPassThroughNetworkQueue(imp, threadcount);
		// then create the actual network
		
		NettyTCPReceiver rcv = new NettyTCPReceiver(
				imp.getMembership().getMe().getInetSocketAddress(), ptnq, threadcount);

	}

}
