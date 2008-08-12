package org.jcommon.scheduling;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jcommon.jobs.ThreadManager;
import org.jcommon.pool.ObjectPool;
import org.jcommon.scheduling.trigger.ManualTrigger;
import org.jcommon.util.ClassUtilities;

public class ScheduleManager implements Runnable {
	public static int HISTORY = 100;
	public static int HISTORY_INSTANCE = 10;
	/**
	 * How frequently maintenance tasks should be run against tasks. Defaults to
	 * five minutes. Defined in milliseconds.
	 */
	public static long MAINTENANCE_TIMEOUT = 5 * 60 * 1000;
	public static long DELAY = 50;
	
	// Singleton
	private static ScheduleManager instance;
	
	// The complete list of tasks known
	private Queue<Task> tasks;
	// The scheduled tasks
	private LinkedList<Task> scheduled;
	// Tasks ready to execute
//	private Queue<Task> queue;
	// Listeners
	private Queue<WeakReference<TaskListener>> taskListeners;
	
	private SchedulerPersistence persistence;
	
	private boolean keepAlive;
//	private ObjectPool<ExecutionThread> threadPool;
	private Thread updateThread;
	private long maintenanceLastRun;
	private ConcurrentLinkedQueue<TaskExecution> history;
	
	private ThreadManager manager;
	
	private Lock lock;
	
	private ScheduleManager() {
		tasks = new ConcurrentLinkedQueue<Task>();
		scheduled = new LinkedList<Task>();
//		queue = new ConcurrentLinkedQueue<Task>();
		manager = ThreadManager.getInstance("ScheduleManager");
		taskListeners = new ConcurrentLinkedQueue<WeakReference<TaskListener>>();
		history = new ConcurrentLinkedQueue<TaskExecution>();
		
		lock = new ReentrantLock();
		
		keepAlive = true;
//		try {
//			threadPool = new ObjectPool<ExecutionThread>(ExecutionThread.class, 1, -1);
//		} catch(Exception exc) {
//			throw new RuntimeException(exc);
//		}
		
		updateThread = new Thread(this);
		updateThread.setDaemon(true);
		updateThread.start();
	}
	
	public void initialize(SchedulerPersistence persistence) {
		this.persistence = persistence;
		persistence.load();
	}
	
	public void run() {
		while (keepAlive) {
			boolean workDone = false;
			if (updateSchedule()) {
				workDone = true;
			}
//			if (updateQueue()) {
//				workDone = true;
//			}
			
			if (!workDone) {
				// Validate for maintenance
				if (maintenanceLastRun + MAINTENANCE_TIMEOUT < System.currentTimeMillis()) {
					doMaintenance();
				} else {
    				// No work was done, so we take a break
    				try {
    					Thread.sleep(DELAY);
    				} catch(InterruptedException exc) {
    					exc.printStackTrace();
    				}
				}
			}
		}
	}
	
	/**
	 * Looks in the scheduled tasks to see if any are ready to be run and move
	 * them to the run queue if they are.
	 * 
	 * @return
	 * 		true if any scheduled tasks were ready to run
	 */
	private boolean updateSchedule() {
		boolean workDone = false;
		if (scheduled.size() == 0) return false;
		lock.lock();
		try {
    		while (updateScheduleTop()) {
    		}
		} finally {
			lock.unlock();
		}
		return workDone;
	}
	
	private boolean updateScheduleTop() {
		Task task = scheduled.peek();
		if (task != null) {
			if (task.getScheduled() - System.currentTimeMillis() <= 0) {
				if (task.getExecutionStatus() != Task.ExecutionStatus.WAITING) {
					task.setExecutionStatus(Task.ExecutionStatus.WAITING);
				}
    			
//    			queue.add(task);
				manager.addWork(task);
    			
    			scheduled.remove(task);
    			return true;
			}
		}
		return false;
	}
	
	/**
	 * Processes the run queue and attempts to find threads to run items
	 * 
	 * @return
	 * 		true if any Tasks were in the run queue and were able to execute
	 */
	/*private boolean updateQueue() {
		boolean workDone = false;
//		if (queue.size() > 0) System.out.println("Queue Size: " + queue.size());
		Task task;
		while ((task = queue.poll()) != null) {
			try {
    			ExecutionThread thread;
//    			System.out.println("\tTask Priority: " + task.getPriority() + " (" + task + ") - " + queue.size());
    			if (task.getPriority() == Task.Priority.HIGH) {
    				thread = threadPool.get();
    			} else {
    				thread = threadPool.request();
    			}
//    			System.out.println("\tThread: " + thread);
    			if (thread != null) {
    				LogManager.log(task, LogManager.Type.INFO, "Executing");
    				// Execute the thread
    				thread.execute(task, (task.getCurrentTrigger() instanceof ManualTrigger));
    			} else {
    				queue.add(task);
    			}
			} catch(Throwable t) {
				LogManager.log(task, LogManager.Type.ERROR, t);
				break;
			}
		}
		return workDone;
	}*/
	
	public void doMaintenance() {
//		System.out.println("**** Doing ScheduleManager Maintenance ****");
		long time = System.currentTimeMillis();
		try {
    		// Check to see what needs to be deleted
    		for (Task task : getTasks()) {
    			if (task.getStatus() == Task.Status.SUCCESSFUL) {
    				if (task.getDeletionAge() > -1) {
    					if (task.getLastRun() + task.getDeletionAge() < System.currentTimeMillis()) {
    						// Delete task
    						try {
    							remove(task);
    							System.out.println("**** Task Successfully deleted: " + task + " ****");
    						} catch(Exception exc) {
    							exc.printStackTrace();
    						}
    					}
    				}
    			}
    		}
		} catch(Exception exc) {
			exc.printStackTrace();
		} finally {
			maintenanceLastRun = System.currentTimeMillis();
//			System.out.println("**** Completed ScheduleManager Maintenance in " + (System.currentTimeMillis() - time) + "ms ****");
		}
	}
	
	public void execute(long taskId) throws IOException {
		Task task = null;
		for (Task t : tasks) {
			if (t.getId() == taskId) {
				task = t;
				break;
			}
		}
		if (task != null) {
			execute(task);
		}
	}
	
	public void execute(Task task) throws IOException {
		task.addTrigger(new ManualTrigger(0));
		schedule(task);
	}
	
	public boolean schedule(Task task) throws IOException {
		if (!task.isDeleting()) {
			lock.lock();
			try {
    			add(task);
    			scheduled.remove(task);		// Remove the task if it's already in the schedule;
    			if (task.getStatus() != Task.Status.DISABLED) {		// TODO validate if other options necessary
    				long nextRun = task.determineNextRun();		// Find out when the task wants to run again
    				if (nextRun != -1) {
    					task.setStatus(Task.Status.SCHEDULED);
    					scheduled.add(task);		// Add it to the schedule
    					Collections.sort(scheduled);
    					taskScheduled(task);
    					return true;
    				} else {
    					taskManual(task);
    				}
    				if (task.getStatus() == Task.Status.SUCCESSFUL) {
    					Task tmp = task.getTaskQueue().poll();
    					if (tmp != null) {
    						remove(task);
    						tmp.setStatus(Task.Status.MANUAL);
    						tmp.setParent(task);
    						
    						// Add tasks from parent that were not processed to the new task
    						Task t = null;
    						while ((t = task.getTaskQueue().poll()) != null) {
    							tmp.addTask(t);
    						}
    						
    						return schedule(tmp);
    					} else {
    						// Determine what to do next since there are no more tasks
    						// TODO should we delete it or not?
    					}
    				} else {
    					task.setStatus(Task.Status.MANUAL);
    				}
    			}
    			task.changed();
    			return false;
			} finally {
				lock.unlock();
			}
		}
		return false;
	}
	
	public boolean add(Task task) throws IOException {
		if (!task.isDeleting()) {
			if (persistence != null) {
				persistence.save(task);
			}
			if (!tasks.contains(task)) {
				tasks.add(task);
				taskAdded(task);
				return true;
			}
		}
		return false;
	}
	
	public boolean remove(Task task) throws IOException {
		try {
			ClassUtilities.getField(Task.class, "deleting").set(task, true);
		} catch(Exception exc) {}
		if (persistence != null) {
			persistence.delete(task);
		}
//		queue.remove(task);			// If it's waiting to be executed we delete it
		scheduled.remove(task);		// Remove the task if it's already in the schedule
		boolean removed = tasks.remove(task);
		if (removed) {
			taskRemoved(task);
		}
		return removed;
	}
	
	public void addTaskListener(TaskListener listener) {
		taskListeners.add(new WeakReference<TaskListener>(listener));
	}
	
	public boolean removeTaskListener(TaskListener listener) {
		for (WeakReference<TaskListener> ref : taskListeners) {
			if (ref.get() == null) {
				taskListeners.remove(ref);
			} else if (ref.get() == listener) {
				taskListeners.remove(ref);
				return true;
			}
		}
		return false;
	}
	
	public Queue<Task> getTasks() {
		return tasks;
	}
	
	public Task getTask(long id) {
		for (Task t : tasks) {
			if (t.getId() == id) {
				return t;
			}
		}
		return null;
	}
	
	public void addHistory(TaskExecution te) {
		while (history.size() >= HISTORY) {
			history.remove();
		}
		history.add(te);
	}
	
	public Queue<TaskExecution> getHistory() {
		return history;
	}
	
	protected void beforeTask(Task task) {
		for (WeakReference<TaskListener> ref : taskListeners) {
			TaskListener listener = ref.get();
			if (listener == null) {
				taskListeners.remove(ref);
			} else {
				listener.beforeTask(task);
			}
		}
	}
	
	protected void afterTask(Task task) {
		for (WeakReference<TaskListener> ref : taskListeners) {
			TaskListener listener = ref.get();
			if (listener == null) {
				taskListeners.remove(ref);
			} else {
				listener.afterTask(task);
			}
		}
	}
	
	protected void taskAdded(Task task) {
		for (WeakReference<TaskListener> ref : taskListeners) {
			TaskListener listener = ref.get();
			if (listener == null) {
				taskListeners.remove(ref);
			} else {
				listener.added(task);
			}
		}
	}
	
	protected void taskChanged(Task task) {
		synchronized (task) {
    		for (WeakReference<TaskListener> ref : taskListeners) {
    			TaskListener listener = ref.get();
    			if (listener == null) {
    				taskListeners.remove(ref);
    			} else {
    				listener.changed(task);
    			}
    		}
		}
	}
	
	protected void taskScheduled(Task task) {
		for (WeakReference<TaskListener> ref : taskListeners) {
			TaskListener listener = ref.get();
			if (listener == null) {
				taskListeners.remove(ref);
			} else {
				listener.scheduled(task);
			}
		}
	}
	
	protected void taskManual(Task task) {
		for (WeakReference<TaskListener> ref : taskListeners) {
			TaskListener listener = ref.get();
			if (listener == null) {
				taskListeners.remove(ref);
			} else {
				listener.manual(task);
			}
		}
	}
	
	protected void taskRemoved(Task task) {
		for (WeakReference<TaskListener> ref : taskListeners) {
			TaskListener listener = ref.get();
			if (listener == null) {
				taskListeners.remove(ref);
			} else {
				listener.removed(task);
			}
		}
	}
	
	protected void taskErrored(Task task, Throwable t) {
		for (WeakReference<TaskListener> ref : taskListeners) {
			TaskListener listener = ref.get();
			if (listener == null) {
				taskListeners.remove(ref);
			} else {
				listener.errored(task, t);
			}
		}
	}
	
//	protected ObjectPool<ExecutionThread> getThreadPool() {
//		return threadPool;
//	}
	
	public boolean isAlive() {
		return keepAlive;
	}
	
	public void shutdown() {
		keepAlive = false;
	}

	public static final synchronized ScheduleManager get() {
		if (instance == null) {
			instance = new ScheduleManager();
		}
		return instance;
	}
}