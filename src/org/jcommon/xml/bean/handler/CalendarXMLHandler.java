package org.jcommon.xml.bean.handler;

import java.util.Calendar;

import org.jcommon.xml.bean.PersistenceInstance;
import org.jcommon.xml.bean.XMLHandler;
import org.jdom.Element;

public class CalendarXMLHandler implements XMLHandler {
	public Object fromXML(Class<?> c, Element element, PersistenceInstance pi) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.parseLong(element.getText()));
		if (element.getAttribute("id") != null) {
			// Put into cache
			pi.cache(element.getAttributeValue("id"), cal);
		}
		return cal;
	}

	public void toXML(Element element, Object obj, PersistenceInstance pi) {
		element.setText(String.valueOf(((Calendar)obj).getTimeInMillis()));
	}

	public boolean isCacheable() {
		return true;
	}
}
