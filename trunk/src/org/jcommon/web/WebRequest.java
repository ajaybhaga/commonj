package org.jcommon.web;
import java.io.*;
import java.net.*;
import java.util.*;

public class WebRequest extends Thread {
	private WebServer server;
	private Socket s;
	private RequestHandler handler;
	
	public WebRequest(WebServer server, Socket s, RequestHandler handler) {
		this.server = server;
		this.s = s;
		this.handler = handler;
	}
	
	public void run() {
		String line;
		boolean first = true;
		HashMap<String,String> map = new HashMap<String,String>();
		while (!(line = readLine()).equals("")) {
			if (first) {
				String[] split = line.split(" ");
				if (split.length > 0) {
					map.put("PROTOCOL", split[0]);
				}
				if (split.length > 1) {
					map.put("URI", split[1]);
				}
				if (split.length > 2) {
					map.put("VERSION", split[2]);
				}
				first = false;
			} else {
				String[] split = line.split(": ");
				map.put(split[0].toUpperCase(), split[1]);
			}
		}
		
		// Handle WebSession
		WebSession session = null;
		if ((map.get("COOKIE") != null) && (map.get("COOKIE").startsWith("JSESSIONID"))) {
			session = server.getSession(map.get("COOKIE").substring("JSESSIONID=".length()));
		} else {
			session = server.getSession(null);
		}
		
		/*Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String)iterator.next();
			String value = (String)map.get(key);
			System.out.println(key + ": " + value);
		}*/
		
		try {
			handler.handleRequest(map, s, session);
		} catch(IOException exc) {
			exc.printStackTrace();
			// TODO throw to error handler
		}
	}
	
	private String readLine() {
		try {
			StringBuffer buffer = new StringBuffer();
			char c;
			while ((c = (char)s.getInputStream().read()) != -1) {
				if (c == '\r') {
					// Do nothing
				} else if (c == '\n') {
					break;
				} else {
					buffer.append(c);
				}
			}
			return buffer.toString();
		} catch(IOException exc) {
			exc.printStackTrace();
		}
		return null;
	}
	
	public static final HashMap<String,String> parseParametersFromURI(String uri) {
		// Split out URI GET values
		HashMap<String,String> params = new HashMap<String,String>();
		if ((uri != null) && (uri.indexOf('?') > -1)) {
			String[] get = uri.substring(uri.indexOf('?') + 1).split("&");
			for (int i = 0; i < get.length; i++) {
				String[] split = get[i].split("=");
				if (split.length == 1) {
					params.put(split[0], "");
				} else {
					String t = split[1];
					if (split.length > 2) {
						StringBuffer buffer = new StringBuffer();
						for (int j = 1; j < split.length; j++) {
							if (j > 1) buffer.append("=");
							buffer.append(split[j]);
						}
						t = buffer.toString();
					}
					try {
						t = URLDecoder.decode(t, "UTF-8");
					} catch(UnsupportedEncodingException exc) {}
					params.put(split[0], t);
				}
			}
		}
		return params;
	}
}
