package xyz.jacklify.netutils;

import io.netty.buffer.ByteBuf;

public interface PacketBase {
	
	public String getName();
	
	public int getId();
	
	public void writeData(ByteBuf netBuf) throws Exception;
	
	public void readData(ByteBuf netBuf) throws Exception;

	public int getLength();
}
