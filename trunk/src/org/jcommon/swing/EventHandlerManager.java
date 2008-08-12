package org.jcommon.swing;

import java.util.*;

/**
 * @author Matt Hicks
 *
 */
public class EventHandlerManager {
	private ArrayList handlers;
	private ArrayList types;
	private ArrayList queue;
	
	public EventHandlerManager() {
		handlers = new ArrayList();
		types = new ArrayList();
		queue = new ArrayList();
	}
	
	public synchronized void enqueueEvent(EventHandler handler, int type, Object obj) {
		handlers.add(handler);
		types.add(new Integer(type));
		queue.add(obj);
	}
	
	public synchronized boolean hasNext() {
		if (types.size() > 0) return true;
		return false;
	}
	
	public synchronized void processNext() {
		if (types.size() > 0) {
			int type = ((Integer)types.get(0)).intValue();
			((EventHandler)handlers.get(0)).executeEvent(type, queue.get(0));
			handlers.remove(0);
			types.remove(0);
			queue.remove(0);
		}
	}
}
