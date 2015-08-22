package xyz.jacklify.client.netio;

import io.netty.buffer.ByteBuf;
import xyz.jacklify.netutils.PacketBase;

public class Packet1Kick implements PacketBase {

	private int id = 0;
	private String msg;
	
	public Packet1Kick() { }
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
		netBuf.writeInt(this.id);
		netBuf.writeInt(0);
	}

	@Override
	public void readData(ByteBuf netBuf) throws Exception {
		this.id = netBuf.readInt();
		int isMsg = netBuf.readInt();
		if (isMsg == 1) {
			byte[] data = new byte[netBuf.readInt()];
			this.msg = new String(data, "UTF-8");
		}
	}

	@Override
	public int getLength() {
		return 4 + 4;
	}
	
	public String getMessage() {
		return this.msg;
	}
	
	public int getCode() {
		return this.id;
	}
}