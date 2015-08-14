package xyz.jacklify.server;

public class Main {

	public static void main(String[] args) {
		InternalServerManager mgr = new InternalServerManager(null, null);
		mgr.setGameProtocol("chat-test");
		mgr.startServer();
		
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
