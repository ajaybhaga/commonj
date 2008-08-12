/*
 * Created on Sep 20, 2004
 */
package org.jcommon.swing.table;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * @author Matt Hicks
 */
public class BooleanBeanTableCell extends BeanTableCell {
    public Component getNewRenderer(Object value) {
        TableCheckBox renderer = new TableCheckBox();
        renderer.setSelected(((Boolean)value).booleanValue());
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        return renderer;
    }

    public Component getNewEditor(Object value) {
        TableCheckBox editor = new TableCheckBox(getTable());
        editor.setSelected(((Boolean)value).booleanValue());
        editor.setHorizontalAlignment(SwingConstants.CENTER);
        editor.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ((JCheckBox)getRenderer()).setSelected(((JCheckBox)getEditor()).isSelected());
                stopCellEditing();
                //getTable().cellUpdated(row, column);
            }
        });
        return editor;
    }

    public Object getCellEditorValue() {
        return new Boolean(((TableCheckBox)getEditor()).isSelected());
    }

    public boolean shouldSelectCell(EventObject e) {
        return false;
    }
}
