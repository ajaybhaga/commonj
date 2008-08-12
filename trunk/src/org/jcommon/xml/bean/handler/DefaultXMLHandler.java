package org.jcommon.xml.bean.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.jcommon.util.ClassUtilities;
import org.jcommon.xml.bean.PersistenceInstance;
import org.jcommon.xml.bean.XMLBean;
import org.jcommon.xml.bean.XMLHandler;
import org.jdom.Element;

public class DefaultXMLHandler implements XMLHandler {
	public Object fromXML(Class<?> c, Element element, PersistenceInstance pi) {
		try {
			Field[] children = ClassUtilities.getFields(c, false, false);
			if ((children.length > 0) && (element.getChildren().size() == 0)) {
				// Object is actually null
				return null;
			}
			
			Constructor<?> constructor = c.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
    		Object obj = constructor.newInstance(); {
    			if (element.getAttribute("id") != null) {
	    			// Put into cache
	    			pi.cache(element.getAttributeValue("id"), obj);
    			}
    			
    			// Process children
    			for (Field f : children) {
    				try {
    					Element childElement = element.getChild(f.getName());
    					if (childElement == null) {
    						throw new RuntimeException("Unable to find XML for: " + f.getName());
    					}
    					Object child = XMLBean.fromXML(f.getType(), childElement, pi);
    					if (child != null) {
    						f.set(obj, child);
    					}
    				} catch(Exception exc) {
    					exc.printStackTrace();
    				}
    			}
    		}
    		
    		return obj;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

	public void toXML(Element element, Object obj, PersistenceInstance pi) {
		// Process children
		Field[] children = ClassUtilities.getFields(obj, false, false);
		for (Field f : children) {
			try {
				Element child = XMLBean.toXML(f.getName(), f.get(obj), f.getType(), pi, false);
				element.addContent(child);
			} catch(Exception exc) {
				exc.printStackTrace();
			}
		}
	}
	
	public boolean isCacheable() {
		return true;
	}
}
