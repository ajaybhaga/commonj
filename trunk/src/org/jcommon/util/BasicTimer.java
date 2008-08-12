package org.jcommon.util;

import java.util.*;

/**
 * @author Matt Hicks
 *
 */
public class BasicTimer {
	public static final int TYPE_CLOCK = 1;
	public static final int TYPE_ELAPSED = 2;
	public static final int TYPE_REMAINING = 3;

	private int type;
	private long start;
	private long max;
	
	public BasicTimer(int type) {
		this.type = type;
		start = System.currentTimeMillis();
	}
	
	public long getTime() {
		if (type == TYPE_CLOCK) {
			return System.currentTimeMillis();
		} else if (type == TYPE_ELAPSED) {
			return System.currentTimeMillis() - start;
		} else if (type == TYPE_REMAINING) {
			return max - (System.currentTimeMillis() - start);
		}
		throw new RuntimeException("Type does not match anything known: " + type);
	}
	
	public void setMax(long max) {
		this.max = max;
	}
	
	public static void main(String[] args) throws Exception {
		int type = TYPE_REMAINING;
		BasicTimer timer = new BasicTimer(type);
		timer.setMax(10000);
		GregorianCalendar calendar = new GregorianCalendar();
		while (true) {
			Thread.sleep(1000);
			if (type == TYPE_CLOCK) {
				calendar.setTimeInMillis(timer.getTime());
				System.out.println("Clock: " + StringUtilities.format(calendar, "%EEE%, %d% %MMM% %yyyy% %HH%:%mm%:%ss% %Z%"));
			} else if (type == TYPE_ELAPSED) {
				System.out.println("Elapsed: " + (timer.getTime() / 1000) + " seconds");
			} else if (type == TYPE_REMAINING) {
				System.out.println("Remaining: " + (timer.getTime() / 1000) + " seconds");
			}
		}
	}
}
