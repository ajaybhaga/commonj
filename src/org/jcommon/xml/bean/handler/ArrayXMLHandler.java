package org.jcommon.xml.bean.handler;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

import org.jcommon.xml.bean.PersistenceInstance;
import org.jcommon.xml.bean.XMLBean;
import org.jcommon.xml.bean.XMLHandler;
import org.jdom.Element;

public class ArrayXMLHandler implements XMLHandler {
	public Object fromXML(Class<?> c, Element element, PersistenceInstance pi) {
		try {
			Object obj = Array.newInstance(c, element.getChildren().size()); {
    			if (element.getAttribute("id") != null) {
	    			// Put into cache
	    			pi.cache(element.getAttributeValue("id"), obj);
    			}
    			
    			// Process children
    			List<?> list = element.getChildren();
    			for (int i = 0; i < list.size(); i++) {
    				Object o = list.get(i);
    				if (o instanceof Element) {
    					Object child = XMLBean.fromXML(null, (Element)o, pi);
    					Array.set(obj, i, child);
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
		String name = obj.getClass().getCanonicalName();
		name = name.substring(0, name.length() - 2);
		element.setAttribute("class", name);
		for (int i = 0; i < Array.getLength(obj); i++) {
			Object o = Array.get(obj, i);
			Element child = XMLBean.toXML(null, o, null, pi, false);
			element.addContent(child);
		}
	}
	
	public boolean isCacheable() {
		return true;
	}
}
