package test.lattice;

import static java.lang.Double.NaN;
import static lattice.LongLattice.NAN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static test.TestUtils.DELTA;
import static test.TestUtils.DOESNT_MATTER0;

import java.util.Arrays;
import java.util.List;

import lattice.DoubleLattice;
import lattice.LongLattice;
import lattice.LongLattice.Run;

import org.junit.Test;

import util.ArrayUtils;


public class QuantizedLongLatticeTest  {

    @Test
    public final void testDequantization() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { 60, 255 },   
                { 0, LongLattice.NAN }
            }, 1, 2, -10, 255/36.0
            );  

        DoubleLattice expected = new DoubleLattice(new double[][] {
                { -1.5, 26 },   
                { -10, NaN }   
            }, 1, 2
            ); 
        //@formatter:on
        DoubleLattice actual = source.getDequantizedLattice();

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected.getData(), actual.getData(), 0.1));
        assertEquals(expected.ox, actual.ox, DELTA);
        assertEquals(expected.oy, actual.oy, DELTA);
    }

    @Test
    public final void testQuantization() {
        //@formatter:off
        DoubleLattice source = new DoubleLattice(new double[][] {
                { -1.5, 26 },   
                { -10, NaN }   
            }, 1, 2
            ); 
    
        LongLattice expected = new LongLattice(new long[][] {
                { 60, 255 },   
                { 0, LongLattice.NAN }
            }, 1, 2, -10, 255/36.0
            );  
        //@formatter:on
        LongLattice actual = LongLattice.fromLattice(source);

        assertTrue(ArrayUtils.arraysAreCloseEnough(expected.getData(),
                actual.getData()));
        assertEquals(expected.min, actual.min, DELTA);
        assertEquals(expected.factor, actual.factor, DELTA);
        assertEquals(expected.ox, actual.ox, DELTA);
        assertEquals(expected.oy, actual.oy, DELTA);
    }

    @Test
    public final void testNANPositions() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { NAN, NAN, NAN, NAN, NAN, NAN }, 
                { NAN, NAN,  12,  4, 7,   0 }, 
                { NAN,  0,   1,   4, 7,   NAN },
                { 22,   1,   4,   7, NAN, NAN },
                { 11, NAN, NAN, NAN, NAN, NAN } 
            },  DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        short[][] expected = {
                { 5, 0 }, 
                { 1, 6 }, 
                { 0, 5 }, 
                { -1, 4 }, 
                { -1, 1 }
        }; 
        
        //@formatter:on

        assertArrayEquals(expected, source.getNANPositions());
    }

    @Test
    public final void testNANPositionStream() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { NAN, NAN, NAN, NAN, NAN, NAN }, 
                { NAN, NAN,  12,  4, 7,   0 }, 
                { NAN,  0,   1,   4, 7,   NAN },
                { 22,   1,   4,   7, NAN, NAN },
                { 11, NAN, NAN, NAN, NAN, NAN } 
            },  DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        short[][] expected = {
                { 5, 0 }, 
                { -4, 6 }, 
                { -1, -1 }, 
                { -1, -1 }, 
                { 0, -3 }
        }; 
        
        //@formatter:on

        assertArrayEquals(expected, source.getNANPositionStream());
    }

    @Test
    public final void testNANPositionLoad() {
        //@formatter:off
        short[][] source = {
                { 5, 0 }, 
                { -4, 6 }, 
                { -1, -1 }, 
                { -1, -1 }, 
                { 0, -3 }
        }; 
        
        short[][] expected = {
                { 5, 0 }, 
                { 1, 6 }, 
                { 0, 5 }, 
                { -1, 4 }, 
                { -1, 1 }
        }; 
        //@formatter:on
        LongLattice lattice = new LongLattice(new long[5][6], DOESNT_MATTER0,
                DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0);

        lattice.loadNANPositionStream(source); 
        
        assertArrayEquals(expected, lattice.getNANPositions());
    }

    @Test
    public final void testNANPositionLoadSetsNANs() {
        //@formatter:off
        short[][] source = {
                { 5, 0 }, 
                { -4, 6 }, 
                { -1, -1 }, 
                { -1, -1 }, 
                { 0, -3 }
        }; 
        
        LongLattice lattice = new LongLattice(new long[][] {
                { 1, 1, 1, 1, 1, 1 }, 
                { 1, 1,  12,  4, 7,   0 }, 
                { 1,  0,   1,   4, 7,   1 },
                { 22,   1,   4,   7, 1, 1 },
                { 11, 1, 1, 1, 1, 1 } 
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        long[][] expected = new long[][] {
                { NAN, NAN, NAN, NAN, NAN, NAN }, 
                { NAN, NAN,  12,  4, 7,   0 }, 
                { NAN,  0,   1,   4, 7,   NAN },
                { 22,   1,   4,   7, NAN, NAN },
                { 11, NAN, NAN, NAN, NAN, NAN } 
            };
        
        //@formatter:on
        
        lattice.loadNANPositionStream(source); 
        
        assertArrayEquals(expected, lattice.getData());
    }
    
    @Test
    public final void testGetDataAsByteStream() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { NAN, NAN, NAN,  NAN,  NAN,   NAN }, 
                { NAN, NAN,  240, NAN,  NAN,     0 }, 
                { NAN,  0,   NAN,  4,    7,    NAN },
                { 22,   1,    4,   7,   NAN,   NAN },
                { 11,  NAN,  NAN, NAN,  NAN,   NAN } 
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        byte[] expected = { 
                -16, 0, 0, 0, 0, 0, 4, 7, 22, 1, 4, 7, 11 
        }; 
        //@formatter:on

        byte[] actual = source.getDataAsByteStream(); 
        
        assertArrayEquals(expected, actual);
    }

    @Test
    public final void testLoadDataAsByteStream() {
        //@formatter:off
        LongLattice field = new LongLattice(new long[][] {
                { NAN, NAN, NAN,  NAN,  NAN,   NAN }, 
                { NAN, NAN,  35,   35,   35,   0 }, 
                { NAN,  17,   17,  14,   27,    NAN },
                { 17,   33,   34,  57,   NAN,   NAN },
                { 111,  NAN,  NAN, NAN,  NAN,   NAN } 
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        long[][] expected = new long[][] {
                { NAN, NAN, NAN,  NAN,  NAN,   NAN }, 
                { NAN, NAN,  240,   0,    0,     0 }, 
                { NAN,  0,    0,    4,    7,    NAN },
                { 22,   1,    4,    7,   NAN,   NAN },
                { 11,  NAN,  NAN,  NAN,  NAN,   NAN } 
            }; 

        byte[] source = { 
                -16, 0, 0, 0, 0, 0, 4, 7, 22, 1, 4, 7, 11 
        }; 
        //@formatter:on

        field.loadDataAsByteStream(source); 
        
        assertArrayEquals(expected, field.getData());
    }

    @Test
    public final void testGetZeroNanRuns() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { NAN, NAN, NAN,  NAN,  NAN,   NAN }, 
                { NAN, NAN,  240,  5,    6,     0 }, 
                { NAN,  0,    7,  4,    7,    NAN },
                { 22,   1,    4,   7,   NAN,   NAN },
                { 11,  NAN,  NAN, NAN,  NAN,   NAN } 
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        //@formatter:on

        List<Run> runs = source.getNanRuns(); 
        
        assertEquals(0, runs.size());
    }

    @Test
    public final void testGetNanRuns() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { NAN, NAN, NAN,  NAN,  NAN,   NAN }, 
                { NAN, NAN,  240, NAN,  NAN,     0 }, 
                { NAN,  0,   NAN,  4,    7,    NAN },
                { 22,   1,    4,   7,   NAN,   NAN },
                { 11,  NAN,  NAN, NAN,  NAN,   NAN } 
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 
        //@formatter:on

        List<Run> runs = source.getNanRuns(); 
        
        assertTrue(runs.get(0).x == 3 && runs.get(0).y == 1 && runs.get(0).length == 2 
                && runs.get(1).x == 2 && runs.get(1).y == 2 && runs.get(1).length == 1);
    }

    @Test
    public final void testLoadNanRuns() {
        //@formatter:off
        LongLattice field = new LongLattice(new long[][] {
                { NAN, NAN, NAN,  NAN,  NAN,   NAN }, 
                { NAN, NAN,  240,   0,    0,     0 }, 
                { NAN,  0,    0,    4,    7,    NAN },
                { 22,   1,    4,    7,   NAN,   NAN },
                { 11,  NAN,  NAN,  NAN,  NAN,   NAN } 
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        List<Run> source = Arrays.asList(new Run[] { new Run((short)3,(short)1,(short)2), new Run((short)2,(short)2,(short)1) } ); 
        
        long[][] expected = new long[][] {
                { NAN, NAN, NAN,  NAN,  NAN,   NAN }, 
                { NAN, NAN,  240, NAN,  NAN,     0 }, 
                { NAN,  0,   NAN,  4,    7,    NAN },
                { 22,   1,    4,   7,   NAN,   NAN },
                { 11,  NAN,  NAN, NAN,  NAN,   NAN } 
            };
        //@formatter:on

        field.loadNanRuns(source); 
        
        assertArrayEquals(expected, field.getData());
    }


    
}
