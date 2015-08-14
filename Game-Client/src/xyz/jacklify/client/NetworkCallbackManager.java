package xyz.jacklify.client;

import xyz.jacklify.netutils.PacketBase;

public abstract class NetworkCallbackManager {

	private InternalClientManager icm = null;
	
	protected void setClientManager(InternalClientManager icm) {
		this.icm = icm;
	}
	
	public InternalClientManager getClientManager() {
		return this.icm;
	}
	
	public abstract void onConnect();
	
	public abstract void onDisconnect();
	
	public abstract void onConnectFailure(int id);
	
	public abstract void onPacketReceive(PacketBase base);
}
