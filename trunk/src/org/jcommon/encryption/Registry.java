package org.jcommon.encryption;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jcommon.lang.KeyValue;

/**
 * Registry is a simple containment system for Objects. Objects are passed in and assigned a unique identifier
 * to tie back to the Object passed in. Objects can be retrieved via the key assigned to them. Entries are
 * expired after "entryLifespan" has elapsed.
 * 
 * For expirations to occur, the Registry instance must be run within a Thread.
 * 
 * @author Matt Hicks
 *
 * @param <O>
 */
public class Registry<O> implements Runnable {
	/**
	 * The amount of time between updates on the registry
	 */
	public static long UPDATE_INTERVAL = 1 * 1000;
	
	private long entryLifespan;
	private Map<String, KeyValue<Long, O>> map;
	private boolean keepAlive;
	
	/**
	 * Instantiate with an entryLifeSpan (the time an entry should exist before expiring)
	 * 
	 * @param entryLifespan
	 */
	public Registry(long entryLifespan) {
		this.entryLifespan = entryLifespan;
		map = new ConcurrentHashMap<String, KeyValue<Long, O>>();
		keepAlive = true;
	}
	
	public void run() {
		try {
    		while (keepAlive) {
    			update();
    			Thread.sleep(UPDATE_INTERVAL);
    		}
		} catch(InterruptedException exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Called upon each iteration within the run() or may be manually invoked if used within another
	 * thread.
	 */
	public void update() {
		for (Map.Entry<String, KeyValue<Long, O>> entry : map.entrySet()) {
			long time = entry.getValue().getKey();
			if (System.currentTimeMillis() - time > entryLifespan) {
				// Timeout
				map.remove(entry.getKey());
			}
		}
	}
	
	/**
	 * Creates an entry in the registry for the passed Object and returns a unique key for later
	 * retrieval.
	 * 
	 * @param o
	 * @return
	 * 		key
	 */
	public String createEntry(O o) {
		// First make sure an entry doesn't already exist - if does, remove it
		for (Map.Entry<String, KeyValue<Long, O>> entry : map.entrySet()) {
			if (entry.getValue().getValue() == o) {
				// Found an entry, so we remove it
				map.remove(entry.getKey());
			}
		}
		
		// Create new entry
		long time = System.currentTimeMillis();
		String key = SimpleMD5.toHexString(time + ":" + Math.random(), o.toString());
		
		map.put(key, new KeyValue<Long, O>(time, o));
		
		return key;
	}
	
	/**
	 * Retrieves the Object for the given key.
	 * 
	 * @param key
	 * @return
	 * 		O
	 */
	public O get(String key) {
		KeyValue<Long, O> kv = map.get(key);
		if (kv != null) {
			return kv.getValue();
		}
		return null;
	}
}