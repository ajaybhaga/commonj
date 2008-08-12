package org.jcommon.util;

import java.util.Queue;

/**
 * @author Matt Hicks
 */
public class QueueUtilities {
	public static final <O> void addToHead(Queue<O> queue, O o) {
		// Add to tail
		queue.add(o);
		
		// Cycle around until it's back on top
		while (queue.peek() != o) {
			queue.add(queue.poll());
		}
	}
	
	public static final <O> void addToTail(Queue<O> queue, O o) {
		queue.add(o);
	}
	
	public static final <O> O peekTail(Queue<O> queue) {
		// Cycle over entire list
		O o = null;
		for (O t : queue) {
			o = t;
		}
		return o;
	}
	
	public static final <O> O peekHead(Queue<O> queue) {
		return queue.peek();
	}
	
	public static final <O> O pollTail(Queue<O> queue) {
		O o = peekTail(queue);
		queue.remove(o);
		return o;
	}
	
	public static final <O> O pollHead(Queue<O> queue) {
		return queue.poll();
	}

	public static final <O> O get(Queue<O> queue, int index) {
		for (O t : queue) {
			if (index == 0) {
				return t;
			}
			index--;
		}
		return null;
	}
	
	public static final <O> int indexOf(Queue<O> queue, O o) {
		int index = 0;
		for (O t : queue) {
			if (t == o) {
				return index;
			}
			index++;
		}
		return -1;
	}
}
