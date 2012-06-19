package test.filter;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import filter.InterpolatingArray;
import filter.SincRepeater;

import static test.TestUtils.DELTA;

public class SincRepeaterTest {

    @Test
    public final void testMirroredGet() {
        // 1 2 3 4 5 6 1 2 3 4 5 6 1 2 3 4
        // -6-5-4-3-2-1 0 1 2 3 4 5 6 7
        double[] points = { 1, 2, 3, 4, 5, 6 };

        InterpolatingArray sia = new SincRepeater(points, 0);

        assertEquals(1, sia.get(0), DELTA);
        assertEquals(2, sia.get(1), DELTA);
        assertEquals(2, sia.get(-5), DELTA);
        assertEquals(5, sia.get(-8), DELTA);
        assertEquals(2, sia.get(7), DELTA);
        assertEquals(3, sia.get(14), DELTA);
    }

    @Test
    public final void testLowEnoughDistortionForOnes() {

        double[] points = new double[100];
        Arrays.fill(points, 1);
        int order = 4;

        InterpolatingArray sia = new SincRepeater(points, order);
        double[] shifted = sia.getShiftedArray(0.5);
        InterpolatingArray siaShifted = new SincRepeater(shifted, order);
        double[] shiftedBack = siaShifted.getShiftedArray(-0.5);

        double psnr = PSNR.ofArray(points, shiftedBack);

        assertTrue("psnr = " + psnr, psnr > 77);
    }

    @Test
    public final void testLowEnoughDistortionForBrown() {
        int order = 2;
        double[] points = new double[100];
        Random random = new Random(10);
        points[0] = random.nextDouble();
        for (int i = 1; i < points.length; i++)
            points[i] = points[i - 1] + random.nextDouble() - 0.5;

        InterpolatingArray sia = new SincRepeater(points, order);
        double[] shifted = sia.getShiftedArray(0.5);
        InterpolatingArray siaShifted = new SincRepeater(shifted, order);
        double[] shiftedBack = siaShifted.getShiftedArray(-0.5);

        double psnr = PSNR.ofArray(points, shiftedBack);

        assertTrue("psnr = " + psnr, psnr > 39);
    }

    @Test
    public final void testDistortionShiftTwice() {
        int order = 2;
        double[] points = new double[100];
        Random random = new Random(37);
        points[0] = random.nextDouble();
        for (int i = 1; i < points.length; i++)
            points[i] = points[i - 1] + random.nextDouble() - 0.5;

        InterpolatingArray sia = new SincRepeater(points, order);
        double[] shifted = sia.getShiftedArray(0.5);
        InterpolatingArray siaShifted = new SincRepeater(shifted, order);
        double[] shiftedOnce = siaShifted.getShiftedArray(-0.5);

        sia = new SincRepeater(shiftedOnce, order);
        shifted = sia.getShiftedArray(0.5);
        siaShifted = new SincRepeater(shifted, order);
        double[] shiftedTwice = siaShifted.getShiftedArray(-0.5);

        double psnr = PSNR.ofArray(shiftedOnce, shiftedTwice);

        assertTrue("psnr = " + psnr, psnr > 69);
    }

    @Test
    public final void testDistortionOfCorrectedShiftByOne() {
        int order = 2;
        double[] points = new double[100];
        Random random = new Random(12);
        points[0] = random.nextDouble();
        for (int i = 1; i < points.length; i++)
            points[i] = points[i - 1] + random.nextDouble() - 0.5;

        InterpolatingArray sia = new SincRepeater(points, order);
        double[] shifted = sia.getShiftedArray(1);
        InterpolatingArray siaShifted = new SincRepeater(shifted, order);
        double[] shiftedBack = siaShifted.getShiftedArray(-1);

        // for (int i = 0; i < points.length; i++) {
        // System.out.printf("%d: %5.2f %5.2f %5.2f\n", i, points[i],
        // shifted[i], shiftedBack[i]);
        // if (i == 4)
        // i = points.length - 4;
        // }
        double psnr = PSNR.ofArray(points, shiftedBack);
        // System.out.println("psnr = " + psnr);

        assertTrue("psnr = " + psnr, psnr > 303);
    }

}
