package org.jcommon.swing.bean;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;

import org.jcommon.swing.*;
import org.jcommon.swing.layout.*;
import org.jcommon.util.StringUtilities;

public class SwingBean<E> extends JPanel {
	private static final long serialVersionUID = 1L;

	private E bean;
	private ArrayList<Field> fields;
	private HashMap<Field, GraphicalField> mapping;
	
	public SwingBean(E bean) {
		this.bean = bean;
		
		mapping = new HashMap<Field, GraphicalField>();
		
		generate();
	}
	
	private void generate() {
		// We must introspect the fields of this bean
		Class c = bean.getClass();
		fields = new ArrayList<Field>();
		while (c != Object.class) {
			for (Field f : c.getDeclaredFields()) {
				if ((!Modifier.isTransient(f.getModifiers())) && (!Modifier.isStatic(f.getModifiers()))) {
					f.setAccessible(true);
					fields.add(f);
				}
			}
			c = c.getSuperclass();
		}
		
		// Configure the layout
		//BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		//setLayout(layout);
		TableLayout layout = new TableLayout(2);
		//layout.setFill(false);
		layout.setHorizontalAlignment(TableLayout.ALIGN_CENTER);
		layout.setDefaultCellMargin(new Insets(2, 2, 5, 2));
		setLayout(layout);
		
		// Create components per field
		for (Field f : fields) {
			JLabel label = new JLabel(StringUtilities.phrasify(f.getName()) + ": ");
			label.setHorizontalAlignment(JLabel.RIGHT);
			add(label);
			layout.getCell(label).setHorizontalAlignment(TableLayout.ALIGN_RIGHT);
			GraphicalField gf = createGraphicalField(f);
			mapping.put(f, gf);
			add(gf.getComponent());
		}
	}
	
	private GraphicalField createGraphicalField(final Field f) {
		// TODO configure mappings from field to component
		try {
			if (f.getType() == String.class) {
				return new StringField(getBean(), f);
			} else if ((f.getType() == Integer.class) || (f.getType() == int.class)) {
				return new IntegerField(getBean(), f);
			} else if ((f.getType() == Long.class) || (f.getType() == long.class)) {
				return new LongField(getBean(), f);
			} else if ((f.getType() == Float.class) || (f.getType() == float.class)) {
				return new FloatField(getBean(), f);
			} else if ((f.getType() == Double.class) || (f.getType() == double.class)) {
				return new DoubleField(getBean(), f);
			} else if ((f.getType() == Boolean.class) || (f.getType() == boolean.class)) {
				return new BooleanField(getBean(), f);
			} else {
				System.out.println("Unknown type: " + f.getName());
				return null;
			}
		} catch(Exception exc) {
			JException.showException(this, exc);
			throw new RuntimeException(exc);
		}
	}
	
	public E getBean() {
		return bean;
	}
	
	public void applyValues(HashMap<String,Object> map) throws IllegalArgumentException, IllegalAccessException {
		for (String key : map.keySet()) {
			Object value = map.get(key);
			Field f = getField(key);
			f.set(getBean(), value);
			((JTextField)mapping.get(f).getComponent()).setText(String.valueOf(value));
			mapping.get(f).updateToBean();
		}
	}
	
	public HashMap<String, Object> getValues() throws IllegalArgumentException, IllegalAccessException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		for (String fieldName : getFieldNames()) {
			values.put(fieldName, getField(fieldName).get(getBean()));
		}
		return values;
	}
	
	public String[] getFieldNames() {
		String[] names = new String[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			names[i] = fields.get(i).getName().toUpperCase();
		}
		return names;
	}
	
	public Object[] getFieldValues() throws IllegalArgumentException, IllegalAccessException {
		Object[] values = new Object[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			values[i] = fields.get(i).get(getBean());
		}
		return values;
	}
	
	private Field getField(String name) {
		for (Field field : fields) {
			if (field.getName().equalsIgnoreCase(name)) return field;
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		SwingBean sb = new SwingBean(new TestBean());
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(BorderLayout.CENTER, sb);
		frame.pack();
		frame.setVisible(true);
	}
}

class TestBean {
	private String one;
	private String two;
	private String three;
}
