package xyz.jacklify.server.netio;

public abstract class PeerBase {
	private String ipAddress = null;
	private int outboundPort = 0;
	
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
}
