/**
 * Created: Apr 3, 2007
 */
package org.jcommon.util;

import java.util.*;
import java.util.concurrent.locks.*;

/**
 * Much slower than HashMap, but uses == to verify exact matches rather than
 * .equals. Also, this class supports thread-safety features or they can be turned
 * off for better performance.
 * 
 * @author Matt Hicks
 */
public class StrictMap<K, V> implements Map<K, V> {
	private Lock lock;
	private List<K> keys;
	private List<V> values;
	
	public StrictMap(boolean threadSafe) {
		keys = new LinkedList<K>();
		values = new LinkedList<V>();
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
		keys.clear();
		values.clear();
		unlock();
	}

	public boolean containsKey(Object key) {
		try {
			lock();
			for (K k : keys) {
				if (key == k) return true;
			}
			return false;
		} finally {
			unlock();
		}
	}

	public boolean containsValue(Object value) {
		try {
			lock();
			for (V v : values) {
				if (value == v) return true;
			}
			return false;
		} finally {
			unlock();
		}
	}

	public Set<Entry<K, V>> entrySet() {
		throw new RuntimeException("Not implemented");
	}

	public V get(Object key) {
		try {
			lock();
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i) == key) {
					return values.get(i);
				}
			}
			return null;
		} finally {
			unlock();
		}
	}

	public boolean isEmpty() {
		try {
			lock();
			return keys.size() > 0;
		} finally {
			unlock();
		}
	}

	public Set<K> keySet() {
		throw new RuntimeException("Not implemented");
	}

	public V put(K key, V value) {
		try {
			lock();
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i) == key) {
					return values.set(i, value);
				}
			}
			keys.add(key);
			values.add(value);
			return value;
		} finally {
			unlock();
		}
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (K k : m.keySet()) {
			V v = m.get(k);
			put(k, v);
		}
	}

	public V remove(Object key) {
		try {
			lock();
			for (int i = 0; i < keys.size(); i++) {
				if (keys.get(i) == key) {
					keys.remove(i);
					return values.remove(i);
				}
			}
			return null;
		} finally {
			unlock();
		}
	}

	public int size() {
		try {
			lock();
			return keys.size();
		} finally {
			unlock();
		}
	}

	public Collection<V> values() {
		throw new RuntimeException("Not implemented");
	}
}
