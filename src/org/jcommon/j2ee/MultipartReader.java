/*
 * Created on Jul 1, 2005
 */
package org.jcommon.j2ee;

import java.io.*;
import java.util.*;

public class MultipartReader {
    private InputStream in;
    private String boundary;
    private PartInputStream current;
    
    public MultipartReader(InputStream in) throws IOException {
        this.in = in;
        boundary = "\r\n" + readLine();
    }
    
    public PartInputStream nextPart() throws IOException {
        if ((current != null) && (current.isAlive())) current.close();
        
        HashMap headers = new LinkedHashMap();
        String line;
        String key;
        String value;
        while ((line = readLine()) != null) {
            if (line.equals("")) break;
            key = line.substring(0, line.indexOf(":")).trim();
            value = line.substring(line.indexOf(":") + 1).trim();
            headers.put(key.toLowerCase(), value);
        }
        if (headers.size() == 0) {
            return null;
        }
        current = new PartInputStream(headers, boundary, in);
        return current;
    }
    
    private String readLine() throws IOException {
        StringBuffer buffer = new StringBuffer();
        int i;
        char c;
        while ((i = in.read()) != -1) {
            c = (char)i;
            if (c == '\r') {
                i = in.read();
                c = (char)i;
                if (c == '\n') {
                    return buffer.toString();
                }
            }
            buffer.append(c);
        }
        if (buffer.length() == 0) return null;
        return buffer.toString();
    }
    
    public InputStream getInputStream() {
    	return in;
    }
    
    public void close() throws IOException {
    	in.close();
    }
}