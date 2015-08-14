package xyz.jacklify.client.netio;

import io.netty.buffer.ByteBuf;
import xyz.jacklify.netutils.PacketBase;

public class Packet1Kick implements PacketBase {

	private int id = 0;
	@Override
	public String getName() {
		return "IKick";
	}

	@Override
	public int getId() {
		return 1;
	}

	@Override
	public void writeData(ByteBuf netBuf) throws Exception {
		
	}

	@Override
	public void readData(ByteBuf netBuf) throws Exception {
		
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

}
