/*
 * Created on May 26, 2005
 */
package org.jcommon.swing.robust;

import java.util.*;

import org.jcommon.swing.robust.event.*;

/**
 * @author Matt Hicks
 */
public class RobustMonitor extends Thread {
    private static RobustMonitor monitor;
    
    public static int maxHistory = 100;
    
    private ArrayList listeners;
    private ArrayList queue;
    private ArrayList history;
    
    private RobustMonitor() {
        listeners = new ArrayList();
        queue = new ArrayList();
        history = new ArrayList(maxHistory);
        this.setDaemon(true);
        this.start();
    }
    
    public void addMonitorListener(RobustMonitorListener listener) {
        listeners.add(listener);
    }
    
    public void fireEvent(RobustEvent event) {
        queue.add(event);
    }
    
    public static RobustMonitor getInstance() {
        if (monitor == null) {
            monitor = new RobustMonitor();
        }
        return monitor;
    }
    
    public void run() {
        while (true) {
            try {
	            int size = queue.size();
	            for (int i = 0; i < size; i++) {
	                handleEvent((RobustEvent)queue.get(0));
	                queue.remove(0);
	            }
	            Thread.sleep(200);
            } catch(Exception exc) {
                exc.printStackTrace();
            }
        }
    }
    
    private void handleEvent(RobustEvent event) {
        history.add(event);
        while (history.size() > maxHistory) {
            history.remove(0);
        }
        RobustMonitorListener listener;
        for (int i = 0; i < listeners.size(); i++) {
            listener = (RobustMonitorListener)listeners.get(i);
            listener.eventOccurred(event);
        }
    }
    
    public RobustEvent[] getHistory() {
        RobustEvent[] array = new RobustEvent[history.size()];
        for (int i = 0; i < history.size(); i++) {
            array[i] = (RobustEvent)history.get(i);
        }
        return array;
    }
}
