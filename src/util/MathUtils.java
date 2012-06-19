package util;

public class MathUtils {

	public static double[] multiplyVectorByMatrix(double[] vector,
			double[][] matrix) {
		double result[] = new double[matrix[0].length]; 

		for (int i = 0; i < matrix[0].length; i++) {
			double sum = 0; 
			for (int j = 0; j < vector.length; j++) { 
				sum += vector[j] * matrix[j][i]; 
			}
			result[i] = sum; 
		}

		return result;
	}

	public static double[][] multiplyMatrixByNumber(double[][] matrix,
			double number) {
		double result[][] = new double[matrix.length][]; 

		for (int i = 0; i < matrix.length; i++) { 
			result[i] = new double[matrix[i].length];
			for (int j = 0; j < matrix[i].length; j++) { 
				result[i][j] = matrix[i][j] * number; 
			}
		}

		return result;
	}

	public static double[][] invert2x2Matrix(double[][] matrix) {
		double determinant = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]; 
		return new double[][] { { matrix[1][1]/determinant, - matrix[0][1]/determinant }, 
				                { - matrix[1][0]/determinant, matrix[0][0]/determinant } 
		};

	}

}
