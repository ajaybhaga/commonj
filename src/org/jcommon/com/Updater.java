package org.jcommon.com;

public class Updater implements Runnable {
	private TCPServer server;
	private boolean keepAlive;
	
	public Updater(TCPServer server) {
		this.server = server;
		keepAlive = true;
	}
	
	public void run() {
		while (keepAlive) {
			try {
				server.update();
			} catch(Exception exc) {
				exc.printStackTrace();
			}
			try {
				Thread.sleep(10);
			} catch(InterruptedException exc) {}
		}
	}
}