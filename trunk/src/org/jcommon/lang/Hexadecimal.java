package org.jcommon.lang;

public class Hexadecimal {
	// Updated: 5.23.03
	long hex;

	public Hexadecimal(long value) {
		hex = value;
	}

	public Hexadecimal(Long value) {
		hex = value.longValue();
	}

	public Hexadecimal(int value) {
		hex = value;
	}

	public Hexadecimal(Integer value) {
		hex = value.longValue();
	}

	public Hexadecimal(byte value) {
		hex = new Byte(value).longValue();
	}

	public Hexadecimal(Byte value) {
		hex = value.longValue();
	}

	public String toString() {
		return Long.toHexString(hex);
	}

	public String toFormatString(int length) {
		String value = Long.toHexString(hex);
		while (value.length() != length) {
			if (value.length() > length) {
				value = value.substring(1);
			} else {
				value = "0" + value;
			}
		}
		return value;
	}
}