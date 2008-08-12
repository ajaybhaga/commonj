package org.jcommon.scheduling;

/**
 * @author Matt Hicks
 *
 */
public interface TaskListener {
	public void added(Task task);
	
	public void removed(Task task);
	
	public void scheduled(Task task);
	
	public void manual(Task task);
	
	public void beforeTask(Task task);
	
	public void afterTask(Task task);
	
	public void changed(Task task);
	
	public void errored(Task task, Throwable t);
}
