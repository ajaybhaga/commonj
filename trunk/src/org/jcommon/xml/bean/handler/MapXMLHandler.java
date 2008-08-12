package org.jcommon.xml.bean.handler;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcommon.xml.bean.PersistenceInstance;
import org.jcommon.xml.bean.XMLBean;
import org.jcommon.xml.bean.XMLHandler;
import org.jdom.Element;

/**
 * @author mhicks
 */
public class MapXMLHandler implements XMLHandler {
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
    			
    			List<?> list = element.getChildren("entry");
    			for (Object o : list) {
    				if (o instanceof Element) {
    					Element child = (Element)o;
    					
        				Element keyElement = child.getChild("key");
        				Element valueElement = child.getChild("value");
				
        				Object key = XMLBean.fromXML(null, (Element)keyElement.getChildren().get(0), pi);
        				Object value = XMLBean.fromXML(null, (Element)valueElement.getChildren().get(0), pi);
        				((Map<Object, Object>)obj).put(key, value);
    				}
    			}
    		}
    		
    		return obj;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

	public boolean isCacheable() {
		return true;
	}

	@SuppressWarnings("unchecked")
	public void toXML(Element element, Object obj, PersistenceInstance pi) {
		for (Entry<Object, Object> entry : ((Map<Object, Object>)obj).entrySet()) {
			Element entryElement = new Element("entry"); {
    			Element keyElement = new Element("key"); {
    				Element key = XMLBean.toXML(null, entry.getKey(), null, pi, false);
    				keyElement.addContent(key);
    				
    				entryElement.addContent(keyElement);
    			}
    			Element valueElement = new Element("value"); {
    				Element value = XMLBean.toXML(null, entry.getValue(), null, pi, false);
    				valueElement.addContent(value);
    				
    				entryElement.addContent(valueElement);
    			}
    			element.addContent(entryElement);
			}
		}
	}
}
