/**
 * Created: Sep 22, 2006
 */
package org.jcommon.io;

import java.io.*;

/**
 * @author Matt Hicks
 */
public class StringOutputStream extends OutputStream {
	private StringBuffer buffer;
	
	public StringOutputStream() {
		buffer = new StringBuffer();
	}
	
	public void write(int b) throws IOException {
		//System.out.println((char)b);
		buffer.append((char)b);
	}
	
	public String getString() {
		return buffer.toString();
	}
	
	public void clear() {
		buffer.delete(0, buffer.length());
	}
}
