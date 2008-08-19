package org.jcommon.util;

public class EquationSolver {
	public static final float[][] solve(float[][] equation) {
		for (int row = 0; row < equation.length; row++) {
			for (int column = 0; column < equation[row].length; column++) {
				
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		float[][] equation = new float[7][7];
		equation[0] = new float[] {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 25.0f};
		equation[1] = new float[] {0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 25.0f};
		equation[2] = new float[] {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 25.0f};
		equation[3] = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 25.0f};
		equation[4] = new float[] {0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 16.6667f};
		equation[5] = new float[] {0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 16.6667f};
		equation[6] = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 16.6667f};
		System.out.println(toString(equation));
		
		float[][] solution = solve(equation);
		System.out.println(toString(solution));
	}
	
	public static String toString(float[][] equation) {
		if (equation == null) {
			return null;
		}
		
		StringBuilder b = new StringBuilder(); {
			for (float[] row : equation) {
				b.append('[');
				boolean first = true;
				for (float column : row) {
					if (!first) {
						b.append(", ");
					} else {
						first = false;
					}
					b.append(column);
				}
				b.append(']');
				b.append('\r');
				b.append('\n');
			}
		}
		
		return b.toString();
	}
}