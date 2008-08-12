/*
 * Created on Sep 3, 2004
 */
package org.jcommon.logging;

import java.io.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class LoggingSystem {
    protected static HashMap loggingSystems = null;
    protected static boolean open;
    
    protected String name;
    protected List loggers;
    
    protected LoggingSystem(String name) {
        open = true;
        this.name = name;
    }
    
    public static LoggingSystem getInstance(String name) {
        if (loggingSystems == null) loggingSystems = new LinkedHashMap();
        if (loggingSystems.get(name) != null) {
            return (LoggingSystem)loggingSystems.get(name);
        }
        LoggingSystem system = new LoggingSystem(name);
        loggingSystems.put(name, system);
        return system;
    }
    
    public void addLogger(Logger logger) {
        if (loggers == null) {
            loggers = Collections.synchronizedList(new ArrayList());
        }
        loggers.add(logger);
    }
    
    public String getName() {
        return name;
    }
    
    public static boolean isOpen() {
        return open;
    }
    
    public static void close() {
        loggingSystems = null;
        open = false;
    }
    
    public void log(int level, String message) {
        Logger l;
        for (int i = 0; i < loggers.size(); i++) {
            l = (Logger)loggers.get(i);
            l.log(level, message);
        }
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
    
    public void debug(String message) {
        log(Logger.DEBUG, message);
    }
    
    public static void main(String[] args) throws Exception {
        LoggingSystem ls = LoggingSystem.getInstance("testLogger");
        HTMLLayout htmlLayout = new HTMLLayout(ls);
        htmlLayout.setHeader(getFileContents(new File("header.html")));
        htmlLayout.setStartLogOpen(getFileContents(new File("start_log_open.html")));
        htmlLayout.setStartLogHeading(getFileContents(new File("start_log_heading.html")));
        htmlLayout.setStartLogClose(getFileContents(new File("start_log_close.html")));
        htmlLayout.setLogEntryOpen(getFileContents(new File("log_entry_open.html")));
        htmlLayout.setLogEntry(getFileContents(new File("log_entry.html")));
        htmlLayout.setLogEntryClose(getFileContents(new File("log_entry_close.html")));
        htmlLayout.setFooter("");
        FileLogger fileLogger = new FileLogger(new FormattedFileRoller("test %yyyy%.%mmm%.%dd%.html", htmlLayout));
        fileLogger.setLayout(htmlLayout);
        fileLogger.setLogLevels(new int[] {Logger.ERROR, Logger.WARNING, Logger.INFORMATION});
        MailLogger mailLogger = new MailLogger("from@site.com", new String[] {"recipient@site.com"}, "host");
        mailLogger.setLayout(htmlLayout);
        mailLogger.setLogLevels(new int[] {Logger.ERROR, Logger.WARNING, Logger.INFORMATION});
        ls.addLogger(fileLogger);
        ls.addLogger(mailLogger);
        
        long time = System.currentTimeMillis();
        
        ls.log(Logger.INFORMATION, "Testing: " + (System.currentTimeMillis() - time) + "<br/>");
        ls.log(Logger.WARNING, "Testing a warning message into the system: " + (System.currentTimeMillis() - time) + "<br/>");
        LoggingSystem.close();
        System.out.println("Total time: " + (System.currentTimeMillis() - time));
    }
    
    public static String getFileContents(File file) {
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\r\n");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    
    public static String replaceAll(String text, String replace, String value) {
		if ((text != null) && (replace != null) && (value != null)) {
			int previous = 0;
			int current = text.indexOf(replace);
			StringBuffer buffer = new StringBuffer();
			while (current > -1) {
				buffer.append(text.substring(previous, current));
				buffer.append(value);
				current += replace.length();
				previous = current;
				current = text.indexOf(replace, current);
			}
			if (previous < text.length()) {
				buffer.append(text.substring(previous));
			}
			return buffer.toString();
		}
		return null;
	}
}
