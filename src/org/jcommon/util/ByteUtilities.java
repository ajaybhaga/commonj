package org.jcommon.util;

public class ByteUtilities {
	public static final byte[] toBytes(long v, byte[] bytes, int start, int length) {
		for (int i = 0; i < length; i++) {
			bytes[start + i] = (byte)((v >> (8 * (length - i - 1))) & 0x000000FF);
		}
		return bytes;
	}
	
	public static final long fromBytes(byte[] b, int offset, int length) {
		long v = 0;
		
		for (int i = offset; i < offset + length; i++) {
			int shift = (length - (i - offset) - 1) * 8;
			v |= ((long)(b[i] & 0xff)) << shift;
		}
		
		return v;
	}
	
	public static final byte[] toBytes(short s) {
		return toBytes(s, new byte[2], 0, 2);
	}
	
	public static final byte[] toBytes(int i) {
		return toBytes(i, new byte[4], 0, 4);
	}
	
	public static final byte[] toBytes(long l) {
		return toBytes(l, new byte[8], 0, 8);
	}
	
	public static final byte[] toBytes(float f) {
		return toBytes(Float.floatToIntBits(f));
	}
	
	public static final byte[] toBytes(double d) {
		return toBytes(Double.doubleToLongBits(d));
	}
	
	public static final short toShort(byte[] bytes, int offset) {
		return (short)fromBytes(bytes, offset, 2);
	}
	
	public static final int toInt(byte[] bytes, int offset) {
		return (int)fromBytes(bytes, offset, 4);
	}
	
	public static final long toLong(byte[] bytes, int offset) {
		return fromBytes(bytes, offset, 8);
	}
	
	public static final float toFloat(byte[] bytes, int offset) {
		long l = fromBytes(bytes, offset, 4);
		return Float.intBitsToFloat((int)l);
	}
	
	public static final double toDouble(byte[] bytes, int offset) {
		long l = fromBytes(bytes, offset, 8);
		return Double.longBitsToDouble(l);
	}
}