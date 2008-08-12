package org.jcommon.swing.bean;

import java.lang.reflect.*;

import org.jcommon.swing.*;

public class LongField extends StringField {
	public LongField(Object object, Field field) throws IllegalArgumentException, IllegalAccessException {
		super(object, field);
		CustomizablePlainDocument doc = new CustomizablePlainDocument();
		doc.setAllowedCharacters("-0123456789");
		text.setDocument(doc);
		updateFromBean();
	}
	
	public void updateFromBean() throws IllegalArgumentException, IllegalAccessException {
		long l = (Long)field.get(object);
		text.setText(String.valueOf(l));
	}
	
	public void updateToBean() throws IllegalArgumentException, IllegalAccessException {
		field.set(object, Long.parseLong(text.getText()));
	}
}