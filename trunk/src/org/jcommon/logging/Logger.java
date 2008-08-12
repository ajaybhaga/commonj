/*
 * Created on Sep 3, 2004
 */
package org.jcommon.logging;

/**
 * @author Matt Hicks
 */
public abstract class Logger {
    public static final int DEBUG = 5;
    public static final int WARNING = 2;
    public static final int WARN = 2;
    public static final int INFORMATION = 3;
    public static final int INFO = 3;
    public static final int ERROR = 4;
    
    protected Layout layout;
    protected int[] levels;
    
    public abstract void log(int level, String message);
    
    public void debug(String message) {
        log(Logger.DEBUG, message);
    }
    
    public void warn(String message) {
        log(Logger.WARNING, message);
    }
    
    public void info(String message) {
        log(Logger.INFORMATION, message);
    }
    
    public void error(String message) {
        log(Logger.ERROR, message);
    }
    
    public void setLayout(Layout layout) {
        this.layout = layout;
    }
    
    public Layout getLayout() {
        return layout;
    }
    
    public void setLogLevels(int[] levels) {
        this.levels = levels;
    }
    
    public int[] getLogLevels() {
        return levels;
    }
    
    public static String getLevelString(int level) {
        if (level == Logger.ERROR) {
            return "ERROR";
        } else if (level == Logger.WARNING) {
            return "WARNING";
        } else if (level == Logger.INFORMATION) {
            return "INFORMATION";
        } else if (level == Logger.DEBUG) {
            return "DEBUG";
        }
        return null;
    }
    
    public static String getLevelStringHTML(int level) {
        if (level == Logger.ERROR) {
            return "<font color=\"#993333\"><b>ERROR</b></font>";
        } else if (level == Logger.WARNING) {
            return "<font color=\"#999900\"><b>WARN</b></font>";
        } else if (level == Logger.INFORMATION) {
            return "<font color=\"#0000FF\"><b>INFO</b></font>";
        } else if (level == Logger.DEBUG) {
            return "<font color=\"#AAAAAA\"><b>DEBUG</b></font>";
        }
        return null;
    }
    
    public static String getHTMLStackTrace(Exception e) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<span style=\"background: #ffaaaa\"><b>" + e.getClass().getName() + " (" + e.getMessage() + ") Trace Follows:</b></span><br/>\r\n");
        StackTraceElement[] trace = e.getStackTrace();
        String source;
        for (int i = 0; i < trace.length; i++) {
            if (trace[i].getFileName() != null) {
                source = "<b>" + trace[i].getFileName() + ":" + trace[i].getLineNumber() + "</b>";
            } else {
                source = "<i>Unknown Source</i>";
            }
            buffer.append("&#160;&#160;&#160;&#160;" + trace[i].getClassName() + "." + trace[i].getMethodName() + "(" + source + ")<br/>\r\n");
        }
        return buffer.toString();
    }
}
