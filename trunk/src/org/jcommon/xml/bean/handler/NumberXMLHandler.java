package org.jcommon.xml.bean.handler;

import org.jcommon.util.ClassUtilities;
import org.jcommon.xml.bean.PersistenceInstance;
import org.jcommon.xml.bean.XMLHandler;
import org.jdom.Element;

public class NumberXMLHandler implements XMLHandler {
	public Object fromXML(Class<?> c, Element element, PersistenceInstance pi) {
		if (element.getText().length() == 0) {
			return null;
		}
		
		c = ClassUtilities.updateClass(c);
		String n1 = element.getName();
		String n2 = element.getAttributeValue("name");
		if (n2 == null) {
			n2 = "";
		}
		if ((c == Boolean.class) || (n1.equals("boolean")) || (n2.equals("boolean"))) {
			return Boolean.parseBoolean(element.getText());
		} else if ((c == Byte.class) || (n1.equals("byte")) || (n2.equals("byte"))) {
			return Byte.parseByte(element.getText());
		} else if ((c == Short.class) || (n1.equals("short")) || (n2.equals("short"))) {
			return Short.parseShort(element.getText());
		} else if ((c == Integer.class) || (n1.equals("integer")) || (n2.equals("int"))) {
			return Integer.parseInt(element.getText());
		} else if ((c == Float.class) || (n1.equals("float")) || (n2.equals("float"))) {
			return Float.parseFloat(element.getText());
		} else if ((c == Long.class) || (n1.equals("long")) || (n2.equals("long"))) {
			return Long.parseLong(element.getText());
		} else if ((c == Double.class) || (n1.equals("double")) || (n2.equals("double"))) {
			return Double.parseDouble(element.getText());
		}
		throw new RuntimeException("Unable to process: " + c + ", " + element.getName() + ", " + element.getText());
	}

	public void toXML(Element element, Object obj, PersistenceInstance pi) {
		element.setText(String.valueOf(obj));
	}

	public boolean isCacheable() {
		return false;
	}
}