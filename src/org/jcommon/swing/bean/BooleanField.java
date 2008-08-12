package org.jcommon.swing.bean;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;

public class BooleanField extends GraphicalField implements ActionListener {
	private JCheckBox checkBox;
	
	public BooleanField(Object object, Field field) throws IllegalArgumentException, IllegalAccessException {
		super(new JCheckBox(), object, field);
		checkBox = (JCheckBox)getComponent();
		updateFromBean();
		checkBox.addActionListener(this);
	}
	

	public void updateFromBean() throws IllegalArgumentException, IllegalAccessException {
		checkBox.setSelected((Boolean)field.get(object));
	}

	public void updateToBean() throws IllegalArgumentException, IllegalAccessException {
		field.set(object, checkBox.isSelected());
	}



	public void actionPerformed(ActionEvent e) {
		try {
			updateToBean();
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}

}
