/**
 * Created: Mar 20, 2007
 */
package org.jcommon.scheduling.trigger;

import java.util.*;

import org.jcommon.scheduling.*;
import org.jcommon.util.StringUtilities;

/**
 * @author Matt Hicks
 *
 */
public class DateTrigger extends Trigger {
	private static final long serialVersionUID = 1L;
	
	public static int MAX_RUN_OUT_DAYS = 1826;

	private Calendar lastRun;
	
	private String type;
	
	private transient Calendar calendar;
	private boolean[] daysOfYear;
	private boolean[] daysOfMonth;
	private boolean[] hours;
	private boolean[] minutes;
	private boolean[] daysOfWeek;
	private boolean[] monthsOfYear;
	private List<Calendar> dates;
	private boolean skippable;
	
	public DateTrigger() {
		calendar = new GregorianCalendar();
		daysOfYear = new boolean[365];
		daysOfMonth = new boolean[32];
		hours = new boolean[24];
		minutes = new boolean[60];
		daysOfWeek = new boolean[7];
		monthsOfYear = new boolean[12];
		dates = new ArrayList<Calendar>();
		skippable = true;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public void addDayOfYear(int date) {
		daysOfYear[date] = true;
	}
	
	public boolean[] getDaysOfYear() {
		return daysOfYear;
	}
	
	public void addDayOfMonth(int dayOfMonth) {
		daysOfMonth[dayOfMonth - 1] = true;
	}
	
	public boolean[] getDaysOfMonth() {
		return daysOfMonth;
	}
	
	public void addHour(int hour) {
		hours[hour] = true;
	}
	
	public boolean[] getHours() {
		return hours;
	}
	
	public void addMinute(int minute) {
		minutes[minute] = true;
	}
	
	public boolean[] getMinutes() {
		return minutes;
	}
	
	public void addDayOfWeek(int day) {
		daysOfWeek[day] = true;
	}
	
	public boolean[] getDaysOfWeek() {
		return daysOfWeek;
	}
	
	/**
	 * The month to add 0-based.
	 * 
	 * @param month
	 */
	public void addMonthOfYear(int month) {
		monthsOfYear[month] = true;
	}
	
	public boolean[] getMonthsOfYear() {
		return monthsOfYear;
	}
	
	public void addDate(Calendar calendar) {
		dates.add(calendar);
	}
	
	public List<Calendar> getDates() {
		return dates;
	}
	
	public boolean isSkippable() {
		return skippable;
	}
	
	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}
	
	private transient boolean filterMinutes;
	private transient boolean filterHours;
	private transient boolean filterDayOfYear;
	private transient boolean filterDayOfMonth;
	private transient boolean filterDayOfWeek;
	private transient boolean filterMonthOfYear;
	private transient boolean filterDate;
	
	private void updateFilters() {
//		 Determine what filters should apply
		filterMinutes = false;
		for (boolean b : minutes) {
			if (b) {
				filterMinutes = true;
				break;
			}
		}
		filterHours = false;
		for (boolean b : hours) {
			if (b) {
				filterHours = true;
				break;
			}
		}
		filterDayOfYear = false;
		for (boolean b : daysOfYear) {
			if (b) {
				filterDayOfYear = true;
				break;
			}
		}
		filterDayOfMonth = false;
		for (boolean b : daysOfMonth) {
			if (b) {
				filterDayOfMonth = true;
				break;
			}
		}
		filterDayOfWeek = false;
		for (boolean b : daysOfWeek) {
			if (b) {
				filterDayOfWeek = true;
				break;
			}
		}
		filterMonthOfYear = false;
		for (boolean b : monthsOfYear) {
			if (b) {
				filterMonthOfYear = true;
				break;
			}
		}
		filterDate = false;
		if (dates.size() > 0) filterDate = true;
	}
	
	private void updateCalendarForNextRun() {
		if (lastRun == null) return;
		
		calendar.setTimeInMillis(lastRun.getTimeInMillis());
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (filterMinutes) {
			calendar.add(Calendar.MINUTE, 1);
		} else if (filterHours) {
			calendar.add(Calendar.HOUR, 1);
		} else if ((filterDayOfYear) || (filterDayOfMonth) || (filterDayOfWeek) || (filterDate)) {
			calendar.add(Calendar.DATE, 1);
		}
	}
	
	public long nextRunInMillis(long time) {
		if (!isEnabled()) return -1;
		if (calendar == null) {
			calendar = new GregorianCalendar();
		}
		if (!skippable) {
			calendar.setTimeInMillis(System.currentTimeMillis());
		} else {
			calendar.setTimeInMillis(time);
		}
		
		updateFilters();
		
		updateCalendarForNextRun();
		
		boolean changed;
		if (!isBeginnable(calendar)) {
			calendar.setTimeInMillis(getBegin().getTimeInMillis());
		}
		do {
			changed = false;
			if (findNextDay()) changed = true;
			if (findNextHour()) changed = true;
			if (findNextMinute()) changed = true;
		} while (changed);
		
		if (calendar == null) return -1;
		long nextRun = calendar.getTimeInMillis();
		if ((!skippable) && (nextRun < time)) {
			nextRun = time;
		}
		if ((getExpiration() != null) && (nextRun > getExpiration().getTimeInMillis())) {
			nextRun = -1;
			// TODO should this remove the trigger?
		}
		return nextRun;
	}
	
	private boolean isValidDate() {
		if (!filterDate) return true;
		for (Calendar c : dates) {
			if ((c.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) &&
				(c.get(Calendar.DATE) == calendar.get(Calendar.DATE)) &&
				(c.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
				return true;
			}
		}
		return false;
	}
	
	private boolean findNextDay() {
		boolean changed = false;
		boolean validDay = false;
		int count = 0;
		while (count++ < MAX_RUN_OUT_DAYS) {
			validDay = true;
			if ((filterDayOfYear) && (!daysOfYear[calendar.get(Calendar.DAY_OF_YEAR) - 1])) validDay = false;
			if ((filterDayOfWeek) && (!daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1])) validDay = false;
			if ((filterDayOfMonth) && (!daysOfMonth[calendar.get(Calendar.DAY_OF_MONTH) - 1])) validDay = false;
			if ((filterMonthOfYear) && (!monthsOfYear[calendar.get(Calendar.MONTH)])) validDay = false;
			if (!isValidDate()) validDay = false;
			if (validDay) return changed;
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			changed = true;
		}
		calendar = null;
		return false;
	}
	
	private boolean findNextHour() {
		if (calendar == null) return false;
		
		boolean changed = false;
		if (filterHours) {
			while (!hours[calendar.get(Calendar.HOUR_OF_DAY)]) {
				calendar.set(Calendar.MINUTE, 0);
				calendar.add(Calendar.HOUR_OF_DAY, 1);
				changed = true;
			}
		}
		return changed;
	}
	
	private boolean findNextMinute() {
		if (calendar == null) return false;
		
		boolean changed = false;
		if (filterMinutes) {
			while (!minutes[calendar.get(Calendar.MINUTE)]) {
				calendar.add(Calendar.MINUTE, 1);
				changed = true;
			}
		}
		return changed;
	}

	public void triggeredBegin(Task task) {
	}

	public void triggeredEnd(Task task) {
		lastRun = new GregorianCalendar();
		lastRun.setTimeInMillis(System.currentTimeMillis());
	}
}
