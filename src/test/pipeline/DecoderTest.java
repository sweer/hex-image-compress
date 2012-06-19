package test.pipeline;

import static lattice.LongLattice.NAN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static test.TestUtils.DELTA;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import lattice.LongLattice;
import lattice.LongLattice.Run;

import org.junit.Test;

import pipeline.Decoder;
import pipeline.Decoder.DecodeRunner;
import pipeline.Encoder;
import test.TestUtils;

public class DecoderTest {

    @Test
    public final void testDecodeHeader() {
        //@formatter:off
        LongLattice template = new LongLattice(new long[][] {
                { NAN, 2,  NAN, NAN }, 
                { NAN, NAN,   7,   0 }, 
                {   4,   7, NAN,  3 },
                { 11, NAN, 13,  NAN } 
            }, 15, 16, 17, 18 
            ); 
        byte[] source = {0, 15, 0, 16, 0, 4, 0, 4, 64, 49, 0, 0, 0, 0, 0, 
                0, 64, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1, 0, 2, -1, -2, 0, 0, 0, 0, -1, -1};
        //@formatter:on

        LongLattice actual = DecodeRunner.decodeHeader(source);

        assertEquals(template.getData().length, actual.getData().length);
        assertEquals(template.getData()[0].length, actual.getData()[0].length);
        assertEquals(template.ox, actual.ox);
        assertEquals(template.oy, actual.oy);
        assertEquals(template.min, actual.min, TestUtils.DELTA);
        assertEquals(template.factor, actual.factor, TestUtils.DELTA);
        assertArrayEquals(template.getNANPositions(), actual.getNANPositions());
    }

    @Test
    public final void testLoadNanRuns() {
        //@formatter:off
        List<Run> expected = Arrays.asList(new Run[] { 
                new Run((short)3,(short)1,(short)2), new Run((short)2,(short)2,(short)1) 
        } ); 

        byte[] source = {0, 3, 0, 1, 0, 2, 0, 2, 0, 2, 0, 1};
        //@formatter:on

        List<Run> actual = DecodeRunner.decodeNanRuns(source);

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).x, actual.get(i).x);
            assertEquals(expected.get(i).y, actual.get(i).y);
            assertEquals(expected.get(i).length, actual.get(i).length);
        }
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
        Encoder encoder = new Encoder(image);
        LongLattice expected = Encoder.representImageAsHexagonalLattice(image); 
        
        byte[] encoded = encoder.encode();
        LongLattice actual = Decoder.decodeToHexagonalLattice(encoded);

        assertArrayEquals(expected.getData(), actual.getData());
        assertArrayEquals(expected.getNANPositions(), actual.getNANPositions());
        assertEquals(expected.min, actual.min, DELTA);
        assertEquals(expected.factor, actual.factor, DELTA);
        assertEquals(expected.ox, actual.ox, DELTA);
        assertEquals(expected.oy, actual.oy, DELTA);
    }
}
