package org.jcommon.util;

import java.util.concurrent.*;

public class WorkManager {
	private ConcurrentLinkedQueue<Worker> workers;
	private ConcurrentLinkedQueue<Runnable> work;
	private String threadName;
	private int maxThreads;
	private volatile int created;
	
	public WorkManager() {
	}
	
	public WorkManager(String threadName, int maxThreads) {
		this.threadName = threadName;
		this.maxThreads = maxThreads;
		workers = new ConcurrentLinkedQueue<Worker>();
		work = new ConcurrentLinkedQueue<Runnable>();
	}
	
	/**
	 * Adds work to the queue to be done by worker threads
	 * 
	 * @param runnable
	 */
	public void addWork(Runnable runnable) {
		Worker worker = workers.poll();
		if (worker == null) {
			if (created >= maxThreads) {
				work.add(runnable);
				return;
			}
			worker = new Worker(this);
			worker.setName(threadName + "[" + created + "]");
			worker.setDaemon(true);
			worker.start();
			created++;
		}
		worker.doWork(runnable);
	}

	// Called when a worker is finished to release it
	protected void release(Worker worker) {
		Runnable r = work.poll();
		if (r != null) {
			worker.doWork(r);
		} else {
			workers.add(worker);
		}
	}

	public boolean hasWork() {
		return work.size() > 0;
	}
	
	public boolean isWorking() {
		return workers.size() != created;
	}

	/**
	 * Allows an external thread pick up some work to do.
	 * 
	 * @return
	 * 		Runnable
	 */
	public Runnable getWork() {
		return work.poll();
	}
	
	/**
	 * Executes a single unit of work in the current thread if there
	 * is any to do.
	 */
	public void doWork() {
		Runnable runnable = getWork();
		if (runnable != null) {
			runnable.run();
		}
	}
}

class Worker extends Thread {
	private WorkManager manager;
	private Runnable work;
	
	public Worker(WorkManager manager) {
		this.manager = manager;
	}
	
	public void doWork(Runnable work) {
		this.work = work;
		synchronized(this) {
			notifyAll();
		}
	}
	
	public void run() {
		while (true) {
			if (work != null) {
				try {
					work.run();
				} catch(Throwable t) {
					t.printStackTrace();
					// TODO add support for exception handler
				} finally {
					work = null;
					manager.release(this);
				}
			} else {
				try {
					synchronized(this) {
						wait(100);
					}
				} catch(InterruptedException exc) {
				}
			}
		}
	}
}
