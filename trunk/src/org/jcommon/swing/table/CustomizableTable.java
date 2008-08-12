/*
 * Created on Nov 18, 2004
 */
package org.jcommon.swing.table;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

/**
 * @author Matt Hicks
 */
public class CustomizableTable extends JTable {
    private Font defaultEditorFont = new Font("Arial", Font.PLAIN, 10);
    private Font defaultRendererFont = new Font("Arial", Font.PLAIN, 10);
    
    public void setDefaultEditorFont(Font defaultEditorFont) {
        this.defaultEditorFont = defaultEditorFont;
    }
    
    public void setDefaultRendererFont(Font defaultRendererFont) {
        this.defaultRendererFont = defaultRendererFont;
    }
    
    public TableCellEditor getCellEditor(int row, int column) {
        TableCellEditor tce = super.getCellEditor(row, column);
        if (tce instanceof DefaultCellEditor) {
	        DefaultCellEditor editor = (DefaultCellEditor)tce;
	        Component c = editor.getComponent();
	        c.setFont(defaultEditorFont);
	        return editor;
        }
        return tce;
    }
    
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer tcr = super.getCellRenderer(row, column);
        if (tcr instanceof DefaultTableCellRenderer) {
	        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)tcr;
	        renderer.setFont(defaultRendererFont);
	        return renderer;
        }
        return tcr;
    }
}
