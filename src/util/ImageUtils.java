package util;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ImageUtils {

    public static double[][] imageRasterToDoubleArray(
    		final BufferedImage source) {
    
    	WritableRaster sourceRaster = source.getRaster();
    
    	int width = sourceRaster.getWidth();
    	int height = sourceRaster.getHeight();
    	double[][] result = new double[height][width];
    
    	for (int y = 0; y < height; y++) {
    	    double[] row = result[y]; 
    	    for (int x = 0; x < width; x++) {
    			row[x] = sourceRaster.getSample(x, height-1-y, 0);
    		}
    	}
    	return result;
    }

    public static void doubleArrayToImageRaster(
    		final BufferedImage destination, double[][] source) {
    
    	WritableRaster destinationRaster = destination.getRaster();
    	int height = source.length;
    	for (int x = 0; x < source[0].length; x++) {
    		for (int y = 0; y < height; y++) {
    			double value = source[y][x]; 
    			if (value < 0) { value = 0; } 
    			if (value > 255) { value = 255; }
    			destinationRaster.setSample(x, height-1-y, 0, value);
    		}
    	}
    }

}
