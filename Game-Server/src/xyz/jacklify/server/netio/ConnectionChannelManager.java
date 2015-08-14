package xyz.jacklify.server.netio;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import xyz.jacklify.netutils.PacketDecoder;
import xyz.jacklify.netutils.PacketEncoder;
import xyz.jacklify.server.InternalServerManager;


public class ConnectionChannelManager extends ChannelInitializer<SocketChannel>{

	private SslContext context;
	private InternalServerManager server = null;
	
	public ConnectionChannelManager(InternalServerManager server) {
		this.context = null;
		this.server = server;
	}
	
	public ConnectionChannelManager(SslContext ssl, InternalServerManager server) {
		this.context = ssl;
		this.server = server;
	}

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		// Check if we are using SSL
		if (context != null) {
			ChannelHandler sslHandler = this.context.newHandler(arg0.alloc());
			arg0.pipeline().addFirst("ssl", sslHandler);
		}
		
		// Load NetUtils
		if (context != null) {
			arg0.pipeline().addAfter("ssl", "encoder", new PacketEncoder());
		} else {
			arg0.pipeline().addFirst("encoder", new PacketEncoder());
		}
		
		arg0.pipeline().addAfter("encoder", "decoder", new PacketDecoder());
		
		// Load client manager
		arg0.pipeline().addLast(new Connection(this.server));
	}
	
	
}
