/*
 * Created on Nov 24, 2004
 */
package org.jcommon.logging;

import java.io.*;

/**
 * @author Matt Hicks
 */
public class LoggingOutputStream extends OutputStream {
    private LoggingSystem ls;
    private Logger logger;
    private int level;
    private StringBuffer buffer;
    
    public LoggingOutputStream(LoggingSystem ls, int level) {
        this.ls = ls;
        this.level = level;
        buffer = new StringBuffer();
    }
    
    public LoggingOutputStream(Logger logger, int level) {
        this.logger = logger;
        this.level = level;
        buffer = new StringBuffer();
    }
    
    public void write(int b) throws IOException {
        char c = (char)b;
        if (c == '\r') {
            // Ignore
        } else if (c == '\n') {
            if (ls != null) {
                ls.log(level, buffer.toString());
            } else {
                logger.log(level, buffer.toString());
            }
            buffer = new StringBuffer();
        } else {
            buffer.append(c);
        }
    }
}
