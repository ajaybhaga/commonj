package org.jcommon.web;
import java.io.*;
import java.net.*;
import java.util.*;

public class WebServer extends Thread {
	private InetAddress address;
	private int port;
	private RequestHandler handler;
	private boolean keepAlive;
	private HashMap<String,WebSession> sessions;
	
	private ServerSocket server;
	
	public WebServer(InetAddress address, int port, RequestHandler handler) throws IOException {
		this.address = address;
		this.port = port;
		this.handler = handler;
		sessions = new HashMap<String,WebSession>();
		
		server = new ServerSocket(port, -1, address);
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public RequestHandler getRequestHandler() {
		return handler;
	}
	
	public void setRequestHandler(RequestHandler handler) {
		this.handler = handler;
	}
	
	public void run() {
		keepAlive = true;
		
		System.out.println("WebServer started...");
		while (keepAlive) {
			try {
				new WebRequest(this, server.accept(), handler).start();
			} catch(IOException exc) {
				exc.printStackTrace();
			}
		}
		System.out.println("WebServer stopped successfully.");
	}
	
	public WebSession getSession(String sessionId) {
		// TODO expiration of sessions
		WebSession session = null;
		if (sessionId != null) {
			session = sessions.get(sessionId);
		}
		if (session == null) {
			session = new WebSession(createSessionId());
			sessions.put(session.getSessionId(), session);
		}
		session.referenced();
		return session;
	}
	
	public static final String createSessionId() {
		long rand = Math.round(Math.random() * Long.MAX_VALUE);
		return Long.toHexString(rand).toUpperCase();
	}
	
	public void shutdown() {
		keepAlive = false;
	}
}
