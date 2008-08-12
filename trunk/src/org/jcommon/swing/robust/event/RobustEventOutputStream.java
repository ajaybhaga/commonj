/*
 * Created on Jun 2, 2005
 */
package org.jcommon.swing.robust.event;

import java.io.*;

import org.jcommon.swing.robust.*;

/**
 * @author Matt Hicks
 */
public class RobustEventOutputStream extends OutputStream {
    private String type;
    private RobustMonitor monitor;
    private StringBuffer buffer;
    
    public RobustEventOutputStream(String type) {
        this.type = type;
        monitor = RobustMonitor.getInstance();
        buffer = new StringBuffer();
    }

    public void write(int b) throws IOException {
        char c = (char)b;
        buffer.append(c);
        if (c == '\n') {
            monitor.fireEvent(new RobustEvent(this, type, buffer.toString()));
            buffer.delete(0, buffer.length());
        }
    }
}
