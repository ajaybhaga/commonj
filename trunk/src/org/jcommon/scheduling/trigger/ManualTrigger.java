/**
 * Created: May 23, 2007
 */
package org.jcommon.scheduling.trigger;

import org.jcommon.scheduling.*;

/**
 * @author Matt Hicks
 *
 */
public class ManualTrigger extends Trigger {
	private static final long serialVersionUID = 1L;

	private long lastRun = -1;
	private long offset;
	
	public ManualTrigger(long offset) {
		this.offset = offset;
	}
	
	public long nextRunInMillis(long time) {
		if (!isEnabled()) return -1;
		
		if (lastRun == -1) {
			return time + offset;
		}
		return -1;
	}

	public void triggeredBegin(Task task) {
	}

	public void triggeredEnd(Task task) {
		lastRun = System.currentTimeMillis();
		task.removeTrigger(this);
	}
}
