package xyz.jacklify.server.debug;

import xyz.jacklify.server.NetworkCallbackManager;
import xyz.jacklify.server.internal.Packet0Login;
import xyz.jacklify.server.netio.Client;
import xyz.jacklify.server.netio.ServerPeer;

public class StubbedCallbackManager extends NetworkCallbackManager {

	@Override
	public void onServerStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServerFailure(String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onClientConnect(Client client, Packet0Login login) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onClientDisconnect(Client client) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPeerConnect(ServerPeer peer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPeerDisconnect(ServerPeer peer) {
		// TODO Auto-generated method stub
		
	}

}
