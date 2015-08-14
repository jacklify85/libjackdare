package xyz.jacklify.netutils;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PacketDecoder extends ByteToMessageDecoder{

	private boolean isWorking = false;
	private int length;
	
	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1,	List<Object> arg2) throws Exception {
		// first is always in int containing length 
		if (arg1.readableBytes() >= 4 && !isWorking) {
			this.isWorking = true;
			this.length = arg1.readInt();
			
			if (this.length < 0) {
				System.out.println("Got malformed packet, kicking client");
				return;
			}
			if (length > arg1.readableBytes()) {
				return;
			}
		}
		
		if (isWorking && this.length <= arg1.readableBytes()) {
			this.isWorking = false;
			// Get packet ID and create packet
			int id = arg1.readInt();
			
			try {
				PacketBase packet = PacketFactory.getPacket(id);
				packet.readData(arg1);
				arg2.add(packet);
			} catch (Exception e) {
				System.out.println("Got malformed packet, Kicking client");
				e.printStackTrace();
			}
		}
	}
}
