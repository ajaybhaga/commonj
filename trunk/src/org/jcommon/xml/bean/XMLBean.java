package org.jcommon.xml.bean;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcommon.util.ClassUtilities;
import org.jcommon.util.StringUtilities;
import org.jcommon.xml.XMLSerialization;
import org.jcommon.xml.bean.handler.ArrayXMLHandler;
import org.jcommon.xml.bean.handler.ClassXMLHandler;
import org.jcommon.xml.bean.handler.CollectionXMLHandler;
import org.jcommon.xml.bean.handler.DefaultXMLHandler;
import org.jcommon.xml.bean.handler.EnumXMLHandler;
import org.jcommon.xml.bean.handler.MapXMLHandler;
import org.jcommon.xml.bean.handler.NumberXMLHandler;
import org.jcommon.xml.bean.handler.StringXMLHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author Matt Hicks
 */
public class XMLBean {
	public static XMLHandler DEFAULT_HANDLER = new DefaultXMLHandler();

	private static final Map<String, XMLInstance> namedHandlers = new HashMap<String, XMLInstance>();
	private static final Map<Class<?>, XMLInstance> classHandlers = new HashMap<Class<?>, XMLInstance>();

	static {
		register(String.class, new StringXMLHandler());

		XMLHandler numberHandler = new NumberXMLHandler();
		register(Number.class, numberHandler);
		register("byte", Byte.class, numberHandler);
		register("short", Short.class, numberHandler);
		register("int", Integer.class, numberHandler);
		register("long", Long.class, numberHandler);
		register("float", Long.class, numberHandler);
		register("double", Double.class, numberHandler);
		register("boolean", Boolean.class, numberHandler);

		XMLHandler collectionHandler = new CollectionXMLHandler();
		register(Collection.class, collectionHandler);
		register("list", ArrayList.class, collectionHandler);

		register(Enum.class, new EnumXMLHandler());

		register("array", null, new ArrayXMLHandler());
		
		XMLHandler mapHandler = new MapXMLHandler();
		register("map", Map.class, mapHandler);
		register("hashMap", HashMap.class, mapHandler);
		
		register("class", Class.class, new ClassXMLHandler());
	}

	public static void register(String name, Class<?> c) {
		register(name, c, DEFAULT_HANDLER);
	}

	public static void register(Class<?> c) {
		register(c, DEFAULT_HANDLER);
	}

	public static void register(Class<?> c, XMLHandler handler) {
		String name = c.getSimpleName().substring(0, 1).toLowerCase()
				+ c.getSimpleName().substring(1);
		register(name, c, handler);
	}

	public static void register(String name, Class<?> c, XMLHandler handler) {
		XMLInstance instance = new XMLInstance(name, c, handler);
		namedHandlers.put(name, instance);
		classHandlers.put(c, instance);
	}

	public static Element toXML(Object obj) {
		return toXML(null, obj);
	}

	public static Element toXML(String name, Object obj) {
		return toXML(name, obj, obj.getClass(), new PersistenceInstance(), true);
	}

	public static Element toXML(String originalName, Object obj, Class<?> c,
			PersistenceInstance pi, boolean topLevel) {
		String name = originalName;

		XMLInstance instance = getXMLInstance(c);
		if ((instance == null) && (obj != null)) {
			instance = getXMLInstance(obj.getClass());
		}
		XMLHandler handler = null;
		if (instance == null) {
			// Unable to find an instance
			if (name == null) {
				if (c != null) {
					name = StringUtilities.dephrasify(c.getSimpleName());
				} else if (obj != null) {
					name = StringUtilities.dephrasify(obj.getClass()
							.getSimpleName());
				}
			}
			handler = DEFAULT_HANDLER;
		} else {
			// Instance found
			if (name == null) {
				name = instance.getName();
			}
			handler = instance.getHandler();
		}

		Element element = new Element(name);
		{
			if (obj == null) {
				return element;
			}

			// Check cache
			if (pi.isCached(obj)) {
				// Exists in cache, so set the id
				element.setAttribute("id", String.valueOf(pi.getCachedId(obj)));
			} else {
				// Put in cache and then process contents
				if (handler.isCacheable()) {
					pi.cache(obj, element);
				}

				if ((instance == null) && (topLevel)) {
					// Using Default Handler
					element.setAttribute("class", obj.getClass()
							.getCanonicalName());
				} else if (ClassUtilities.updateClass(obj.getClass()) != ClassUtilities
						.updateClass(c)) {
					c = ClassUtilities.updateClass(obj.getClass());
					if (classHandlers.containsKey(c)) {
						// Use the name specified in the registry
						String instanceName = classHandlers.get(c).getName();
						if (!name.equals(instanceName)) {
							element.setAttribute("name", instanceName);
						}
					} else {
						// Not the same, so we have to set the class to use
						element.setAttribute("class", obj.getClass()
								.getCanonicalName());
					}
				}

				handler.toXML(element, obj, pi);
			}
		}
		return element;
	}

	public static String toString(Element element) {
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		return outputter.outputString(element);
	}

	public static Object fromXML(Element e) {
		return fromXML(null, e);
	}

	public static Object fromXML(Class<?> c, Element e) {
		return fromXML(c, e, new PersistenceInstance());
	}

	public static Object fromXML(Class<?> c, Element e, PersistenceInstance pi) {
		XMLInstance instance = null;

		// Check to see if it's cached
		if ((e.getAttributeValue("id") != null)
				&& (pi.isCached(e.getAttributeValue("id")))) {
			// Found cached reference, so return it
			return pi.getCachedById(e.getAttributeValue("id"));
		}

		c = ClassUtilities.updateClass(c);

		if (namedHandlers.containsKey(e.getName())) { // Check if there's a name
														// reference
			instance = namedHandlers.get(e.getName());
		}
		if (e.getAttributeValue("class") != null) { // If there is a class
													// reference we try to find
													// a handler for that
			try {
				c = Class.forName(e.getAttributeValue("class"));
			} catch (ClassNotFoundException exc) {
				exc.printStackTrace();
			}
		} else if (e.getAttributeValue("name") != null) { // If there is a name
															// reference we use
															// that
			instance = namedHandlers.get(e.getAttributeValue("name"));
			if (instance.getClazz() != null) {
				c = instance.getClazz();
			}
		}
		if (instance == null) { // If we can't find any other alternative we use
								// the Class
			instance = getXMLInstance(c);
		} else if (c == null) {
			c = instance.getClazz();
		}
		if (instance == null) {
			// Use our default handler
			return DEFAULT_HANDLER.fromXML(c, e, pi);
		} else {
			return instance.getHandler().fromXML(c, e, pi);
		}
	}

	public static Object fromXML(String s) throws JDOMException, IOException {
		Document document = (new SAXBuilder()).build(new StringReader(s));
		return fromXML(document.getRootElement());
	}
	
	public static XMLInstance getXMLInstance(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		if (clazz.isPrimitive()) {
			return classHandlers.get(clazz);
		} else if (clazz.isArray()) {
			return namedHandlers.get("array");
		}

		List<Class<?>> classes = ClassUtilities.getClassHierarchy(clazz, true);
		for (Class<?> c : classes) {
			if (classHandlers.containsKey(c)) {
				XMLInstance instance = classHandlers.get(c);
				return instance;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		// Object[] s = new Object[] {"One", "Two", "Three"};
		// Element e = toXML(s);
		// s = (Object[])fromXML(e);
		// System.out.println(toString(toXML(s)));
		
		
	}
}