/*
 * Created on May 27, 2005
 */
package org.jcommon.swing.robust;

import java.util.*;

/**
 * @author Matt Hicks
 */
public class RobustException {
    private Throwable t;
    
    public RobustException(Throwable t) {
        this.t = t;
    }
    
    public String[] getStackTrace() {
        return getStackTrace(t);
    }
    
    public static String[] getStackTrace(Throwable t) {
        ArrayList list = new ArrayList();
        list.add(t.getClass().getName() + " (" + t.getMessage() + ") Trace Follows:");
        StackTraceElement[] trace = t.getStackTrace();
        String start;
        String source;
        int depth = 0;
        while (t != null) {
	        for (int i = 0; i < trace.length; i++) {
	            if (trace[i].getFileName() != null) {
	                source = trace[i].getFileName() + ":" + trace[i].getLineNumber() + "\r\n";
	            } else {
	                source = "Unknown Source";
	            }
	            if (depth > 0) {
	                start = "\t\t";
	            } else {
	                start = "\t";
	            }
	            list.add(start + trace[i].getClassName() + "." + trace[i].getMethodName() + "(" + source + ")");
	        }
	        t = t.getCause();
	        depth++;
        }

        String[] st = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            st[i] = (String)list.get(i);
        }
        return st;
    }
}
