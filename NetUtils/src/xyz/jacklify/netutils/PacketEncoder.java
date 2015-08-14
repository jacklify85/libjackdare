package xyz.jacklify.netutils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<PacketBase>{
	@Override
	protected void encode(ChannelHandlerContext arg0, PacketBase arg1, ByteBuf arg2) throws Exception {
		arg2.writeInt(arg1.getLength() + 4);
		arg2.writeInt(arg1.getId());
		try {
			arg1.writeData(arg2);
		} catch (Exception e) {
			
		}
	}
}

