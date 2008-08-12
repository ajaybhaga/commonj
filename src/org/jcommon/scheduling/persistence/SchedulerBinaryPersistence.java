package org.jcommon.scheduling.persistence;

import java.io.*;

import org.jcommon.scheduling.*;

/**
 * @author Matt Hicks
 *
 */
public class SchedulerBinaryPersistence implements SchedulerPersistence {
	private File directory;
	
	public SchedulerBinaryPersistence(File directory) {
		this.directory = directory;
		directory.mkdirs();
	}
	
	public void load() {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".serialized")) {
				ObjectInputStream ois = null;
				try {
					ois = new ObjectInputStream(new FileInputStream(files[i]));
					Task task = (Task)ois.readObject();
					ScheduleManager.get().schedule(task);
				} catch(WriteAbortedException exc) {
					System.err.println("Task Class failed persisting, cannot load.");
					try {
						ois.close();
						files[i].delete();
					} catch(Exception exc2) {
						exc2.printStackTrace();
					}
				} catch(InvalidClassException exc) {
					System.err.println("Task Class has changed since last persistence, cannot load.");
					try {
						ois.close();
						files[i].delete();
					} catch(Exception exc2) {
						exc2.printStackTrace();
					}
				} catch(Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}

	public void save(Task task) throws IOException {
		File file = new File(directory, "task" + task.getId() + ".serialized");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(task);
		oos.flush();
		oos.close();
//		System.out.println("Written file: " + file.getAbsolutePath());
	}
	
	public void delete(Task task) throws IOException {
		File file = new File(directory, "task" + task.getId() + ".serialized");
		if (!file.delete()) {
			file.deleteOnExit();
		}
	}
}
