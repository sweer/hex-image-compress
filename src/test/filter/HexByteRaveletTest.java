package test.filter;

import static lattice.LongLattice.NAN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static test.TestUtils.DOESNT_MATTER0;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lattice.DoubleLattice;
import lattice.LongLattice;

import org.junit.Test;

import filter.H2O;
import filter.HexByteRavelet;

import util.ImageUtils;

public class HexByteRaveletTest {

    @Test
    public final void testLevel0Encode() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { NAN, 2, 2, 3 }, 
                { NAN, 4, 5, NAN },
                { 6,   7, 8, NAN }
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0
            ); 

        long[][] expected = {
                { NAN, 255,  3,  0 }, 
                { NAN,  0,   1,  NAN },
                {  5,   2,   6,  NAN }
            }; 
        
        byte[][] expectedBits = { 
                { 0, 0, 0, 0 }, 
                { 0, 0, 0, 0 }, 
                { 2, 0, 2, 0 }
        };
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);
        r.encodeLevel(0);

        assertArrayEquals(expected, r.getData());
        assertArrayEquals(expectedBits, r.getRemainderBits()[0]);
    }

    @Test
    public final void testLevel0Decode() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { NAN, 255,  3,  0 }, 
                { NAN,  0,   1,  NAN },
                {  5,   2,   6,  NAN }
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        byte[][][] sourceBits = { 
                { 
                    { 0, 0, 0, 0 }, 
                    { 0, 0, 0, 0 }, 
                    { 2, 0, 2, 0 }
                } 
        }; 
        
        long[][] expected = {
                { NAN, 2, 2, 3 }, 
                { NAN, 4, 5, NAN },
                { 6,   7, 8, NAN }
            }; 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);
        r.setRemainderBits(sourceBits);
        r.decodeLevel(0);

        assertArrayEquals(expected, r.getData());
    }

    @Test
    public final void testLevel1Encode() {
        //@formatter:off
         LongLattice source = new LongLattice(new long[][] {
                { NAN,  NAN,    NAN,    NAN,    3,      NAN,    5,      NAN,    5,      NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                { NAN,  NAN,    NAN,    NAN,    7,      NAN,    7,      NAN,    6,      NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                { NAN,  NAN,    4,      NAN,    7,      NAN,    7,      NAN,    NAN,    NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                { NAN,  NAN,    7,      NAN,    7,      NAN,    6,      NAN,    NAN,    NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                {   3,  NAN,    5,      NAN,    5,      NAN,    NAN,    NAN,    NAN,    NAN } 
        }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 

        long[][] expected = {
                { NAN,  NAN,    NAN,    NAN,    5 ,     NAN,    0,      NAN,     5,     NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                { NAN,  NAN,    NAN,    NAN,    2,      NAN,    2,      NAN,     1,     NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                { NAN,  NAN,    254,    NAN,     6,     NAN,    1,      NAN,    NAN,    NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                { NAN,  NAN,    2,      NAN,    2,      NAN,    1,      NAN,    NAN,    NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                {   5,  NAN,    0,      NAN,     5,     NAN,    NAN,    NAN,    NAN,    NAN } 
            }; 

        byte[][] expectedBits = { 
                { 0,      0,      0,      0,     1,       0,      0,      0,      3,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     2,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 1,      0,      0,      0,     3,       0,      0,      0,      0,     0  } 
        };
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);
        r.encodeLevel(1);

        assertArrayEquals(expected, r.getData());
        assertArrayEquals(expectedBits, r.getRemainderBits()[1]);
    }

    public final void testLevel1Decode() {
        //@formatter:off
         LongLattice source = new LongLattice(new long[][] {
                 { NAN,  NAN,    NAN,    NAN,    5 ,     NAN,    0,      NAN,     5,     NAN }, 
                 { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                 { NAN,  NAN,    NAN,    NAN,    2,      NAN,    2,      NAN,     1,     NAN }, 
                 { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                 { NAN,  NAN,    254,    NAN,     6,     NAN,    1,      NAN,    NAN,    NAN }, 
                 { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                 { NAN,  NAN,    2,      NAN,    2,      NAN,    1,      NAN,    NAN,    NAN }, 
                 { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                 {   5,  NAN,    0,      NAN,     5,     NAN,    NAN,    NAN,    NAN,    NAN } 
        }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 

        long[][] expected = {
                { NAN,  NAN,    NAN,    NAN,    3,      NAN,    5,      NAN,    5,      NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                { NAN,  NAN,    NAN,    NAN,    7,      NAN,    7,      NAN,    6,      NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                { NAN,  NAN,    4,      NAN,    7,      NAN,    7,      NAN,    NAN,    NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                { NAN,  NAN,    7,      NAN,    7,      NAN,    6,      NAN,    NAN,    NAN }, 
                { NAN,  NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN,    NAN }, 
                {   3,  NAN,    5,      NAN,    5,      NAN,    NAN,    NAN,    NAN,    NAN } 
            }; 

        byte[][][] sourceBits = { {{ }}, { 
                { 0,      0,      0,      0,     1,       0,      0,      0,      3,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     2,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 0,      0,      0,      0,     0,       0,      0,      0,      0,     0  }, 
                { 1,      0,      0,      0,     3,       0,      0,      0,      0,     0  } 
        } };

        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);
        r.setRemainderBits(sourceBits);
        r.decodeLevel(1);

        assertArrayEquals(expected, r.getData());
    }

    @Test
    public final void testEncodeDecode200x200() {
        //@formatter:off
        int size = 200; 
        long[][] data = new long[size][size]; 
        long[][] expected = new long[size][size];
        for (int y=0; y<data.length; y++) { 
            for (int x=0; x<data[y].length; x++) { 
                if ((x+y+x*y) % 7 == 1) { 
                    expected[y][x] = data[y][x] = NAN; 
                } else { 
                    expected[y][x] = data[y][x] = (x*y+x+y) % 256;
                } 
            }
        }
        LongLattice source = new LongLattice( data, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);
        r.encode();
        r.decode();

        assertArrayEquals(expected, r.getData());
    }

    @Test
    public final void testGetMaxLevel1() {
        //@formatter:off
        int size = 7; 
        long[][] data = new long[size][size]; 

        LongLattice source = new LongLattice( data, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);

        assertEquals(2, r.getMaxLevel());
    }

    @Test
    public final void testGetMaxLevel2() {
        //@formatter:off
        int size = 8; 
        long[][] data = new long[size][size]; 

        LongLattice source = new LongLattice( data, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);

        assertEquals(3, r.getMaxLevel());
    }

    @Test
    public final void testPointLevelMax() {
        //@formatter:off
        int size = 8; 
        long[][] data = new long[size][size]; 

        LongLattice source = new LongLattice( data, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);

        assertEquals(r.getMaxLevel(), r.getPointLevelXY(0, 0));
    }

    @Test
    public final void testPointLevel0() {
        //@formatter:off
        int size = 8; 
        long[][] data = new long[size][size]; 

        LongLattice source = new LongLattice( data, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);

        assertEquals(0, r.getPointLevelXY(1, 0));
    }

    @Test
    public final void testPointLevel1() {
        //@formatter:off
        int size = 8; 
        long[][] data = new long[size][size]; 

        LongLattice source = new LongLattice( data, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);

        assertEquals(1, r.getPointLevelXY(2, 0));
    }

    @Test
    public final void testPointLevel2() {
        //@formatter:off
        int size = 8; 
        long[][] data = new long[size][size]; 

        LongLattice source = new LongLattice( data, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0); 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);

        assertEquals(2, r.getPointLevelXY(4, 4));
    }

    @Test
    public final void testGetRemainderBitStream() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { NAN, 255,  3,  0 }, 
                { NAN,  0,   1,  NAN },
                {  5,   2,   6,  NAN }
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        byte[][][] sourceBits = { 
                { 
                    { 1, 0, 2, 0 }, 
                    { 0, 0, 0, 0 }, 
                    { 3, 0, 3, 0 }
                }, { 
                    { 2, 0, 0, 0 }, 
                    { 0, 0, 0, 0 }, 
                    { 0, 0, 0, 0 }
                }
        }; 
        byte[] expected = { 107, -64 }; 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);
        r.setRemainderBits(sourceBits);

        assertArrayEquals(expected, r.getReminderBitStream());
    }

    @Test
    public final void testLoadRemainderBitStream() {
        //@formatter:off
        LongLattice field = new LongLattice(new long[][] {
                { NAN, 255,  3,  0 }, 
                { NAN,  0,   1,  NAN },
                {  5,   2,   6,  NAN }
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0 
            ); 

        byte[][][] expected = { 
                { 
                    { 1, 0, 2, 0 }, 
                    { 0, 0, 0, 0 }, 
                    { 3, 0, 3, 0 }
                }, { 
                    { 2, 0, 0, 0 }, 
                    { 0, 0, 0, 0 }, 
                    { 0, 0, 0, 0 }
                }, { 
                    { 0, 0, 0, 0 }, 
                    { 0, 0, 0, 0 }, 
                    { 0, 0, 0, 0 }
                }
        }; 
        byte[] source = { 107, -64 }; 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(field);
        r.loadRemainderBitStream(source);

        assertArrayEquals(expected, r.getRemainderBits());
    }

    @Test
    public final void testEncodeDecodeLena100() {
        BufferedImage image;

        try {
            File file = new File("images/Lena100.jpg");
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        double[][] sourceArray = ImageUtils.imageRasterToDoubleArray(image);
        DoubleLattice hexDoubleLattice = H2O.o2h(sourceArray);
        LongLattice hexExpectedLongLattice = LongLattice.fromLattice(hexDoubleLattice);
        LongLattice hexLongLatticeToTransform = LongLattice.fromLattice(hexDoubleLattice);

        HexByteRavelet r = new HexByteRavelet(hexLongLatticeToTransform);
        r.encode();
        r.decode();

        assertArrayEquals(hexExpectedLongLattice.getData(), r.getData());
    }

    @Test
    public final void testGetLevel1Lattice() {
        //@formatter:off
        LongLattice source = new LongLattice(new long[][] {
                { 2, 1, 2, 1 }, 
                { 1, 1, 1, 1 },
                { 2, 1, 2, 1 }
            }, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0, DOESNT_MATTER0
            ); 

        long[][] expected = {
                { 2, 2 }, 
                { 2, 2 }
            }; 
        
        //@formatter:on
        HexByteRavelet r = new HexByteRavelet(source);

        assertArrayEquals(expected, r.getHigherLevelLattice(1));
    }

    
}
