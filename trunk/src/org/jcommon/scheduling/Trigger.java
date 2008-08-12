/**
 * Created: Mar 19, 2007
 */
package org.jcommon.scheduling;

import java.io.*;
import java.util.*;

/**
 * @author Matt Hicks
 *
 */
public abstract class Trigger implements Serializable {
	private Calendar begin;
	private Calendar expiration;
	private boolean enabled;
	
	public Trigger() {
		enabled = true;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Invoked when this trigger is used to execute (before the task is executed)
	 */
	public abstract void triggeredBegin(Task task);
	
	/**
	 * Invoked when this trigger is used to execute (after the task is executed)
	 */
	public abstract void triggeredEnd(Task task);
	
	/**
	 * Determine the time the next iteration of this trigger should run
	 * 
	 * @return
	 * 		time in milliseconds
	 */
	public abstract long nextRunInMillis(long time);
	
	public void setBegin(Calendar begin) {
		this.begin = begin;
	}
	
	public boolean isBeginnable(Calendar calendar) {
		if (begin != null) {
			if (calendar.before(begin)) {
				return false;
			}
		}
		return true;
	}
	
	public Calendar getBegin() {
		return begin;
	}
	
	public void setExpiration(Calendar expiration) {
		this.expiration = expiration;
	}
	
	public boolean isExpired() {
		if (expiration != null) {
			if (expiration.before(new GregorianCalendar())) {
				return true;
			}
		}
		return false;
	}
	
	public Calendar getExpiration() {
		return expiration;
	}
}