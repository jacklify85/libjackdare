package xyz.jacklify.server.tasks;

import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import xyz.jacklify.netutils.DisconnectCodes;
import xyz.jacklify.server.InternalServerManager;
import xyz.jacklify.server.netio.Connection;

public class UnauthenticatedFloodHandler extends TimerTask{
	private InternalServerManager server = null;
	private long kickTime = 2000;
	
	public UnauthenticatedFloodHandler(InternalServerManager server, long kickTime) {
		this.server = server;
		this.kickTime = kickTime;
	}
	
	@Override
	public void run() {
		CopyOnWriteArrayList<Connection> peers = server.getUnauthenticatedPeers();
		
		for (int i = 0; i < peers.size(); i++) {
			Connection peer = peers.get(i);
			if (System.currentTimeMillis() - peer.getConnectTime() >= this.kickTime) {
				peers.remove(peer);
		    	peer.kick(DisconnectCodes.TIME_OUT.getId(), "Login timed out");
			}
		}
	}
}
