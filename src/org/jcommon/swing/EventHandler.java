package org.jcommon.swing;

import java.util.*;

/**
 * @author Matt Hicks
 *
 */
public abstract class EventHandler {
	private EventHandlerManager manager;
	private EventQueue queue;
	
	private ArrayList listeners;
	
	public EventHandler(EventHandlerManager manager, EventQueue queue) {
		this.manager = manager;
		this.queue = queue;
		listeners = new ArrayList();
	}
	
	public void setEventQueue(EventQueue queue) {
		this.queue = queue;
	}
	
	public void enqueueEvent(int type, Object obj) {
		manager.enqueueEvent(this, type, obj);
		synchronized(queue.getEventThread()) {
			queue.getEventThread().notify();
		}
	}
	
	public abstract void executeEvent(int type, Object obj);
	
	public void addListener(Object obj) {
		listeners.add(obj);
	}
	
	public ArrayList getListeners() {
		return listeners;
	}
}
