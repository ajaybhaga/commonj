package org.jcommon.j2ee;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.jcommon.util.*;

public class PartInputStream extends InputStream {
    private HashMap headers;
    private InputStream in;
    private String boundary;
    private int[] readAhead;
    private boolean alive;
    private boolean eos;
    private String value;
    
    public PartInputStream(HashMap headers, String boundary, InputStream in) {
        this.headers = headers;
        this.in = in;
        this.boundary = boundary;
        alive = true;
        eos = false;
    }
    
    public boolean isFile() {
        if (getFilename() == null) {
            return false;
        }
        return true;
    }
    
    public boolean isVariable() {
        return !isFile();
    }
    
    public String getHeader(String key) {
        return (String)headers.get(key.toLowerCase());
    }
    
    public HashMap getHeaders() {
    	return headers;
    }
    
    public String getName() {
        String contentDisposition = getHeader("Content-Disposition");
        int pos = contentDisposition.indexOf("name=");
        String name = contentDisposition.substring(pos + 6, contentDisposition.indexOf("\"", pos + 6));
        return name;
    }
    
    public String getFilename() {
        String contentDisposition = getHeader("Content-Disposition");
        int pos = contentDisposition.indexOf("filename=");
        if (pos > -1) {
            String filename = contentDisposition.substring(pos + 10, contentDisposition.indexOf("\"", pos + 10));
            return filename;
        }
        return null;
    }
    
    public String getFilenameShort() {
        String filename = getFilename();
        if (filename == null) return null;
        if (filename.indexOf("\\") > -1) {
            filename = filename.substring(filename.lastIndexOf("\\") + 1);
        }
        if (filename.indexOf("/") > -1) {
            filename = filename.substring(filename.lastIndexOf("/") + 1);
        }
        return filename;
    }
    
    public int read() throws IOException {
        return nextInt();
    }
    
    public String getValue() throws IOException {
    	if (value == null) {
    		value = StringUtilities.toString(this);
    	}
        return value;
    }
    
    private int nextInt() throws IOException {
        if (!alive) return -1;
        if (readAhead == null) {
            readAhead = new int[boundary.length()];
            for (int i = 0; i < readAhead.length; i++) {
                readAhead[i] = in.read();
            }
        }
        if (isValidBoundary()) {
            readAhead = null;
            alive = false;
            return -1;
        }
        int value = readAhead[0];
        int[] temp = new int[boundary.length()];
        int i = 0;
        for (; i < temp.length - 1; i++) {
            temp[i] = readAhead[i + 1];
        }
        if (!eos) temp[i] = in.read();
        if (temp[i] == -1) {
            eos = true;
        }
        readAhead = temp;
        return value;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public void close() throws IOException {
        while (read() != -1);
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
    
    private boolean isValidBoundary() throws IOException {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < readAhead.length; i++) {
            buffer.append((char)readAhead[i]);
        }
        if (buffer.toString().startsWith(boundary)) {
            // Get past \r and \n
            in.read();
            in.read();
            
            return true;
        }
        return false;
    }
}