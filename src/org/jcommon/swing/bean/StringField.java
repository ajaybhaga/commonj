package org.jcommon.swing.bean;

import java.awt.event.*;
import java.lang.reflect.*;

import javax.swing.*;

public class StringField extends GraphicalField implements KeyListener {
	protected JTextField text;
	
	public StringField(Object object, Field field) throws IllegalArgumentException, IllegalAccessException {
		super(new JTextField(25), object, field);
		text = (JTextField)getComponent();
		updateFromBean();
		text.addKeyListener(this);
	}

	public void updateFromBean() throws IllegalArgumentException, IllegalAccessException {
		text.setText((String)field.get(object));
	}
	
	public void updateToBean() throws IllegalArgumentException, IllegalAccessException {
		field.set(object, text.getText());
	}
	
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		try {
			updateToBean();
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}

	public void keyTyped(KeyEvent e) {
	}
}