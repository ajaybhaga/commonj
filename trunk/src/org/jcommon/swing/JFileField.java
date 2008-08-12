package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import org.jcommon.swing.layout.*;

public class JFileField extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final int MODE_DEFAULT = 1;
	public static final int MODE_FIELD_ONLY = 2;
	public static final int MODE_BUTTON_ONLY = 3;
	
	private int mode;
	private JTextField field;
	private JButton button;

	public JFileField(int cols) {
		mode = MODE_DEFAULT;
		
		TableLayout layout = new TableLayout(2);
		setLayout(layout);
		//setBorder(BorderFactory.createLineBorder(Color.BLACK));
		field = new JTextField(cols);
		field.setEditable(false);
		add(field);
		button = new JButton("Browse");
		button.addActionListener(this);
		add(button);
	}
	
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}
	
	public boolean isEnabled() {
		return button.isEnabled();
	}
	
	public void setText(String text) {
		field.setText(text);
	}
	
	public String getText() {
		return field.getText();
	}
	
	public void changeMode(int mode) {
		this.mode = mode;
		if (mode == MODE_DEFAULT) {
			field.setVisible(true);
			button.setVisible(true);
		} else if (mode == MODE_FIELD_ONLY) {
			field.setVisible(true);
			button.setVisible(false);
		} else if (mode == MODE_BUTTON_ONLY) {
			field.setVisible(false);
			button.setVisible(true);
		}
	}
	
	public void setEditable(boolean editable) {
		field.setEditable(editable);
	}
	
	public boolean isEditable() {
		return field.isEditable();
	}
	
	public void actionPerformed(ActionEvent evt) {
		try {
			JFileChooser chooser = new JFileChooser(new File(field.getText()));
			if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
				setText(chooser.getSelectedFile().getCanonicalPath());
			}
		} catch(Exception exc) {
			JException.showException(this, exc);
		}
	}
}
