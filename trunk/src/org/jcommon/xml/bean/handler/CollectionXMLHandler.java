package org.jcommon.xml.bean.handler;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

import org.jcommon.xml.bean.PersistenceInstance;
import org.jcommon.xml.bean.XMLBean;
import org.jcommon.xml.bean.XMLHandler;
import org.jdom.Element;

public class CollectionXMLHandler implements XMLHandler {
	@SuppressWarnings("unchecked")
	public Object fromXML(Class<?> c, Element element, PersistenceInstance pi) {
		try {
			Constructor<?> constructor = c.getConstructor(new Class[0]);
			constructor.setAccessible(true);
    		Object obj = constructor.newInstance(); {
    			if (element.getAttribute("id") != null) {
	    			// Put into cache
	    			pi.cache(element.getAttributeValue("id"), obj);
    			}
    			
    			// Process children
    			List<?> list = element.getChildren();
    			for (Object o : list) {
    				if (o instanceof Element) {
    					Object child = XMLBean.fromXML(null, (Element)o, pi);
    					((Collection<Object>)obj).add(child);
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
		for (Object o : (Collection<?>)obj) {
			Element child = XMLBean.toXML(null, o, null, pi, false);
			element.addContent(child);
		}
	}

	public boolean isCacheable() {
		return true;
	}
}