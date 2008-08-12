package org.jcommon.util;

import java.io.*;

public class StreamUtilities {
	public static void stream(InputStream in, OutputStream out) throws IOException {
		stream(in, out, true);
	}
	
	public static void stream(InputStream in, OutputStream out, boolean finish) throws IOException {
		byte[] b = new byte[512];
		int len;
		while ((len = in.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.flush();
		if (finish) {
			out.close();
			in.close();
		}
	}
	
	public static void stream(String in, OutputStream out) throws IOException {
		out.write(in.getBytes());
	}
}
