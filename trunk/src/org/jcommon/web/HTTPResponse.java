package org.jcommon.web;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.imageio.*;

import org.jcommon.util.*;

public class HTTPResponse {
	public static final HashMap<String,String> MIME_TYPES = new HashMap<String,String>();
	static {
		MIME_TYPES.put("gif", "image/gif");
		MIME_TYPES.put("jpeg", "image/jpeg");
		MIME_TYPES.put("jpg", "image/jpeg");
		MIME_TYPES.put("jpe", "image/jpeg");
		MIME_TYPES.put("bmp", "image/bmp");
		MIME_TYPES.put("png", "image/png");
		MIME_TYPES.put("tif", "image/tiff");
		MIME_TYPES.put("tiff", "image/tiff");
		MIME_TYPES.put("jnlp", "application/x-java-jnlp-file");
		MIME_TYPES.put("js", "application/x-javascript");
		MIME_TYPES.put("doc", "application/msword");
		MIME_TYPES.put("bin", "application/octet-stream");
		MIME_TYPES.put("exe", "application/octet-stream");
		MIME_TYPES.put("pdf", "application/pdf");
		MIME_TYPES.put("ai", "application/postscript");
		MIME_TYPES.put("eps", "application/postscript");
		MIME_TYPES.put("ps", "application/postscript");
		MIME_TYPES.put("rtf", "application/rtf");
		MIME_TYPES.put("class", "application/x-java-vm");
		MIME_TYPES.put("ser", "application/x-java-serialized-object");
		MIME_TYPES.put("jar", "application/x-java-archive");
		MIME_TYPES.put("sh", "application/x-sh");
		MIME_TYPES.put("tar", "application/x-tar");
		MIME_TYPES.put("zip", "application/zip");
		MIME_TYPES.put("ua", "audio/basic");
		MIME_TYPES.put("wav", "audio/x-wav");
		MIME_TYPES.put("mid", "audio/x-midi");
		MIME_TYPES.put("htm", "text/html");
		MIME_TYPES.put("html", "text/html");
		MIME_TYPES.put("css", "text/css");
		MIME_TYPES.put("txt", "text/plain");
		MIME_TYPES.put("mpeg", "video/mpeg");
		MIME_TYPES.put("mpg", "video/mpeg");
		MIME_TYPES.put("mpe", "video/mpeg");
		MIME_TYPES.put("qt", "video/quicktime");
		MIME_TYPES.put("mov", "video/quicktime");
		MIME_TYPES.put("avi", "video/avi");
		MIME_TYPES.put("movie", "video/x-sgi-movie");
	}
	
	public static String SERVER = "JavaWebServer/1.0";
	
	public static final int OK = 200;
	
	private Socket s;
	private int mode;
	private WebSession session;
	private HashMap<String,String> headers;
	private HashSet<String> keys;
	
	public HTTPResponse(Socket s, int mode, WebSession session) throws IOException {
		this.s = s;
		this.mode = mode;
		this.session = session;
		
		headers = new HashMap<String,String>();
		keys = new HashSet<String>();
		
		if (session.getStatus() == WebSession.UNSET) {
			addHeader("Set-Cookie", "JSESSIONID=" + session.getSessionId() + "; Path=/");
			session.setStatus(WebSession.SET);
		}
	}
	
	public void addHeader(String header, String value) {
		keys.add(header.toLowerCase());
		headers.put(header, value);
	}
	
	private void writeHeaders() throws IOException {
		if (mode == OK) {
			writeLine("HTTP/1.1 200 OK");
		}
		if (!keys.contains("server")) {
			headers.put("Server", SERVER);
		}
		if (!keys.contains("date")) {
			headers.put("Date", StringUtilities.format(new GregorianCalendar(), "%EEE%, %d% %MMM% %yyyy% %HH%:%mm%:%ss% %Z%"));
		}
		
		Iterator<String> iterator = headers.keySet().iterator();
		String key;
		String value;
		while (iterator.hasNext()) {
			key = iterator.next();
			value = headers.get(key);
			writeLine(key + ": " + value);
		}
		
		writeLine("");
	}
	
	private void writeLine(String string) throws IOException {
		s.getOutputStream().write((string + "\r\n").getBytes());
	}
	
	public void writeFile(File f) throws IOException {
		if ((!keys.contains("content-type")) && (f.getName().indexOf('.') > -1)) {
			String ext = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
			if (MIME_TYPES.containsKey(ext)) {
				headers.put("Content-Type", MIME_TYPES.get(ext));
			}
		}
		if (!keys.contains("content-length")) {
			headers.put("Content-Length", String.valueOf(f.length()));
		}
		writeHeaders();
		
		FileInputStream fis = new FileInputStream(f);
		byte[] b = new byte[512];
		int len;
		while ((len = fis.read(b)) > -1) {
			s.getOutputStream().write(b, 0, len);
		}
		s.getOutputStream().flush();
		s.close();
		fis.close();
	}
	
	public void writeURL(URL url) throws IOException {
		if ((!keys.contains("content-type")) && (url.getPath().indexOf('.') > -1)) {
			String ext = url.toString().substring(url.toString().lastIndexOf('.') + 1).toLowerCase();
			if (MIME_TYPES.containsKey(ext)) {
				headers.put("Content-Type", MIME_TYPES.get(ext));
			}
		}
		writeHeaders();
		
		InputStream is = url.openStream();
		byte[] b = new byte[512];
		int len;
		while ((len = is.read(b)) > -1) {
			s.getOutputStream().write(b, 0, len);
		}
		s.getOutputStream().flush();
		s.close();
		is.close();
	}
	
	public void writeString(String string) throws IOException {
		writeHeaders();
		s.getOutputStream().write(string.getBytes());
		s.getOutputStream().flush();
		s.close();
	}

	public void writeImage(RenderedImage image) throws IOException {
		headers.put("Content-Type", MIME_TYPES.get("png"));
		writeHeaders();
		
		ImageIO.write(image, "png", s.getOutputStream());
		s.getOutputStream().flush();
		s.close();
	}
}
