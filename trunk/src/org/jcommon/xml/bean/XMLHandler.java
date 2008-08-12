package org.jcommon.xml.bean;

import org.jdom.Element;

public interface XMLHandler {
	/**
	 * Configuration of creation of Element and "name", "class", and "id" attributes are handled before this
	 * method is invoked.
	 * 
	 * @param element
	 * @param obj
	 */
	public void toXML(Element element, Object obj, PersistenceInstance pi);
	
	/**
	 * Create or load from PersistenceInstance the object reference in the Element.
	 * 
	 * @param c
	 * @param element
	 * @param pi
	 * @return
	 * 		Object
	 */
	public Object fromXML(Class<?> c, Element element, PersistenceInstance pi);
	
	public boolean isCacheable();
}
