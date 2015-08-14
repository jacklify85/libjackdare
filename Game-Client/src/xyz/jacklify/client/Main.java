package xyz.jacklify.client;

import xyz.jacklify.netutils.EasyProperties;

public class Main {

	public static void main(String[] args) {
		InternalClientManager mgr = new InternalClientManager("chat-test", null, new EasyProperties());
		mgr.connect("localhost", 3124);
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
