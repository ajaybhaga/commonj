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

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Individual processing of WorkUnits is done within ProcessingThreads.
 * These are utilized explicitly by ThreadManager to handle multi-threading
 * of work-loads.
 * 
 * @author Matt Hicks
 */
public class ProcessingThread extends Thread {
	private ThreadManager manager;
	
	private boolean keepAlive;
	private AtomicLong startTime;
	
	private WorkUnit workUnit;
	
	public ProcessingThread(ThreadManager manager) {
		this.manager = manager;
		
		keepAlive = true;
		startTime = new AtomicLong(-1);
	}
	
	public void run() {
		while (keepAlive) {
			try {
    			workUnit = manager.getWork();
    			if (workUnit != null) {
    				processWork();
    			} else if ((manager.getThreadCount() > manager.getMaxThreads()) && (System.currentTimeMillis() > manager.getLastOverage() + ThreadManager.THREAD_SETTLING)) {
    				// Maximum number of threads exceeded and minimum lifespan reached
    				if (manager.checkRemoveThread(this)) {
    					// Thread has been removed
    					break;
    				}
    			} else {
    				synchronized(manager.getThread()) {
    					manager.getThread().wait(manager.getThreadWait());
    				}
    			}
			} catch(InterruptedException exc) {
				if (workUnit != null) {
					Logger.getLogger("jCommon").log(Level.WARNING, "Processing took too long and was interrupted", exc);
				}
			} catch(Throwable t) {
				Logger.getLogger("jCommon").log(Level.WARNING, "Uncaught Exception in ProcessingThread", t);
			} finally {
				if (workUnit != null) {
					workUnit = null;
					startTime.set(-1);
				}
			}
		}
	}
	
	private void processWork() throws InterruptedException {
		startTime.set(System.currentTimeMillis());
		workUnit.doWork();
	}
	
	protected void updateTimeout() {
		long startTime = this.startTime.get();
		WorkUnit work = this.workUnit;
		if ((startTime != -1) && (work != null) && (work.getTimeout() > 0) && (startTime + work.getTimeout() < System.currentTimeMillis())) {
			System.err.println("Interrupting: " + startTime + ", " + work.getTimeout());
			startTime = -1;
			this.interrupt();
		}
	}

	public boolean isWorking() {
		return startTime.get() != -1;
	}
}