package org.jcommon.clone;

import java.util.Map;

public interface Cloner {
	public Object shallowClone(Object original) throws Exception;
	
	public Object deepClone(Object original, Map<Object, Object> cache) throws Exception;
}
