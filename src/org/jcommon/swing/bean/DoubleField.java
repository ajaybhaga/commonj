package org.jcommon.swing.bean;

import java.lang.reflect.*;

import org.jcommon.swing.*;

public class DoubleField extends StringField {
	public DoubleField(Object object, Field field) throws IllegalArgumentException, IllegalAccessException {
		super(object, field);
		CustomizablePlainDocument doc = new CustomizablePlainDocument();
		doc.setAllowedCharacters("-0123456789.");
		text.setDocument(doc);
	}
	
	public void updateFromBean() throws IllegalArgumentException, IllegalAccessException {
		text.setText(((Double)field.get(object)).toString());
	}
	
	public void updateToBean() throws IllegalArgumentException, IllegalAccessException {
		field.set(object, Double.parseDouble(text.getText()));
	}
}