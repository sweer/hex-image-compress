package test.rangecoder;
/**

 * Demo program for Range Encoding Compressor implementation.
 * See http://winterwell.com/software/compressor.php
 */

import java.util.Random;

import rangecoder.Compressor;

class CompressorTest {

	public static void main(String[] args) {
		byte[] test = new byte[300000];
		Random myRND = new Random();
		for (int i = 0; i < test.length; i++) {
			test[i] = (byte) (myRND.nextInt() & 0xaa);
		}

		byte[] compd = Compressor.compress(test);
		
		System.out.println("Compressed " + test.length + " bytes to " + compd.length + " bytes");
		System.out.println("Compression " + compd.length * 100/test.length + "%");
		
		byte[] result = Compressor.decompress(compd);
		
		if (result.length != test.length) {
			throw new RuntimeException("DECODE LENGTH ERROR");
		}
		for (int i = 0; i < test.length; i++) {
			if (test[i] != result[i]) {
				throw new RuntimeException("DECODE ERROR");
			}
		}
		System.out.println("Done!");
	}
}
