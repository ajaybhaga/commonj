package org.jcommon.scheduling.persistence;

import java.io.*;
import java.lang.reflect.*;

import org.jcommon.util.*;
import org.jcommon.xml.*;
import org.jdom.transform.*;

import org.jcommon.scheduling.*;

/**
 * @author Matt Hicks
 *
 */
public class SchedulerXMLPersistence implements SchedulerPersistence {
	private File directory;
	
	public SchedulerXMLPersistence(File directory) {
		this.directory = directory;
		directory.mkdirs();
	}
	
	public void load() {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".task")) {
				try {
					String xml = StringUtilities.toString(files[i]);
					Task task = (Task)XMLSerialization.fromXML(xml, Thread.currentThread().getContextClassLoader());
					ScheduleManager.get().schedule(task);
					System.out.println("Task added: " + task);
				} catch(Exception exc) {
					exc.printStackTrace();
//					if (!files[i].delete()) {
//						files[i].deleteOnExit();
//					}
				}
			}
		}
	}

	public void save(Task task) throws IOException {
		File file = new File(directory, "task" + task.getId() + ".task");
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		String xml = null;
		try {
			xml = XMLSerialization.toXMLString(task, null, false);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			//throw new IOException(e);
		} catch (XSLTransformException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			if (xml != null) writer.write(xml);
			writer.flush();
			writer.close();
		}
		
//		System.out.println("Written file: " + file.getAbsolutePath() + " - Size: " + StringUtilities.format(file.length(), StringUtilities.FILE_FORMAT));
	}
	
	public void delete(Task task) throws IOException {
		File file = new File(directory, "task" + task.getId() + ".task");
		if (!file.delete()) {
			file.deleteOnExit();
		}
	}
}
