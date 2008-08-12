package org.jcommon.scheduling;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jcommon.jobs.WorkUnit;
import org.jcommon.scheduling.trigger.ManualTrigger;

/**
 * @author Matt Hicks
 *
 */
public abstract class Task implements Comparable<Task>, Serializable, WorkUnit {
	public static enum Status {
		/**
		 * Scheduled to run
		 */
		SCHEDULED(true, false),
		/**
		 * No schedule and has been run manually
		 */
		MANUAL(true, false),
		/**
		 * The task should continue to exist, but cannot be run until its status is changed
		 */
		DISABLED(false, false),
		/**
		 * Successfully completed and should not execute again
		 */
		SUCCESSFUL(false, true),
		/**
		 * Failed to complete properly and should not execute again
		 */
		FAILURE(false, true),
		/**
		 * Killed externally and should not execute again
		 */
		KILLED(false, true);

		private boolean runnable;
		private boolean finished;

		Status(boolean runnable, boolean finished) {
			this.runnable = runnable;
			this.finished = finished;
		}

		public boolean isRunnable() {
			return runnable;
		}

		public boolean isFinished() {
			return finished;
		}
	}

	public static enum ExecutionStatus {
		NEW,
		WAITING,
		RUNNING,
		SUCCESSFUL,
		FAILURE,
		STOPPED,
		INTERRUPTED
	}

	private Task parent;
	private long id;
	private String name;
	private Status status;
	private String statusMessage;
	private float statusProgress;
	private long deletionAge;
	private ExecutionStatus executionStatus;
	private Priority priority;
	private List<Trigger> triggers;

	private transient boolean deleting;

	protected long executionId;
	protected long scheduled;
	private long lastRun;
	private Trigger currentTrigger;

	private boolean manualAllowed;
	private boolean schedulingAllowed;
	protected boolean isScheduled;
	private long threshold;

	private Thread executionThread;

	private Map<String, Object> properties;

	private Queue<Task> taskQueue; // Tasks that have yet to be executed

	private ConcurrentLinkedQueue<TaskExecution> history;

	public Task() {
		id = Math.round(Math.random() * Long.MAX_VALUE);
		status = Status.MANUAL;
		executionStatus = ExecutionStatus.NEW;
		priority = Priority.NORMAL;
		triggers = new ArrayList<Trigger>();
		executionId = -1;
		scheduled = -1;
		lastRun = -1;
		deletionAge = -1;

		manualAllowed = true;
		schedulingAllowed = true;

		properties = new HashMap<String, Object>();
		history = new ConcurrentLinkedQueue<TaskExecution>();

		taskQueue = new ConcurrentLinkedQueue<Task>();
	}

	public void setParent(Task parent) {
		this.parent = parent;
	}

	public Task getParent() {
		return parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Adds a Task that will be executed FIFO upon completion of this Task.
	 * For example, if upon completion of this Task you wanted another task
	 * to execute you could simply addTask with the next task and it will be
	 * executed in the workflow process upon completion.
	 * 
	 * @param task
	 */
	public void addTask(Task task) {
		taskQueue.add(task);
	}

	/**
	 * Returns the queue of tasks that have yet to be executed that have been added
	 * to via the addTask(Task) method.
	 * 
	 * @return
	 * 		Queue<Task>
	 */
	public Queue<Task> getTaskQueue() {
		return taskQueue;
	}

	public Task find(Class<? extends Task> taskClass) {
		if (getClass() == taskClass) {
			return this;
		} else if (hasParent()) {
			return getParent().find(taskClass);
		}
		return null;
	}

	public void doWork() {
		try {
			ScheduleManager.get().beforeTask(this);
			run(!(getCurrentTrigger() instanceof ManualTrigger));
		} catch (Exception exc) {
			LogManager.log(this, LogManager.Type.ERROR, exc);
		}
		ScheduleManager.get().afterTask(this);

		// Attempt to re-schedule
		try {
			ScheduleManager.get().schedule(this);
		} catch (Exception exc) {
			LogManager.log(this, LogManager.Type.ERROR, exc);
		}
	}

	protected void run(boolean scheduled) throws InterruptedException {
		isScheduled = scheduled;
		// TODO add handling for Status checks and throwing errors
		try {
			executionId = Math.round(Math.random() * Long.MAX_VALUE);
			setExecutionStatus(ExecutionStatus.RUNNING);
			setStatusMessage("Running...");
			setStatusProgress(0.0f);
			setExecutionThread(Thread.currentThread());
			currentTrigger.triggeredBegin(this);
		} catch (Throwable exc) {
			LogManager.log(this, LogManager.Type.ERROR, exc);
		}
		long started = System.currentTimeMillis();
		try {
			ExecutionStatus status = execute();
			setExecutionStatus(status);
		} catch (InterruptedException exc) {
			LogManager.log(this, LogManager.Type.ERROR, exc);
			setExecutionStatus(Task.ExecutionStatus.INTERRUPTED);
		} catch (Throwable exc) {
			LogManager.log(this, LogManager.Type.ERROR, exc);
			setExecutionStatus(Task.ExecutionStatus.FAILURE);
			ScheduleManager.get().taskErrored(this, exc);
		} finally {
			setExecutionThread(null);
			setStatusProgress(0.0f);
		}
		long completed = System.currentTimeMillis();
		TaskExecution te = new TaskExecution(this, getExecutionStatus(), started, completed);
		addHistory(te);
		try {
			currentTrigger.triggeredEnd(this);
			currentTrigger = null;
			this.scheduled = -1;
			lastRun = System.currentTimeMillis();
		} catch (Throwable exc) {
			LogManager.log(this, LogManager.Type.ERROR, exc);
		}
	}

	private void addHistory(TaskExecution te) {
		while (history.size() >= ScheduleManager.HISTORY_INSTANCE) {
			history.remove();
		}
		history.add(te);
		ScheduleManager.get().addHistory(te);
	}

	public Queue<TaskExecution> getHistory() {
		return history;
	}

	public TaskExecution getLastExecution() {
		TaskExecution last = null;
		for (TaskExecution te : history) {
			last = te;
		}
		return last;
	}

	protected abstract ExecutionStatus execute() throws Exception;

	public long getId() {
		return id;
	}

	public long getLastRun() {
		return lastRun;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
		changed();
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
		changed();
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) throws InterruptedException {
		// Check interrupted status
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		this.statusMessage = statusMessage;
		changed();
	}

	public float getStatusProgress() {
		return statusProgress;
	}

	public void setStatusProgress(float statusProgress) throws InterruptedException {
		// Check interrupted status
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		if (statusProgress > 1.0f) {
			// Convert to 1.0 scale
			statusProgress /= 100.0f;
		}
		
		this.statusProgress = statusProgress;
		changed();
	}

	/**
	 * The lifespan in milliseconds the Task should survive after reaching a Status
	 * of SUCCESSFUL. If deletionAge is -1 it will never be deleted. Defaults to -1.
	 * 
	 * @param deletionAge
	 */
	public void setDeletionAge(long deletionAge) {
		this.deletionAge = deletionAge;
	}

	/**
	 * The lifespan in milliseconds the Task should survive after reaching a Status
	 * of SUCCESSFUL. If deletionAge is -1 it will never be deleted. Defaults to -1.
	 * 
	 * @return
	 * 		deletionAge
	 */
	public long getDeletionAge() {
		return deletionAge;
	}

	public ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(ExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
		changed();
	}

	public Thread getExecutionThread() {
		return executionThread;
	}

	public void setExecutionThread(Thread executionThread) {
		this.executionThread = executionThread;
	}

	public String getName() {
		if ((name == null) && (getParent() != null))
			return getParent().getName();
		return name;
	}

	public void setName(String name) {
		this.name = name;
		changed();
	}

	public long getExecutionId() {
		return executionId;
	}

	public long getScheduled() {
		return scheduled;
	}

	public Trigger getCurrentTrigger() {
		return currentTrigger;
	}

	public boolean addTrigger(Trigger trigger) {
		try {
			synchronized (triggers) {
				return triggers.add(trigger);
			}
		} finally {
			changed();
		}
	}

	public boolean removeTrigger(Trigger trigger) {
		try {
			synchronized (triggers) {
				return triggers.remove(trigger);
			}
		} finally {
			changed();
		}
	}

	public List<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<Trigger> triggers) {
		synchronized (this.triggers) {
			this.triggers = triggers;
		}
		changed();
	}

	public String getType() {
		return getClass().getSimpleName();
	}

	public Object getProperty(String key) {
		Object prop = properties.get(key);
		if ((prop == null) && (hasParent())) {
			prop = getParent().getProperty(key);
		}
		return prop;
	}

	public void setProperty(String key, Object value) {
		properties.put(key, value);
		changed();
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
		changed();
	}

	public long determineNextRun() {
		scheduled = -1;
		if (!status.isRunnable()) {
			return scheduled;
		}

		synchronized (triggers) {
			long nextRun = -1;
			long current;
			Iterator<Trigger> iterator = triggers.iterator();
			long time = lastRun + threshold;
			if (time < System.currentTimeMillis()) {
				time = System.currentTimeMillis();
			}
			while (iterator.hasNext()) {
				Trigger t = iterator.next();
				if (t.isExpired()) {
					iterator.remove();
					continue;
				}
				current = t.nextRunInMillis(time);
				if (current == -1)
					continue;
				if ((nextRun == -1) || (current < nextRun)) {
					nextRun = current;
					currentTrigger = t;
				}
			}
			scheduled = nextRun;
			if (nextRun != -1) {
				status = Status.SCHEDULED;
			} else {
				status = Status.MANUAL;
			}
			return nextRun;
		}
	}

	public boolean isManualAllowed() {
		return manualAllowed;
	}

	public void setManualAllowed(boolean manualAllowed) {
		this.manualAllowed = manualAllowed;
		changed();
	}

	public boolean isSchedulingAllowed() {
		return schedulingAllowed;
	}

	public void setSchedulingAllowed(boolean schedulingAllowed) {
		this.schedulingAllowed = schedulingAllowed;
		changed();
	}

	public long getThreshold() {
		return threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

	public boolean isDeleting() {
		return deleting;
	}

	public int compareTo(Task t) {
		if (getScheduled() == -1) {
			return 1; // Always put disabled tasks at the bottom
		} else if (getScheduled() > t.getScheduled()) {
			return 1;
		} else if (getScheduled() < t.getScheduled()) {
			return -1;
		}
		return ((Integer)getPriority().ordinal()).compareTo(t.getPriority().ordinal());
	}

	public String toString() {
		String name = getName();
		if (name == null) {
			name = "#" + getId();
		}
		return name + " (" + getClass().getSimpleName() + ")";
	}

	public boolean equals(Object o) {
		if (o instanceof Task) {
			return getId() == ((Task)o).getId();
		}
		return false;
	}

	public void changed() {
		ScheduleManager.get().taskChanged(this);
	}

	public long getTimeout() {
		return -1;
	}
}