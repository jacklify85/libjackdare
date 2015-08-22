package xyz.jacklify.server.netio;

import xyz.jacklify.netutils.PacketBase;

public abstract class PeerBase {
	private Connection connection = null;
	
	public PeerBase(Connection connection) {
		this.connection = connection;
	}
	
	public String getIP() {
		return this.connection.ipAddress;
	}
	
	public int getPort() {
		return this.connection.outboundPort;
	}
	

	public void kick(int id, String msg) {
		this.connection.kick(id, msg);
	}
	
	public void sendPacket(PacketBase base) {
		this.connection.getContext().writeAndFlush(base);
	}
}
