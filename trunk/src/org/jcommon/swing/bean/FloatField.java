package org.jcommon.swing.bean;

import java.lang.reflect.*;

import org.jcommon.swing.*;

public class FloatField extends StringField {
	public FloatField(Object object, Field field) throws IllegalArgumentException, IllegalAccessException {
		super(object, field);
		CustomizablePlainDocument doc = new CustomizablePlainDocument();
		doc.setAllowedCharacters("-0123456789.");
		text.setDocument(doc);
	}
	
	public void updateFromBean() throws IllegalArgumentException, IllegalAccessException {
		text.setText(((Float)field.get(object)).toString());
	}
	
	public void updateToBean() throws IllegalArgumentException, IllegalAccessException {
		field.set(object, Float.parseFloat(text.getText()));
	}
}
