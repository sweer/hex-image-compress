package lattice;

import static java.lang.Math.abs;

public class Point {
	public double x;
	public double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public boolean isClose(Point p, double delta) {
		return abs(p.x - x) < delta && abs(p.y - y) < delta;
	}

	public Point multiplyByMatrix2x2(double[][] matrix) {
		double rx = x * matrix[0][0] + y * matrix[1][0];
		double ry = x * matrix[0][1] + y * matrix[1][1];

		return new Point(rx, ry);
	}

	public static Point[] transformPoints(Point[] srcPoints,
			double[][] transformMatrix) {
		Point dstPoints[] = new Point[srcPoints.length];
		for (int i = 0; i < srcPoints.length; i++) {
			Point point = srcPoints[i].multiplyByMatrix2x2(transformMatrix);
			dstPoints[i] = point;
		}
		return dstPoints;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}

}