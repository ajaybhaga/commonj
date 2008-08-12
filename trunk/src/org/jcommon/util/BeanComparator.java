package org.jcommon.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BeanComparator<T> implements Comparator<T> {
	private Class<? extends T> c;
	private List<Field> fields;
	private List<Boolean> directions;
	
	public BeanComparator(Class<? extends T> c) {
		this.c = c;
		fields = new ArrayList<Field>();
		directions = new ArrayList<Boolean>();
	}
	
	@SuppressWarnings("all")
	public BeanComparator(Field f) {
		this((Class<? extends T>)f.getDeclaringClass());
		
		addSort(f.getName(), true);
	}
	
	public BeanComparator(Class<? extends T> c, String ... fieldNames) {
		this(c);
		for (String s : fieldNames) {
			addSort(s, true);
		}
	}
	
	/**
	 * Adds a field to sort on. Direction true equals ascending, false
	 * equals descending.
	 * 
	 * @param fieldName
	 * @param direction
	 */
	public void addSort(String fieldName, boolean direction) {
		Field field = ClassUtilities.getField(c, fieldName, false);
		if (field == null) {
			throw new RuntimeException("Cannot find field: " + fieldName + " in " + c);
		}
		fields.add(field);
		directions.add(direction);
	}
	
	@SuppressWarnings("all")
	public int compare(Object o1, Object o2) {
		if (o1 == null) return -1;
		else if (o2 == null) return 1;
		try {
			int value = 0;
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				boolean direction = directions.get(i);
				
				Object v1 = field.get(o1);
				Object v2 = field.get(o2);
				if ((v1 == null) && (v2 == null)) {
					// Equal
				} else if (v1 == null) {
					return fix(-1, direction);
				} else if (v2 == null) {
					return fix(1, direction);
				} else if ((v1 instanceof Comparable) && (v2 instanceof Comparable)) {
					value = fix(((Comparable)v1).compareTo(v2), direction);
				} else {
					value = fix(String.valueOf(v1).compareTo(String.valueOf(v2)), direction);
				}
				if (value != 0) return value;
			}
		} catch(Exception exc) {
			exc.printStackTrace();
		}
		return 0;
	}
	
	private static final int fix(int value, boolean direction) {
		if (!direction) {
			if (value == -1) {
				return 1;
			} else if (value == 1) {
				return -1;
			}
		}
		return value;
	}
}