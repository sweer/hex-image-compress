package test.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static test.TestUtils.DELTA;
import lattice.DoubleLattice;
import lattice.Lattice;

import org.junit.Ignore;
import org.junit.Test;

import util.ArrayUtils;
import filter.H2O;
import filter.H2O.Direction;
import filter.H2O.H2ORunner;
import filter.SincInterpolatingArray;
import filter.SincRepeater;

public class H2OTest {

    private int expectedOX;
    private int expectedOY;

    @Test
    public final void testSetGetData() {
        // @formatter:off     
        final double[][] source = {
                { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 } 
        };

        expectedOX = 0; 
        expectedOY = 0;
        
        // @formatter:on     

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.O2H);
        DoubleLattice result = H2ORunner.getResult();

        assertTrue(ArrayUtils.arraysAreCloseEnough(source, result.getData(), DELTA));
        assertOxOy(result);
    }

    private void assertOxOy(Lattice result) {
        assertEquals(expectedOX, result.ox);
        assertEquals(expectedOY, result.oy);
    }

    @Test
    public final void testSetGetRawData() {
// @formatter:off     
        final double[][] source = {
                { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 } 
        };
        final double[][] expected = {
                { Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN },
                { Double.NaN, Double.NaN, 1, 2, 3, 4 },
                { Double.NaN, Double.NaN, 5, 6, 7, 8 },
                { Double.NaN, Double.NaN, 9, 10, 11, 12 },
                { Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN }
        };
// @formatter:on     

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.O2H);

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected, H2ORunner.getRawData(), DELTA));
    }

    @Test
    public final void testShearM3Right() {
        // @formatter:off	  
		/*
		Let's take array
		9	10	11	12
		5 	6 	7	8
		1	2	3	4
		
		and shear it right by 0.6. We'll get (N/shifted_by(hex y,x coordinates))
		
		9/0.2(2,-1) 9/1.2(2,0)	10/1.2(2,1)	11/1.2(2,2)	
		5/0.6(1,0)	6/0.6(1,1)	7/0.6(1,2)	8/0.6(1,3)
		1(0,0)		2(0,1)		3(0,2)		4(0,3) 
		
		*/

		final double[][] source = {
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 } 
		};
		
		final double[] a5_8 = { 5, 6, 7, 8 };
		final double[] a9_12 = { 9, 10, 11, 12 };

		final double[][] expected = {
				{ Double.NaN, 1, 2, 3, 4 },
				{ Double.NaN, lpi(0.6, a5_8), lpi(1.6, a5_8), lpi(2.6, a5_8), lpi(3.6, a5_8) }, 
				{ lpi(0.2, a9_12), lpi(1.2, a9_12), lpi(2.2, a9_12), lpi(3.2, a9_12), Double.NaN } 
		};
		
        expectedOX = 1; 
        expectedOY = 0;
		
        // @formatter:on     

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.O2H);
        H2ORunner.shearRightM3(0.6);
        DoubleLattice result = H2ORunner.getResult();

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected, result.getData(), DELTA));
        assertOxOy(result);
    }

    @Test
    public final void testShearM3ThereAndBack() {
        // @formatter:off	  
		/*
		Let's take array and shear it there and back by the same amount. 
		It shall return to the same values minus sinc noise. 
		*/

		final double[][] source = {
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 } 
		};
        // @formatter:on     

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.O2H);
        H2ORunner.shearRightM3(0.6);
        DoubleLattice sheared = H2ORunner.getResult();

        H2ORunner h2oBack = new H2ORunner(SincRepeater.getFactory());
        h2oBack.setData(sheared, Direction.H2O);
        H2ORunner.shearRightM3(-0.6);
        DoubleLattice returned = H2ORunner.getResult();

        assertTrue(ArrayUtils.arraysAreCloseEnough(source, returned.getData(), 0.5));
    }

    @Test
    public final void testShearM2ThereAndBack() {
        // @formatter:off	  
		/*
		Let's take array and shear it there and back by the same amount. 
		It shall return to the same values minus sinc noise. 
		*/

		final double[][] source = {
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 } 
		};
        // @formatter:on     

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.O2H);
        H2ORunner.shearDownRightM2(0.3);
        DoubleLattice sheared = H2ORunner.getResult();

        H2ORunner h2oBack = new H2ORunner(SincRepeater.getFactory());
        h2oBack.setData(sheared, Direction.H2O);
        H2ORunner.shearDownRightM2(-0.3);
        DoubleLattice returned = H2ORunner.getResult();

        assertTrue(ArrayUtils.arraysAreCloseEnough(source, returned.getData(), 1.6));
    }

    @Test
    public final void testShearM1ThereAndBack() {
        // @formatter:off	  
		/*
		Let's take array and shear it there and back by the same amount. 
		It shall return to the same values minus sinc noise. 
		*/

		final double[][] source = {
				{ 1, 2, 3, 4 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 } 
		};
        // @formatter:on     

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.O2H);
        H2ORunner.shearUpM1(0.3);
        DoubleLattice sheared = H2ORunner.getResult();

        H2ORunner h2oBack = new H2ORunner(SincRepeater.getFactory());
        h2oBack.setData(sheared, Direction.H2O);
        H2ORunner.shearUpM1(-0.3);
        DoubleLattice returned = H2ORunner.getResult();

        assertTrue(ArrayUtils.arraysAreCloseEnough(source, returned.getData(), 0.7));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testShearM3RightOutOfRange() {
        // @formatter:off     

        final double[][] source = {
                { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 } 
        };
        
        // @formatter:on     

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.O2H);
        H2ORunner.shearRightM3(16);

    }

    @Test
    public final void testShearM3Left() {
        //@formatter:off    
         /*
         Let's take array
         9 10 11 12
         5 6 7 8
         1 2 3 4
    
         and shear it left by 1.1. We'll get (N/shifted_by(hex y,x coordinates))
    
         9/-0.2(1,-2)       10/-0.2(1,-1)   11/-0.2(1,0)    12/-0.2(1,1)
         5/-0.1(1,-1)       6/-0.1(1,0)     7/-0.1(1,1)     8/-0.1(1,2)
         1(0,0)             2(0,1)          3(0,2)          4(0,3)
         */
     final double[][] data = {
         { 1, 2, 3, 4 },
         { 5, 6, 7, 8 },
         { 9, 10, 11, 12 }
     };
    
     final double[] a5_8 = { 5, 6, 7, 8 };
     final double[] a9_12 = { 9, 10, 11, 12 };
    
     final double[][] expected = {
         { 1, 2, 3, 4, Double.NaN, Double.NaN },
         { Double.NaN, lpi(-0.1, a5_8), lpi(0.9, a5_8), lpi(1.9, a5_8), lpi(2.9, a5_8), Double.NaN },
         { Double.NaN, Double.NaN, lpi(-0.2, a9_12), lpi(0.8, a9_12), lpi(1.8, a9_12), lpi(2.8, a9_12) }
     };

     expectedOX = 0; 
     expectedOY = 0;

     //@formatter:on

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(data, Direction.H2O);
        H2ORunner.shearRightM3(-1.1);
        DoubleLattice result = H2ORunner.getResult();
        double[][] actual = result.getData();

        // for (int y = 0; y < actual.length; y++) {
        // System.out.println(Arrays.toString(actual[y]));
        // }

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected, actual, DELTA));
        assertOxOy(result);
    }

    @Test
    public final void testShearM2DownRight() {
        //@formatter:off
         /*
         Let's take array
         13 14  15  16
         9  10  11  12
         5  6   7   8
         1  2   3   4
    
         and shear it down-right by 0.4. We'll get (N/shifted_by(hex y,x coordinates))

         13/0.2(4,-1)   14/0.6(4,0) 15/0(5,0)   16/0.4(5,1)
         9/0.8(2,0)     10/0.2(3,0) 11/0.6(3,1) 12/0(4,1)
         5/0.4(1,0)     6/0.8(1,1)  7/0.2(2,1)  8/0.6(2,2)
         1(0,0)         2/0.4(0,1)  3/0.8(0,2)  4/0.2(1,2)

5:        15     16/0.4
4: 13/0.2 14/0.6 12
3:        10/0.2 11/0.6
2:         9/0.8  7/0.2   8/0.6
1:         5/0.4  6/0.8   4/0.2
0:         1      2/0.4   3/0.8      
 +---------------------------
   -1      0      1       2       
 
*/    
        final double[][] source = {
             { 1,   2,  3,  4 },
             { 5,   6,  7,  8 },
             { 9,   10, 11, 12 },
             { 13,  14, 15, 16 }
        };
        final double[] a5_2 = { 5, 2 };
        final double[] a9_3 = { 9, 6, 3 };
        final double[] a13_4 = { 13, 10, 7, 4 };
        final double[] a14_8 = { 14, 11, 8 };
        final double[] a16 = { 16 };
/*
0:         1      2/0.4   3/0.8      
1:         5/0.4  6/0.8   4/0.2
2:         9/0.8  7/0.2   8/0.6
3:        10/0.2 11/0.6
4: 13/0.2 14/0.6 12
5:        15     16/0.4
 +---------------------------
   -1      0      1       2       
*/
        final double[][] expected = {
             { Double.NaN,              1,      lpi(1.4, a5_2), lpi(2.8, a9_3) }, 
             { Double.NaN,      lpi(0.4, a5_2), lpi(1.8, a9_3), lpi(3.2, a13_4) },
             { Double.NaN,      lpi(0.8, a9_3), lpi(2.2, a13_4), lpi(2.6, a14_8) },
             { Double.NaN,      lpi(1.2, a13_4), lpi(1.6, a14_8), Double.NaN },
             { lpi(0.2, a13_4), lpi(0.6, a14_8),        12,       Double.NaN }, 
             { Double.NaN,      15,              lpi(0.4, a16),   Double.NaN }
        };

        expectedOX = 1; 
        expectedOY = 0;
    
     //@formatter:on

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.TEST_O2H);
        H2ORunner.shearDownRightM2(0.4);
        DoubleLattice result = H2ORunner.getResult();

        // System.out.println("-----------------------------------------------------------------------");
        // ArrayUtils.print2dArray(result.data);

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected, result.getData(), DELTA));
        assertOxOy(result);
    }

    @Test
    public final void testShearM2UpLeft() {
        //@formatter:off

         /*
         Let's take array
         13 14  15  16
         9  10  11  12
         5  6   7   8
         1  2   3   4
    
         and shear it up-left by 0.5. We'll get (N/shifted_by(hex y,x coordinates))
    
         13/-0.5(2,1)   14/0(1,3)       15/-0.5(1,4)    16/0(0,6)
         9/0(1,1)       10/-0.5(1,2)    11/0(0,4)       12/-0.5(0,5)
         5/-0.5(1,0)    6/0(0,2)        7/-0.5(0,3)     8/0(-1,5)
         1(0,0)         2/-0.5(0,1)     3/0(-1,3)       4/-0.5(-1,4)


 2:         13/-0.5
 1: 5/-0.5  9/0       10/-0.5       14/0        15/-0.5     
 0: 1       2/-0.5     6/0          7/-0.5      11/0        12/-0.5     16/0 
-1:                                 3/0         4/-0.5      8/0
-------------------------------------------------------------------------
    0         1          2           3           4           5           6
    
     */
    
         final double[][] source = {
             { 1, 2, 3, 4 },
             { 5, 6, 7, 8 },
             { 9, 10, 11, 12 },
             { 13, 14, 15, 16 }
         };
    
         final double[] a5_2 = { 5, 2 };
         final double[] a13_4 = { 13, 10, 7, 4 };
         final double[] a15_12 = { 15, 12 };
/*
-1:                                 3/0         4/-0.5      8/0
 0: 1       2/-0.5     6/0          7/-0.5      11/0        12/-0.5     16/0 
 1: 5/-0.5  9/0       10/-0.5       14/0        15/-0.5     
 2:         13/-0.5
-------------------------------------------------------------------------
    0         1          2           3           4           5           6
 */
         final double[][] expected = {
             { Double.NaN,      Double.NaN,     Double.NaN,     3,               lpi(2.5, a13_4), 8,                Double.NaN  },  
             { 1,               lpi(0.5, a5_2), 6,              lpi(1.5, a13_4), 11,              lpi(0.5, a15_12), 16          },
             { lpi(-0.5, a5_2), 9,              lpi(0.5, a13_4),14,              lpi(-0.5, a15_12),Double.NaN,      Double.NaN  },
             { Double.NaN,      lpi(-0.5, a13_4), Double.NaN,   Double.NaN,      Double.NaN,       Double.NaN,      Double.NaN  }
         };

         expectedOX = 0; 
         expectedOY = 1;
     
     //@formatter:on    
        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.TEST_H2O);
        H2ORunner.shearDownRightM2(-0.5);
        DoubleLattice result = H2ORunner.getResult();

        // System.out.println("-----------------------------------------------------------------------");
        // ArrayUtils.print2dArray(result.data);

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected, result.getData(), DELTA));
        assertOxOy(result);
    }

    @Test
    public final void testShearM1Up() {
        //@formatter:off
        /*
        Let's take array
        13 14 15 16
        9 10 11 12
        5 6 7 8
        1 2 3 4
        
        and shear it up by 0.7. We'll get (N/shifted_by(hex y,x coordinates))
        
        13(3,0) 14/0.7(3,1)     15/0.4(2,2)     16/0.1(1,3)
        9(2,0)  10/0.7(2,1)     11/0.4(1,2)     12/0.1(0,3)
        5(1,0)  6/0.7(1,1)      7/0.4(0,2)      8/0.1(-1,3)
        1(0,0)  2/0.7(0,1)      3/0.4(-1,2)     4/0.1(-2,3)
        */

        final double[][] source = { 
                { 1, 2, 3, 4 }, 
                { 5, 6, 7, 8 }, 
                { 9, 10, 11, 12 }, 
                { 13, 14, 15, 16 } 
        };

        final double[] a2_14 = { 2, 6, 10, 14 };
        final double[] a3_15 = { 3, 7, 11, 15 };
        final double[] a4_16 = { 4, 8, 12, 16 };

        final double[][] expected = {
                { Double.NaN,   Double.NaN,         Double.NaN,         lpi(0.1, a4_16) }, 
                { Double.NaN,   Double.NaN,         lpi(0.4, a3_15),    lpi(1.1, a4_16) }, 
                { 1,            lpi(0.7, a2_14),    lpi(1.4, a3_15),    lpi(2.1, a4_16) },
                { 5,            lpi(1.7, a2_14),    lpi(2.4, a3_15),    lpi(3.1, a4_16) },
                { 9,            lpi(2.7, a2_14),    lpi(3.4, a3_15),    Double.NaN },
                { 13,           lpi(3.7, a2_14),    Double.NaN,         Double.NaN } 
        };

        expectedOX = 0; 
        expectedOY = 2;
        
        //@formatter:on

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.TEST_O2H);
        H2ORunner.shearUpM1(0.7);
        DoubleLattice result = H2ORunner.getResult();

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected, result.getData(), DELTA));
        assertOxOy(result);
    }

    @Test
    public final void testShearM1Down() {
        //@formatter:off

         /*
         Let's take array
         13 14 15
         9 10 11
         5 6 7
         1 2 3
    
         and shear it down by 0.7. We'll get (N/shifted_by(hex y,x coordinates))
    
         13(3,0)    14/-0.7(3,1)    15/-0.4(4,2)
         9(2,0)     10/-0.7(2,1)    11/-0.4(3,2)
         5(1,0)     6/-0.7(1,1)     7/-0.4(2,2)
         1(0,0)     2/-0.7(0,1)     3/-0.4(1,2)
         */
    
         final double[][] source = {
                 { 1, 2, 3 },
                 { 5, 6, 7 },
                 { 9, 10, 11 },
                 { 13, 14, 15 }
         };
    
         final double[] a2_14 = { 2, 6, 10, 14 };
         final double[] a3_15 = { 3, 7, 11, 15 };
    
         final double[][] expected = {
                 { 1,           lpi(-0.7, a2_14),   Double.NaN },
                 { 5,           lpi( 0.3, a2_14),   lpi(-0.4, a3_15) },
                 { 9,           lpi( 1.3, a2_14),   lpi( 0.6, a3_15) },
                 { 13,          lpi(2.3, a2_14),    lpi( 1.6, a3_15) },
                 { Double.NaN,  Double.NaN,         lpi( 2.6, a3_15) } 
         };
         expectedOX = 0; 
         expectedOY = 0;
         
         //@formatter:on

        H2ORunner H2ORunner = new H2ORunner(SincRepeater.getFactory());
        H2ORunner.setData(source, Direction.H2O);
        H2ORunner.shearUpM1(-0.7);
        DoubleLattice result = H2ORunner.getResult();

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected, result.getData(), DELTA));
        assertOxOy(result);
    }

    @Test
    public final void testO2HAndBack() {
        /*
         * Let's take array and transform it O2H and then H2ORunner.
         * It shall return to the same values minus sinc noise.
         */

        final double[][] source = new double[200][200];
        for (int y = 0; y < source.length; y++) {
            double row[] = source[y];
            for (int x = 0; x < row.length; x++) {
                row[x] = Math.sin(x) * y + Math.sin(y) * x;
            }
        }

        DoubleLattice hexLattice = H2O.o2h(source);
        DoubleLattice actualLattice = H2O.h2o(hexLattice);

        double[] noise = ArrayUtils.subtract2DArraysAs1D(source, actualLattice.getData());
        double psnr = PSNR.ofNoise(noise, 200);

        assertTrue("psnr = " + psnr, psnr > 43);
    }

    @Test
    @Ignore
    public final void testO2HShearingBuffer() {
        /*
         * There was a bug: H2O2.shearDownRightM2 IllegalArgumentException:
         * Shearing O2H by
         * 0.06939514089790044 wouldn't fit into the data lattice.
         * This case will throw an exception if error reoccurs. However, it
         * takes 10 minutes to complete,
         * so don't unignore it often.
         */

        final double[][] source = new double[512][512];
        for (int y = 0; y < source.length; y++) {
            double row[] = source[y];
            for (int x = 0; x < row.length; x++) {
                row[x] = Math.sin(x) * y + Math.sin(y) * x;
            }
        }

        DoubleLattice hexLattice = H2O.o2h(source);
        assertTrue("Anything is ok as long as it doesn't throw an exception", true);
    }

    private static double lpi(double shift, double[] points) {
        SincInterpolatingArray sia = new SincRepeater(points);
        return sia.get(shift);
    }

}
