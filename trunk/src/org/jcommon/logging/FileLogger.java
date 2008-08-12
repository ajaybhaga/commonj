/*
 * Created on Sep 3, 2004
 */
package org.jcommon.logging;
/**
 * @author Matt Hicks
 */
public class FileLogger extends Logger {
    protected FileLogHandler flh;
    
    public FileLogger(FileLogHandler flh) {
        this.flh = flh;
        levels = new int[] {Logger.INFORMATION, Logger.WARNING, Logger.ERROR};
    }
    
    public void log(int level, String message) {
        for (int i = 0; i < getLogLevels().length; i++) {
            if (level == getLogLevels()[i]) {
                flh.log(getLayout().format(level, message));
                break;
            }
        }
    }
}
