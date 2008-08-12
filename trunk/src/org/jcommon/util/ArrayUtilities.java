/*
 * Created on Sep 16, 2004
 */
package org.jcommon.util;

import java.util.*;

/**
 * @author Matt Hicks
 */
public class ArrayUtilities {
    public static Collection toCollection(Object[] o) {
        ArrayList c = new ArrayList(o.length);
        for (int i = 0; i < o.length; i++) {
            c.add(o[i]);
        }
        return c;
    }
    
    public static List toList(Object[] o) {
        return (List)toCollection(o);
    }

    public static Class[] toClasses(Object[] o) {
    	Class[] classes = new Class[o.length];
    	for (int i = 0; i < o.length; i++) {
    		classes[i] = o[i].getClass();
    	}
    	return classes;
    }
}
