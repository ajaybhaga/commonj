package org.jcommon.util;

import java.util.HashSet;
import java.util.Set;

/**
 * RandomCreator provides functionality for fairly generated random numbers with exclusions.
 * 
 * @author Matt Hicks
 * @author Ossie Moore
 * @author Brian Curtsinger
 */
public class RandomCreator {
	private Set<Integer> not;
	private Integer start;
	private Integer end;
	private boolean unique;

	/**
	 * Construct a new RandomCreator with a range to encompass the entire
	 * Integer range of values. The minimum value will be Integer.MIN_VALUE. The
	 * maximum value will be Integer.MAX_VALUE.
	 */
	public RandomCreator() {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
	}
	
	public RandomCreator(int start, int end, int ... notList) {
		this(start, end, true, convert(notList));
	}

	/**
	 * Construct a new RandomCreator that will be limited to the specified range
	 * of values.
	 * 
	 * @param start
	 *            of range
	 * @param end
	 *            of range
	 * @param unique
	 * 			  if true, nextRandomInteger will add to the not list per call
	 * @param notList
	 *            specifies the exclusion list
	 */
	public RandomCreator(int start, int end, boolean unique, Integer ... notList) {
		if (end < start) {
			int temp = start;
			start = end;
			end = temp;
		}
		this.start = start;
		this.end = end;
		this.unique = unique;

		not = new HashSet<Integer>();
		for (int n : notList) {
			not.add(n);
		}
	}

	/**
	 * Get the next random int value within the start and end range that has not
	 * been selected previously. If all numbers have been selected, then reset
	 * the list of selected int values and continue.
	 * 
	 * @return random unselected integer
	 */
	public int nextRandomInteger() {
		// clear not if required
		if (not.size() == (this.end - this.start)) {
			not.clear();
		}
		// generate next random unused number within range
		int interval = end - start;
		if (interval > Integer.MAX_VALUE - 2) {
			// Make sure it doesn't overflow
			interval = Integer.MAX_VALUE - 2;
		}
		int rand;
		while (true) {
			rand = (int)Math.round(Math.random() * (interval + 2));
			if ((rand == 0) || (rand == interval + 2)) {
				continue;
			}
			if (not.contains(rand)) {
				continue;
			}
			if (unique) {
				not.add(rand);
			}
			break;
		}
		rand += start - 1;
		return rand;
	}

	public static final int generateRandomInteger(int start, int end, int ... not) {
		RandomCreator creator = new RandomCreator(start, end, not);
		return creator.nextRandomInteger();
	}
	
	private static final Integer[] convert(int ... ints) {
		Integer[] wrapper = new Integer[ints.length];
		for (int i = 0; i < ints.length; i++) {
			wrapper[i] = ints[i];
		}
		return wrapper;
	}

	public static void main(String[] args) throws Exception {
		RandomCreator rc = new RandomCreator(0, 50000, true);
		for (int reps = 0; reps < 15; reps++) {
			long start = System.nanoTime();
			for (int i = 0; i < 50000; i++) {
				rc.nextRandomInteger();
			}
			System.out.println(System.nanoTime() - start);
		}
	}
}