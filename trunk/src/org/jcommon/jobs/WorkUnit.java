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
 * Created: Feb 6, 2008
 */
package org.jcommon.jobs;

/**
 * Represents a unit of work to be done.
 * 
 * @author Matt Hicks
 */
public interface WorkUnit {
	public static enum Priority {
		LOW,
		NORMAL,
		HIGH
	}
	
	/**
	 * The amount of time to allow this unit of work process before timing out.
	 * If value is -1 no timeout is applied. If a WorkUnit times out it will
	 * receive an InterruptedException.
	 * 
	 * @return
	 * 		timeout
	 */
	public long getTimeout();
	
	/**
	 * Invoked to do the unit of work this WorkUnit represents.
	 * 
	 * @throws InterruptedException
	 */
	public void doWork() throws InterruptedException;

	/**
	 * The priority of this unit of work. Depending on the configuration of the ThreadManager
	 * the work will be given preference or potentially even add additional threads if the priority
	 * is high enough.
	 * 
	 * @return
	 * 		priority
	 */
	public Priority getPriority();
}