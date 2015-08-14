package xyz.jacklify.server.internal;

import io.netty.buffer.ByteBuf;
import xyz.jacklify.netutils.PacketBase;

public class Packet0Login implements PacketBase{

	// SERVER -> SERVER
	private String passphrase = null;
	
	// CLIENT, SERVER -> SERVER
	private int type = 0; // 0 - Client, 1 = Server
	
	private String protocolVersion;
	private String gameVersion;
	private String[] extraData = null;
	
	// SERVER -> CLIENT, SERVER
	private int responseCode = 0;
	private String optionalMsg = null;
	
	public Packet0Login() { }
	
	public Packet0Login(int responseCode, String optionalMsg) {
		this.responseCode = responseCode;
		this.optionalMsg = optionalMsg;
	}
	
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
		netBuf.writeInt(this.responseCode);
		netBuf.writeInt((this.optionalMsg != null) ? 0 : 1);
		if (this.optionalMsg != null) {
			netBuf.writeInt(this.optionalMsg.length());
			netBuf.writeBytes(this.optionalMsg.getBytes());
		}
	}

	@Override
	public void readData(ByteBuf netBuf) throws Exception {
		this.type = netBuf.readInt();
		
		if (this.type == 1) {
			byte[] pass = new byte[netBuf.readInt()];
			netBuf.readBytes(pass);
			this.passphrase = new String(pass, "UTF-8");
		}
		
		byte[] protocol = new byte[netBuf.readInt()];
		netBuf.readBytes(protocol);
		this.protocolVersion = new String(protocol, "UTF-8");
		byte[] game = new byte[netBuf.readInt()];
		netBuf.readBytes(game);
		this.protocolVersion = new String(game, "UTF-8");
		
		int extra = netBuf.readInt();
		if (extra > 0) {
			this.extraData = new String[extra];
			for (int i = 0; i < extra; i++) {
				byte[] data = new byte[netBuf.readInt()];
				netBuf.readBytes(data);
				this.extraData[i] = new String(data, "UTF-8");
			}
		}
	}

	@Override
	public int getLength() {
		return 4 + 4 + ((this.optionalMsg != null) ? (4 + this.optionalMsg.length()) : 0);
	}

	// CLIENT -> SERVER
	public int getType() {
		return this.type;
	}
	
	public String getPassphrase() {
		return this.passphrase;
	}
	
	public String getProtocolVersion() {
		return this.protocolVersion;
	}
	
	public String getImplementationVersion() {
		return this.gameVersion;
	}
	
	public String[] getExtraData() {
		return this.extraData;
	}
	
	// SERVER -> CLIENT
	public void setResponseCode(int code) {
		this.responseCode = code;
	}
	
	public void setOptionalMsg(String msg) {
		this.optionalMsg = msg;
	}
}
