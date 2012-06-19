package filter;

public class RectangleDiagonal {

	public static double[] getDiagonal(int n, double[][] data) {
		final int height = data.length;
		final int width = data[0].length;

		DiagonalParameters dp = DiagonalParameters.getDiagonalParameters(width,
				height, n);

		double diagonal[] = new double[dp.diagonalLength];

		for (int i = 0; i < dp.diagonalLength; i++) {
			diagonal[i] = data[dp.startY - i][dp.startX + i];
		}

		return diagonal;
	}

	static public class DiagonalParameters {
		final public int diagonalLength;
		final public int startX;
		final public int startY;

		private DiagonalParameters(int diagonalLength, int startX, int startY) {
			this.diagonalLength = diagonalLength;
			this.startX = startX;
			this.startY = startY;
		}

		public static DiagonalParameters getDiagonalParameters(int width,
				int height, int n) {
			if (width == height) {
				return getSquareDiagonalParameters(width, height, n);
			} else if (width < height) {
				return getTallRectangleDiagonalParameters(width, height, n);
			} else {
				return getLongRectangleDiagonalParameters(width, height, n);
			}
		}

		private static DiagonalParameters getLongRectangleDiagonalParameters(
				int width, int height, int n) {
			final int diagonalLength;
			final int startX;
			final int startY;
			if (n < height) {
				startX = 0;
				startY = n;
				diagonalLength = n + 1;
			} else {
				startX = n - height + 1;
				startY = height - 1;
				final int fromTheLastColumn = (width - 1) - startX;
				if (fromTheLastColumn < height - 1) {
					diagonalLength = fromTheLastColumn + 1;
				} else {
					diagonalLength = height;
				}
			}

			return new DiagonalParameters(diagonalLength, startX, startY);
		}

		private static DiagonalParameters getTallRectangleDiagonalParameters(
				int width, int height, int n) {

			final int diagonalLength;
			final int startX;
			final int startY;
			if (n < height) {
				startX = 0;
				startY = n;
				if (n < width - 1) {
					diagonalLength = n + 1;
				} else {
					diagonalLength = width;
				}

			} else {
				startX = n - height + 1;
				startY = height - 1;
				final int fromTheLastColumn = (width - 1) - startX;
				diagonalLength = fromTheLastColumn + 1;
			}

			return new DiagonalParameters(diagonalLength, startX, startY);
		}

		private static DiagonalParameters getSquareDiagonalParameters(
				int width, int height, int n) {

			final int distanceFromCornerDiagonal = Math.abs(n - (width - 1));
			final int diagonalLength = width - distanceFromCornerDiagonal;
			final int startX;
			final int startY;
			if (n < height) {
				startX = 0;
				startY = n;
			} else {
				startX = n - width + 1;
				startY = height - 1;
			}

			return new DiagonalParameters(diagonalLength, startX, startY);
		}
	}

}
