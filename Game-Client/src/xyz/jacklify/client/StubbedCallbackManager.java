package xyz.jacklify.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.jacklify.netutils.DisconnectCodes;
import xyz.jacklify.netutils.PacketBase;

class StubbedCallbackManager extends NetworkCallbackManager {

	private Logger logger = LogManager.getLogger("Game-Client");
	
	@Override
	public void onConnect() {
		this.logger.info("Callbacks (stub): Connected to server successfully");
	}

	@Override
	public void onDisconnect() {
		this.logger.info("Callbacks (stub): Disconnected from server!");
	}

	@Override
	public void onConnectFailure(int id) {
		this.logger.info("Callbacks (stub): Failed to connect to server, an error has occurred. Code: " + id);
		for (DisconnectCodes code : DisconnectCodes.values()) {
			if (code.getId() == id) {
				this.logger.info("Callbacks (stub): " + code.getMessage());
				return;
			}
		}
	}

	@Override
	public void onPacketReceive(PacketBase base) {
		this.logger.info("Callbacks (stub): Received Packet! {@CLASS=" + base.getClass().getSimpleName() + "@ID=" + base.getId() + "@NAME=" + base.getName() + "}, Discarding.");
	}

}
