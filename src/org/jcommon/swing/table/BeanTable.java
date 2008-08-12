/*
 * Created on Sep 17, 2004
 */
package org.jcommon.swing.table;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.print.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import java.util.List;

import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.jcommon.io.export.FileExportManager;
import org.jcommon.util.StringUtilities;

/**
 * @author Matt Hicks
 */
public class BeanTable extends JPanel implements TableModelListener, MouseListener, ActionListener, ListSelectionListener {
    protected Class bean;
    protected String[] names;
    protected HashMap methods;
    
    protected JTable table;
    protected BeanTableCell[] cells;
    protected DefaultTableModel model;
    protected JScrollPane scroller;
    protected JPopupMenu popup;
    protected JMenuItem itemNew;
    protected JMenuItem itemRemove;
    protected JMenuItem itemView;
    protected JMenuItem copyCell;
    protected JMenuItem copyRow;
    protected JMenuItem exportTable;
    
    protected List data;
    protected int lastSorted;
    protected boolean editable;
    protected boolean editOptions;
    protected ArrayList listeners;
    
    private boolean removing;
    private boolean sorting;
    private int clickedColumn;
    
    private Font defaultEditorFont = new Font("Arial", Font.PLAIN, 12);
    private Font defaultRendererFont = new Font("Arial", Font.PLAIN, 12);
    
    private int maxHeight;
    
    public BeanTable(Class bean) {
        this(bean, true);
    }
    
    public BeanTable(Class bean, boolean editable) {
        this(bean, null, editable);
    }
    
    public BeanTable(Class bean, String[] names, boolean editable) {
        this(bean, names, editable, editable);
    }
    
    public BeanTable(Class bean, String[] names, boolean editable, boolean editOptions) {
        this.names = names;
        this.bean = bean;
        this.editable = editable;
        this.editOptions = editOptions;
        listeners = new ArrayList();
        clickedColumn = -1;
        data = Collections.synchronizedList(new ArrayList());
        lastSorted = -1;
        popup = new JPopupMenu();
        itemNew = new JMenuItem("New");
        itemNew.setName("New");
        itemNew.addActionListener(this);
        itemRemove = new JMenuItem("Remove");
        itemRemove.setName("Remove");
        itemRemove.addActionListener(this);
        itemView = new JMenuItem("View");
        itemView.setName("View");
	    itemView.addActionListener(this);
        copyCell = new JMenuItem("Copy Cell");
        copyCell.setName("CopyCell");
        copyCell.addActionListener(this);
        copyRow = new JMenuItem("Copy Row");
        copyRow.setName("CopyRow");
        copyRow.addActionListener(this);
        exportTable = new JMenuItem("Export Table");
        exportTable.setName("ExportTable");
        exportTable.addActionListener(this);
        maxHeight = -1;
        parseClass();
        generateTable();
        generateCells();
    }
    
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
    
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if ((maxHeight != -1) && (d.height > maxHeight)) {
            d.height = maxHeight;
        }
        return d;
    }
    
    public void setHeaderFont(Font font) {
        table.getTableHeader().setFont(font);
    }
    
    public Class getBeanClass() {
        return bean;
    }
    
    public Class getClassAt(int column) {
        return (Class)methods.get(names[column]);
    }
    
    public BeanTableCell getTableCell(int column) {
        return cells[column];
    }
    
    public void setDefaultEditorFont(Font defaultEditorFont) {
        this.defaultEditorFont = defaultEditorFont;
    }
    
    public Font getDefaultEditorFont() {
        return defaultEditorFont;
    }
    
    public void setDefaultRendererFont(Font defaultRendererFont) {
        this.defaultRendererFont = defaultRendererFont;
    }
    
    public Font getDefaultRendererFont() {
        return defaultRendererFont;
    }
    
    public BeanTableCell getTableCellByName(String name) {
        for (int i = 0; i < names.length; i++) {
            if (name.equalsIgnoreCase(names[i])) {
                return getTableCell(i);
            }
        }
        return null;
    }
    
    public void setTableCellByName(String name, BeanTableCell cell) {
        for (int i = 0; i < names.length; i++) {
            if (name.equalsIgnoreCase(names[i])) {
                cells[i] = cell;
            }
        }
        refresh();
    }
    
    public void setSelectedRow(int row) {
        table.addRowSelectionInterval(row, row);
    }
    
    public void setSelectedBean(Object o) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) == o) {
                setSelectedRow(i);
                return;
            }
        } 
    }
    
    public int getSelectedRow() {
        return table.getSelectedRow();
    }
    
    public int getSelectedColumn() {
        return table.getSelectedColumn();
    }
    
    public void setSize(int width, int height) {
        Dimension dim = new Dimension(width, height);
        scroller.setPreferredSize(dim);
    }
    
    private void parseClass() {
        methods = new LinkedHashMap();
        Method[] m = bean.getMethods();
        int modifiers;
        ArrayList columns = new ArrayList();
        String name;
        for (int i = 0; i < m.length; i++) {
            name = m[i].getName();
            modifiers = m[i].getModifiers();
            if ((name.startsWith("get")) && (!name.equals("getClass")) && (m[i].getParameterTypes().length == 0) && (!Modifier.isStatic(modifiers))) {
                methods.put(name, m[i]);
                methods.put(name.substring(3), m[i].getReturnType());
                columns.add(name.substring(3));
            } else if (name.startsWith("set")) {
                methods.put(name, m[i]);
            } else if (name.startsWith("avo")) {
                methods.put(name, m[i]);
            } else if ((name.startsWith("is")) && (m[i].getParameterTypes().length == 0) && (!Modifier.isStatic(modifiers))) {
            	methods.put(name, m[i]);
            	methods.put(name.substring(2), m[i].getReturnType());
                columns.add(name.substring(2));
            } else if ((name.startsWith("has")) && (!name.equals("hashCode")) && (m[i].getParameterTypes().length == 0) && (!Modifier.isStatic(modifiers))) {
            	methods.put(name, m[i]);
                methods.put(name.substring(3), m[i].getReturnType());
                columns.add(name.substring(3));
            }
        }
        if (names == null) {
	        names = new String[columns.size()];
	        for (int i = 0; i < columns.size(); i++) {
	            names[i] = (String)columns.get(i);
	        }
        }
    }
    
    private void generateTable() {
        table = new CustomizableTable();
        model = new DefaultTableModel();
        model.addTableModelListener(this);
        table.setModel(model);
        table.getTableHeader().addMouseListener(this);
        table.getTableHeader().setReorderingAllowed(false);		// TODO re-enable after all problems have been worked out
        table.addMouseListener(this);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        
        for (int i = 0; i < names.length; i++) {
            StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < names[i].length(); j++) {
                if (j > 0) {
                    if (Character.isUpperCase(names[i].charAt(j))) {
                        buffer.append(" ");
                    }
                }
                buffer.append(names[i].charAt(j));
            }
            model.addColumn(buffer.toString());
        }
        scroller = new JScrollPane(table);
        scroller.addMouseListener(this);
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, scroller);
    }
    
    public JTable getTable() {
        return table;
    }
    
    public void refresh() {
        for (int i = 0; i < names.length; i++) {
            cells[i].setEditable(editable);
            cells[i].setTable(this);
            table.getColumnModel().getColumn(i).setCellEditor(cells[i]);
            table.getColumnModel().getColumn(i).setCellRenderer(cells[i]);
        }
    }
    
    public void setColumnEditable(int column, boolean editable) {
        cells[column].setEditable(editable);
    }
    
    public void setColumnEditableByName(String name, boolean editable) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase(name)) {
                setColumnEditable(i, editable);
                return;
            }
        }
    }
    
    public void refreshRow(int row) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            refreshColumn(row, i);
        }
    }
    
    public void refreshColumn(int row, int column) {
        Object o = data.get(row);
        if (o.getClass().getName().equals(bean.getName())) {
            Object[] columns = new Object[names.length];
            try {
                columns[column] = ((Method)methods.get("get" + names[column])).invoke(o, new Object[0]);
                //System.out.println("R:" + row + ",C:" + i + ", Value:" + columns[i]);
            } catch(InvocationTargetException e) {
                e.printStackTrace();
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
            model.setValueAt(columns[column], row, column);
            //model.insertRow(row, columns);
            //data.add(row, o);
        } else {
            throw new ClassCastException("Received: " + o.getClass().getName() + ", Expected: " + bean.getName());
        }
    }
    
    private void generateCells() {
        cells = new BeanTableCell[names.length];
        for (int i = 0; i < names.length; i++) {
            BeanTableCell cell = BeanTableCell.createInstance((Class)methods.get(names[i]));
            cell.setEditable(editable);
            cell.setTable(this);
            cells[i] = cell;
            table.getColumnModel().getColumn(i).setCellEditor(cell);
            table.getColumnModel().getColumn(i).setCellRenderer(cell);
        }
    }
    
    public void setColumnWidthByHeaders() {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int charWidth = Math.round(table.getTableHeader().getFont().getSize() * 0.7f);
        if (table.getTableHeader().getFont().isBold()) {
            //charWidth = Math.round(charWidth * 1.2f);
        }
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(table.getModel().getColumnName(i).length() * charWidth);
        }
    }
    
    public void setColumnWidth(int column, int width) {
        table.getColumnModel().getColumn(column).setPreferredWidth(width);
    }
    
    public void setColumnWidthByName(String name, int width) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase(name)) {
                setColumnWidth(i, width);
                return;
            }
        }
    }
    
    public void setColumnHeaderName(int column, String name) {
        table.getTableHeader().getColumnModel().getColumn(column).setHeaderValue(name);
    }
    
    public void setColumnHeaderNameByName(String name, String header) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase(name)) {
                setColumnHeaderName(i, header);
                return;
            }
        }
    }
    
    public void add(Object o) {
        insert(model.getRowCount(), o);
    }
    
    public void hideColumn(int column) {
        TableColumn c = table.getColumnModel().getColumn(column);
        c.setMinWidth(0);
        c.setWidth(0);
        c.setMaxWidth(0);
    }
    
    public void hideColumnByName(String name) {
        for (int i = 0; i < names.length; i++) {
            if (name.equalsIgnoreCase(names[i])) {
                hideColumn(i);
                return;
            }
        }
    }
    
    public void insert(int row, Object o) {
        if (o.getClass().getName().equals(bean.getName())) {
            Object[] columns = new Object[names.length];
            for (int i = 0; i < names.length; i++) {
                try {
                	Method m = (Method)methods.get("get" + names[i]);
                	if (m == null) {
                		m = (Method)methods.get("is" + names[i]);
                	}
                	if (m == null) {
                		m = (Method)methods.get("has" + names[i]);
                	}
                    columns[i] = m.invoke(o, new Object[0]);
                    //System.out.println("R:" + row + ",C:" + i + ", Value:" + columns[i]);
                } catch(InvocationTargetException e) {
                    e.printStackTrace();
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                } catch(NullPointerException e) {
                	System.err.println("Unable to find column: " + names[i]);
                	e.printStackTrace();
                }
            }
            model.insertRow(row, columns);
            data.add(row, o);
            beanAdded(row, -1);
        } else {
            throw new ClassCastException("Received: " + o.getClass().getName() + ", Expected: " + bean.getName());
        }
    }
    
    public void moveTo(int from, int to) {
        model.moveRow(from, from, to);
        Object o = data.get(from);
        if (from < to) {
            data.remove(from);
            data.add(to, o);
        } else {
            data.remove(from);
            data.add(to, o);
        }
    }
    
    public void removeAll() {
        removing = true;
        while (data.size() > 0) {
            remove(0);
        }
        removing = false;
    }
    
    public void remove(int row) {
        removing = true;
        deselect();
        data.remove(row);
        model.removeRow(row);
        removing = false;
        selectClosest(row);
    }
    
    public void selectClosest(int row) {
    	if (model.getRowCount() == 0) return;
    	if (model.getRowCount() > row) {
    		setSelectedRow(row);
    	} else {
    		setSelectedRow(model.getRowCount() - 1);
    	}
    }
    
    public void deselect() {
        if (table.isEditing()) {
            CellEditor ce = table.getCellEditor(table.getSelectedRow(), table.getSelectedColumn());
            if (!ce.stopCellEditing()) {
                ce.cancelCellEditing();
            }
        }
        table.clearSelection();
    }
    
    public void remove(Object o) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals(o)) {
                remove(i);
                break;
            }
        }
    }
    
    public Object getSelectedBean() {
        int row = table.getSelectedRow();
        if (row > -1) {
            return data.get(row);
        }
        return null;
    }
    
    public void removeSelectedBean() {
        remove(getSelectedBean());
    }
    
    public Object getBeanAt(int row) {
        return data.get(row);
    }
    
    public Object[] getBeans() {
        Object[] objects = new Object[data.size()];
        for (int i = 0; i < data.size(); i++) {
            objects[i] = data.get(i);
        }
        return objects;
    }
    
    public int getLength() {
        return data.size();
    }
    
    public boolean hasAvailableValues(int column) {
        return (methods.get("avo" + names[column]) != null);
    }
    
    public Object[] getAvailableValues(int row, int column) {
        if (hasAvailableValues(column)) {
            try {
                return (Object[])((Method)methods.get("avo" + names[column])).invoke(getBeanAt(row), new Object[0]);
            } catch(InvocationTargetException e) {
                e.printStackTrace();
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    protected void swapColumnLocations(int from, int to) {
        String name = names[from];
        names[from] = names[to];
        names[to] = name;
    }
    
    public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
	    if (!this.isEnabled()) return;
	    if (e.getSource() instanceof JTable) {
	        if (e.getButton() == MouseEvent.BUTTON3) {
	            setSelectedRow(table.rowAtPoint(e.getPoint()));
	            clickedColumn = table.columnAtPoint(e.getPoint());
	            popup.removeAll();
	            if ((editable) && (editOptions)) {
	                popup.add(itemNew);
	                popup.add(itemRemove);
	                popup.setPopupSize(90, 100);
	                popup.addSeparator();
	            } else {
	                popup.setPopupSize(90, 60);
	            }
	            popup.add(copyCell);
	            popup.add(copyRow);
	            popup.add(exportTable);
	            //popup.add(itemView);
	            popup.show((JTable)e.getSource(), e.getX(), e.getY());
	        }
	    } else if (e.getSource() instanceof JScrollPane) {
	        if (e.getButton() == MouseEvent.BUTTON3) {
	            if (popup != null) {
	                clickedColumn = -1;
		            popup.removeAll();
		            if ((editable) && (editOptions)) {
		                popup.add(itemNew);
		                popup.setPopupSize(90, 40);
		                popup.addSeparator();
		            } else {
		                popup.setPopupSize(90, 20);
		            }
		            popup.add(exportTable);
		            popup.show((JScrollPane)e.getSource(), e.getX(), e.getY());
	            }
	        }
	    }
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	
	public void mouseClicked(MouseEvent e) {
	    if (e.getSource().getClass() == JTableHeader.class) {
	        JTableHeader header = (JTableHeader)e.getSource();
	        sort(header.columnAtPoint(e.getPoint()));
	    }
	}
    
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
	    int column = table.convertColumnIndexToView(e.getColumn());
	    if ((row > -1) && (column > -1)) {
	        Object o = data.get(row);
	        Object value = table.getValueAt(row, column);
	        Object beanValue = null;
	        try {
	        	Method m = (Method)methods.get("get" + names[column]);
	        	if (m == null) {
	        		m = (Method)methods.get("is" + names[column]);
	        	}
	        	if (m == null) {
	        		m = (Method)methods.get("has" + names[column]);
	        	}
	            beanValue = m.invoke(o, new Object[0]);
	        } catch(InvocationTargetException exc) {
	            exc.printStackTrace();
	        } catch(IllegalAccessException exc) {
	            exc.printStackTrace();
	        }
	        if ((beanValue == null) || (!value.equals(beanValue))) {
		        if (methods.get("set" + names[column]) != null) {
		            try {
		                //System.out.println("Method: " + methods.get("set" + names[column]) + ", Column: " + names[column]);
		                ((Method)methods.get("set" + names[column])).invoke(o, new Object[] {value});
		            } catch(IllegalAccessException exc) {
		                exc.printStackTrace();
		            } catch(InvocationTargetException exc) {
		                exc.printStackTrace();
		            }
		        }
		        beanChanged(row, column);
	        }
	    }
    }
    
    public void actionPerformed(ActionEvent e) {
        Component c = (Component)e.getSource();
        if (c.getName() != null) {
            if (c.getName().equals("New")) {
                try {
                    Constructor constructor = bean.getConstructor(new Class[0]);
                    add(constructor.newInstance(new Object[0]));
                } catch(NoSuchMethodException exc) {
                    exc.printStackTrace();
                } catch(IllegalAccessException exc) {
                    exc.printStackTrace();
                } catch(InvocationTargetException exc) {
                    exc.printStackTrace();
                } catch(InstantiationException exc) {
                    exc.printStackTrace();
                }
            } else if (c.getName().equals("Remove")) {
                beanRemoved(getSelectedRow());
                remove(getSelectedRow());
            } else if (c.getName().equals("CopyCell")) {
                Clipboard clipboard = getToolkit().getSystemClipboard();
                Object o = table.getValueAt(table.getSelectedRow(), clickedColumn);
                if (o != null) {
                    StringSelection data = new StringSelection(o.toString());
                    clipboard.setContents(data, data);
                }
            } else if (c.getName().equals("CopyRow")) {
                Clipboard clipboard = getToolkit().getSystemClipboard();
                Object o;
                StringBuffer tsv = new StringBuffer();
                for (int i = 0; i < table.getColumnCount(); i++) {
                    o = table.getValueAt(table.getSelectedRow(), i);
                    if (i != 0) {
                        tsv.append("\t");
                    }
                    tsv.append("\"");
                    if (o != null) {
                        if (o instanceof Calendar) {
                            tsv.append(StringUtilities.format(((Calendar)o), "%EEE%, %d% %MMM% %yyyy% %hh%:%mm%:%ss%%amPM% %Z%"));
                        } else {
                            tsv.append(StringUtilities.replaceAll(o.toString(), "\"", "\\\""));
                        }
                    }
                    tsv.append("\"");
                }
                StringSelection data = new StringSelection(tsv.toString());
                clipboard.setContents(data, data);
            } else if (c.getName().equals("ExportTable")) {
                try {
	                System.out.println("ExportTable");
	                String[][] data = new String[table.getRowCount()][names.length];
	                Object o;
	                for (int i = 0; i < table.getRowCount(); i++) {
	                    for (int j = 0; j < names.length; j++) {
	                        o = table.getValueAt(i, j);
	                        if (o != null) {
	                            if (o instanceof Calendar) {
	                                data[i][j] = StringUtilities.format(((Calendar)o), "%EEE%, %d% %MMM% %yyyy% %hh%:%mm%:%ss%%amPM% %Z%");
	                            } else {
	                                data[i][j] = o.toString();
	                            }
	                        }
	                    }
	                }
	                String title = this.getName();
	                if (title == null) title = "Table Export";
	                FileExportManager.exportFile(this, title, names, data);
                } catch(Exception exc) {
                    exc.printStackTrace();
                }
            }
        }
    }
    
    public void valueChanged(ListSelectionEvent e) {
        if ((!e.getValueIsAdjusting()) && (getSelectedBean() != null) && (!removing) && (!sorting)) {
	        BeanChangeListener listener;
	        BeanChangeEvent evt = new BeanChangeEvent(this, getSelectedRow(), getSelectedColumn(), this, getBeanAt(getSelectedRow()), BeanChangeEvent.SELECTED);
	        for (int i = 0; i < listeners.size(); i++) {
	            listener = (BeanChangeListener)listeners.get(i);
	            listener.beanChanged(evt);
	        }
        }
    }
    
    public void addBeanChangeListener(BeanChangeListener listener) {
        listeners.add(listener);
    }
    
    public void beanAdded(int row, int column) {
    	BeanChangeListener listener;
        BeanChangeEvent e = new BeanChangeEvent(this, row, column, this, getBeanAt(row), BeanChangeEvent.ADDED);
        for (int i = 0; i < listeners.size(); i++) {
            listener = (BeanChangeListener)listeners.get(i);
            listener.beanChanged(e);
        }
    }
    
    public void beanChanged(int row, int column) {
        BeanChangeListener listener;
        BeanChangeEvent e = new BeanChangeEvent(this, row, column, this, getBeanAt(row), BeanChangeEvent.CHANGED);
        for (int i = 0; i < listeners.size(); i++) {
            listener = (BeanChangeListener)listeners.get(i);
            listener.beanChanged(e);
        }
    }
    
    public void beanRemoved(int row) {
        BeanChangeListener listener;
        BeanChangeEvent e = new BeanChangeEvent(this, row, -1, this, getBeanAt(row), BeanChangeEvent.REMOVED);
        for (int i = 0; i < listeners.size(); i++) {
            listener = (BeanChangeListener)listeners.get(i);
            listener.beanChanged(e);
        }
    }
    
    public void sortByColumnName(String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase(name)) {
                sort(i);
                return;
            }
        }
    }
    
    public void sort(int column) {
        /*BeanTableCell cell;
        for (int i = 0; i < cells.length; i++) {
            cell = cells[i];
            if (!cell.stopCellEditing()) {
                cell.cancelCellEditing();
            }
        }*/
        
        // TODO fix sorting breaking after columns are re-ordered
        //System.out.println("Sorting column: " + column);
        sorting = true;
        ArrayList list = new ArrayList();
        Object value;
        for (int i = 0; i < model.getRowCount(); i++) {
            //System.out.println("Value: " + table.getValueAt(i, column) + ", " + i);
            value = table.getValueAt(i, column);
            if (value != null) {
                list.add(value);
            } else {
                Class c = getClassAt(column);
                if (c == Calendar.class) {
                    value = new GregorianCalendar();
                } else {
	                Constructor constructor;
	                try {
		                try {
		                    constructor = c.getConstructor(new Class[0]);
		                    value = constructor.newInstance(new Object[0]);
		                } catch(Exception e1) {
		                    try {
		                        constructor = c.getConstructor(new Class[] {String.class});
		                    } catch(Exception e2) {
		                        System.err.println("Cannot sort because of null in class " + c.getName());
		                        e2.printStackTrace();
		                        sorting = false;
		                        return;
		                    }
		                    value = constructor.newInstance(new Object[] {"0"});
		                }
	                } catch(Exception e3) {
	                    System.err.println("Cannot sort because of null in class " + c.getName());
	                    e3.printStackTrace();
	                    sorting = false;
	                    return;
	                }
                }
                list.add(value);
            }
        }
        ArrayList sorted = new ArrayList(list);
        Collections.sort(sorted);
        if (lastSorted == column) {
            Collections.reverse(sorted);
            lastSorted = -1;
        } else {
            lastSorted = column;
        }
        int[] placement = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            placement[i] = -1;
        }
        boolean used;
        for (int i = 0; i < sorted.size(); i++) {
            for (int j = 0; j < sorted.size(); j++) {
                if (list.get(i) == sorted.get(j)) {
                    used = false;
                    for (int k = 0; k < placement.length; k++) {
                        if (placement[k] == j) used = true;
                    }
                    if (!used) {
                        placement[i] = j;
                        break;
                    }
                }
            }
        }
        Object[] sortedData = new Object[placement.length]; 
        for (int i = 0; i < placement.length; i++) {
            sortedData[placement[i]] = data.get(i);
            model.removeRow(0);
        }
        data = Collections.synchronizedList(new ArrayList());
        for (int i = 0; i < sortedData.length; i++) {
            add(sortedData[i]);
        }
        sorting = false;
    }
    
    public void print(String title) throws PrinterException {
        MessageFormat header = new MessageFormat(title);
        MessageFormat footer = new MessageFormat("Page {0}");
        HashPrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        pras.add(OrientationRequested.LANDSCAPE);
        MediaPrintableArea mpa = new MediaPrintableArea(0.25f, 0.25f, 8.0f, 10.5f, MediaPrintableArea.INCH);
        pras.add(mpa);
        getTable().print(JTable.PrintMode.FIT_WIDTH, header, footer, true, pras, true);
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        table.setEnabled(enabled);
    }
}
