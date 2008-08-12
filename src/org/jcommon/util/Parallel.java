/**
 * 
 */
package org.jcommon.util;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * The Parallel class provides support for executing tasks in parallel to get better
 * performance from multi-core, multi-processor, and hyperthreaded machines. Even on
 * single-core machines this functionality can often provide better performance as it
 * is not waiting on the completion of other tasks but operates as fast as possible.
 * 
 * @author Matt Hicks
 */
public class Parallel {
	/**
	 * Takes a collection and iterates over it invoking "method" on "object" passing the item
	 * in the collection and passing the work to be done in the referenced WorkManager to make
	 * good concurrent utilization of the work. This is done using reflection.
	 * 
	 * @param collection
	 * @param object
	 * @param method
	 * @param manager
	 * @param blocking
	 */
	public static final void forLoop(Collection<?> collection, Object object, String method, WorkManager manager, boolean blocking) throws InterruptedException {
		Method m = ClassUtilities.getMethod(object.getClass(), method);
		ParallelInvocation[] invocations = new ParallelInvocation[collection.size()];
		int i = 0;
		for (Object obj : collection) {
			invocations[i] = new ParallelInvocation(object, m, obj);
			manager.addWork(invocations[i]);
			i++;
		}
		
		if (blocking) {
			boolean completed = false;
			while (!completed) {
				completed = true;
    			for (ParallelInvocation pi : invocations) {
    				if (!pi.isFinished()) {
    					completed = false;
    				}
    			}
				Thread.sleep(1);
			}
		}
	}
}

class ParallelInvocation implements Runnable {
	private Object object;
	private Method method;
	private Object[] params;
	private boolean finished;
	
	public ParallelInvocation(Object object, Method method, Object ... params) {
		this.object = object;
		this.method = method;
		this.params = params;
		finished = false;
	}
	
	public void run() {
		try {
			method.invoke(object, params);
		} catch(Exception exc) {
			exc.printStackTrace();
		} finally {
			finished = true;
		}
	}
	
	public boolean isFinished() {
		return finished;
	}
}
