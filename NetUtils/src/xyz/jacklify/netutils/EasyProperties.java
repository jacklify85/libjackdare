package xyz.jacklify.netutils;

import java.util.Properties;

public class EasyProperties extends Properties {
	
	public void setBoolean(String name, boolean value) {
		this.setProperty(name, String.valueOf(value));
	}

	public boolean getBoolean(String name, boolean defaultValue) {
		return Boolean.parseBoolean(this.getProperty(name, String.valueOf(defaultValue)));
	}
	
	public boolean getBoolean(String name) {
		return Boolean.parseBoolean(this.getProperty(name));
	}
	
	public void setInteger(String name, int value) {
		this.setProperty(name, String.valueOf(value));
	}
	
	public int getInteger(String name, int defaultValue) {
		return Integer.parseInt(this.getProperty(name, String.valueOf(defaultValue)));
	}
	
	public int getInteger(String name) {
		return Integer.parseInt(this.getProperty(name));
	}
	
	public void setFloat(String name, float value) {
		this.setProperty(name, String.valueOf(value));
	}
	
	public float getFloat(String name, float value) {
		return Float.parseFloat(this.getProperty(name, String.valueOf(value)));
	}
	
	public float getFloat(String name) {
		return Float.parseFloat(this.getProperty(name));
	}
	
	public void setDouble(String name, double value) {
		this.setProperty(name, String.valueOf(value));
	}
	
	public double getDouble(String name, double defaultValue) {
		return Double.parseDouble(this.getProperty(name, String.valueOf(defaultValue)));
	}
	
	public double getDouble(String name) {
		return Double.parseDouble(this.getProperty(name));
	}
	
	public void setShort(String name, short value) {
		this.setProperty(name, String.valueOf(value));
	}
	
	public short getShort(String name, short defaultValue) {
		return Short.parseShort(this.getProperty(name, String.valueOf(defaultValue)));
	}
	
	public short getShort(String name) {
		return Short.parseShort(this.getProperty(name));
	}
}
