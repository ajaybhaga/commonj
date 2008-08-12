package org.jcommon.clone;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jcommon.util.BeanUtilities;
import org.jcommon.util.ClassUtilities;

public class ObjectCloner implements Cloner {
	public Object deepClone(Object original, Map<Object, Object> cache) throws Exception {
		if (original instanceof Number) {	// Numbers are immutable
			if (original instanceof AtomicInteger) {
				// AtomicIntegers are mutable
			} else if (original instanceof AtomicLong) {
				// AtomLongs are mutable
			} else {
				return original;
			}
		} else if (original instanceof String) {	// Strings are immutable
			return original;
		} else if (original instanceof Character) {	// Characters are immutable
			return original;
		} else if (original instanceof Class) { // Classes are immutable
			return original;
		}
		
		// To our understanding, this is a mutable object, so clone it
		Class<?> c = original.getClass();
		Field[] fields = ClassUtilities.getFields(c, false);
		try {
    		Object copy = instantiate(original);
    		// Put into cache
    		cache.put(original, copy);
    		for (Field f : fields) {
    			Object object = f.get(original);
    			object = CloneUtilities.deepCloneReflectionInternal(object, cache);
    			f.set(copy, object);
    		}
    		return copy;
		} catch(Throwable t) {
			System.err.println("Exception during clone (returning original): " + t.getMessage());
			return original;
		}
	}

	public Object shallowClone(Object original) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (original instanceof Number) {	// Numbers are immutable
			if (original instanceof AtomicInteger) {
				// AtomicIntegers are mutable
			} else if (original instanceof AtomicLong) {
				// AtomLongs are mutable
			} else {
				return original;
			}
		} else if (original instanceof String) {	// Strings are immutable
			return original;
		} else if (original instanceof Character) {	// Characters are immutable
			return original;
		}
		
		// To our understanding, this is a mutable object, so clone it
		Object copy = instantiate(original);
		BeanUtilities.copy(original, copy, BeanUtilities.CopyMode.FIELDS);
		
		return copy;
	}
	
	public static final Object instantiate(Object original) throws InstantiationException, IllegalAccessException {
		return original.getClass().newInstance();
	}
}
