package org.jcommon.clone;

import java.util.Map;
import java.util.Map.Entry;

public class MapCloner implements Cloner {
	@SuppressWarnings("unchecked")
	public Object deepClone(Object original, Map<Object, Object> cache) throws Exception {
		// Instantiate a new instance
		Map clone = (Map)ObjectCloner.instantiate(original);
		
		// Populate data
		for (Object entry : ((Map)original).entrySet()) {
			clone.put(CloneUtilities.deepCloneReflectionInternal(((Entry)entry).getKey(), cache), CloneUtilities.deepCloneReflectionInternal(((Entry)entry).getValue(), cache));
		}
		
		return clone;
	}

	@SuppressWarnings("unchecked")
	public Object shallowClone(Object original) throws Exception {
		// Instantiate a new instance
		Map clone = (Map)ObjectCloner.instantiate(original);
		
		// Populate data
		for (Object entry : ((Map)original).entrySet()) {
			clone.put(((Entry)entry).getKey(), ((Entry)entry).getValue());
		}
		
		return clone;
	}
}
