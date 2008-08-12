/*
 * Created on Sep 3, 2004
 */
package org.jcommon.logging;

import java.text.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class HTMLLayout implements Layout {
    private static final ArrayList fulldays;
    private static final ArrayList days;
	private static final ArrayList months;
	static {
	    fulldays = new ArrayList();
	    fulldays.add("");
	    fulldays.add("Sunday");
	    fulldays.add("Monday");
	    fulldays.add("Tuesday");
	    fulldays.add("Wednesday");
	    fulldays.add("Thursday");
	    fulldays.add("Friday");
	    fulldays.add("Saturday");
	    
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
    
    protected String header;
    protected String footer;
    protected String format;
    
    protected String startLogOpen;
    protected String startLogHeading;
    protected String startLogClose;
    protected String logEntryOpen;
    protected String logEntryClose;
    
    protected LoggingSystem parent;
    protected Calendar startTime;
    
    public HTMLLayout(LoggingSystem parent) {
        this.parent = parent;
        startTime = new GregorianCalendar();
    }
    
    public void setHeader(String header) {
        this.header = header;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }
    
    public void setStartLogOpen(String startLogOpen) {
        this.startLogOpen = startLogOpen;
    }
    
    public void setStartLogHeading(String startLogHeading) {
        this.startLogHeading = startLogHeading;
    }
    
    public void setStartLogClose(String startLogClose) {
        this.startLogClose = startLogClose;
    }
    
    public void setLogEntryOpen(String logEntryOpen) {
        this.logEntryOpen = logEntryOpen;
    }
    
    public void setLogEntryClose(String logEntryClose) {
        this.logEntryClose = logEntryClose;
    }

    public void setLogEntry(String format) {
        this.format = format;
    }
    
    public String getHeader() {
        return replaceValues(header);
    }
    
    public String getLogStart() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(startLogOpen);
        buffer.append(LoggingSystem.replaceAll(startLogHeading, "%TITLE%", "Time"));
        buffer.append(LoggingSystem.replaceAll(startLogHeading, "%TITLE%", "Thread"));
        buffer.append(LoggingSystem.replaceAll(startLogHeading, "%TITLE%", "Level"));
        buffer.append(LoggingSystem.replaceAll(startLogHeading, "%TITLE%", "Logger"));
        buffer.append(LoggingSystem.replaceAll(startLogHeading, "%TITLE%", "Message"));
        buffer.append(startLogClose);
        return replaceValues(buffer.toString());
    }

    public String format(int level, String message) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(logEntryOpen);
        buffer.append(LoggingSystem.replaceAll(format, "%VALUE%", "%TIME%"));
        buffer.append(LoggingSystem.replaceAll(format, "%VALUE%", "%THREAD%"));
        buffer.append(LoggingSystem.replaceAll(format, "%VALUE%", Logger.getLevelStringHTML(level)));
        buffer.append(LoggingSystem.replaceAll(format, "%VALUE%", "%LOGGER%"));
        buffer.append(LoggingSystem.replaceAll(format, "%VALUE%", message));
        buffer.append(logEntryClose);
        return replaceValues(buffer.toString());
    }
    
    public String replaceValues(String text) {
        Calendar c = new GregorianCalendar();
        text = LoggingSystem.replaceAll(text, "%START_TIME%", formatTime(startTime));
        text = LoggingSystem.replaceAll(text, "%TIME%", formatTime(c));
        text = LoggingSystem.replaceAll(text, "%THREAD%", Thread.currentThread().getName());
        text = LoggingSystem.replaceAll(text, "%LOGGER%", parent.getName());
        return text;
    }
    
    public String formatTime(Calendar c) {
        NumberFormat nf = new DecimalFormat();
		nf.setMinimumIntegerDigits(2);
        String time = "%EEEE%, %MMM% %d%, %yyyy% %HH%:%mm%:%ss% %Z%";
        time = LoggingSystem.replaceAll(time, "%EEE%", (String)days.get(c.get(Calendar.DAY_OF_WEEK)));
        time = LoggingSystem.replaceAll(time, "%EEEE%", (String)fulldays.get(c.get(Calendar.DAY_OF_WEEK)));
        time = LoggingSystem.replaceAll(time, "%d%", String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
        time = LoggingSystem.replaceAll(time, "%dd%", nf.format(c.get(Calendar.DAY_OF_MONTH)));
        time = LoggingSystem.replaceAll(time, "%MMM%", (String)months.get(c.get(Calendar.MONTH)));
        time = LoggingSystem.replaceAll(time, "%mmm%", nf.format(c.get(Calendar.MONTH) + 1));
		time = LoggingSystem.replaceAll(time, "%yyyy%", String.valueOf(c.get(Calendar.YEAR)));
		time = LoggingSystem.replaceAll(time, "%HH%", nf.format(c.get(Calendar.HOUR_OF_DAY)));
		time = LoggingSystem.replaceAll(time, "%mm%", nf.format(c.get(Calendar.MINUTE)));
		time = LoggingSystem.replaceAll(time, "%ss%", nf.format(c.get(Calendar.SECOND)));
		time = LoggingSystem.replaceAll(time, "%Z%", c.getTimeZone().getID());
		return time;
    }
}
