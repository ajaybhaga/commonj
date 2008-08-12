package org.jcommon.util;

import java.util.*;

/**
 * @author Matt Hicks
 *
 */
public class MonitoredGregorianCalendar extends GregorianCalendar {
	private ArrayList listeners;
	
	private boolean blocked;
	
	public MonitoredGregorianCalendar() {
		super();
		listeners = new ArrayList();
	}
	
	public MonitoredGregorianCalendar(int year, int month, int dayOfMonth) {
		super(year, month, dayOfMonth);
		listeners = new ArrayList();
	}
	
	public MonitoredGregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		super(year, month, dayOfMonth, hourOfDay, minute);
		listeners = new ArrayList();
	}
	
	public MonitoredGregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		super(year, month, dayOfMonth, hourOfDay, minute, second);
		listeners = new ArrayList();
	}
	
	public MonitoredGregorianCalendar(Locale aLocale) {
		super(aLocale);
		listeners = new ArrayList();
	}
	
	public MonitoredGregorianCalendar(TimeZone zone) {
		super(zone);
		listeners = new ArrayList();
	}
	
	public void add(int field, int amount) {
		if (!blocked) {
			blocked = true;
			super.add(field, amount);
			blocked = false;
			changed(CalendarChangeListener.TYPE_ADD, field, amount);
		} else {
			super.add(field, amount);
		}
	}
	
	public void roll(int field, boolean up) {
		if (!blocked) {
			blocked = true;
			super.roll(field, up);
			blocked = false;
			int amount = 0;
			if (up) amount = 1;
			changed(CalendarChangeListener.TYPE_ROLL, field, amount);
		} else {
			super.roll(field, up);
		}
	}
	
	public void roll(int field, int amount) {
		if (!blocked) {
			blocked = true;
			super.roll(field, amount);
			blocked = false;
			changed(CalendarChangeListener.TYPE_ROLL, field, amount);
		} else {
			super.roll(field, amount);
		}
	}
	
	public void setGregorianChange(Date date) {
		if (!blocked) {
			blocked = true;
			super.setGregorianChange(date);
			blocked = false;
			changed(CalendarChangeListener.TYPE_SET, CalendarChangeListener.TIME_IN_MILLIS, date.getTime());
		} else {
			super.setGregorianChange(date);
		}
	}
	
	public void setTimeZone(TimeZone zone) {
		if (!blocked) {
			blocked = true;
			super.setTimeZone(zone);
			blocked = false;
			changed(CalendarChangeListener.TYPE_SET, CalendarChangeListener.TIME_ZONE, zone.getRawOffset());
		} else {
			super.setTimeZone(zone);
		}
	}
	
	public void set(int field, int value) {
		if (!blocked) {
			blocked = true;
			super.set(field, value);
			blocked = false;
			changed(CalendarChangeListener.TYPE_SET, field, value);
		} else {
			super.set(field, value);
		}
	}
	
	public void setFirstDayOfWeek(int value) {
		if (!blocked) {
			blocked = true;
			super.setFirstDayOfWeek(value);
			blocked = false;
			changed(CalendarChangeListener.TYPE_SET, CalendarChangeListener.FIRST_DAY_OF_WEEK, value);
		} else {
			super.setFirstDayOfWeek(value);
		}
	}
	
	public void setLenient(boolean lenient) {
		if (!blocked) {
			blocked = true;
			super.setLenient(lenient);
			blocked = false;
			int amount = 0;
			if (lenient) amount = 1;
			changed(CalendarChangeListener.TYPE_SET, CalendarChangeListener.LENIENT, amount);
		} else {
			super.setLenient(lenient);
		}
	}
	
	public void setMinimalDaysInFirstWeek(int value) {
		if (!blocked) {
			blocked = true;
			super.setMinimalDaysInFirstWeek(value);
			blocked = false;
			changed(CalendarChangeListener.TYPE_SET, CalendarChangeListener.MINIMAL_DAYS_IN_FIRST_WEEK, value);
		} else {
			super.setMinimalDaysInFirstWeek(value);
		}
	}
	
	public void setTimeInMillis(long millis) {
		if (!blocked) {
			blocked = true;
			super.setTimeInMillis(millis);
			blocked = false;
			changed(CalendarChangeListener.TYPE_SET, CalendarChangeListener.TIME_IN_MILLIS, millis);
		} else {
			super.setTimeInMillis(millis);
		}
	}
	
	private void changed(int type, int field, long value) {
		if ((listeners != null) && (!blocked)) {
			for (int i = 0; i < listeners.size(); i++) {
				((CalendarChangeListener)listeners.get(i)).changed(this, type, field, value);
			}
		}
	}
	
	public void addListener(CalendarChangeListener listener) {
		listeners.add(listener);
	}
}
