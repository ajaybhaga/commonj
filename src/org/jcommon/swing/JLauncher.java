package org.jcommon.swing;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

public class JLauncher extends Thread {
	private JUpdater[] updaters;
	private File jarFile;
	private String className;
	private String methodName;
	private Runnable[] after;
	
	public JLauncher(JUpdater[] updaters, File jarFile, String className, String methodName, Runnable[] after) {
		this.updaters = updaters;
		this.jarFile = jarFile;
		this.className = className;
		this.methodName = methodName;
		this.after = after;
	}
	
	public void run() {
		try {
			for (int i = 0; i < updaters.length; i++) {
				updaters[i].start();
				updaters[i].waitForCompletion();
			}
			
			// Execute runnables
			for (int i = 0; i < after.length; i++) {
				after[i].run();
			}
			
			// Launch
			URLClassLoader loader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, null);
			Class c = Class.forName(className, true, loader);
			Method m = c.getMethod(methodName, new Class[0]);
			m.invoke(null, new Object[0]);
		} catch(Exception exc) {
			exc.printStackTrace();
			JException.showException(null, exc);
		}
	}
}