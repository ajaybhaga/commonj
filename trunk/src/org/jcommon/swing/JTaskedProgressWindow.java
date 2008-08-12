/*
 * Created on Nov 19, 2004
 */
package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class JTaskedProgressWindow extends Thread implements ActionListener {
    private JProgressWindow window;
    private Component parent;
    private String[] labels;
    private Method[] methods;
    private Object[] obj;
    private ArrayList args;
    
    private boolean completed;
    private boolean completedSuccessfully;
    private boolean interrupted;
    
    public JTaskedProgressWindow(Component parent, String title, String[] labels, Method[] methods, Object[] obj, ArrayList args) {
        window = new JProgressWindow(parent, title, methods.length + 1);
        this.parent = parent;
        this.labels = labels;
        this.methods = methods;
        this.obj = obj;
        this.args = args;
    }
    
    public void run() {
        window.start();
        window.enableCancel();
        window.addCancelListener(this);
        try {
	        for (int i = 0; i < methods.length; i++) {
	            window.setProgress(i + 1);
	            window.setNote(labels[i]);
	            methods[i].invoke(obj[i], (Object[])args.get(i));
	        }
	        window.setProgress(methods.length + 1);
	        window.setNote("Completed");
	        try {
	            Thread.sleep(500);
	        } catch(InterruptedException e) {
	            // do nothing
	        }
        } catch(Exception e) {
            if (interrupted) {
                window.destroy();
                completedSuccessfully = false;
	            return;
            } else {
	            e.printStackTrace();
	            window.destroy();
	            JException.showException(parent, e);
	            completedSuccessfully = false;
	            return;
            }
        } finally {
            completed = true;
        }
        completedSuccessfully = true;
        window.destroy();
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public boolean isCompletedSuccessfully() {
        return completedSuccessfully;
    }
    
    public void actionPerformed(ActionEvent e) {
        interrupted = true;
        this.interrupt();
    }
}
