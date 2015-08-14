package xyz.jacklify.netutils;

import java.io.IOException;
import java.util.HashMap;

public class PacketFactory {

	private static HashMap<Integer, Class<? extends PacketBase>> registeredPackets = new HashMap<Integer, Class<? extends PacketBase>>();
	
	public static PacketBase getPacket(int id) throws IOException, InstantiationException, IllegalAccessException {
		if (registeredPackets.get(id) == null) {
			throw new IOException("Illegal packet id " + id);
		}
		return registeredPackets.get(id).newInstance();
	}
	
	public static void registerPacket(int id, Class<? extends PacketBase> pClass) {
		registeredPackets.put(id, pClass);
	}

}
