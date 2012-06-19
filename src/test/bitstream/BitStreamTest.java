package test.bitstream;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import bitstream.BitInputStream;
import bitstream.BitOutputStream;

public class BitStreamTest {

    @Test
    public final void testBitOutput() {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BitOutputStream bitOut = new BitOutputStream(byteOut);

        bitOut.write(2, 7);
        bitOut.flush();
        
        assertEquals(3, (byteOut.toByteArray()[0] & 0xC0) >> 6);
    }

    @Test
    public final void testBitOutputInput() {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            BitOutputStream bitOut = new BitOutputStream(byteOut);

            bitOut.write(2, 7);
            bitOut.write(8, 121);
            bitOut.write(6, 27);
            bitOut.flush();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            BitInputStream bitIn = new BitInputStream(byteIn);

            assertEquals(3, bitIn.read(2));
            assertEquals(121, bitIn.read(8));
            assertEquals(27, bitIn.read(6));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
