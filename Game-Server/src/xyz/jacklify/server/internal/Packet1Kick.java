package xyz.jacklify.server.internal;

import io.netty.buffer.ByteBuf;
import xyz.jacklify.netutils.PacketBase;
import xyz.jacklify.netutils.ProtocolViolationException;

public class Packet1Kick implements PacketBase {

	// SERVER <-> CLIENT
	private int code = 0;
	private String msg = null;
	
	public Packet1Kick(int code, String string) {
		this.code = code;
		this.msg = string;
	}
	
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
		netBuf.writeInt(this.code);
		netBuf.writeInt((this.msg == null) ? 0 : 1);
		if (this.msg != null) {
			netBuf.writeInt(msg.length());
			netBuf.writeBytes(msg.getBytes());
		}
	}

	@Override
	public void readData(ByteBuf netBuf) throws Exception {
		this.code = netBuf.readInt();
		int res = netBuf.readInt();
		switch (res) {
		case 0: {
			break;
		}
		case 1: {
			byte[] data = new byte[netBuf.readInt()];
			netBuf.readBytes(data);
			this.msg = new String(data, "UTF-8");
			break;
		}
		default: {
			throw new ProtocolViolationException("Packet1Kick: Got unexpected message response code '" + res + "'. Valid options are 0, 1");
		}
		}
	}

	@Override
	public int getLength() {
		return 4 + 4 + (this.msg == null ? 0 : 4 + this.msg.length());
	}
}
