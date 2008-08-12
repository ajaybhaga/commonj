package org.jcommon.xml.bean.handler;

import org.jcommon.xml.bean.PersistenceInstance;
import org.jcommon.xml.bean.XMLHandler;
import org.jdom.Element;

public class ClassXMLHandler implements XMLHandler {
	public Object fromXML(Class<?> c, Element element, PersistenceInstance pi) {
		try {
			return Class.forName(element.getText());
		} catch(ClassNotFoundException exc) {
			throw new RuntimeException("Unable to find Class " + element.getText());
		}
	}

	public boolean isCacheable() {
		return false;
	}

	public void toXML(Element element, Object obj, PersistenceInstance pi) {
		element.setText(((Class<?>)obj).getCanonicalName());
	}
}
