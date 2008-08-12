/*
 * Created on Sep 17, 2004
 */
package org.jcommon.swing.table;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

import javax.swing.*;

import org.jcommon.swing.FormattedLabel;
import org.jcommon.swing.JOptionField;

/**
 * @author Matt Hicks
 */
public class DefaultBeanTableCell extends BeanTableCell {
    public Component getNewRenderer(Object value) {
        FormattedLabel renderer = new FormattedLabel(FormattedLabel.TABLE_CELL_TYPE, FormattedLabel.TEXT_FORMAT);
        renderer.setFont(getTable().getDefaultRendererFont());
        if (value != null) {
            renderer.setText(value.toString());
        } else {
            renderer.setText("");
        }
        return renderer;
    }

    public Component getNewEditor(Object value) {
        if (getAVO() != null) {
            JOptionField editor = new JOptionField(getAVO(), value, false);
            editor.setFont(getTable().getDefaultEditorFont());
            editor.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    stopCellEditing();
                }
            });
            return editor;
        } else {
	        JTextField editor = new JTextField();
	        editor.setFont(getTable().getDefaultEditorFont());
	        if (value != null) {
	            editor.setText(value.toString());
	        } else {
	            editor.setText("");
	        }
	        return editor;
        }
    }

    /**
     * Attempts to create an instance of the Object requested in the Bean
     * in a Constructor that accepts the editor value as a String.  If no
     * Contructor may be found, the method returns the String value.
     */
    public Object getCellEditorValue() {
        String value;
        if (getAVO() != null) {
            return ((JOptionField)getEditor()).getSelectedObject();
        } else {
            value = ((JTextField)getEditor()).getText();
        }
        try {
            Constructor c = getType().getConstructor(new Class[] {String.class});
            return c.newInstance(new Object[] {value});
        } catch(NoSuchMethodException e) {
            return value;
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
