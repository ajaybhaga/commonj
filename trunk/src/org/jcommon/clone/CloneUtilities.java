package org.jcommon.clone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.jcommon.util.ClassUtilities;
import org.jcommon.xml.XMLSerialization;
import org.jdom.JDOMException;

/**
 * Utilities to simplify the cloning of Objects.
 * 
 * @author Matt Hicks
 */
public class CloneUtilities {
	public static Type DEEP_CLONE_TYPE = Type.REFLECTION;
	
	public enum Type {
		REFLECTION,
		FAST_SERIALIZATION,
		SERIALIZATION,
		XML_SERIALIZATION
	}
	
	public static final Map<Class<?>, Cloner> cloneMap = new HashMap<Class<?>, Cloner>();
	static {
		// Configure default cloners
		cloneMap.put(Object.class, new ObjectCloner());
		cloneMap.put(Map.class, new MapCloner());
	}
	
	@SuppressWarnings("unchecked")
	public static final <O> O shallowClone(O original) throws Exception {
		// Get the Cloner for this Object
		Cloner cloner = getCloner(original);
		// Deep clone
		Object clone = cloner.shallowClone(original);
		
		return (O)clone;
	}
	
	public static final <O> O deepClone(O original) throws Exception {
		switch (DEEP_CLONE_TYPE) {
			case REFLECTION:
				return deepCloneReflection(original);
			case FAST_SERIALIZATION:
				return deepCloneFastSerialization(original);
			case SERIALIZATION:
				return deepCloneSerialization(original);
			case XML_SERIALIZATION:
				return deepCloneXMLSerialization(original);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static final <O> O deepCloneReflection(O original) throws Exception {
		return (O)deepCloneReflectionInternal(original, new HashMap<Object, Object>());
	}
	
	protected static final Object deepCloneReflectionInternal(Object original, Map<Object, Object> cache) throws Exception {
		if (original == null) {				// No need to clone nulls
			return original;
		} else if (cache.containsKey(original)) {
			return cache.get(original);
		}
		
		// Get the Cloner for this Object
		Cloner cloner = getCloner(original);
		// Deep clone
		Object clone = cloner.deepClone(original, new HashMap<Object, Object>());
		
		return clone;
	}
	
	private static final Cloner getCloner(Object obj) {
		// Get a reversed list of Classes
		List<Class<?>> classes = ClassUtilities.getClassHierarchy(obj.getClass(), true);
		for (Class<?> c : classes) {
			if (cloneMap.containsKey(c)) {
				return cloneMap.get(c);
			}
		}
		return cloneMap.get(Object.class);
	}
	
	@SuppressWarnings("unchecked")
	public static final <O> O deepCloneFastSerialization(O original) throws IOException, ClassNotFoundException {
        FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(fbos);
        out.writeObject(original);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
        O o = (O)in.readObject();
        in.close();
        return o;
	}
	
	@SuppressWarnings("unchecked")
	public static final <O> O deepCloneSerialization(O original) throws IOException, ClassNotFoundException {
        // Write the object out to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(original);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        O o = (O)in.readObject();
        in.close();
        return o;
	}
	
	@SuppressWarnings("unchecked")
	public static final <O> O deepCloneXMLSerialization(O original) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException, IOException, JDOMException {
		String xml = XMLSerialization.toXMLString(original, null, false);
		O o = (O)XMLSerialization.fromXML(xml, null);
		return o;
	}

	@SuppressWarnings("all")
	public static void main(String[] args) throws Exception {
        // Make a reasonable large test object. Note that this doesn't
        // do anything useful -- it is simply intended to be large, have
        // several levels of references, and be somewhat random. We start
        // with a hashtable and add vectors to it, where each element in
        // the vector is a Date object (initialized to the current time),
        // a semi-random string, and a (circular) reference back to the
        // object itself. In this case the resulting object produces
        // a serialized representation that is approximate 700K.
        HashMap obj = new HashMap();
        for (int i = 0; i < 100; i++) {
            Vector v = new Vector();
            for (int j = 0; j < 100; j++) {
                v.addElement(new Object[] {
                    new Date(),
                    "A random number: " + Math.random(),
                    obj
                 });
            }
            obj.put(new Integer(i), v);
        } 

        int iterations = 1000;

        // Make copies of the object using the unoptimized version
        // of the deep copy utility.
        long unoptimizedTime = 0L;
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            Object copy = deepCloneSerialization(obj);
            unoptimizedTime += (System.nanoTime() - start);

            // Avoid having GC run while we are timing...
            copy = null;
            System.gc();
        }
        System.out.println("Unoptimized time: " + TimeUnit.MILLISECONDS.convert(unoptimizedTime, TimeUnit.NANOSECONDS));

        // Repeat with the optimized version
        long optimizedTime = 0L;
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            Object copy = deepCloneFastSerialization(obj);
            optimizedTime += (System.nanoTime() - start);

            // Avoid having GC run while we are timing...
            copy = null;
            System.gc();
        }
        System.out.println("  Optimized time: " + TimeUnit.MILLISECONDS.convert(optimizedTime, TimeUnit.NANOSECONDS));
        
        // Repeat with the XML Serialization version
        long optimizedTime2 = 0L;
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            Object copy = deepCloneReflection(obj);
            optimizedTime2 += (System.nanoTime() - start);

            // Avoid having GC run while we are timing...
            copy = null;
            System.gc();
        }
        System.out.println(" Optimized time2: " + TimeUnit.MILLISECONDS.convert(optimizedTime2, TimeUnit.NANOSECONDS));
    }
}