package xyz.jacklify.client.netio;

import io.netty.buffer.ByteBuf;
import xyz.jacklify.netutils.PacketBase;

public class Packet0Login implements PacketBase {

	private String protocolVersion = null;
	private String gameVersion = null;
	private String[] extraData = null;
	///////////////////////////////////
	private int response_code = -1;
	private int hasMsg = 0;
	private String msg = null;
	
	public Packet0Login(String protocolVersion, String gameVersion, String[] extraData) {
		this.protocolVersion = protocolVersion;
		this.gameVersion = gameVersion;
		this.extraData = extraData;
	}
	
	public Packet0Login() { }

	@Override
	public String getName() {
		return "ILogin";
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public void writeData(ByteBuf netBuf) throws Exception {
		netBuf.writeInt(0);
		netBuf.writeInt(this.protocolVersion.length());
		netBuf.writeBytes(this.protocolVersion.getBytes());
		netBuf.writeInt(this.gameVersion.length());
		netBuf.writeBytes(this.gameVersion.getBytes());
		netBuf.writeInt((this.extraData == null) ? 0 : this.extraData.length);
		if (this.extraData != null) {
			for (int i = 0; i < this.extraData.length; i++) {
				String data = this.extraData[i];
				netBuf.writeInt(data.length());
				netBuf.writeBytes(data.getBytes());
			}
		}
	}

	@Override
	public void readData(ByteBuf netBuf) throws Exception {
		this.response_code = netBuf.readInt();
		this.hasMsg = netBuf.readInt();
		switch (this.hasMsg) {
		case 0: {
			int length = netBuf.readInt();
			byte[] data = new byte[length];
			this.msg = new String(data, "UTF-8");
			return;
		}
		case 1: {
			return;
		}
		}
	}

	@Override
	public int getLength() {
		int length = 4 + 4 + this.protocolVersion.length() + 4 + this.gameVersion.length() + 4;
		if (this.extraData != null) {
			for (int i = 0; i < this.extraData.length; i++) {
				length += (4 + this.extraData[i].length());
			}
		}
		return length;
	}
}
