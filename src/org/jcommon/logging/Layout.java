/*
 * Created on Sep 3, 2004
 */
package org.jcommon.logging;

/**
 * @author Matt Hicks
 */
public interface Layout {
    public void setHeader(String header);
    
    public void setFooter(String footer);
    
    public void setLogEntry(String format);
    
    public String getHeader();
    
    public String getLogStart();
    
    public String format(int level, String message);
}
