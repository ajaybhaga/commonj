package org.jcommon.util;

/**
 * @author Rebecca Hicks
 */
public class EquationSolver {
	public static boolean DEBUG = false;
	
	public static final float[][] solve(float[][] equation) {
		return solve(equation, null);
	}
	
	public static final float[][] solve(float[][] equation, Integer r) {
		int i = 0;
		
		float round = 1.0f;
		if (r != null) {
    		if (r > 0) {
    			for (int k = 1; k <= r; k++) {
    				round *= 10;
    			}
    		} else if (r < 0) {
    			r = Math.abs(r);
    			for (int k = 1; k <=r; k++) {
    				round /=10;
    			}
    		}
		}
		
		for (int row = 0; row < equation.length; row++) {
			for (int col = i; col < (equation[row].length - 1); col++) {
				int temp;
				if (r == null) {
					temp = findNonzero(equation, row, col);
				} else {
					temp = findNonzero(equation, row, col, round);
				}
				if (temp == -1) {
					i++;
				} else {
					if (row != temp) {
						swapRow(equation, row, temp);
					}
					if (equation[row][col] != 1) {
						divideRow(equation, row, col);
					}
					eliminate(equation, row, col);
					i++;
					break;
				}
			}
		}
//		swapRow(equation, 0, 1);
//		divideRow(equation, 0, 0);
//		eliminate(equation, 0, 0);
//		findNonzero(equation, 0, 0);
		return equation;
	}
	
	public static void main(String[] args) throws Exception {
		int round = 0;
		
		// test case 1
		float[][] equation = new float[8][];
		equation[0] = new float[] {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 25.0f};
		equation[1] = new float[] {0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 25.0f};
		equation[2] = new float[] {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 25.0f};
		equation[3] = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 25.0f};
		equation[4] = new float[] {0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 16.66667f};
		equation[5] = new float[] {0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 16.66667f};
		equation[6] = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 16.66667f};
		equation[7] = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 16.66667f};
		
		//test case 2
//		float[][] equation = new float[4][5];
//		equation[0] = new float[] {1.0f, -1.0f, 1.0f, 2.0f, 1.0f};
//		equation[1] = new float[] {2.0f, -1.0f, 0.0f, 3.0f, 0.0f};
//		equation[2] = new float[] {-1.0f, 1.0f, 1.0f, 1.0f, -1.0f};
//		equation[3] = new float[] {0.0f, 1.0f, 0.0f, 1.0f, 1.0f};
		
		//test case 3
//		float[][] equation = new float[4][5];
//		equation[0] = new float[] {0.0f, -1.0f, 1.0f, 2.0f, 1.0f};
//		equation[1] = new float[] {0.0f, -1.0f, 0.0f, 3.0f, 0.0f};
//		equation[2] = new float[] {0.0f, 1.0f, 1.0f, 1.0f, -1.0f};
//		equation[3] = new float[] {0.0f, 1.0f, 0.0f, 1.0f, 1.0f};
		
		if (DEBUG) System.out.println(toString(equation));
		float[][] solution = solve(equation);
		if (DEBUG) System.out.println(toString(solution));
	}
	
	// Swaps row a with row b
	public static void swapRow(float[][] matrix, int a, int b) {
		float[] temp = new float[matrix[a].length];
		for (int i = 0; i < matrix[a].length; i++) {
			temp[i] = matrix[a][i];
			matrix[a][i] = matrix[b][i];
			matrix[b][i] = temp[i];				
		}
		if (DEBUG) System.out.println("Swapped row: " + a + " with row: " + b + ".  Temp row: " + StringUtilities.toString(temp));
	}
	
	// Divides row a by matrix[a][b]
	public static void divideRow(float[][] matrix, int a, int b) {
		float temp = matrix[a][b];
		for (int i = 0; i < matrix[a].length; i++) {
			if (matrix[a][i] != 0 ) {
				matrix[a][i] /= temp;
			}
		}
		if (DEBUG) System.out.println("Divided row: " + a + " by index: [" + a + ", " + b + "] integer: " + temp + " result:");
		if (DEBUG) System.out.println(toString(matrix));
	}

	// Eliminate all rows with a nonzero value in column b, using row a
	public static void eliminate(float[][] matrix, int a, int b) {
		for (int i = 0; i < matrix.length; i++) {
			if (i != a) {
				if (matrix[i][b] != 0) {
					float temp = -1 * (matrix[i][b]);
					for (int j = 0; j < matrix[i].length; j++) {
						matrix[i][j] += (temp * matrix[a][j]);
					}
					if (DEBUG) System.out.println("Pivot value: " + temp);
				} else {
					if (DEBUG) System.out.println("Skipped row: " + i);
				}
			} else {
				if (DEBUG) System.out.println("Pivot row: " + i);
			}
		}
		if (DEBUG) System.out.println("Eliminated column: " + b + " using row: " + a + " result:");
		if (DEBUG) System.out.println(toString(matrix));
	}

	// Finds first row >= a with nonzero value in column b
	public static int findNonzero(float[][] matrix, int a, int b) {
		for (int i = a; i < matrix.length; i++) {
			if (matrix[i][b] != 0) {
				if (DEBUG) System.out.println("Row: " + i + " has nonzero value: " + matrix[i][b] + " in column: " + b + ". a is " + a);
				return i;
			}
		}
		if (DEBUG) System.out.println("No row found with nonzero value in column: " + b);
		return -1;
	}
	
	// Finds first row >= a with nonzero value in column b, ignoring values closer to zero than a factor of r
	public static int findNonzero(float[][] matrix, int a, int b, float r) {
		for (int i = a; i < matrix.length; i++) {
			if (Math.round(matrix[i][b] * r) == 0) {
				if (DEBUG) System.out.println("Rounding! Location [" + i + ", " + b + "] with value of " + matrix[i][b] + " to zero");
				matrix[i][b] = 0;
			}
			if (matrix[i][b] != 0) {
				if (DEBUG) System.out.println("Row: " + i + " has nonzero value: " + matrix[i][b] + " in column: " + b + ". a is " + a);
				return i;
			}
		}
		if (DEBUG) System.out.println("No row found with nonzero value in column: " + b);
		return -1;
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