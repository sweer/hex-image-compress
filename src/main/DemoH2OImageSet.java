package main;

import static java.lang.Math.abs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import lattice.DoubleLattice;
import rangecoder.Compressor;
import test.filter.PSNR;
import util.ArrayUtils;
import util.ImageUtils;
import filter.H2O;
/**
 * Transforms the given set of images O2H and H2O, measures noise level. 
 * Copyright (C) 2012 Aleksejs Truhans
 **/ 
public class DemoH2OImageSet {
    //@formatter:off
    private static final String[] TEST_FILES = {     
        "Lena100.png"
//        "bird.png",
//        "bridge.png","camera.png",
//        "goldhill1.png",
//        "lena1.png",

//        "circles.png","crosses.png",
//        "horiz.png",
//        "montage.png","slope.png","squares.png","text.png"
//        "mandrill.png"
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
    		double[][] raster = ImageUtils.imageRasterToDoubleArray(image);

    		DoubleLattice hexLattice = H2O.o2h(raster);
    		double[][] backRaster = H2O.h2o(hexLattice).getData();

    		double noise[] = ArrayUtils.subtract2DArraysAs1D(raster,  backRaster);  
    		byte noiseByte[] = new byte[noise.length]; 
    		for (int i=0; i<noise.length; i++) {
    			noiseByte[i] = (byte) Math.round(noise[i]); 
    		}
    		
    		byte[] compressedNoise = Compressor.compress(noiseByte); 
    		
    		System.out.printf("%s compressed noise is %f bits per pixel\n", filename, compressedNoise.length * 8.0 / noise.length);
    		
//    		double psnr = PSNR.ofNoise(noise, 255); 
//    		
//            int countAbsErrLessThanHalf = 0;
//            double sum = 0; 
//            double noiseAbs[] = new double[noise.length]; 
//            for (int i=0; i<noise.length; i++) {
//            	sum += noise[i];
//            	noiseAbs[i] = abs(noise[i]);
//            	if (noiseAbs[i]<0.5) { 
//            		countAbsErrLessThanHalf++; 
//            	}
//            }
//            
//            Arrays.sort(noiseAbs); 
//			double absErrRange90Percent = noiseAbs[(int)(0.9*noiseAbs.length)];
//			
//			Arrays.sort(noise); 
//			System.out.printf("PSNR = %7.3f, abs err <0.5: %7.3f, 90perc are abs err < %7.3f, mean = %7.3f, min = %7.3f, max = %7.3f, 1st quart = %7.3f, 3rd quart = %7.3f\n\n", 
//					psnr, (countAbsErrLessThanHalf * 1.0 / noise.length), absErrRange90Percent,  
//					(sum / noise.length), noise[0], noise[noise.length-1], noise[noise.length/4],  
//					noise[3*noise.length/4]) ;
			
			
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}