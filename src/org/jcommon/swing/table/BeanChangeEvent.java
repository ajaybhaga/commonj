/*
 * Created on Oct 15, 2004
 */
package org.jcommon.swing.table;

import java.util.*;

/**
 * @author Matt Hicks
 */
public class BeanChangeEvent extends EventObject {
    public static final int CHANGED = 1;
    public static final int REMOVED = 2;
    public static final int SELECTED = 3;
    public static final int ADDED = 4;
    
    private int row;
    private int column;
    private BeanTable table;
    private Object bean;
    private int type;
    
    public BeanChangeEvent(Object source, int row, int column, BeanTable table, Object bean, int type) {
        super(source);
        this.row = row;
        this.column = column;
        this.table = table;
        this.bean = bean;
        this.type = type;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getColumn() {
        return column;
    }
    
    public BeanTable getBeanTable() {
        return table;
    }

    public int getType() {
        return type;
    }

    public Object getBean() {
    	return bean;
    }
}
