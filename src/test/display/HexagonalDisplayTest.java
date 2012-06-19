package test.display;

import static org.junit.Assert.*;

import org.junit.Test;

import display.HexagonalDisplay;

import util.ArrayUtils;
import static test.TestUtils.DELTA;

public class HexagonalDisplayTest {

    @Test
    public final void testSingleCell() {
        double src[][] = { { 1 } };
        double res[][] = new double[9][8];
// @formatter:off
        double expected[][] = { 
                { 0, 0, 0, 0, 0, 0, 0, 0 }, 
                { 0, 0, 0, 1, 1, 0, 0, 0 },
                { 0, 0, 1, 1, 1, 1, 0, 0 }, 
                { 0, 1, 1, 1, 1, 1, 1, 0 }, 
                { 0, 1, 1, 1, 1, 1, 1, 0 },
                { 0, 1, 1, 1, 1, 1, 1, 0 }, 
                { 0, 0, 1, 1, 1, 1, 0, 0 }, 
                { 0, 0, 0, 1, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 } 
        };
// @formatter:on
        HexagonalDisplay.render(src, res);

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected, res, DELTA));

    }

    @Test
    public final void test9cells() {
//@formatter:off
        double src[][] = { 
                { 7, 8, 9 }, 
                { 4, 5, 6 }, 
                { 1, 2, 3 } 
        };
        double res[][] = new double[19][24];
        double expected[][] = {
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 7, 7, 0, 0, 0, 0, 8, 8, 0, 0, 0, 0, 9, 9, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 7, 7, 7, 7, 0, 0, 8, 8, 8, 8, 0, 0, 9, 9, 9, 9, 0, 0, 0, 0, 0, 0, 0 },
                { 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 0, 0, 0, 0, 0, 0 },
                { 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 0, 0, 0, 0, 0, 0 },
                { 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 0, 0, 0, 0, 0, 0 },
                { 0, 7, 7, 7, 7, 4, 4, 8, 8, 8, 8, 5, 5, 9, 9, 9, 9, 6, 6, 0, 0, 0, 0, 0 },
                { 0, 0, 7, 7, 4, 4, 4, 4, 8, 8, 5, 5, 5, 5, 9, 9, 6, 6, 6, 6, 0, 0, 0, 0 },
                { 0, 0, 0, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 0, 0, 0 },
                { 0, 0, 0, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 0, 0, 0 },
                { 0, 0, 0, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 0, 0, 0 },
                { 0, 0, 0, 0, 4, 4, 4, 4, 1, 1, 5, 5, 5, 5, 2, 2, 6, 6, 6, 6, 3, 3, 0, 0 },
                { 0, 0, 0, 0, 0, 4, 4, 1, 1, 1, 1, 5, 5, 2, 2, 2, 2, 6, 6, 3, 3, 3, 3, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3 },
                { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3 },
                { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3 },
                { 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 3, 3, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } 
        };
// @formatter:on
        HexagonalDisplay.render(src, res);

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected, res, DELTA));

    }
}