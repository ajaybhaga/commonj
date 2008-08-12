package org.jcommon.scheduling;

import java.io.*;

/**
 * @author Matt Hicks
 */
public interface SchedulerPersistence {
	public void load();
	
	public void save(Task task) throws IOException;
	
	public void delete(Task task) throws IOException;
}
