package org.jcommon.io.jar;

import java.io.*;
import java.util.jar.*;

public class JARInputStream extends InputStream {
	private JarInputStream jis;
	private JarEntry entry;
	
	public JARInputStream(File file, String resource) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		jis = new JarInputStream(fis);
		while ((entry = jis.getNextJarEntry()) != null) {
			if (entry.getName().equals(resource)) {
				break;
			}
			entry = null;
		}
		if (entry == null) {
			throw new IOException("Unable to find resource: " + resource);
		}
	}
	
	public int read() throws IOException {
		return jis.read();
	}
	
	public void close() throws IOException {
		jis.close();
	}
}