/**
 * 
 */
package org.jcommon.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Matthew CTR Hicks
 *
 */
public class StringInputStream extends InputStream {
	private String s;
	private int position;
	
	public StringInputStream(String s) {
		this.s = s;
		position = 0;
	}
	
	public int read() throws IOException {
		if (s.length() > position) {
			return s.charAt(position++);
		}
		return -1;
	}
}
