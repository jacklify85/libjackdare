package xyz.jacklify.client.netio;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import xyz.jacklify.client.InternalClientManager;
import xyz.jacklify.netutils.PacketDecoder;
import xyz.jacklify.netutils.PacketEncoder;

public class NetChannelHandler extends ChannelInitializer<SocketChannel>{
	
	private InternalClientManager icm = null;
	private String protocolVersion = null;
	private String gameVersion = null;
	private String[] extraData = null;
	
	public NetChannelHandler(InternalClientManager icm, String protocolVersion, String gameVersion, String[] extraData) {
		this.icm = icm;
		this.protocolVersion = protocolVersion;
		this.gameVersion = gameVersion;
		this.extraData = extraData;
	}
	
	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		arg0.pipeline().addFirst("encoder", new PacketEncoder());
		arg0.pipeline().addAfter("encoder", "decoder", new PacketDecoder());
		arg0.pipeline().addLast(new ConnectionManager(this.icm, this.protocolVersion, this.gameVersion, this.extraData));
	}
}
