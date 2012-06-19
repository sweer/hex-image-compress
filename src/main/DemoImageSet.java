package main;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static test.TestUtils.DELTA;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import lattice.LongLattice;

import pipeline.Decoder;
import pipeline.Encoder;
/**
 * Compresses given set of images using the ravelet and using jpeg2000. 
 * Allows comparing these two methods and PNG. 
 * Copyright (C) 2012 Aleksejs Truhans
 **/ 
public class DemoImageSet {
    //@formatter:off
    private static final String[] TEST_FILES = {     
//      "Lena100.png"
    "bird.png",
    "bridge.png","camera.png",
    "goldhill1.png",
    "lena1.png",

//    "circles.png","crosses.png",
//    "horiz.png",
//    "montage.png","slope.png","squares.png","text.png"
//    "mandrill.png"
    };
    //@formatter:on

    public static void main(String[] args) {

        for (String fileName : TEST_FILES) {
            testImage("images/test/" + fileName);
        }
    }

    private static void testImage(String filename) {
        System.out.println("Processing " + filename + "...");
        BufferedImage image;

        try {
            File file = new File(filename);
            image = ImageIO.read(file);
            long time = System.currentTimeMillis();
            Encoder encoder = new Encoder(image);
            time = System.currentTimeMillis() - time;
//            System.out.println(" o2h done in " + (time / 1000) + " sec.");

            LongLattice expected = LongLattice.createCopyOf(encoder.getLattice()); 

            time = System.currentTimeMillis();
            byte[] encoded = encoder.encode();
//            writeEncoded(encoded, filename + ".rav");
            time = System.currentTimeMillis() - time;
//            System.out.println("Encoded in " + time + " ms.");
            Encoder.Statistics statistics = encoder.getStatistics(); 
            
            System.out.printf("%s %7.2f %7.2f %7.2f %7.2f\n", filename, statistics.getCompressedHeaderSizeBytes() / 1024.0, 
            		statistics.getCompressedLatticeSizeBytes() / 1024.0, statistics.getNanRunsSizeBytes() / 1024.0, 
            		statistics.getRemainderBitsSizeBytes() / 1024.0); 

//            LongLattice actual = Decoder.decodeToHexagonalLattice(encoded);
//
//            assertArrayEquals(expected.getData(), actual.getData());
//            assertArrayEquals(expected.getNANPositions(), actual.getNANPositions());
//            assertEquals(expected.min, actual.min, DELTA);
//            assertEquals(expected.factor, actual.factor, DELTA);
//            assertEquals(expected.ox, actual.ox, DELTA);
//            assertEquals(expected.oy, actual.oy, DELTA);

//            BufferedImage imageDecoded = Decoder.hexagonalLatticeToBufferedImage(actual);
//            ImageIO.write(imageDecoded, "png", new File(filename + "-decoded.png"));
//
//            System.out.println("Written decoded image");

//            Process child = Runtime.getRuntime().exec(
//                    "image_to_j2k -i " + filename + " -o " + filename + ".jp2");
//
//            if (child.waitFor() != 0) {
//                System.out
//                        .println("image_to_j2k didn't work. If you don't have it - install OpenJPEG library please");
//            }

        } catch (IOException e) {
            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }

    }

    private static void writeEncoded(byte[] encoded, String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(encoded);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}