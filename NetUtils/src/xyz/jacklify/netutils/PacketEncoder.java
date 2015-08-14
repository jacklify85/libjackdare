package xyz.jacklify.netutils;

import java.io.Serializable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class PacketEncoder extends MessageToByteEncoder<PacketBase>{
/*	@Override
	protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
		System.out.println("SS");
		if (!(msg instanceof PacketBase)) {
			super.encode(ctx, msg, out);
		} else {
			System.out.println("LOL");
			PacketBase packet = (PacketBase)msg;
			out.writeInt(packet.getLength() + 4);
			out.writeInt(packet.getId());
			try {
				packet.writeData(out);
			} catch (Exception e) {
				// Failed to send data.
			}
		}
	} */

	@Override
	protected void encode(ChannelHandlerContext arg0, PacketBase arg1, ByteBuf arg2) throws Exception {
		arg2.writeInt(arg1.getLength() + 4);
		arg2.writeInt(arg1.getId());
		try {
			arg1.writeData(arg2);;
		} catch (Exception e) {
			
		}
	}
}

