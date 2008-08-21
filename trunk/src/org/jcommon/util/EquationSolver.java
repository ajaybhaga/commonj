package org.jcommon.util;

/**
 * @author Rebecca Hicks
 */
public class EquationSolver {
	
	public static final float[][] solve(float[][] equation) {
		return solve(equation, null);
	}
	
	public static final float[][] solve(float[][] equation, Integer r) {
		int i = 0;
		
		//creates rounding factor
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
		
		//solves matrix
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
		return equation;
	}
	
	public static void main(String[] args) throws Exception {
	}
	
	// Swaps row a with row b
	public static void swapRow(float[][] matrix, int a, int b) {
		float[] temp = new float[matrix[a].length];
		for (int i = 0; i < matrix[a].length; i++) {
			temp[i] = matrix[a][i];
			matrix[a][i] = matrix[b][i];
			matrix[b][i] = temp[i];				
		}
	}
	
	// Divides row a by matrix[a][b]
	public static void divideRow(float[][] matrix, int a, int b) {
		float temp = matrix[a][b];
		for (int i = 0; i < matrix[a].length; i++) {
			if (matrix[a][i] != 0 ) {
				matrix[a][i] /= temp;
			}
		}
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
				}
			}
		}
	}

	// Finds first row >= a with nonzero value in column b
	public static int findNonzero(float[][] matrix, int a, int b) {
		for (int i = a; i < matrix.length; i++) {
			if (matrix[i][b] != 0) {
				return i;
			}
		}
		return -1;
	}
	
	// Finds first row >= a with nonzero value in column b, ignoring values closer to zero than a factor of r
	public static int findNonzero(float[][] matrix, int a, int b, float r) {
		for (int i = a; i < matrix.length; i++) {
			if (Math.round(matrix[i][b] * r) == 0) {
				matrix[i][b] = 0;
			}
			if (matrix[i][b] != 0) {
				return i;
			}
		}
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