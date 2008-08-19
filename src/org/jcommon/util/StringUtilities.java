/*
 * Created on May 3, 2004
 */
package org.jcommon.util;

/**
 * @author matthew_hicks
 */

import java.io.*;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.*;

public class StringUtilities {
	public static final long SECOND = 1000;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;
	public static final long YEAR = 365 * DAY;
	
	/**
	 * Formats numbers in proper size (ex. 40.1 GB)
	 */
    public static final int FILE_FORMAT = 1;
    /**
     * Formats numbers with proper ending characters (ex. 32nd)
     */
    public static final int NUMBER_FORMAT = 2;
    /**
     * Formats numbers in proper time (ex. 32.3s)
     */
    public static final int TIME_FORMAT = 3;
    
    private static Calendar calendar;
	private static final ArrayList days;
	private static final ArrayList months;
	static {
		days = new ArrayList();
		days.add("");
		days.add("Sun");
		days.add("Mon");
		days.add("Tues");
		days.add("Wed");
		days.add("Thu");
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
	public static final String[] longMonths = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	
	public static final String format(Calendar c, String format) {
		// %EEE%, %d% %MMM% %yyyy% %HH%:%mm%:%ss% %Z%
		// %MMMM% %d%, %yyyy% %hh%:%mm%:%ss%%amPM%
		// %MM%/%dd%/%yyyy%
		NumberFormat nf = new DecimalFormat();
		nf.setMinimumIntegerDigits(2);
		format = replaceAll(format, "%EEE%", (String)days.get(c.get(Calendar.DAY_OF_WEEK)));
		format = replaceAll(format, "%d%", String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
		format = replaceAll(format, "%dd%", nf.format(c.get(Calendar.DAY_OF_MONTH)));
		format = replaceAll(format, "%MMMM%", longMonths[c.get(Calendar.MONTH)]);
		format = replaceAll(format, "%MMM%", (String)months.get(c.get(Calendar.MONTH)));
		format = replaceAll(format, "%MM%", nf.format(c.get(Calendar.MONTH) + 1));
		format = replaceAll(format, "%M%", String.valueOf(c.get(Calendar.MONTH) + 1));
		format = replaceAll(format, "%yyyy%", String.valueOf(c.get(Calendar.YEAR)));
		format = replaceAll(format, "%HH%", nf.format(c.get(Calendar.HOUR_OF_DAY)));
		if (c.get(Calendar.HOUR) == 0) {
			format = replaceAll(format, "%hh%", "12");
		} else {
			format = replaceAll(format, "%hh%", nf.format(c.get(Calendar.HOUR)));
		}
		format = replaceAll(format, "%m%", String.valueOf(c.get(Calendar.MINUTE)));
		format = replaceAll(format, "%mm%", nf.format(c.get(Calendar.MINUTE)));
		format = replaceAll(format, "%s%", String.valueOf(c.get(Calendar.SECOND)));
		format = replaceAll(format, "%ss%", nf.format(c.get(Calendar.SECOND)));
		format = replaceAll(format, "%ms%", String.valueOf(c.get(Calendar.MILLISECOND)));
		String amPM = "am";
		if (c.get(Calendar.AM_PM) == Calendar.PM) {
		    amPM = "pm";
		}
		format = replaceAll(format, "%amPM%", amPM);
		format = replaceAll(format, "%Z%", c.getTimeZone().getID());
		return format;
	}
	
	public static final String formatElapsed(long elapsed) {
		StringBuffer buffer = new StringBuffer();
		long years = 0;
		long days = 0;
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		while (elapsed >= YEAR) {
			years++;
			elapsed -= YEAR;
		}
		while (elapsed >= DAY) {
			days++;
			elapsed -= DAY;
		}
		while (elapsed >= HOUR) {
			hours++;
			elapsed -= HOUR;
		}
		while (elapsed >= MINUTE) {
			minutes++;
			elapsed -= MINUTE;
		}
		while (elapsed >= SECOND) {
			seconds++;
			elapsed -= SECOND;
		}
		if (years > 0) {
			buffer.append(years + "y, ");
		}
		if (days > 0) {
			buffer.append(days + "d, ");
		}
		if (hours > 0) {
			buffer.append(hours + "h, ");
		}
		if (minutes > 0) {
			buffer.append(minutes + "m, ");
		}
		if (seconds > 0) {
			buffer.append(seconds + "s, ");
		}
		buffer.append(elapsed + "ms");
		return buffer.toString();
	}
	
	public static final Calendar convertToCalendar(String date, String format) {
		GregorianCalendar calendar = new GregorianCalendar();
		char c;
		int datePosition = 0;
		int start = -1;
		String type = null;
		String temp;
		for (int i = 0; i < format.length(); i++) {
			c = format.charAt(i);
			if (c == '%') {
				if (start == -1) {
					start = i;
				} else {
					type = format.substring(start, i + 1);
					if ("%d%,%dd%,%M%,%MM%,%yyyy%,%yy%,%HH%,%hh%,%mm%,%ss%,%ms%,%amPM%".indexOf(type) != -1) {
						temp = nextIntAsString(date, datePosition);
						datePosition += temp.length();
						if ((type.equals("%d%")) || (type.equals("%dd%"))) {
							calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(temp));
						} else if ((type.equals("%M%")) || (type.equals("%MM%"))) {
							calendar.set(Calendar.MONTH, Integer.parseInt(temp) - 1);
						} else if (type.equals("%yyyy%")) {
							calendar.set(Calendar.YEAR, Integer.parseInt(temp));
						} else if (type.equals("%yy%")) {
							int year = Integer.parseInt(temp);
							if (year > 60) {
								year += 1900;
							} else {
								year += 2000;
							}
							calendar.set(Calendar.YEAR, year);
						} else if (type.equals("%HH%")) {
							calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp));
						} else if (type.equals("%hh%")) {
							calendar.set(Calendar.HOUR, Integer.parseInt(temp));
						} else if (type.equals("%mm%")) {
							calendar.set(Calendar.MINUTE, Integer.parseInt(temp));
						} else if (type.equals("%ss%")) {
							calendar.set(Calendar.SECOND, Integer.parseInt(temp));
						} else if (type.equals("%ms%")) {
							calendar.set(Calendar.MILLISECOND, Integer.parseInt(temp));
						} else if (type.equals("%amPM%")) {
							if (temp.equalsIgnoreCase("am")) {
								calendar.set(Calendar.AM_PM, Calendar.AM);
							} else if (temp.equalsIgnoreCase("pm")) {
								calendar.set(Calendar.AM_PM, Calendar.PM);
							}
						}
					} else if ("%EEE%,%MMM%,%MMMM%,%Z%".indexOf(type) != -1) {
						System.err.println("Not implemented: " + type);
					} else {
						start = i;
						continue;
					}
					start = -1;
				}
			} else if (start == -1) {
				datePosition++;
			}
		}
		return calendar;
	}
	
	public static final String nextIntAsString(String s, int start) {
		char c;
		for (int i = 0; i < s.length() - start; i++) {
			c = s.charAt(i + start);
			if (!Character.isDigit(c)) {
				if (i == 0) {
					start++;
					i--;
				} else {
					return s.substring(start, start + i);
				}
			}
		}
		return s.substring(start);
	}
    
    public static final int nextInt(String s, int start) {
        return Integer.parseInt(nextIntAsString(s, start));
    }
	
    public static final String numberToWord(int n) {
    	switch(n) {
    		case 0:		return "Zero";
    		case 10:	return "Ten";
    		case 11:	return "Eleven";
    		case 12:	return "Twelve";
    		case 13:	return "Thirteen";
    		case 14:	return "Fourteen";
    		case 15:	return "Fifteen";
    		case 16:	return "Sixteen";
    		case 17:	return "Seventeen";
    		case 18:	return "Eighteen";
    		case 19:	return "Nineteen";
    	}
    	StringBuilder builder = new StringBuilder();
    	String s = String.valueOf(n);
    	for (int i = 0; i < s.length(); i++) {
    		int digit = Character.digit(s.charAt(i), 10);
    		// TODO finish implementing
    	}
    	return "";
    }
    
    public static final synchronized String format(long timeInMillis, String format) {
    	if (calendar == null) {
    		calendar = new GregorianCalendar();
    	}
    	calendar.setTimeInMillis(timeInMillis);
    	return format(calendar, format);
    }
    
	public static final String format(long l, int type) {
	    if (type == FILE_FORMAT) {
	        String s = String.valueOf(l);
	        int digits = 0;
	        while (s.length() > 3) {
	            s = s.substring(0, s.length() - 3);
	            digits++;
	        }
	        StringBuffer buffer = new StringBuffer();
	        buffer.append(s);
	        if ((s.length() == 1) && (String.valueOf(l).length() >= 3)) {
	            buffer.append(".");
	            buffer.append(String.valueOf(l).substring(1, 3));
	        } else if ((s.length() == 2) && (String.valueOf(l).length() >= 3)) {
	            buffer.append(".");
	            buffer.append(String.valueOf(l).substring(2, 3));
	        }
	        if (digits == 0) {
	            buffer.append(" B");
	        } else if (digits == 1) {
	            buffer.append(" KB");
	        } else if (digits == 2) {
	            buffer.append(" MB");
	        } else if (digits == 3) {
	            buffer.append(" GB");
	        } else if (digits == 4) {
	            buffer.append(" TB");
	        }
	        return buffer.toString();
	    } else if (type == NUMBER_FORMAT) {
	    	String s = String.valueOf(l);
			int i = Integer.parseInt(s.charAt(s.length() - 1) + "");
			String end = "th";
			if ((l >= 10) && (l <= 19)) end = "th";
			else if (i == 1) end = "st";
			else if (i == 2) end = "nd";
			else if (i == 3) end = "rd";
			return l + end;
	    } else if (type == TIME_FORMAT) {
	    	String ending;
	    	float f = l;
	    	
	    	if (f > 24 * 60 * 60 * 1000) {
	    		f /= 24 * 60 * 1000;
	    		ending = "d";
	    	} else if (f > 60 * 60 * 1000) {
	    		f /= 60 * 60 * 1000;
	    		ending = "h";
	    	} else if (f > 60 * 1000) {
	    		f /= 60 * 1000;
	    		ending = "m";
	    	} else if (f > 1000) {
	    		f /= 1000;
	    		ending = "s";
	    	} else {
	    		ending = "ms";
	    	}
	        
	    	NumberFormat format = NumberFormat.getInstance();
	    	format.setMaximumFractionDigits(2);
	        return format.format(f) + ending;
	    }
	    return null;
	}

	public static final String breakLines(String s, int width) {
		if (s == null) return null;
		
		StringBuffer buffer = new StringBuffer();
		int p = 0;
		char c;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (((p >= width) && (c == ' ')) || (c == '\n')) {
				buffer.append("\n");
				p = 0;
			} else {
				buffer.append(c);
			}
			p++;
		}
		return buffer.toString();
	}
	
	public static final String breakLinesHTML(String s, int width) {
		if (s == null) return null;
		
		StringBuffer buffer = new StringBuffer();
		//buffer.append("<html><body><font face=\"Arial\" size=\"-1\">");
		int p = 0;
		char c;
		boolean inTag = false;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if ((!inTag) && ((p >= width) && (c == ' '))||(c == '\n')) {
				buffer.append("<br>");
				p = 0;
			} else {
				if (c == '<') inTag = true;
				if (c == '>') inTag = false;
				buffer.append(c);
				String test = buffer.toString();
				if (test.endsWith("<br>")) {
					p = 0;
				} else if (test.endsWith("<br")) {
					p = 0;
				} else if (test.endsWith("<dl")) {
					p = 0;
				} else if (test.endsWith("<dt")) {
					p = 0;
				} else if (test.endsWith("<dd")) {
					p = 0;
				}
			}
			if (!inTag) p++;
		}
		//buffer.append("</font></body></html>");
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
	
	public static int countOccurrences(String text, String value) {
		int i = 0;
		if ((text != null) && (value != null)) {
			//int previous = 0;
			int current = text.indexOf(value);
			while (current > -1) {
				i++;
				current += value.length();
				//previous = current;
				current = text.indexOf(value, current);
			}
		}
		return i;
	}
	
	public static String convertToCSL(Object[] list) {
		//converts the list of Integers to a comma seperated string. 
		if (list == null) return null;
		if (list.length == 0) return null; //don't do anything if there are no elements
		StringBuffer sb = new StringBuffer (list.length*8); //8 characters per int should be a good guess
		int listLength = list.length;
		for (int x = 0; x<listLength; x++) {
			if (list[x] != null) {
				if (sb.length() != 0 ) {
					sb.append(",");
				}
				sb.append(list[x].toString());
			}
		}
		
		if (sb.length() == 0) sb = null;
		
		return (sb != null?sb.toString():null);
	}

	public static String reverse(String s) {
	    StringBuffer buffer = new StringBuffer();
	    for (int i = s.length() - 1; i >= 0; i--) {
	        buffer.append(s.charAt(i));
	    }
	    return buffer.toString();
	}
	
	public static Map<String, String> toMap(String data, String groupSplit, String pairSplit) {
		HashMap<String, String> map = new HashMap<String, String>();
		return toMap(map, data, groupSplit, pairSplit);
	}
	
	public static Map<String, String> toMap(Map<String, String> map, String data, String groupSplit, String pairSplit) {
		map.clear();
		String[] parser = data.split(groupSplit);
		for (int i = 0; i < parser.length; i++) {
			int index = parser[i].indexOf(pairSplit);
			if (index != -1) {
				String key = parser[i].substring(0, index);
				String value = parser[i].substring(index + 1);
				value = replaceAll(value, "%26", "&");
				value = replaceAll(value, "&lt;", "<");
				value = replaceAll(value, "&gt;", ">");
				map.put(key, value);
			} else {
				//System.err.println("Cannot parse: " + parser[i]);
			}
		}
		return map;
	}
	
	public static String toString(Map map) {
		StringBuffer buffer = new StringBuffer();
		Iterator iterator = map.keySet().iterator();
		String key, value;
		while (iterator.hasNext()) {
			key = (String)iterator.next();
			value = toString(map.get(key));
			buffer.append("[" + key + ": " + value + "], ");
		}
		return buffer.toString();
	}

	public static String toString(InputStream is) throws IOException {
		if (is == null) return null;
		StringBuffer buffer = new StringBuffer();
		int i;
		while ((i = is.read()) != -1) {
			buffer.append((char)i);
		}
		return buffer.toString();
	}
	
	public static String toString(Object[] array) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				buffer.append(", ");
			}
			if ((array[i] != null) && (array[i].getClass().isArray())) {
				buffer.append(StringUtilities.toString((Object[])array[i]));
			} else {
				buffer.append(String.valueOf(array[i]));
			}
		}
		buffer.append("}");
		return buffer.toString();
	}
	
	public static String toString(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		String s = toString(fis);
		fis.close();
		return s;
	}
	
	public static String toString(Object o) {
		if (o == null) return null;
		if (o.getClass().isArray()) {
			int length = Array.getLength(o);
			StringBuffer buffer = new StringBuffer();
			
			for (int i = 0; i < length ; i++) {
				Object child = Array.get(o, i);
				if (buffer.length() > 0) {
					buffer.append (';');
				}
				buffer.append(toString(child));
			}
			return buffer.toString();
		} else if (o.getClass().isEnum()) {
//			return ((Enum)o).name().toLowerCase();
			return String.valueOf(((Enum)o).ordinal());
		} else if (o instanceof Calendar) {
			return StringUtilities.format((Calendar)o, "%MMMM% %d%, %yyyy% %hh%:%mm%:%ss%%amPM%");
		}
		return o.toString();
	}
	
	public static List<String> toList(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		List<String> list = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			list.add(line);
		}
		is.close();
		return list;
	}
	
	public static String replaceEnvironmentVariables(String s) {
		try {
			Map<String,String> map = System.getenv();
			Iterator<String> iterator = map.keySet().iterator();
			String key;
			String value;
			while (iterator.hasNext()) {
				key = iterator.next();
				value = map.get(key);
				if (s.indexOf("%" + key + "%") > -1) {
					s = replaceAll(s, "%" + key + "%", value);
				}
				if (s.indexOf("$" + key) > -1) {
					s = replaceAll(s, "$" + key, value);
				}
			}
		} catch(Throwable t) {
		}
		Properties props = System.getProperties();
		Iterator iterator = props.keySet().iterator();
		String key;
		String value;
		while (iterator.hasNext()) {
			key = (String)iterator.next();
			value = (String)props.get(key);
			if (s.indexOf("%" + key + "%") > -1) {
				s = replaceAll(s, "%" + key + "%", value);
			}
			if (s.indexOf("$" + key) > -1) {
				s = replaceAll(s, "$" + key, value);
			}
		}
		return s;
	}
	
	public static Object convert(String s, Class type) {
		if (s == null) return null;
		if (type == String.class) {
			return s;
		} else if ((type == int.class) || (type == Integer.class)) {
			return new Integer(Integer.parseInt(s));
		} else if ((type == long.class) || (type == Integer.class)) {
			return new Long(Long.parseLong(s));
		} else if ((type == byte.class) || (type == Byte.class)) {
			return new Byte(Byte.parseByte(s));
		} else if ((type == boolean.class) || (type == Boolean.class)) {
			return new Boolean(Boolean.parseBoolean(s));
		} else if ((type == float.class) || (type == Float.class)) {
			return new Float(Float.parseFloat(s));
		} else if ((type == double.class) || (type == Double.class)) {
			return new Double(Double.parseDouble(s));
		} else if (type.isAssignableFrom(Calendar.class)) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(Long.parseLong(s));
			System.out.println("CALENDAR: " + s + ", " + calendar.get(Calendar.DAY_OF_MONTH));
			return calendar;
		} else if (type == String[].class) {
			if (s.length() == 0) {
				return new String[0];
			}
			return s.split(",");
		} else if (type == int[].class) {
			if (s.length() == 0) return new int[] {Integer.parseInt(s)};
			String[] split = s.split(",");
			int[] values = new int[split.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = Integer.parseInt(split[i]);
			}
			return values;
		}
		System.err.println("Unknown type for conversion: " + type.getSimpleName());
		return null;
	}

	public static String getHTMLStackTrace(Throwable e) {
        return getHTMLStackTrace(e, false);
    }
    
    public static String getHTMLStackTrace(Throwable e, boolean causedBy) {
        StringBuffer buffer = new StringBuffer();
        if (causedBy) buffer.append("&#160;&#160;&#160;&#160;");
        buffer.append("<span style=\"font-family: arial,sans-serif; font-size: small;\"><b><font color=\"#dd1111\">" + e.getClass().getName() + " (" + e.getMessage() + ")<br/><br/>Trace Follows:</font></b><br>");
        StackTraceElement[] trace = e.getStackTrace();
        String source;
        for (int i = 0; i < trace.length; i++) {
            if (trace[i].getFileName() != null) {
                source = "<b>" + trace[i].getFileName() + ":" + trace[i].getLineNumber() + "</b>";
            } else {
                source = "<i>Unknown Source</i>";
            }
            if (causedBy) buffer.append("&#160;&#160;&#160;&#160;");
            buffer.append("&#160;&#160;&#160;&#160;" + trace[i].getClassName() + "." + trace[i].getMethodName() + "(" + source + ")<br>");
        }
        buffer.append("</span>");
        if (e.getCause() != null) {
            buffer.append("<span style=\"font-family: arial,sans-serif; font-size: small;\"><b>Caused by:</b><br>");
            buffer.append(getHTMLStackTrace(e.getCause(), true));
        }
        return buffer.toString();
    }
    
    public static String getTextStackTrace(Throwable e) {
        return getTextStackTrace(e, false);
    }
    
    public static String getTextStackTrace(Throwable e, boolean causedBy) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(e.getClass().getName() + " (" + e.getMessage() + ") Trace Follows:\r\n");
        StackTraceElement[] trace = e.getStackTrace();
        String source;
        for (int i = 0; i < trace.length; i++) {
            if (trace[i].getFileName() != null) {
                source = trace[i].getFileName() + ":" + trace[i].getLineNumber();
            } else {
                source = "Unknown Source";
            }
            if (causedBy) buffer.append("\t");
            buffer.append("\t" + trace[i].getClassName() + "." + trace[i].getMethodName() + "(" + source + ")\r\n");
        }
        if (e.getCause() != null) {
            buffer.append("Caused by:");
            buffer.append(getTextStackTrace(e.getCause(), true));
        }
        return buffer.toString();
    }
    
    public static String stripXML(String s) {
    	s = s.replaceAll("<.*?>", "");
    	return s;
    }
    
    public static String text2HTML(String text) {
    	// Make HTML elements visible
    	text = replaceAll(text, "<", "&lt;");
    	text = replaceAll(text, ">", "&gt;");
    	// Generate HTML
    	text = replaceAll(text, "\t", "&#160;&#160;&#160;&#160;");
    	text = replaceAll(text, " ", "&#160;");
    	text = replaceAll(text, "\r\n", "<br/>\r\n");
    	return text;
    }

    public static String capitalize(String s) {
    	if (s.length() == 0) return s;
    	else if (s.length() == 1) return s.toUpperCase();
    	return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    public static String formatEnum(Object obj) {
    	if (obj == null) {
    		return null;
    	}
    	String s = String.valueOf(obj).toLowerCase();
    	StringBuilder builder = new StringBuilder();
    	char p = ' ';
    	for (int i = 0; i < s.length(); i++) {
    		char c = s.charAt(i);
    		if (c == '_') {
    			c = ' ';
    		} else if (p == ' ') {
    			c = Character.toUpperCase(c);
    		}
    		p = c;
    		builder.append(c);
    	}
    	return capitalize(builder.toString());
    }
    
    /**
     * The getPaddedString method will ensure that the source String is padded
     * using the pad character to a the length specified. The following rules
     * will apply to the supplied parameters:
     * <ul>
     * <li>Providing a null source String will result in a String being returned
     * that consists entirely of the padding character equal to the specified
     * length.</li>
     * <li>Providing a null pad character will result in NullPointerException
     * being thrown.</li>
     * <li>Providing a length that is less than 1 will result in the source 
     * String return unchanged. In this scenario, if the source String is null, 
     * the return value will be null.</li>
     * </ul>
     * 
     * @param source - String to apply padding to.
     * @param pad - Character to pad with.
     * @param width - Pad string to this length.
     * @param append - Append padding to end of string.
     * @return
     */
    public static String getPaddedString(String source, Character pad, long length, boolean append) {
    	if (pad == null) {
    		throw new NullPointerException("A null value is not allowed for the pad parameter.");
    	}
    	if (length < 1) {
    		return source;
    	}
    	StringBuilder result = new StringBuilder();
    	long remaining = (source==null) ? length : length - source.length();
    	for( ; remaining > 0 ; remaining--) {
    		result.append(pad);
    	}
    	if (append) { 
    		result.insert(0, source); 
    	} else { 
    		result.append(source); 
    	}
    	return result.toString();
    }

    /**
     * Can be used on Java method and field names to convert to human
     * readable phrases. For example, "myCoolMethod" would be converted
     * to "My Cool Method".
     * 
     * @param s
     * @return
     * 		phrase
     */
    public static String phrasify(String s) {
    	StringBuilder builder = new StringBuilder();
    	char p = ' ';
    	for (int i = 0; i < s.length(); i++) {
    		char c = s.charAt(i);
    		if ((i > 0) && ((Character.isUpperCase(c)) || (Character.isDigit(c))) && (!Character.isUpperCase(p))) {
    			builder.append(' ');
    		} else if (p == ' ') {
    			c = Character.toUpperCase(c);
    		}
    		p = c;
    		builder.append(c);
    	}
    	return capitalize(builder.toString());
    }
    
    public static String dephrasify(String s) {
    	StringBuilder builder = new StringBuilder();
    	for (int i = 0; i < s.length(); i++) {
    		char c = s.charAt(i);
    		if (i == 0) {
    			c = Character.toLowerCase(c);
    		}
    		if (c == ' ') {
    			continue;
    		}
    		builder.append(c);
    	}
    	return builder.toString();
    }

    public static Integer[] getRange(String range) {
    	char c;
    	StringBuilder builder = new StringBuilder();
    	List<Integer> list = new ArrayList<Integer>();
    	boolean rangeAtNextSplit = false;
    	for (int i = 0; i < range.length(); i++) {
    		c = range.charAt(i);
    		if (Character.isDigit(c)) {
    			builder.append(c);
    		} else if (c == '-') {
    			if (builder.length() > 0) {
    				list.add(Integer.parseInt(builder.toString()));
    				builder.delete(0, builder.length());
    			}
    			rangeAtNextSplit = true; 
    		} else if ((Character.isWhitespace(c)) || (c == ',') || (c == ';')) {
    			if (builder.length() > 0) {
    				list.add(Integer.parseInt(builder.toString()));
    				builder.delete(0, builder.length());
    				
    				if (rangeAtNextSplit) {
        				int first = list.get(list.size() - 2);
        				int last = list.get(list.size() - 1);
        				for (int r = first + 1; r < last; r++) {
        					list.add(list.size() - 1, r);
        				}
        				rangeAtNextSplit = false;
        			}
    			}
    		}
    	}
    	if (builder.length() > 0) {
			list.add(Integer.parseInt(builder.toString()));
			builder.delete(0, builder.length());
			
			if (rangeAtNextSplit) {
				int first = list.get(list.size() - 2);
				int last = list.get(list.size() - 1);
				for (int r = first + 1; r < last; r++) {
					list.add(list.size() - 1, r);
				}
				rangeAtNextSplit = false;
			}
		}
    	return list.toArray(new Integer[list.size()]);
    }

    public static void zeroTime(Calendar calendar) {
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    }

    public static boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

    public static boolean isBlank(String str){
        return str == null || str.trim().length() == 0;
    }

    public static boolean isEqual(String s1, String s2) {
    	if ((s1 == null) && (s2 == null)) {
    		return true;
    	} else if (s1 == null) {
    		return false;
    	}
    	return s1.equals(s2);
    }
    
    /**
     * Filters the passed String "s" to only return the characters in
     * the String that are contained in the "filter" String.
     * 
     * @param s
     * @param filter
     * @return
     * 		filtered String
     */
    public static final String filter(String s, String filter) {
    	StringBuilder builder = new StringBuilder();
    	for (int i = 0; i < s.length(); i++) {
    		char c = s.charAt(i);
    		if (filter.indexOf(c) > -1) {
    			builder.append(c);
    		}
    	}
    	return builder.toString();
    }
}