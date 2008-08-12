package org.jcommon.com;

import java.nio.*;

import org.jcommon.pool.ObjectGenerator;
import org.jcommon.util.*;

public class ByteBufferGenerator implements ObjectGenerator<ByteBuffer> {
	private int size;
	
	public ByteBufferGenerator(int size) {
		this.size = size;
	}

	public ByteBuffer newInstance() throws Exception {
		return ByteBuffer.allocateDirect(size);
	}

	public void disable(ByteBuffer t) {
	}

	public void enable(ByteBuffer t) {
	}
}
