package main;

import static test.TestUtils.DOESNT_MATTER0;
import lattice.LongLattice;
import util.ArrayUtils;
import filter.HexByteRavelet;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        int size = 64; 
        long[][] data = new long[size][size]; 
        data[16][16] = 1; 
        LongLattice source = new LongLattice( data, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);
        r.decode();

        ArrayUtils.saveArrayAsTextFileSkipNAN(data, "heatmap.txt");

	}

}
