/**
 * Copyright (c) 2005-2007 jCommon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jCommon' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created: Mar 18, 2008
 */
package org.jcommon.jobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.jcommon.util.StringUtilities;

/**
 * ThreadManager provides thread management work processing within any
 * system.
 * 
 * @author Matt Hicks
 */
public class ThreadManager implements Runnable {
	/**
	 * The amount of time to delay after exceeding the maximum thread count
	 * before settling the count back down.
	 */
	public static long THREAD_SETTLING = 5 * 60 * 1000;
	
	/**
	 * If set to true, WorkUnits that provide a positive timeout value
	 * will be interrupted if they take too long. This defaults to true.
	 */
	public static boolean TIMEOUT_WORK = true;

	/**
	 * The threshold that must be achieved or exceeded in the WorkUnits
	 * waiting in the queue before another thread is added. This will only
	 * apply up to the maxThreads. Defaults to 2.
	 */
	public static long WORK_THRESHOLD = 2;

	/**
	 * The amount of time between iterations of the monitoring thread. This
	 * is what checks to see if a WorkUnit that has a timeout period has been
	 * exceeded and should be terminated. Also, this determines the delay between
	 * checks if the max thread count should be increased. Defaults to 200ms.
	 */
	public static long MONITORING_DELAY = 100;

	/**
	 * The amount of time for each ProcessingThread to wait before recycling.
	 */
	public static long THREAD_WAIT = 5000;

	/**
	 * The default starting maximum thread count
	 */
	public static final int DEFAULT_MAX_THREADS = 500;

	private static ThreadManager instance;
	private static Map<String, ThreadManager> mappings;

	private Thread thread; // The monitor thread
	private int maxThreads; // Maximum threads for this instance
	private volatile int threadCount; // Currently running threads
	private boolean keepAlive; // Whether the ThreadManager should continue running

	private boolean timeoutWork;
	private long workThreshold;
	private long monitoringDelay;
	private long threadWait;

	// Currently living ProcessingThreads
	private Queue<ProcessingThread> threads;
	// Threads currently in use and may timeout
	private Queue<ProcessingThread> monitored;
	// Work queue
	private Queue<WorkUnit> queueLow;
	private Queue<WorkUnit> queueNormal;
	private Queue<WorkUnit> queueHigh;
	
	// The last time there was an 
	private volatile long lastOverage;

	private ThreadManager() {
		maxThreads = DEFAULT_MAX_THREADS;
		timeoutWork = TIMEOUT_WORK;
		workThreshold = WORK_THRESHOLD;
		monitoringDelay = MONITORING_DELAY;
		threadWait = THREAD_WAIT;

		threads = new ConcurrentLinkedQueue<ProcessingThread>();
		monitored = new ConcurrentLinkedQueue<ProcessingThread>();
		queueLow = new ConcurrentLinkedQueue<WorkUnit>();
		queueNormal = new ConcurrentLinkedQueue<WorkUnit>();
		queueHigh = new ConcurrentLinkedQueue<WorkUnit>();

		keepAlive = true;

		thread = new Thread(this);
		thread.setName("JSLThreadManager");
		thread.setDaemon(true);
		thread.start();
		
		// Add a single thread to begin
		addThread();
	}

	public boolean isTimeoutWork() {
		return timeoutWork;
	}

	public void setTimeoutWork(boolean timeoutWork) {
		this.timeoutWork = timeoutWork;
	}

	public long getWorkThreshold() {
		return workThreshold;
	}

	public void setWorkThreshold(long workThreshold) {
		this.workThreshold = workThreshold;
	}

	public long getMonitoringDelay() {
		return monitoringDelay;
	}

	public void setMonitoringDelay(long monitoringDelay) {
		this.monitoringDelay = monitoringDelay;
	}

	public long getThreadWait() {
		return threadWait;
	}

	public void setThreadWait(long threadWait) {
		this.threadWait = threadWait;
	}

	/**
	 * Pre-allocates threads up to maxThreads to keep ramp-up issues from occurring.
	 */
	public void preAllocate() {
		while (getThreadCount() != getMaxThreads()) {
			addThread();
		}
	}

	/**
	 * The maximum number of threads the ThreadManager can utilize
	 * when processing work. Note, it will only use them if necessary
	 * when processing work. If this number is met by active threads
	 * the work will wait until currently processing threads complete
	 * before processing additional work. This defaults to the static
	 * DEFAULT_MAX_THREADS value.
	 * 
	 * @return
	 * 		maxThreads
	 */
	public int getMaxThreads() {
		return maxThreads;
	}

	/**
	 * The maximum number of threads the ThreadManager can utilize
	 * when processing work. Note, it will only use them if necessary
	 * when processing work. If this number is met by active threads
	 * the work will wait until currently processing threads complete
	 * before processing additional work. This defaults to the static
	 * DEFAULT_MAX_THREADS value.
	 * 
	 * @param maxThreads
	 */
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * The current count of threads currently in the employ of the ThreadManager. These
	 * may be idle threads or busy threads.
	 * 
	 * @return
	 * 		threadCount
	 */
	public int getThreadCount() {
		return threadCount;
	}

	/**
	 * Adds work to be done by the thread manager. If there are available
	 * threads or the maximum number of processing threads has not been
	 * exceeded processing will commence immediately. However, if no threads
	 * are available and no more threads can be created the work will remain
	 * enqueued for processing when a processing thread becomes available.
	 * 
	 * @param work
	 * @return
	 * 		true if work was added
	 */
	public boolean addWork(WorkUnit work) {
		boolean b = false;
		switch(work.getPriority()) {
			case LOW:
				b = queueLow.add(work);
				break;
			case NORMAL:
				b = queueNormal.add(work);
				break;
			case HIGH:
				b = queueHigh.add(work);
				break;
		}
		synchronized (thread) {
			thread.notifyAll();
		}
		return b;
	}

	protected Thread getThread() {
		return thread;
	}

	/**
	 * Retrieves and removes the first available WorkUnit in the queue for processing.
	 * Returns null if no work is currently in the queue.
	 * 
	 * @return
	 * 		WorkUnit
	 */
	public WorkUnit getWork() {
		WorkUnit work = queueHigh.poll();
		if (work == null) {
			work = queueNormal.poll();
			if (work == null) {
				work = queueLow.poll();
			}
		}
		return work;
	}

	public void run() {
		while (keepAlive) {
			try {
				// Check to see if any monitored threads are taking too long to complete
				if (isTimeoutWork()) {
					for (ProcessingThread t : monitored) {
						t.updateTimeout();
					}
				}

				// Check to see if there is too much work for the number of threads
				if ((threadCount < maxThreads) && (queueNormal.size() >= getWorkThreshold())) {
					// Add another thread since we have more work than is currently being handled
					addThread();
				} else if (queueHigh.size() >= getWorkThreshold()) {
					// Add another thread if there's too much high priority work
					addThread();
				} else if ((queueHigh.size() > 0) && (allProcessing())) {
					// Add another thread if all threads are processing and there's a high priority item waiting
					addThread();
				}
				
				Thread.sleep(getMonitoringDelay());
			} catch (InterruptedException exc) {
				Logger.getLogger("ThreadManager").warning("ThreadManager: InterruptedException");
			}
		}
	}
	
	public long getLastOverage() {
		return lastOverage;
	}

	private synchronized boolean addThread() {
		ProcessingThread thread = new ProcessingThread(this); {
			thread.setDaemon(true);
			thread.start();
			threads.add(thread);
			threadCount++;
		}
		
		if (threadCount > maxThreads) {
			lastOverage = System.currentTimeMillis();
		}
		
		Logger.getLogger("ThreadManager").fine("ThreadManager: Thread count increased to " + threadCount);
		return true;
	}
	
	protected synchronized boolean checkRemoveThread(ProcessingThread thread) {
		if (getThreadCount() > getMaxThreads()) {
    		threads.remove(thread);
    		monitored.remove(thread);
    		System.out.println("Removed thread!");
    		return true;
		}
		return false;
	}
	
	/**
	 * Determines if all active threads are currently working
	 * 
	 * @return
	 * 		boolean
	 */
	public boolean allProcessing() {
		for (ProcessingThread thread : threads) {
			if (!thread.isWorking()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Shuts down the ThreadManager.
	 */
	public void shutdown() {
		keepAlive = false;
	}
	
	/**
	 * Returns the complete trace count for this manager
	 * 
	 * @return
	 */
	public List<StackTraceElement[]> getStackTraces() {
		List<StackTraceElement[]> traces = new ArrayList<StackTraceElement[]>();
		for (ProcessingThread pt : threads) {
			traces.add(pt.getStackTrace());
		}
		return traces;
	}
	
	public void dumpStackTraces(File directory, String prefix) throws IOException {
		List<StackTraceElement[]> traces = getStackTraces();
		for (int i = 0; i < traces.size(); i++) {
			File f = new File(directory, prefix + (i + 1) + ".trace");
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			for (StackTraceElement e : traces.get(i)) {
				writer.append(e.getClassName());
				writer.append('.');
				writer.append(e.getMethodName());
				if (e.getLineNumber() > 0) {
    				writer.append(" (" + e.getLineNumber() + ")");
				} else {
					writer.append(" (Unknown)");
				}
				writer.newLine();
			}
			writer.flush();
			writer.close();
		}
	}

	/**
	 * Determines whether the current thread that invoked this method is a ProcessingThread.
	 * 
	 * @return
	 * 		boolean
	 */
	public static final boolean isProcessingThread() {
		if (Thread.currentThread() instanceof ProcessingThread) {
			return true;
		}
		return false;
	}

	/**
	 * Returns a singleton instance of ThreadManager.
	 * 
	 * @return
	 * 		ThreadManager
	 */
	public static final synchronized ThreadManager getInstance() {
		if (instance == null) {
			instance = new ThreadManager();
		}
		return instance;
	}
	
	/**
	 * Similar to getInstance(), but allows the existence of multiple ThreadManagers referenced
	 * by name. If the name passed does not exist it is created.
	 * 
	 * @param name
	 * @return
	 * 		ThreadManager
	 */
	public static final synchronized ThreadManager getInstance(String name) {
		if (mappings == null) {
			mappings = new HashMap<String, ThreadManager>();
		}
		ThreadManager manager = mappings.get(name);
		if (manager == null) {
			manager = new ThreadManager();
			mappings.put(name, manager);
		}
		return manager;
	}
	
	/**
	 * Returns the existent state of the name passed in of a ThreadManager instance.
	 * 
	 * @param name
	 * @return
	 * 		boolean
	 */
	public static final boolean exists(String name) {
		if (mappings == null) {
			return false;
		}
		return mappings.containsKey(name);
	}
	
	/**
	 * Creates an array of all named ThreadManagers.
	 * 
	 * @return
	 * 		ThreadManager[]
	 */
	public static final ThreadManager[] getNamedInstances() {
		if (mappings == null) {
			return new ThreadManager[0];
		}
		return mappings.values().toArray(new ThreadManager[mappings.size()]);
	}
}
