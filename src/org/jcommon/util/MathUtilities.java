package org.jcommon.util;

public class MathUtilities {
	public static final float fix(float f, int precision) {
		float remainder = f % 1.0f;
		
		// Create min and max
		StringBuilder minBuilder = new StringBuilder("0.");
		StringBuilder maxBuilder = new StringBuilder("0.");
		for (int i = 0; i < precision - 1; i++) {
			minBuilder.append('0');
			maxBuilder.append('9');
		}
		minBuilder.append('1');
		
		float min = Float.parseFloat(minBuilder.toString());
		float max = Float.parseFloat(maxBuilder.toString());
		
		if (remainder < min) {
			return f - remainder;
		} else if (remainder > max) {
			return f + (1.0f - remainder);
		}
		return f;
	}
}