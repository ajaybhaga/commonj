/*
 * Created on Sep 17, 2004
 */
package org.jcommon.swing.table;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * @author Matt Hicks
 */
public abstract class BeanTableCell implements TableCellRenderer, TableCellEditor {
    //public static final Color ALTERNATE_COLOR = new Color(0.9f, 0.9f, 0.9f);
    public static final Color ALTERNATE_COLOR = new Color(238, 238, 255);
    
    private static HashMap mappings;
    static {
        mappings = new HashMap();
        mappings.put("Default", DefaultBeanTableCell.class);
        mappings.put(Integer.class, NumberBeanTableCell.class);
        mappings.put(Long.class, NumberBeanTableCell.class);
        mappings.put(Float.class, NumberBeanTableCell.class);
        mappings.put(Double.class, NumberBeanTableCell.class);
        mappings.put(Boolean.class, BooleanBeanTableCell.class);
        mappings.put(org.jcommon.lang.Currency.class, NumberBeanTableCell.class);
        mappings.put(Calendar.class, CalendarBeanTableCell.class);
        mappings.put(GregorianCalendar.class, CalendarBeanTableCell.class);
    }
    
    private Component renderer;
    private Component editor;
    private Class type;
    private BeanTable table;
    private Object[] avo;
    protected HashSet listeners;
    protected boolean editable;
    
    public BeanTableCell() {
        listeners = new HashSet();
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    protected void setType(Class type) {
        this.type = determineClassByClass(type);
    }
    
    public Class getType() {
        return type;
    }
    
    public void setTable(BeanTable table) {
        this.table = table;
    }
    
    public BeanTable getTable() {
        return table;
    }
    
    public void setAVO(Object[] avo) {
        this.avo = avo;
    }
    
    public Object[] getAVO() {
        return avo;
    }
    
    public void setRenderer(Component renderer) {
        this.renderer = renderer;
    }
    
    public Component getRenderer() {
        return renderer;
    }
    
    public void setEditor(Component editor) {
        this.editor = editor;
    }
    
    public Component getEditor() {
        return editor;
    }
    
    public abstract Component getNewRenderer(Object value);
    
    public abstract Component getNewEditor(Object value);
    
    public Component getTableCellRendererComponent(JTable table, Object o, boolean isSelected, boolean hasFocus, int row, int column) {
        setRenderer(getNewRenderer(o));
        if (isSelected) {
            getRenderer().setForeground(table.getSelectionForeground());
            getRenderer().setBackground(table.getSelectionBackground());
        } else if (row % 2 != 0) {
            getRenderer().setBackground(BeanTableCell.ALTERNATE_COLOR);
        } else {
            getRenderer().setBackground(Color.WHITE);
        }
        return getRenderer();
    }
    
    public Component getTableCellEditorComponent(JTable table, Object o, boolean isSelected, int row, int column) {
        if (getTable().hasAvailableValues(column)) {
            Object[] av = getTable().getAvailableValues(row, column);
            setAVO(av);
        }
        setEditor(getNewEditor(o));
        if (row % 2 != 0) {
            getEditor().setBackground(BeanTableCell.ALTERNATE_COLOR);
        } else {
            getEditor().setBackground(Color.WHITE);
        }
        return getEditor();
    }

    public abstract Object getCellEditorValue();

    public boolean isCellEditable(EventObject e) {
        return editable;
    }

    public boolean shouldSelectCell(EventObject e) {
        return true;
    }
    
    public boolean stopCellEditing() {
        if (getCellEditorValue() != null) {
	        CellEditorListener listener;
	        Iterator iterator = listeners.iterator();
	        while (iterator.hasNext()) {
	            listener = (CellEditorListener)iterator.next();
	            listener.editingStopped(new ChangeEvent(getEditor()));
	        }
	        editingStopped();
	        return true;
        }
        return false;
    }

    public void cancelCellEditing() {
        CellEditorListener listener;
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
            listener = (CellEditorListener)iterator.next();
            listener.editingCanceled(new ChangeEvent(getEditor()));
        }
        editingCancelled();
    }

    public void editingStopped() {
        // Intended for override
    }
    
    public void editingCancelled() {
        // Intended for override
    }
    
    public void addCellEditorListener(CellEditorListener l) {
        listeners.add(l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
        listeners.remove(l);
    }
    
    public static void addMapping(Class type, Class cell) {
        mappings.put(type, cell);
    }
    
    public static void setDefaultMapping(Class cell) {
        mappings.put("Default", cell);
    }
    
    public static BeanTableCell createInstance(Class type) {
        type = determineClassByClass(type);
        Class cellClass;
        if (mappings.get(type) != null) {
            cellClass = (Class)mappings.get(type);
        } else {
            cellClass = (Class)mappings.get("Default");
        }
        try {
            BeanTableCell cell = (BeanTableCell)cellClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            cell.setType(type);
            return cell;
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(InstantiationException e) {
            e.printStackTrace();
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static final Class determineClassByClass(Class type) {
        if (type == boolean.class) {
            return Boolean.class;
        } else if (type == int.class) {
            return Integer.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == double.class) {
            return Double.class;
        }
        return type;
    }
}
