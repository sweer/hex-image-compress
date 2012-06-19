package test.filter;

import static org.junit.Assert.*;

import org.junit.Test;

public class PSNRTest {

	
	@Test
	public final void testPSNRnonzero() {
		double[] original = {1, 2, 3};
		double[] distorted = {2, 4, 6}; 
		// (1 + 4 + 9)/3 = 4 2/3, max = 3 ==> 9 / 4.666 ==> 2.85
		assertEquals(2.85, PSNR.ofArray(original, distorted), 0.01); 
	}

	@Test
	public final void testPSNRZero() {
		double[] original = {1, 2, 3};
		assertEquals(Double.POSITIVE_INFINITY, PSNR.ofArray(original, original), 0.01); 
	}

	
	
	
}
