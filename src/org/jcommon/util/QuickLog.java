package org.jcommon.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuickLog {
	private static final Map<String, BufferedWriter> map = new ConcurrentHashMap<String, BufferedWriter>();
	
	public static final synchronized void log(String filename, String message) {
		BufferedWriter writer = map.get(filename);
		if (writer == null) {
			File file = new File(filename);
			try {
				writer = new BufferedWriter(new FileWriter(file));
			} catch(Exception exc) {
				throw new RuntimeException("Failed to create file: " + filename, exc);
			}
			map.put(filename, writer);
		}
		try {
    		writer.write(message);
    		writer.newLine();
    		writer.flush();
		} catch(Exception exc) {
			throw new RuntimeException("Failed to write to file: " + filename, exc);
		}
	}
}