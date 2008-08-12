package org.jcommon.swing;

import java.util.*;

/**
 * EventThread is the processing thread for EventQueue.
 * 
 * This allows a single or multiple thread model to be
 * supported for event queues and give the developer
 * more control over how events should be processed.
 * 
 * @author Matt Hicks
 */
public class EventThread extends Thread {
	private ArrayList events;
	private ArrayList queues;
	private ArrayList types;
	private EventHandlerManager manager;
	
	public EventThread() {
		this.setDaemon(true);
		
		events = new ArrayList();
		queues = new ArrayList();
		types = new ArrayList();
	}
	
	public void setEventHandlerManager(EventHandlerManager manager) {
		this.manager = manager;
	}
	
	public void run() {
		EventObject evt;
		EventQueue queue;
		int type;
		while (true) {
			try {
				synchronized(this) {
					if ((events.size() == 0) && ((manager == null) || (!manager.hasNext()))) {
						wait();
					}
					if (events.size() > 0) {
						evt = (EventObject)events.get(0);
						queue = (EventQueue)queues.get(0);
						type = ((Integer)types.get(0)).intValue();
						queue.threadedEvent(evt, type);
						events.remove(0);
						queues.remove(0);
						types.remove(0);
					}
					if (manager != null) {
						manager.processNext();
					}
				}
			} catch(InterruptedException exc) {
			}
		}
	}
	
	public synchronized void enqueue(EventObject evt, EventQueue queue, int type) {
		events.add(evt);
		queues.add(queue);
		types.add(new Integer(type));
		queue.getEventThread().notify();
	}
}
