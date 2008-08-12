package org.jcommon.xml.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdom.Element;

public class PersistenceInstance {
	private Map<Object, Element> cachedElements;
	private Map<String, Object> cachedObjects;
	private AtomicInteger ids;
	
	public PersistenceInstance() {
		cachedElements = new HashMap<Object, Element>();
		cachedObjects = new HashMap<String, Object>();
		ids = new AtomicInteger(0);
	}
	
	public boolean isCached(Object obj) {
		return cachedElements.containsKey(obj);
	}
	
	public boolean isCached(String id) {
		return cachedObjects.containsKey(id);
	}
	
	public void cache(Object obj, Element element) {
		cachedElements.put(obj, element);
	}
	
	public void cache(String id, Object obj) {
		cachedObjects.put(id, obj);
	}
	
	public int getCachedId(Object obj) {
		Element element = cachedElements.get(obj);
		if (element.getAttributeValue("id") == null) {
			element.setAttribute("id", String.valueOf(nextId()));
		}
		return Integer.parseInt(element.getAttributeValue("id"));
	}
	
	public Object getCachedById(String id) {
		return cachedObjects.get(id);
	}
	
	private int nextId() {
		return ids.addAndGet(1);
	}
}
