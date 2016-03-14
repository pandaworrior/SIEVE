// CHECKED ALLEN 2011.04.19

package replicationlayer.core.network.netty;

import replicationlayer.core.network.NetworkQueue;
import replicationlayer.core.util.Pair;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
  
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
 
/**
* Handler implementation for the server.
*
* @author The Netty Project (netty-dev@lists.jboss.org)
* @author Trustin Lee (tlee@redhat.com)
*
* @version $Rev: 1685 $, $Date: 2009-08-28 16:15:49 +0900 (Fri, 28 Aug 2009) $
*/
@ChannelPipelineCoverage("one")
public class ServerHandler extends SimpleChannelUpstreamHandler {

    NetworkQueue NWQ;
    
    public ServerHandler(NetworkQueue _NWQ) {
        NWQ = _NWQ;
    }
    
    private static final Logger logger = Logger.getLogger(
            ServerHandler.class.getName());

    private final AtomicLong transferredBytes = new AtomicLong();

    public long getTransferredBytes() {
        return transferredBytes.get();
    }

    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) {
    	Pair<Integer, byte[]> pair = (Pair<Integer, byte[]>)e.getMessage();
    	NWQ.addWork(pair.getRight());
    }

    @Override
    synchronized public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) {
        // Close the connection when an exception is raised.
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.",
                e.getCause());

	System.out.println("Exception caught");
	Channel c = ctx.getChannel();
	System.out.println(c.getLocalAddress());
	System.out.println(c.getRemoteAddress());

	c = e.getChannel();
	System.out.println(c.getLocalAddress());
	System.out.println(c.getRemoteAddress());
	e.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{

	System.out.println("Channel connected from "+ctx.getChannel().getRemoteAddress()+" to "+ctx.getChannel().getLocalAddress());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
	Throwable e2=new Throwable();
	//e2.printStackTrace();
        System.out.println("Channel disconnected from "+ctx.getChannel().getRemoteAddress());
    }

}
