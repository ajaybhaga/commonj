package org.jcommon.io.jar;

import java.io.*;
import java.net.*;

/**
 * @author Matt Hicks
 *
 */
public class JARStreamHandler extends URLStreamHandler {
	protected URLConnection openConnection(URL u) throws IOException {
		return new JARConnection(u);
	}
}
