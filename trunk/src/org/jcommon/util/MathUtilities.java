package org.jcommon.util;

import java.text.NumberFormat;

public class MathUtilities {
	public static final float fix(float f, int precision) {
		float remainder = f % 1.0f;
		NumberFormat nf = NumberFormat.getInstance(); {
			nf.setMaximumFractionDigits(30);
		}
		
		// Create min and max
		StringBuilder minBuilder = new StringBuilder("0.");
		StringBuilder maxBuilder = new StringBuilder("0.");
		for (int i = 0; i < precision; i++) {
			minBuilder.append('0');
			maxBuilder.append('9');
		}
		minBuilder.append('1');
		maxBuilder.append('1');
		
		float min = Float.parseFloat(minBuilder.toString());
		float max = Float.parseFloat(maxBuilder.toString());
		
		if (remainder < min) {
			return f - remainder;
		} else if (remainder > max) {
			return f + (1.0f - remainder);
		}
		return f;
	}
	
	public static void main(String[] args) throws Exception {
		float f = 5.0000005f;
		System.out.println("Float: " + f + " - " + fix(f, 5));;
	}
}