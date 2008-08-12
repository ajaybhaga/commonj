package org.jcommon.util;

import java.lang.reflect.*;
import java.util.*;

public class BeanUtilities {
	public static enum Type {
		PHRASE,
		ALL_WORDS,
		ANY_WORD,
		REGEXP,
		EXACT
	}
	
	public enum CopyMode {
		FIELDS,
		METHODS
	}
	
	private static final Map<Class<?>, Field[]> fieldMap = new HashMap<Class<?>, Field[]>();
	
	public static final <T> T searchFirst(List<T> beans, String query, Type type, boolean caseSensitive, String ... fieldNames) {
		List<T> list = search(beans, query, type, caseSensitive, 1, fieldNames);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public static final <T> List<T> search(List<T> beans, String query, Type type) {
		return search(beans, query, type, false, -1);
	}
	
	public static final <T> List<T> search(List<T> beans, String query, Type type, boolean caseSensitive, int maxResults, String ... fieldNames) {
		List<T> results = new ArrayList<T>();
		if (beans.size() == 0) return results;
		
		Field[] allFields = fieldMap.get(beans.get(0).getClass());
		if (allFields == null) {
			allFields = ClassUtilities.getFields(beans.get(0), false);
			fieldMap.put(beans.get(0).getClass(), allFields);
		}
		
		// Create our list of fields to use
		Field[] fields;
		if ((fieldNames == null) || (fieldNames.length == 0)) {
			fields = allFields;
		} else {
			fields = new Field[fieldNames.length];
			for (int i = 0; i < fields.length; i++) {
				for (Field f : allFields) {
					if (f.getName().equals(fieldNames[i])) {
						fields[i] = f;
					}
				}
				if (fields[i] == null) throw new NoSuchFieldError("Cannot find field by name " + fieldNames[i] + " in " + beans.get(0).getClass().getName());
			}
		}
		
		for (T t : beans) {
			if (isMatch(t, query, type, caseSensitive, fields)) {
				results.add(t);
			}
			if ((maxResults != -1) && (results.size() >= maxResults)) {
				break;
			}
		}
		
		return results;
	}
	
	public static final <T> boolean isMatch(T t, String query, Type type, boolean caseSensitive, Field ... fields) {
		String queryLowercase = null;
		if (query != null) {
			queryLowercase = query.toLowerCase();
		}
		
		if (type == Type.PHRASE) {
			for (Field f : fields) {
				String s = getString(t, f, caseSensitive);
				if (s != null) {
					if ((caseSensitive) && (s.indexOf(query) != -1)) {
						return true;
					} else if ((!caseSensitive) && (s.indexOf(queryLowercase) != -1)) {
						return true;
					}
				}
			}
		} else if (type == Type.REGEXP) {
			for (Field f : fields) {
				String s = getString(t, f, caseSensitive);
				if ((s != null) && (s.matches(query))) {
					return true;
				}
			}
		} else if (type == Type.ANY_WORD) {
			String[] words = getWords(query.toLowerCase(), caseSensitive);
			for (String word : words) {
				for (Field f : fields) {
					String s = getString(t, f, caseSensitive);
					if (s != null) {
						if (s.indexOf(word) != -1) {
							if (word.startsWith("-")) {
								return false;	// Exclusion match
							}
							return true;
						}
					}
				}
			}
		} else if (type == Type.ALL_WORDS) {
			String[] words = getWords(query.toLowerCase(), caseSensitive);
			for (String word : words) {
				boolean matched = false;
				for (Field f : fields) {
					String s = getString(t, f, caseSensitive);
					if (s != null) {
						if (s.indexOf(word) != -1) {
							if (word.startsWith("-")) {
								return false;	// Exclusion match
							}
							matched = true;
						}
					}
				}
				if (!matched) return false;
			}
			return true;
		} else if (type == Type.EXACT) {
			for (Field f : fields) {
				String s = getString(t, f, caseSensitive);
				if (s != null) {
					if ((caseSensitive) && (s.equals(query))) {
						return true;
					} else if ((!caseSensitive) && (s.equals(queryLowercase))) {
						return true;
					}
				} else if (query == null) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static final String[] getWords(String query, boolean caseSensitive) {
		StringBuffer buffer = new StringBuffer();
		List<String> list = new ArrayList<String>();
		char c;
		boolean quoted = false;
		for (int i = 0; i < query.length(); i++) {
			c = query.charAt(i);
			if (quoted) {
				if (c == '"') {
					quoted = false;
				} else {
					buffer.append(c);
				}
			} else if (c == '"') {
				quoted = true;
			} else if (c == ' ') {
				if (buffer.length() > 0) {
					list.add(caseSensitive ? buffer.toString() : buffer.toString().toLowerCase());
					buffer.delete(0, buffer.length());
				}
			} else {
				buffer.append(c);
			}
		}
		if (buffer.length() > 0) {
			list.add(caseSensitive ? buffer.toString() : buffer.toString().toLowerCase());
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static final String getString(Object object, Field field, boolean caseSensitive) {
		try {
			field.setAccessible(true);
			String s = StringUtilities.toString(field.get(object));
			if ((s != null) && (!caseSensitive)) {
				s = s.toLowerCase();
			}
			return s;
		} catch(Exception exc) {
		}
		return null;
	}

	public static final void sort(List<? extends Object> list, String field) {
		sort(list, field, true);
	}
	
	public static final void sort(List<? extends Object> list, String field, boolean ascending) {
		if (list.size() == 0) return;
		Field f = ClassUtilities.getField(list.get(0).getClass(), field);
		f.setAccessible(true);
		BeanComparator<Object> c = new BeanComparator<Object>(f);
		Collections.sort(list, c);
		
		if (!ascending) {
			Collections.reverse(list);
		}
	}
	
	public static final void replaceAll(Object object, Object value, Object replacement, String ... fieldNames) {
		Field[] allFields = fieldMap.get(object.getClass());
		if (allFields == null) {
			allFields = ClassUtilities.getFields(object, false);
			fieldMap.put(object.getClass(), allFields);
		}
		
		// Create our list of fields to use
		Field[] fields;
		if ((fieldNames == null) || (fieldNames.length == 0)) {
			fields = allFields;
		} else {
			fields = new Field[fieldNames.length];
			for (int i = 0; i < fields.length; i++) {
				for (Field f : allFields) {
					if (f.getName().equals(fieldNames[i])) {
						fields[i] = f;
					}
				}
				if (fields[i] == null) throw new NoSuchFieldError("Cannot find field by name " + fieldNames[i] + " in " + object.getClass().getName());
			}
		}
		
		for (Field f : fields) {
			try {
				Object v = f.get(object);
				boolean match = false;
				if (v == value) {
					match = true;
				} else if ((v != null) && (v.equals(value))) {
					match = true;
				}
				if (match) {
					f.set(object, replacement);
				}
			} catch(Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	public static final String toValuesHeaders(Object obj, String separator) {
		Field[] fields = ClassUtilities.getFields(obj, false);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (i > 0) {
				builder.append(separator);
			}
			builder.append(fields[i].getName());
		}
		return builder.toString();
	}
	
	public static final String toValues(Object obj, String separator) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = ClassUtilities.getFields(obj, false);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (i > 0) {
				builder.append(separator);
			}
			builder.append(StringUtilities.toString(fields[i].get(obj)));
		}
		return builder.toString();
	}
	
	public static final Map<String, Object> toMap(Object obj) throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = ClassUtilities.getFields(obj, false);
		for (int i = 0; i < fields.length; i++) {
			map.put(fields[i].getName(), fields[i].get(obj));
		}
		return map;
	}

	public static final <T> List<T> contains(List<T> beans, Object ... objects) {
		List<T> results = new ArrayList<T>();
		
		if (beans.size() == 0) return results;
		
		Field[] allFields = fieldMap.get(beans.get(0).getClass());
		if (allFields == null) {
			allFields = ClassUtilities.getFields(beans.get(0), false);
			fieldMap.put(beans.get(0).getClass(), allFields);
		}
		beans: for (T t : beans) {
			objects: for (Object o : objects) {
				for (Field f : allFields) {
					try {
						Object tmp = f.get(t);
    					if ((o != null) && (o.equals(tmp))) {
    						continue objects;
    					} else if (o == tmp) {
    						continue objects;
    					}
					} catch(Exception exc) {
						exc.printStackTrace();
					}
				}
				continue beans;
			}
			results.add(t);
		}
		return results;
	}
	
	public static final <T> T containsFirst(List<T> beans, Object ... objects) {
		List<T> results = contains(beans, objects);
		if (results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	public static final boolean equals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		} else if ((o1 == null) || (o2 == null)) {
			return false;
		} 
		return o1.equals(o2);
	}

	/**
	 * Convenience method for copying identical fields or getter/setters from one Object to
	 * another. This does not require the Objects to be of similar type, only that the field
	 * names and types be identical within.
	 * 
	 * @param source
	 * @param target
	 * @param mode
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static final void copy(Object source, Object target, CopyMode mode) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (mode == CopyMode.FIELDS) {
    		Field[] sourceFields = ClassUtilities.getFields(source, false);
    		Field[] targetFields = ClassUtilities.getFields(target, false);
    		for (Field sf : sourceFields) {
    			Field tf = matchField(sf, targetFields);
    			if (tf != null) {
    				tf.set(target, sf.get(source));
    			}
    		}
		} else if (mode == CopyMode.METHODS) {
			Method[] sourceMethods = ClassUtilities.getMethods(source.getClass(), false);
			Method[] targetMethods = ClassUtilities.getMethods(target.getClass(), false);
			for (Method sm : sourceMethods) {
				if (isGetter(sm)) {
					Method tm = getSetterForGetter(sm, targetMethods);
					if (tm != null) {
						tm.invoke(target, sm.invoke(source));
					}
				}
			}
		}
	}
	
	public static final Field matchField(Field field, Field[] fields) {
		for (Field f : fields) {
			if (f.getName().equals(field.getName())) {
				if (f.getType() == field.getType()) {
					return f;
				}
			}
		}
		return null;
	}

	public static final boolean isGetter(Method method) {
		if (method.getName().startsWith("get")) {
			if (method.getName().length() > 3) {
				if (method.getParameterTypes().length == 0) {
					if (method.getReturnType() != void.class) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static final Method getSetterForGetter(Method getter, Method[] methods) {
		String methodName = "s" + getter.getName().substring(1);
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				if (m.getParameterTypes().length == 1) {
					if (m.getParameterTypes()[0] == getter.getReturnType()) {
						return m;
					}
				}
			}
		}
		return null;
	}
}