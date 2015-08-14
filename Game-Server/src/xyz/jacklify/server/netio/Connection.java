package xyz.jacklify.server.netio;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import xyz.jacklify.netutils.DisconnectCodes;
import xyz.jacklify.netutils.PacketBase;
import xyz.jacklify.server.InternalServerManager;
import xyz.jacklify.server.internal.Packet0Login;
import xyz.jacklify.server.internal.Packet1Kick;

public class Connection extends ChannelHandlerAdapter {

	private ChannelHandlerContext context = null;
	private PeerBase handler = null;
	private boolean authenticated = false;
	private InternalServerManager server = null;
	
	private Logger logger = LogManager.getLogger("Game-Server");
	private long connectTime = System.currentTimeMillis();
	protected String ipAddress;
	protected int outboundPort;
	
	public Connection(InternalServerManager server) {
		this.server = server;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.context = ctx;
		InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
		this.ipAddress = addr.getHostString();
		this.outboundPort = addr.getPort();
		this.logger.error("Net: Got connection from " + this.ipAddress + "/" + this.outboundPort);
		this.server.addUnauthenticatedPeer(this);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		this.logger.error("Net: Inactive");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		
		if (msg instanceof PacketBase) {
			
			if (this.authenticated) {
				
			} else {
				// Check for internal packets
				if (msg instanceof Packet0Login) {
					Packet0Login login = (Packet0Login)msg;
					switch (login.getType()) {
					case 0: { // Client
						this.handler = new Client(this);
						break;
					}
					case 1: { // Server
						this.handler = new ServerPeer(this);
						break;
					}
					default: { // Invalid
						this.server.removeUnauthenticatedPeer(this);
						this.logger.error("Connection: (channelRead): Protocol Violation! Client [" + this.handler.getIP() + ":" + this.handler.getPort() +"] tried to sent an invalid Packet0Login before authenticating. Disconnecting..");
					    ctx.writeAndFlush(new Packet1Kick(DisconnectCodes.PROTOCOL_VIOLATION.getId(), "Invalid Packet0Login"));
					    ctx.close();
					    return;
					}
					}
					this.server.removeUnauthenticatedPeer(this);
					ctx.writeAndFlush(login);
					if (this.server.handleConnect(this.handler, login)) {
						
					} else {
						System.out.println("Nooo!");
					}
 				} else {
					this.logger.error("Connection: (channelRead): Protocol Violation! Client [" + this.handler.getIP() + ":" + this.handler.getPort() +"] tried to send another packet then Packet0Login before authenticating. Disconnecting..");
				    ctx.writeAndFlush(new Packet1Kick(DisconnectCodes.PROTOCOL_VIOLATION.getId(), "You must send Packet0Login and authenticate before sending any other packets!"));
				}
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	public ChannelHandlerContext getContext() {
		return this.context;
	}

	public void kick(int id, String msg) {
		 this.getContext().writeAndFlush(new Packet1Kick(id, msg));
		 this.getContext().channel().close();
	}

	public long getConnectTime() {
		return this.connectTime;
	}
}