package org.jcommon.io;

import java.io.*;
import java.net.*;

/**
 * This server simply listens for incoming connections and
 * outputs what it receives from the client.
 * 
 * @author Matt Hicks
 */
public class ProxyServer {
	public static void main(String[] args) throws Exception {
		ServerSocket server = new ServerSocket(80);
		final Socket s = server.accept();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		final Socket remote = new Socket(InetAddress.getLocalHost(), 8080);
		final BufferedReader remoteReader = new BufferedReader(new InputStreamReader(remote.getInputStream()));
		final BufferedWriter remoteWriter = new BufferedWriter(new OutputStreamWriter(remote.getOutputStream()));
		
		Thread one = new Thread() {
			public void run() {
				try {
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println("Client> " + line);
						remoteWriter.write(line + "\r\n");
						remoteWriter.flush();
					}
					s.close();
				} catch(Exception exc) {
					exc.printStackTrace();
				}
			}
		};
		one.start();
		
		Thread two = new Thread() {
			public void run() {
				try {
					String line;
					while ((line = remoteReader.readLine()) != null) {
						System.out.println("Server> " + line);
						writer.write(line + "\r\n");
						writer.flush();
					}
				} catch(Exception exc) {
					exc.printStackTrace();
				}
			}
		};
		two.start();
	}
}
