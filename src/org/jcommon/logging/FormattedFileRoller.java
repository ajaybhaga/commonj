/*
 * Created on Sep 3, 2004
 */
package org.jcommon.logging;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class FormattedFileRoller extends Thread implements FileLogHandler {
    private static final NumberFormat nf = new DecimalFormat();
	static {
	    nf.setMinimumIntegerDigits(2);
	}
    private static final ArrayList days;
	private static final ArrayList months;
	static {
		days = new ArrayList();
		days.add("");
		days.add("Sun");
		days.add("Mon");
		days.add("Tues");
		days.add("Wed");
		days.add("Thurs");
		days.add("Fri");
		days.add("Sat");
		
		months = new ArrayList();
		months.add("Jan");
		months.add("Feb");
		months.add("Mar");
		months.add("Apr");
		months.add("May");
		months.add("Jun");
		months.add("Jul");
		months.add("Aug");
		months.add("Sep");
		months.add("Oct");
		months.add("Nov");
		months.add("Dec");
	}
	
    protected String formattedFile;
    protected Layout layout;
    protected List buffer;
    protected File previous;
    
    public FormattedFileRoller(String formattedFile, Layout layout) {
        this.formattedFile = formattedFile;
        this.layout = layout;
        buffer = Collections.synchronizedList(new ArrayList());
        this.start();
    }
    
    public void run() {
        while (LoggingSystem.isOpen()) {
            try {
                Thread.sleep(500);
            } catch(InterruptedException e) {
            }
            threadedLogging();
        }
        threadedLogging();
    }
    
    public synchronized void threadedLogging() {
        if (buffer.size() > 0) {
	        try {
	            File file = deriveFile();
	            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
	            int size = buffer.size() - 1;
	            for (int i = 0; i <= size; i++) {
	                writer.write((String)buffer.get(i));
	            }
	            for (int i = size; i >= 0; i--) {
	                buffer.remove(i);
	            }
	            writer.flush();
	            writer.close();
	            previous = file;
	        } catch(Exception e) {
	            // TODO determine how errors should be handled
	            e.printStackTrace();
	        }
        }
    }
    
    public void log(String message) {
        buffer.add(message);
        
        // Keep from running out of memory
        if (buffer.size() > 10000) {
            threadedLogging();
        }
    }

    public File deriveFile() {
        Calendar c = new GregorianCalendar();
        String format = formattedFile;
        format = LoggingSystem.replaceAll(format, "%EEE%", (String)days.get(c.get(Calendar.DAY_OF_WEEK)));
		format = LoggingSystem.replaceAll(format, "%d%", String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
		format = LoggingSystem.replaceAll(format, "%dd%", nf.format(c.get(Calendar.DAY_OF_MONTH)));
		format = LoggingSystem.replaceAll(format, "%MMM%", (String)months.get(c.get(Calendar.MONTH)));
		format = LoggingSystem.replaceAll(format, "%mmm%", nf.format(c.get(Calendar.MONTH) + 1));
		format = LoggingSystem.replaceAll(format, "%yyyy%", String.valueOf(c.get(Calendar.YEAR)));
		format = LoggingSystem.replaceAll(format, "%HH%", nf.format(c.get(Calendar.HOUR_OF_DAY)));
		format = LoggingSystem.replaceAll(format, "%mm%", nf.format(c.get(Calendar.MINUTE)));
		format = LoggingSystem.replaceAll(format, "%ss%", nf.format(c.get(Calendar.SECOND)));
		format = LoggingSystem.replaceAll(format, "%Z%", c.getTimeZone().getID());
		File file = new File(format);
		if (!file.exists()) {
		    buffer.add(0, layout.getHeader());
		    buffer.add(1, layout.getLogStart());
		} else if (previous == null) {
		    buffer.add(0, layout.getLogStart());
		}
		return file;
    }    
}
