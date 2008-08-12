package org.jcommon.web;

import java.util.*;

/**
 * @author Matt Hicks
 */
public class WebSession {
	public static final int UNSET = 1;
	public static final int SET = 2;
	
	private String sessionId;
	private HashMap<Object,Object> values;
	private long lastReferenced;
	private int status;
	
	public WebSession(String sessionId) {
		this.sessionId = sessionId;
		values = new HashMap<Object,Object>();
		status = UNSET;
	}
	
	public void set(Object key, Object value) {
		values.put(key, value);
	}
	
	public Object get(Object key) {
		return values.get(key);
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void referenced() {
		lastReferenced = System.currentTimeMillis();
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
}
