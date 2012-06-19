package test.util;

import static org.junit.Assert.assertArrayEquals;
import static test.TestUtils.DELTA;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import util.ArrayUtils;

public class ArrayUtilsTest {

    @Test
    public final void testArraySubtract() {
    
        double a[][] = { { 1, 2 }, { 3, 4 } };
        double b[][] = { { 0, 1 }, { 4, 2 } };
        double expected[] = { 1, 1 ,  -1, 2  }; 
    
        assertArrayEquals(expected, ArrayUtils.subtract2DArraysAs1D(a, b), DELTA);
    }

    @Test
    public final void testJoinArrays() {
    
        List<byte[]> source = new LinkedList<byte[]>(); 
        source.add(new byte[] { 2, 5 }); 
        source.add(new byte[] { 3, 1, 7 }); 
        byte[] expected = { 2, 5, 3, 1, 7 }; 
        assertArrayEquals(expected, ArrayUtils.joinArrays(source));
    }

}
