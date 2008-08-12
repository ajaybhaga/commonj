package org.jcommon.io.jar;

import java.io.*;
import java.net.*;

/**
 * @author Matt Hicks
 */
public class JARConnection extends URLConnection {
	protected JARConnection(URL url) {
		super(url);
	}

	public void connect() throws IOException {
	}
	
	public InputStream getInputStream() throws IOException {
		String s = url.toString();
		s = s.substring(s.indexOf('/'));
		while (s.startsWith("/")) {
			s = s.substring(1);
		}
		s = "/" + s;
		s = s.substring(0, s.indexOf('!'));
		File file = new File(s);
		return new JARInputStream(file, url.getFile().substring(url.getFile().indexOf("!") + 2));
	}
}
