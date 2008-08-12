/*
 * Created on Nov 22, 2004
 */
package org.jcommon.logging;

import java.util.*;

/**
 * @author Matt Hicks
 */
public class LogEvent {
    private Calendar dateTime;
    private Thread thread;
    private int level;
    private LoggingSystem ls;
    private String message;
    
    public LogEvent(Calendar dateTime, Thread thread, int level, LoggingSystem ls, String message) {
        this.dateTime = dateTime;
        this.thread = thread;
        this.level = level;
        this.ls = ls;
        this.message = message;
    }
    
    public Calendar getDateTime() {
        return dateTime;
    }
    
    public int getLevel() {
        return level;
    }
    
    public LoggingSystem getLoggingSystem() {
        return ls;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Thread getThread() {
        return thread;
    }

    public String getLogLevel() {
        return Logger.getLevelString(getLevel());
    }
    
    public String getThreadName() {
        return getThread().getName();
    }
}
