package org.jcommon.xml.bean.handler;

import org.jcommon.xml.bean.PersistenceInstance;
import org.jcommon.xml.bean.XMLHandler;
import org.jdom.Element;

public class EnumXMLHandler implements XMLHandler {
	@SuppressWarnings("unchecked")
	public Object fromXML(Class<?> c, Element element, PersistenceInstance pi) {
		if (element.getText().length() == 0) {
			return null;
		}
		return Enum.valueOf((Class<Enum>)c, element.getText());
	}

	public void toXML(Element element, Object obj, PersistenceInstance pi) {
		element.setText(((Enum<?>)obj).name());
	}
	
	public boolean isCacheable() {
		return false;
	}
}