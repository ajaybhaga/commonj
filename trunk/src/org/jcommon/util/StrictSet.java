/**
 * Created: Apr 4, 2007
 */
package org.jcommon.util;

import java.util.*;
import java.util.concurrent.locks.*;

/**
 * @author Matt Hicks
 *
 */
public class StrictSet<T> implements Set<T> {
	private Lock lock;
	private List<T> values;
	
	public StrictSet(boolean threadSafe) {
		values = new LinkedList<T>();
		if (threadSafe) {
			lock = new ReentrantLock();
		}
	}
	
	public void lock() {
		if (lock != null) {
			lock.lock();
		}
	}
	
	public void unlock() {
		if (lock != null) {
			lock.unlock();
		}
	}

	public void clear() {
		lock();
		values.clear();
		unlock();
	}

	public int size() {
		try {
			lock();
			return values.size();
		} finally {
			unlock();
		}
	}

	public boolean add(T value) {
		try {
			lock();
			for (int i = 0; i < values.size(); i++) {
				if (values.get(i) == values) {
					return false;
				}
			}
			values.add(value);
			return true;
		} finally {
			unlock();
		}
	}

	public boolean addAll(Collection<? extends T> c) {
		try {
			lock();
			for (T t : c) {
				add(t);
			}
			return true;
		} finally {
			unlock();
		}
	}

	public boolean contains(Object o) {
		try {
			lock();
			for (T t : values) {
				if (o == t) return true;
			}
			return false;
		} finally {
			unlock();
		}
	}

	public boolean containsAll(Collection<?> c) {
		try {
			lock();
			for (T t : values) {
				boolean found = false;
				for (Object o : c) {
					if (o == t) found = true;
				}
				if (!found) return false;
			}
			return true;
		} finally {
			unlock();
		}
	}

	public boolean isEmpty() {
		return values.size() == 0;
	}

	public Iterator<T> iterator() {
		return values.iterator();
	}

	public boolean remove(Object o) {
		try {
			lock();
			return values.remove(o);
		} finally {
			unlock();
		}
	}

	public boolean removeAll(Collection<?> c) {
		try {
			lock();
			for (Object o : c) {
				remove(o);
			}
			return true;
		} finally {
			unlock();
		}
	}

	public boolean retainAll(Collection<?> c) {
		throw new RuntimeException("Not implemented");
	}

	public Object[] toArray() {
		try {
			lock();
			return values.toArray();
		} finally {
			unlock();
		}
	}

	public <T> T[] toArray(T[] a) {
		try {
			lock();
			return values.toArray(a);
		} finally {
			unlock();
		}
	}
}
