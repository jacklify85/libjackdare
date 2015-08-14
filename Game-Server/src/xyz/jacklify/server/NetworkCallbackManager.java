package xyz.jacklify.server;

import xyz.jacklify.server.internal.Packet0Login;
import xyz.jacklify.server.netio.Client;
import xyz.jacklify.server.netio.ServerPeer;

public abstract class NetworkCallbackManager {

	private InternalServerManager server = null;
	
	protected void setServer(InternalServerManager server) {
		this.server = server;
	}
	
	public InternalServerManager getServer() {
		return this.server;
	}
	
	public abstract void onServerStart();
	
	public abstract void onServerFailure(String reason);
	
	public abstract boolean onClientConnect(Client client, Packet0Login login);
	
	public abstract void onClientDisconnect(Client client);
	
	public abstract boolean onPeerConnect(ServerPeer peer);
	
	public abstract void onPeerDisconnect(ServerPeer peer);
}
