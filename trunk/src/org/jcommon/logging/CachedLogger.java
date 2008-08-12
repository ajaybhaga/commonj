/*
 * Created on Nov 22, 2004
 */
package org.jcommon.logging;

import java.util.*;

/**
 * @author Matt Hicks
 */
public class CachedLogger extends Logger {
    public static int maxSize = 200;
    
    private LoggingSystem ls;
    
    private List debugLog;
    private List infoLog;
    private List warnLog;
    private List errorLog;
    
    public CachedLogger(LoggingSystem ls) {
        super();
        this.ls = ls;
        
        debugLog = new ArrayList();
        infoLog = new ArrayList();
        warnLog = new ArrayList();
        errorLog = new ArrayList();
    }
    
    public void log(int level, String message) {
        LogEvent event = new LogEvent(new GregorianCalendar(), Thread.currentThread(), level, ls, message);
        if (level == Logger.DEBUG) {
            if (debugLog.size() >= maxSize) {
                debugLog.remove(0);
            }
            debugLog.add(event);
        } else if (level == Logger.INFORMATION) {
            if (infoLog.size() >= maxSize) {
                infoLog.remove(0);
            }
            infoLog.add(event);
        } else if (level == Logger.WARNING) {
            if (warnLog.size() >= maxSize) {
                warnLog.remove(0);
            }
            warnLog.add(event);
        } else if (level == Logger.ERROR) {
            if (errorLog.size() >= maxSize) {
                errorLog.remove(0);
            }
            errorLog.add(event);
        }
    }
    
    public List getDebugLog() {
        return debugLog;
    }
    
    public List getInfoLog() {
        return infoLog;
    }
    
    public List getWarnLog() {
        return warnLog;
    }
    
    public List getErrorLog() {
        return errorLog;
    }
    
    public List getAllLogs() {
        List all = new ArrayList();
        all.addAll(getDebugLog());
        all.addAll(getInfoLog());
        all.addAll(getWarnLog());
        all.addAll(getErrorLog());
        return all;
    }
}
