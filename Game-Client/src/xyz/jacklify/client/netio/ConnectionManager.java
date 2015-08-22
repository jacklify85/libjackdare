package xyz.jacklify.client.netio;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import xyz.jacklify.client.InternalClientManager;
import xyz.jacklify.netutils.PacketBase;

public class ConnectionManager extends ChannelHandlerAdapter{

	private InternalClientManager icm = null;
	private String[] extraData = null;
	private String protocolVersion = null;
	private String gameVersion = null;
	
	private ChannelHandlerContext ctx;
	
	public ConnectionManager(InternalClientManager icm, String protocolVersion, String gameVersion, String[] extra) {
		this.icm = icm;
		this.extraData = extra;
		this.protocolVersion = protocolVersion;
		this.gameVersion = gameVersion;
	}
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		// Send Packet0Login
		Packet0Login login = new Packet0Login(this.protocolVersion, this.gameVersion, this.extraData);
		ctx.writeAndFlush(login);
		this.ctx = ctx;
		this.icm.mgr.onConnect();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		this.icm.mgr.onDisconnect();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		this.icm.mgr.onPacketReceive((PacketBase)msg);
		if (!(msg instanceof PacketBase)) {
			super.channelRead(ctx, msg);
		} else {
			if (msg instanceof Packet0Login) {
				Packet0Login login = (Packet0Login)msg;
				
			}
		}
	}


	public void send(PacketBase base) {
		this.ctx.writeAndFlush(base);
	}
}