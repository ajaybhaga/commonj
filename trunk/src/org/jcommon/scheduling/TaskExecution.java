package org.jcommon.scheduling;

import org.jcommon.scheduling.Task.ExecutionStatus;

public class TaskExecution {
	private String name;
	private String type;
	private long id;
	private ExecutionStatus status;
	private long started;
	private long completed;
	
	private TaskExecution() {
	}
	
	public TaskExecution(Task task, ExecutionStatus status, long started, long completed) {
		name = task.getName();
		type = task.getType();
		id = task.getId();
		this.status = status;
		this.started = started;
		this.completed = completed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ExecutionStatus getStatus() {
		return status;
	}

	public void setStatus(ExecutionStatus status) {
		this.status = status;
	}

	public long getStarted() {
		return started;
	}

	public void setStarted(long started) {
		this.started = started;
	}

	public long getCompleted() {
		return completed;
	}

	public void setCompleted(long completed) {
		this.completed = completed;
	}
}
