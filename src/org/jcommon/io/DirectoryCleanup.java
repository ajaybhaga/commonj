package org.jcommon.io;

import java.io.File;

public class DirectoryCleanup implements Runnable {
	/**
	 * The cycle determines how long to wait between checks. Defaults
	 * to 5 hours.
	 */
	public static long CYCLE = 5 * 60 * 60 * 1000;
	
	private File directory;
	private long lifetime;
	
	private long lastRun;
	
	public DirectoryCleanup(File directory, long lifetime) {
		this.directory = directory;
		this.lifetime = lifetime;
		
		directory.mkdirs();
		
		if (!directory.exists()) {
			throw new RuntimeException("Unable to create directory: " + directory.getAbsolutePath());
		}
	}
	
	public void run() {
		try {
			while (true) {
        		if (lastRun + CYCLE < System.currentTimeMillis()) {
        			// Run
        			try {
        				long life = System.currentTimeMillis() + lifetime;
        				for (File f : directory.listFiles()) {
        					try {
        						if (f.lastModified() < life) {
        							// Too old, lets try to delete
        							if (!f.delete()) {
        								f.deleteOnExit();
        							}
        						}
        					} catch(Exception exc) {
        						exc.printStackTrace();
        					}
        				}
        			} catch(Exception exc) {
        				exc.printStackTrace();
        			}
        			lastRun = System.currentTimeMillis();
        		}
        		Thread.sleep(5000);
			}
		} catch(InterruptedException exc) {
			exc.printStackTrace();
		}
	}
}