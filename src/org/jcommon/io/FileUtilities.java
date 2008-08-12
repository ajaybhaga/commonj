/*
 * Created on Sep 12, 2004
 */
package org.jcommon.io;

import java.io.*;

/**
 * @author Matt Hicks
 */
public class FileUtilities {
    private static final boolean debug = false;
    
    public static boolean deltree(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (!deltree(files[i])) {
						return false;
					}
				} else if (!files[i].delete()) {
					if (debug) System.out.println("Unable to delete: " + files[i]);
					return false;
				}
			}
			return directory.delete();
		}
		return false;
	}

    public static void copy(File src, File dest) throws IOException {
    	byte[] buf = new byte[512];
    	int len;
    	FileInputStream is = new FileInputStream(src);
    	FileOutputStream os = new FileOutputStream(dest);
    	while ((len = is.read(buf)) != -1) {
    		os.write(buf, 0, len);
    	}
    	os.flush();
    	os.close();
    	is.close();
    }

    public static void output(String s, File dest) throws IOException {
    	BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
    	writer.write(s);
    	writer.flush();
    	writer.close();
    }
}
