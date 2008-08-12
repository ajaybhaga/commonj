package org.jcommon.util;

/**
 * @author Matt Hicks
 *
 */
public interface CalendarChangeListener {
	public static final int TYPE_ADD = 1;
	public static final int TYPE_ROLL = 2;
	public static final int TYPE_SET = 3;
	
	public static final int TIME_ZONE = 1;
	public static final int FIRST_DAY_OF_WEEK = 2;
	public static final int LENIENT = 3;
	public static final int MINIMAL_DAYS_IN_FIRST_WEEK = 4;
	public static final int TIME_IN_MILLIS = 5;
	
	/**
	 * This method is called when a MonitoredGregorianCalendar changes.
	 * 
	 * @param c
	 * 		A reference to the MonitoredGregorianCalendar that has been changed.
	 * @param type
	 * 		The type of change that occurred:
	 * 			TYPE_ADD, TYPE_ROLL, or TYPE_SET
	 * @param field
	 * 		The field that was changed either a Calendar type or:
	 * 			TIME_ZONE, FIRST_DAY_OF_WEEK, LENIENT, MINIMAL_DAYS_IN_FIRST_WEEK, or TIME_IN_MILLIS
	 * @param value
	 * 		The value associated with the change.
	 */
	public void changed(MonitoredGregorianCalendar c, int type, int field, long value);
}
