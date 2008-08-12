/**
 * Created: Mar 19, 2007
 */
package org.jcommon.scheduling.trigger;

import org.jcommon.scheduling.*;

/**
 * @author Matt Hicks
 */
public class SimpleTrigger extends Trigger {
	private static final long serialVersionUID = 1L;
	
	private long distance;
	private TriggerMode mode;
	
	private long lastRun;
	
	private SimpleTrigger() {
	}
	
	public SimpleTrigger(long distance, TriggerMode mode) {
		this();
		this.distance = distance;
		this.mode = mode;
		lastRun = System.currentTimeMillis();
		if (getBegin() != null) {
			lastRun = getBegin().getTimeInMillis() - distance;
		}
	}
	
	public long getDistance() {
		return distance;
	}
	
	public long nextRunInMillis(long time) {
		if (!isEnabled()) return -1;
		
		long nextTime = lastRun + distance;
		if ((nextTime < time) && (nextTime != -1)) return time;
		return nextTime;
	}

	public void triggeredBegin(Task task) {
		if (mode == TriggerMode.EXECUTION_OFFSET) {
			lastRun = System.currentTimeMillis();
		}
	}

	public void triggeredEnd(Task task) {
		if (mode == TriggerMode.COMPLETION_OFFSET) {
			lastRun = System.currentTimeMillis();
		}
	}
}
