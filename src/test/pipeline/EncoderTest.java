package test.pipeline;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.List;

import lattice.LongLattice.Run;

import org.junit.Test;

import pipeline.Encoder;


public class EncoderTest {

    @Test
    public final void testGenerateNanRuns() {
        //@formatter:off
        List<Run> source = Arrays.asList(new Run[] { 
                new Run((short)3,(short)1,(short)2), new Run((short)2,(short)2,(short)1) 
        } ); 

        byte[] expected = {0, 3, 0, 1, 0, 2, 0, 2, 0, 2, 0, 1};
        //@formatter:on

        byte[] actual = Encoder.generateNanRuns(source); 
        
        assertArrayEquals(expected, actual);
    }

}
