package org.jcommon.pool;

import java.util.concurrent.*;

/**
 * <code>ObjectPool</code> allows re-use of Objects.
 * 
 * @author Matt Hicks
 */
public class ObjectPool<T> {
	private Class<T> c;
	private ConcurrentLinkedQueue<T> queue;
	private ObjectGenerator<T> generator;
	private volatile int total;
	private int max;
	
	public ObjectPool(ObjectGenerator<T> generator, int preAllocate, int max) throws Exception {
		queue = new ConcurrentLinkedQueue<T>();
		this.generator = generator;
		this.max = max;
		for (int i = 0; i < preAllocate; i++) {
			queue.offer(newInstance());
		}
	}
	
	public ObjectPool(Class<T> c, int preAllocate, int max) throws Exception {
		queue = new ConcurrentLinkedQueue<T>();
		this.c = c;
		this.max = max;
		for (int i = 0; i < preAllocate; i++) {
			queue.offer(newInstance());
		}
	}
	
	/**
	 * The maximum number of objects to create in this pool or -1 to disable
	 * limiting factors.
	 * 
	 * @param max
	 */
	public void setMax(int max) {
		this.max = max;
	}
	
	protected T newInstance() throws Exception {
		T t = null;
		if (generator != null) {
			t = generator.newInstance();
		} else if (c != null) {
			t = c.newInstance();
		}
		if (t != null) total++;
		return t;
	}
	
	/**
	 * Retrieves the first available object in the pool or creates a new instance
	 * if there are none available. If the 'max' has been reached and none are available
	 * it will return null.
	 * 
	 * @return
	 * 		T
	 * @throws Exception
	 */
	public T get() throws Exception {
		T t = queue.poll();
		if ((t == null) && ((total < max) || (max == -1))) {
			t = newInstance();
		}
		if (generator != null) generator.enable(t);
		return t;
	}
	
	/**
	 * Retrieves the first available object in the pool or returns null if none
	 * are available.
	 * 
	 * @return
	 * 		T
	 * @throws Exception
	 */
	public T request() throws Exception {
		T t = queue.poll();
		if (t != null) {
			if (generator != null) generator.enable(t);
		}
		return t;
	}
	
	/**
	 * Releases the object back into the pool for re-use.
	 * 
	 * @param t
	 * @return
	 * 		boolean
	 */
	public boolean release(T t) {
		if (generator != null) generator.disable(t);
		return queue.offer(t);
	}
	
	/**
	 * Retrieves the number of objects available in the queue.
	 * 
	 * @return
	 * 		int
	 */
	public int available() {
		return queue.size();
	}
	
	/**
	 * Returns the actual number of T's created by this ObjectPool.
	 * 
	 * @return
	 * 		int
	 */
	public int size() {
		return total;
	}
}