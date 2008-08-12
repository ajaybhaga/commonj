/**
 * Created: Feb 12, 2007
 */
package org.jcommon.xml;

import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

import org.jcommon.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.transform.*;

/**
 * @author Matt Hicks
 *
 */
public class XMLSerialization {
	public static final HashMap<Class, CustomInstantiator> customInstantiations;
	private long identifier;
	private StrictMap<Object, Object> cache;
	private ClassLoader loader;
	private boolean useShortNames;

	static {
		customInstantiations = new HashMap<Class, CustomInstantiator>();

		CustomInstantiator instantiator = new CustomInstantiator() {

			public Object instantiate(Element element) {
				try {
					String declaringClass = null;
					String methodName = null;
					String fileName = null;
					int lineNumber = -1;
					List list = element.getChildren();
					for (int i = 0; i < list.size(); i++) {
						if (!(list.get(i) instanceof Element))
							continue;
						Element child = (Element)list.get(i);
						if ("ClassName".equals(child.getAttributeValue("name"))) {
							declaringClass = child.getTextTrim();
							continue;
						} else if ("declaringClass".equals(child.getAttributeValue("name"))) {
							declaringClass = child.getTextTrim();
							continue;
						}
						if ("MethodName".equalsIgnoreCase(child.getAttributeValue("name"))) {
							methodName = child.getTextTrim();
							continue;
						}
						if ("FileName".equalsIgnoreCase(child.getAttributeValue("name"))) {
							fileName = child.getTextTrim();
							continue;
						}
						if (!"LineNumber".equalsIgnoreCase(child.getAttributeValue("name")))
							continue;
						try {
							lineNumber = Integer.parseInt(child.getTextTrim());
						} catch (Exception exc) {
						}
					}

					if (declaringClass == null) {
						XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
						outputter.output(element, System.out);
					}
					return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
				} catch (Throwable t) {
					t.printStackTrace();
				}
				return null;
			}

		};
		customInstantiations.put(java.lang.StackTraceElement.class, instantiator);
		instantiator = new CustomInstantiator() {

			public Object instantiate(Element element) {
				Throwable targetException = null;
				String message = null;
				List nodes = element.getChildren();
				for (int i = 0; i < nodes.size(); i++) {
					try {
						if (!(nodes.get(i) instanceof Element))
							continue;
						Element e = (Element)nodes.get(i);
						if (e.getAttributeValue("name").equals("TargetException")) {
							targetException = (Throwable)XMLSerialization.fromXML(e, Thread.currentThread().getContextClassLoader());
							System.out.println("Got the target exception!");
						}
						continue;
					} catch (Exception exc) {
						exc.printStackTrace();
					}
					System.exit(0);
				}

				return new InvocationTargetException(targetException, message);
			}

		};
		customInstantiations.put(java.lang.reflect.InvocationTargetException.class, instantiator);
		instantiator = new CustomInstantiator() {
			public Object instantiate(Element element) {
				return new GregorianCalendar();
			}
		};
		customInstantiations.put(java.util.Calendar.class, instantiator);
		instantiator = new CustomInstantiator() {
			public Object instantiate(Element element) {
				try {
					return Thread.currentThread().getContextClassLoader().loadClass(element.getText());
				} catch (Exception exc) {
					exc.printStackTrace();
					throw new RuntimeException("Cannot load class: " + element.getText(), exc);
				}
			}
		};
		customInstantiations.put(Class.class, instantiator);
	}

	private XMLSerialization() {
		identifier = 0L;
		cache = new StrictMap<Object, Object>(false);
		loader = getClass().getClassLoader();
	}

	public void setClassLoader(ClassLoader loader) {
		this.loader = loader;
		if (loader == null)
			this.loader = getClass().getClassLoader();
	}

	public void setUseShortNames(boolean useShortNames) {
		this.useShortNames = useShortNames;
	}

	public Element convertObjectToElement(Object o, String name) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Element e = new Element("object");
		if (name != null) {
			e.setAttribute("name", name);
		}
		if (o == null) {
			return e;
		}
		
		String className = getClassName(o.getClass());
		e.setAttribute("class", className);
		if (cache.containsKey(o)) {
			Element ref = (Element)cache.get(o);
			if (ref.getAttribute("xmlIdentifier") == null || ref.getAttributeValue("xmlIdentifier").trim().length() == 0) {
				ref.setAttribute("xmlIdentifier", String.valueOf(identifier));
				identifier++;
			}
			e.setAttribute("xmlIdentifier", ((Element)cache.get(o)).getAttributeValue("xmlIdentifier"));
			return e;
		}
		if (isCachable(o))
			cache.put(o, e);
		if (o instanceof String)
			e.setText((String)o);
		else if (o instanceof Enum)
			e.setText(((Enum)o).name());
		else if (o instanceof Boolean)
			e.setText(o.toString());
		else if (o instanceof Character)
			e.setText(o.toString());
		else if (o instanceof Byte)
			e.setText(o.toString());
		else if (o instanceof Integer)
			e.setText(o.toString());
		else if (o instanceof Long)
			e.setText(o.toString());
		else if (o instanceof Float)
			e.setText(o.toString());
		else if (o instanceof Double)
			e.setText(o.toString());
		else if (o instanceof BigDecimal)
			e.setText(o.toString());
		else if (o instanceof java.util.Date)
			e.setText(String.valueOf(((java.util.Date)o).getTime()));
		else if (o instanceof Calendar)
			e.setText(String.valueOf(((Calendar)o).getTimeInMillis()));
		else if (o.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(o); i++) {
				Element child = convertObjectToElement(Array.get(o, i), null);
				e.addContent(child);
			}
		} else if (o instanceof AbstractList) {
			AbstractList list = (AbstractList)o;
			for (int i = 0; i < list.size(); i++) {
				Element child = convertObjectToElement(list.get(i), null);
				e.addContent(child);
			}

		} else if (o instanceof AbstractCollection) {
			AbstractCollection collection = (AbstractCollection)o;
			Element child;
			for (Iterator iterator = collection.iterator(); iterator.hasNext(); e.addContent(child))
				child = convertObjectToElement(iterator.next(), null);

		} else if (o instanceof Dictionary) {
			Dictionary map = (Dictionary)o;
			Element child;
			for (Enumeration iterator = map.keys(); iterator.hasMoreElements(); e.addContent(child)) {
				child = new Element("entry");
				Object key = iterator.nextElement();
				Object value = map.get(key);
				Element temp = convertObjectToElement(key, "key");
				child.addContent(temp);
				temp = convertObjectToElement(value, "value");
				child.addContent(temp);
			}

		} else if (o instanceof AbstractMap) {
			AbstractMap map = (AbstractMap)o;
			Element child;
			for (Iterator iterator = map.keySet().iterator(); iterator.hasNext(); e.addContent(child)) {
				child = new Element("entry");
				Object key = iterator.next();
				Object value = map.get(key);
				Element temp = convertObjectToElement(key, "key");
				child.addContent(temp);
				temp = convertObjectToElement(value, "value");
				if (temp != null)
					child.addContent(temp);
			}
		} else if (o instanceof Class) {
			e.setText(((Class)o).getName());
		} else {
			// Introspect Bean
			Field[] fields = ClassUtilities.getFields(o, false, false);
			for (int i = 0; i < fields.length; i++) {
				Element child = null;
				Object value = fields[i].get(o);
				if (value == null) {
					//child = new Element(getClassName(fields[i].getType()));
					child = new Element("object");
					child.setAttribute("name", fields[i].getName());
				} else {
					child = convertObjectToElement(value, fields[i].getName());
				}
				if (child != null) {
					e.addContent(child);
				}
			}

			/*Method methods[] = o.getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (!methods[i].getName().startsWith("get") && !methods[i].getName().startsWith("has")
								&& !methods[i].getName().startsWith("is") || methods[i].getParameterTypes().length != 0
								|| methods[i].getName().equals("getClass") || methods[i].getName().equals("hashCode"))
					continue;
				if (methods[i].getName().startsWith("is")) name = methods[i].getName().substring(2);
				else name = methods[i].getName().substring(3);
				Object value;
				try {
					value = methods[i].invoke(o, new Object[0]);
				} catch (IllegalAccessException exc) {
					value = null;
				}
				if (value != null) {
					Element child = convertObjectToElement(value, name);
					e.addContent(child);
				} else {
					Element child = new Element(getClassName(methods[i].getReturnType()));
					child.setAttribute("name", name);
					e.addContent(child);
				}
			}*/

		}
		return e;
	}

	private String getClassName(Class c) {
		String className = c.getName();
		className = className.replaceAll(";", "");

		if (c.isArray()) {
			className = c.getComponentType().getName() + "_Array";
		} else if (className.startsWith("[L")) {
			String end = "_Array";
			if (useShortNames)
				end = "Array";
			className = className.substring(2) + end;
		} else if (className.startsWith("[[L")) {
			String end = "_Array";
			if (useShortNames)
				end = "Array";
			className = className.substring(3, className.length() - 1) + end;
		}
		if (useShortNames) {
			className = className.substring(className.lastIndexOf('.') + 1);
			className = Character.toLowerCase(className.charAt(0)) + className.substring(1);
		}
		return className;
	}

	public Object convertElementToObject(Element e) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		if (e.getAttributeValue("xmlIdentifier") != null) {
			String ident = e.getAttributeValue("xmlIdentifier").intern();
			if (cache.containsKey(ident)) {
				return cache.get(ident);
			}
		}
		String text = e.getAttributeValue("class");
		boolean isArray = false;
		if (text == null)
			return null;
		if (text.endsWith("_Array")) {
			text = text.replaceAll("_Array", "");
			isArray = true;
		}
		Object o = null;
		Class c = null;
		if ((isArray) && ("boolean, char, byte, short, int, long, float, double".indexOf(text) != -1)) {
			if ("boolean".equals(text)) {
				c = boolean.class;
			} else if ("byte".equals(text)) {
				c = byte.class;
			} else if ("char".equals(text)) {
				c = char.class;
			} else if ("short".equals(text)) {
				c = short.class;
			} else if ("int".equals(text)) {
				c = int.class;
			} else if ("long".equals(text)) {
				c = long.class;
			} else if ("float".equals(text)) {
				c = float.class;
			} else if ("double".equals(text)) {
				c = double.class;
			}
		} else {
			c = loader.loadClass(text);
		}
		if (isArray) {
			int size = 0;
			Iterator iterator = e.getChildren().iterator();
			do {
				if (!iterator.hasNext())
					break;
				if (iterator.next() instanceof Element)
					size++;
			} while (true);
			o = Array.newInstance(c, size);
		} else if (c == Character.class) {
			o = e.getText().charAt(0);
		} else if (c == java.util.Date.class) {
			o = new java.util.Date(Long.parseLong(e.getText()));
		} else if (c == java.sql.Date.class) {
			o = new java.sql.Date(Long.parseLong(e.getText()));
		} else if (customInstantiations.containsKey(c)) {
			CustomInstantiator instantiator = (CustomInstantiator)customInstantiations.get(c);
			o = instantiator.instantiate(e);
		} else if (c.isEnum()) {
			o = Enum.valueOf(c, e.getText());
		} else {
			Constructor constructor = null;
			try {
				constructor = c.getDeclaredConstructor(new Class[] {
					String.class
				});
			} catch (NoSuchMethodException exc) {
			}
			if (constructor == null) {
				constructor = c.getDeclaredConstructor(new Class[0]);
				constructor.setAccessible(true);
				o = constructor.newInstance();
			} else {
				constructor.setAccessible(true);
				o = constructor.newInstance(new Object[] {
					e.getText()
				});
			}
		}
		if (e.getAttributeValue("xmlIdentifier") != null) {
			cache.put(e.getAttributeValue("xmlIdentifier").intern(), o);
		}
		if (o instanceof AbstractList) {
			Iterator list = e.getChildren().iterator();
			do {
				if (!list.hasNext())
					break;
				Content tmp = (Content)list.next();
				if (tmp instanceof Element) {
					try {
						((AbstractList)o).add(convertElementToObject((Element)tmp));
					} catch(NoSuchMethodException exc) {
						// Unable to construct this object
					}
				}
			} while (true);
		} else if (o instanceof AbstractCollection) {
			Iterator list = e.getChildren().iterator();
			do {
				if (!list.hasNext())
					break;
				Content tmp = (Content)list.next();
				if (tmp instanceof Element) {
					try {
						((AbstractCollection)o).add(convertElementToObject((Element)tmp));
					} catch(NoSuchMethodException exc) {
						// Unable to construct this object
					}
				}
			} while (true);
		} else if (o.getClass().isArray()) {
			Iterator list = e.getChildren().iterator();
			int j = 0;
			do {
				if (!list.hasNext())
					break;
				Content tmp = (Content)list.next();
				if (tmp instanceof Element) {
					Object sub = convertElementToObject((Element)tmp);
					//((Object[]) (Object[]) o)[j] = sub;
					Array.set(o, j, sub);
					j++;
				}
			} while (true);
		} else if (o instanceof AbstractMap) {
			Iterator list = e.getChildren().iterator();
			do {
				if (!list.hasNext())
					break;
				Content tmp = (Content)list.next();
				if ((tmp instanceof Element) && ((Element)tmp).getName().equals("entry")) {
					try {
    					Object key = null;
    					Object value = null;
    					Element child = (Element)((Element)tmp).getChildren().get(0);
    					if (child.getAttributeValue("name").equals("key"))
    						key = convertElementToObject(child);
    					else
    						value = convertElementToObject(child);
    					child = (Element)((Element)tmp).getChildren().get(1);
    					if (child.getAttributeValue("name").equals("key"))
    						key = convertElementToObject(child);
    					else
    						value = convertElementToObject(child);
    					if (key == null)
    						System.out.println("CRAP!: " + key + ", " + value);
    					((AbstractMap)o).put(key, value);
					} catch(NoSuchMethodException exc) {
						// Unable to construct this object
					}
				}
			} while (true);
		} else if (o instanceof Dictionary) {
			Iterator list = e.getChildren().iterator();
			do {
				if (!list.hasNext())
					break;
				Content tmp = (Content)list.next();
				if ((tmp instanceof Element) && ((Element)tmp).getName().equals("entry")) {
					try {
    					Object key = null;
    					Object value = null;
    					Element child = (Element)((Element)tmp).getChildren().get(0);
    					if (child.getAttribute("name").equals("key"))
    						key = convertElementToObject(child);
    					else
    						value = convertElementToObject(child);
    					child = (Element)((Element)tmp).getChildren().get(0);
    					if (child.getAttribute("name").equals("key"))
    						key = convertElementToObject(child);
    					else
    						value = convertElementToObject(child);
    					((Dictionary)o).put(key, value);
					} catch(NoSuchMethodException exc) {
						// Unable to construct this object
					}
				}
			} while (true);
		} else if (o instanceof Calendar) {
			try {
				((Calendar)o).setTimeInMillis(Long.parseLong(e.getTextTrim()));
			} catch (NumberFormatException exc) {
				o = null;
			}
		} else {
			for (Content tmp : (List<Content>)e.getChildren()) {
				if (tmp instanceof Element) {
					Element child = (Element)tmp;
					Object value = convertElementToObject(child);
					String name = child.getAttributeValue("name");

					Field field = ClassUtilities.getField(c, name, false);
					if (field != null) {
						field.set(o, value);
					}
				}
			}

			/*Iterator list = e.getChildren().iterator();
			do {
				if (!list.hasNext()) break;
				Content tmp = (Content) list.next();
				if (tmp instanceof Element) {
					Element child = (Element) tmp;
					Object value = convertElementToObject(child);
					try {
						Method setter = null;
						try {
							setter =
											c.getMethod("set" + child.getAttributeValue("name"), new Class[] {value
															.getClass()});
						} catch (Exception exc) {
							setter =
											c.getMethod("set" + child.getAttributeValue("name"),
															new Class[] {convertToPrimitive(value.getClass())});
						}
						setter.invoke(o, new Object[] {value});
					} catch (NoSuchMethodException exc) {
					} catch (Throwable t) {
					}
				}
			} while (true);*/
		}
		if (e.getAttributeValue("xmlIdentifier") != null) {
			cache.put(e.getAttributeValue("xmlIdentifier").intern(), o);
		}
		return o;
	}

	private static final boolean isCachable(Object o) {
		if (o instanceof Boolean)
			return false;
		if (o instanceof Byte)
			return false;
		if (o instanceof Character)
			return false;
		if (o instanceof Integer)
			return false;
		if (o instanceof Long)
			return false;
		if (o instanceof Float)
			return false;
		if (o instanceof Double)
			return false;
		return !(o instanceof String);
	}

	private static final Class convertToPrimitive(Class c) {
		if (c == (java.lang.Boolean.class))
			return Boolean.TYPE;
		if (c == (java.lang.Byte.class))
			return Byte.TYPE;
		if (c == (java.lang.Character.class))
			return Character.TYPE;
		if (c == (java.lang.Integer.class))
			return Integer.TYPE;
		if (c == (java.lang.Long.class))
			return Long.TYPE;
		if (c == (java.lang.Float.class))
			return Float.TYPE;
		if (c == (java.lang.Double.class))
			return Double.TYPE;
		else
			return c;
	}

	public static final synchronized Document toXML(Object o, boolean shortNames) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		XMLSerialization serializer = new XMLSerialization();
		serializer.setUseShortNames(shortNames);
		Element e = serializer.convertObjectToElement(o, null);
		return new Document(e);
	}

	public static final synchronized void appendXML(Element e, Object o) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		XMLSerialization serializer = new XMLSerialization();
		Element child = serializer.convertObjectToElement(o, null);
		if (child != null)
			e.setContent(child);
	}

	public static final String toXMLString(Object o, File xsl, boolean shortNames) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, XSLTransformException {
		Document document = toXML(o, shortNames);
		if (xsl != null) {
			XSLTransformer transformer = new XSLTransformer(xsl);
			document = transformer.transform(document);
		}
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		return outputter.outputString(document);
	}

	public static final Object fromXML(Document document, ClassLoader loader) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		return fromXML(document.getRootElement(), loader);
	}

	public static final Object fromXML(Element element, ClassLoader loader) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		XMLSerialization serializer = new XMLSerialization();
		serializer.setClassLoader(loader);
		return serializer.convertElementToObject(element);
	}

	public static final Object fromXML(String s, ClassLoader loader) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, JDOMException {
		Document document = (new SAXBuilder()).build(new StringReader(s));
		return fromXML(document, loader);
	}

	public static final String generateSample(Class c, ClassLoader loader) throws IllegalArgumentException, XSLTransformException, IllegalAccessException, InvocationTargetException, InstantiationException {
		try {
			Object instance;
			if (c.isArray()) {
				c = loader.loadClass(c.getName().substring(2, c.getName().length() - 1));
				instance = c.newInstance();
				Object array[] = (Object[])(Object[])Array.newInstance(c, 1);
				array[0] = instance;
				instance = ((Object)(array));
			} else {
				instance = c.newInstance();
			}
			return toXMLString(instance, null, false);
		} catch (Exception exc) {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		List<Long> list = new ArrayList<Long>();
		list.add(1L);
		list.add(2L);
		list.add(3L);
		System.out.println(XMLSerialization.toXMLString(list, null, false));
	}
}