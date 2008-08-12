package org.jcommon.xml.bean.handler;

import org.jcommon.xml.bean.PersistenceInstance;
import org.jcommon.xml.bean.XMLHandler;
import org.jdom.Element;

public class StringXMLHandler implements XMLHandler {
	public Object fromXML(Class<?> c, Element element, PersistenceInstance pi) {
		return element.getText();
	}

	public void toXML(Element element, Object obj, PersistenceInstance pi) {
		element.setText((String)obj);
	}

	public boolean isCacheable() {
		return false;
	}
}
