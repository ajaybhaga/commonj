/*
 * Created on Jul 23, 2004
 */
package org.jcommon.j2ee;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

/**
 * @author Matt Hicks
 */
public class AdvancedPOST {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected BufferedReader reader;
    protected String boundary;
    protected HashMap headers;
    protected HashMap parameters;
    
    // TODO rewrite to handle files and other data
    
    public AdvancedPOST(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.request = request;
        this.response = response;
        reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        boundary = reader.readLine();
        parameters = new LinkedHashMap();
    }
    
    public boolean nextPart() throws IOException {
        String line;
        headers = new LinkedHashMap();
        String key;
        String value;
        while ((line = nextLine()) != null) {
            if (line.equals("")) return true;
            String[] header = line.split("; ");
            for (int i = 0; i < header.length; i++) {
                if (header[i].indexOf("=") > 0) {
                    key = header[i].substring(0, header[i].indexOf("="));
                    value = header[i].substring(header[i].indexOf("=") + 2, header[i].length() - 1);
                    headers.put(key, value);
                }
            }
        }
        return false;
    }
    
    public String[] getPartLines() throws IOException {
        ArrayList list = new ArrayList(500);
        String line;
        while ((line = nextLine()) != null) {
            if (line.startsWith(boundary)) {
                if (((String)list.get(list.size() - 1)).length() == 0) list.remove(list.size() - 1);
                String[] lines = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    lines[i] = (String)list.get(i);
                }
                return lines;
            }
            list.add(line);
        }
        throw new IOException("Unexpected end of file.");
    }
    
    public String nextLine() throws IOException {
        String line = reader.readLine();
        return line;
    }
    
    public String getHeader(String key) {
        if (headers != null) {
            return (String)headers.get(key);
        }
        return null;
    }
}
