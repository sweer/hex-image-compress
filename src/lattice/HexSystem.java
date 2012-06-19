package lattice;

import util.MathUtils;

public class HexSystem {

	public static final double[][] hexBaseMatrix = { { 1, 0 },
			{ 0.5, Math.sqrt(3) / 2.0 } };

	public static final double[][] hexBaseMatrixDensity1 = MathUtils
			.multiplyMatrixByNumber(hexBaseMatrix, Math.sqrt(2 / Math.sqrt(3)));

	public static final double[][] hexInverseBaseMatrix = MathUtils
			.invert2x2Matrix(hexBaseMatrix);

	public static Point[] ort2Hex(Point[] ortPoints) {
		Point[] hexPoints = Point.transformPoints(ortPoints,
				hexInverseBaseMatrix);
		return hexPoints;
	}

	public static Point[] hex2Ort(Point[] ortPoints) {
		Point[] hexPoints = Point.transformPoints(ortPoints, hexBaseMatrix);
		return hexPoints;
	}

}
