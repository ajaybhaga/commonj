package org.jcommon.pool;

/**
 * @author Matt Hicks
 */
public interface ObjectGenerator<T> {
	/**
	 * Creates and returns a new instance of T
	 * 
	 * @return
	 * 		T
	 * @throws Exception
	 */
	public T newInstance() throws Exception;
	
	/**
	 * This method is invoked when this object is being enabled for use
	 * out of the pool.
	 * 
	 * @param t
	 */
	public void enable(T t);
	
	/**
	 * This method is invoked when this object is being returned to
	 * the pool.
	 * 
	 * @param t
	 */
	public void disable(T t);
}
