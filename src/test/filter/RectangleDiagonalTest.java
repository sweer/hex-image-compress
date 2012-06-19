package test.filter;

import static org.junit.Assert.*;
import static test.TestUtils.DELTA;

import org.junit.Test;

import filter.RectangleDiagonal;


public class RectangleDiagonalTest {

	@Test
	public final void testGetDiagonalTallRectangleAfterCorner() {
		double[][] tallRectangle = { 
				{ 1, 2, 3 },
				{ 4, 5, 6 },
				{ 7, 8, 9 },
				{ 10, 11, 12 }
		};
		
		double[] expected = { 11, 9 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(4, tallRectangle); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}

	@Test
	public final void testGetDiagonalTallRectangleCorner() {
		double[][] tallRectangle = { 
				{ 1, 2, 3 },
				{ 4, 5, 6 },
				{ 7, 8, 9 },
				{ 10, 11, 12 }
		};
		
		double[] expected = { 10, 8, 6 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(3, tallRectangle); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}
	
	@Test
	public final void testGetDiagonalTallRectangleFirst() {
		double[][] tallRectangle = { 
				{ 1, 2, 3 },
				{ 4, 5, 6 },
				{ 7, 8, 9 },
				{ 10, 11, 12 }
		};
		
		double[] expected = { 1 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(0, tallRectangle); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}

	@Test
	public final void testGetDiagonalTallRectangleBeforeCorner() {
		double[][] tallRectangle = { 
				{ 1, 2, 3 },
				{ 4, 5, 6 },
				{ 7, 8, 9 },
				{ 10, 11, 12 }
		};
		
		double[] expected = { 4, 2 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(1, tallRectangle); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}

	@Test
	public final void testGetDiagonalLongRectangleFirstShorterAfterCorner() {
		double[][] longRectangle = { 
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 }
		};
		
		double[] expected = { 11, 8 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(4, longRectangle); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}

	@Test
	public final void testGetDiagonalLongRectangleLast() {
		double[][] longRectangle = { 
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 }
		};
		
		double[] expected = { 12 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(5, longRectangle); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}

	@Test
	public final void testGetDiagonalLongRectangleAfterCorner() {
		double[][] longRectangle = { 
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 }
		};
		
		double[] expected = { 10, 7, 4 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(3, longRectangle); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}

	
	
	@Test
	public final void testGetDiagonalLongRectangleBeforeCorner() {
		double[][] longRectangle = { 
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 }
		};
		
		double[] expected = { 5, 2 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(1, longRectangle); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}

	@Test
	public final void testGetDiagonalSquareMain() {
		double[][] square = { 
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 },
				{ 13, 14, 15, 16 }
		};
		
		double[] expected = { 13, 10, 7, 4 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(3, square); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}

	@Test
	public final void testGetDiagonalSquareAfterMain() {
		double[][] square = { 
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 },
				{ 13, 14, 15, 16 }
		};
		
		double[] expected = { 15, 12 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(5, square); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}

	@Test
	public final void testGetDiagonalSquareBeforeMain() {
		double[][] square = { 
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 },
				{ 13, 14, 15, 16 }
		};
		
		double[] expected = { 9, 6, 3 }; 
		
		double[] actual = RectangleDiagonal.getDiagonal(2, square); 
		
		assertArrayEquals(expected, actual, DELTA); 
	}
	

}
