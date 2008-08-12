package org.jcommon.util;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Ossie Moore
 * 
 */
public class CalendarUtilities {

	/**
	 * Determine whether the source Calendar date is within the specified period
	 * of time of the target Calendar date.
	 * 
	 * Example Use:
	 * 
	 * You want to confirm whether Bob's birthday of 2/1/08 falls within 5 days
	 * before or after 2/15/08. 
	 * <code>
	 *   Calendar birthday = Calendar.getInstance();
	 *   birthday.set(2008,1,1);
	 *   Calendar target = Calendar.getInstance();
	 *   target.set(2008,1,15);
	 *   boolean inRange = CalendarUtilities.isWithin(birthday, target, TimeUnit.DAYS, 5);
	 * </code>
	 * 
	 * If any parameter is null, a NullPointerException will be thrown.
	 * 
	 * @param source -
	 *            Date to check against a date range.
	 * @param target -
	 *            Date to use as the date range mid point.
	 * @param uom -
	 *            Unit of measure for the date range.
	 * @param units -
	 *            Number of units in time for the date range.
	 * @return boolean - Indicates whether source was in the specified date
	 *         range.
	 */
	public static boolean isWithin(Calendar source, Calendar target, TimeUnit uom, Long units) {
		long bounds = TimeUnit.MILLISECONDS.convert(units, uom);
		return Math.abs(target.getTimeInMillis() - source.getTimeInMillis()) <= bounds;
	}
	
	
	/**
	 * Identical functionality to isWithin(Calendar source, Calendar target,
	 * TimeUnit uom, Long units) except that it accepts java.util.Date values
	 * rather than java.util.Calendar values.
	 * 
	 * @see CalendarUtilities.isWithin(Calendar source, Calendar target, TimeUnit uom, Long units)
	 * 
	 * @param source - Date to check against a date range.
	 * @param target - Date to use as the date range mid point.
	 * @param uom - Unit of measure for the date range.
	 * @param units - Number of units in time for the date range.
	 * @return boolean - Indicates whether source was in the specified date range.
	 */
	public static boolean isWithin(java.util.Date source, java.util.Date target, TimeUnit uom, Long units) {
		Calendar cSource = Calendar.getInstance();
		cSource.setTimeInMillis(source.getTime());
		Calendar cTarget = Calendar.getInstance();
		cTarget.setTimeInMillis(target.getTime());
		return CalendarUtilities.isWithin(cSource, cTarget, uom, units);
	}
	
	/**
	 * Identical functionality to isWithin(Calendar source, Calendar target,
	 * TimeUnit uom, Long units) except that it accepts java.sql.Date values
	 * rather than java.util.Calendar values.
	 * 
	 * @see CalendarUtilities.isWithin(Calendar source, Calendar target, TimeUnit uom, Long units)
	 * 
	 * @param source - Date to check against a date range.
	 * @param target - Date to use as the date range mid point.
	 * @param uom - Unit of measure for the date range.
	 * @param units - Number of units in time for the date range.
	 * @return boolean - Indicates whether source was in the specified date range.
	 */
	public static boolean isWithin(java.sql.Date source, java.sql.Date target, TimeUnit uom, Long units) {
		Calendar cSource = Calendar.getInstance();
		cSource.setTimeInMillis(source.getTime());
		Calendar cTarget = Calendar.getInstance();
		cTarget.setTimeInMillis(target.getTime());
		return CalendarUtilities.isWithin(cSource, cTarget, uom, units);
	}
}
