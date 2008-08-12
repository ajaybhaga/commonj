package org.jcommon.util;

import java.util.Comparator;

public class StringComparator implements Comparator {
	private static StringComparator instance;
	
	public int compare(Object o1, Object o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		}
		return o1.toString().compareTo(o2.toString());
	}

	public static final synchronized StringComparator getInstance() {
		if (instance == null) {
			instance = new StringComparator();
		}
		return instance;
	}
}
