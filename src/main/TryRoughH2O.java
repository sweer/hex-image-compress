package main;
/* This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import lattice.DoubleLattice;
import lattice.HexSystem;
import lattice.Point;

import util.ImageUtils;
import display.HexagonalDisplay;
import filter.H2O;

/**
 * Here we showcase O2H results on a hexagonal display
 * Copyright (C) 2012 Aleksejs Truhans
 **/ 
public class TryRoughH2O extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Drawer drawer;

	
	
	public TryRoughH2O(final BufferedImage[] images) {
		drawer = new Drawer(images);
		add(drawer);
	}

	public static void main(String[] args) {
		h2oResearch2();
	}

	public static void h2oResearch2() {
		BufferedImage[] images = new BufferedImage[2];

		try {
			File file = new File("images/Lena100.jpg");
			images[0] = ImageIO.read(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		double[][] src = ImageUtils.imageRasterToDoubleArray(images[0]);

        DoubleLattice hexLattice = H2O.o2h(src);

        replaceHexLatticeWithRoughHexData(src, hexLattice); 
        
        int sizex = 600; 
        int sizey = 600; 
        
		double[][] dst = new double[sizey][sizex];

        HexagonalDisplay.render(hexLattice.getData(), dst);
        
        images[1] = new BufferedImage(sizex, sizey, BufferedImage.TYPE_BYTE_GRAY);
        
        ImageUtils.doubleArrayToImageRaster(images[1], dst);

		run(new TryRoughH2O(images), sizex + 300, sizey + 100);
	}

	public static void replaceHexLatticeWithRoughHexData(double[][] src,
			DoubleLattice dstLattice) {
		
		double[][] dst = dstLattice.getData();
		int count = 0;
		for (int y=0; y<dst.length; y++) { 
			for (int x=0; x<dst[0].length; x++) {
				Point hex = new Point(x-dstLattice.ox, y-dstLattice.oy);
				Point ort = hex.multiplyByMatrix2x2(HexSystem.hexBaseMatrixDensity1); 
				// System.out.println("Hex " + hex + " -> ort " + ort);
				int ortX = (int) Math.round(ort.x);
				int ortY = (int) Math.round(ort.y);
				
				if (ortX>=0 && ortX<src[0].length && ortY>=0 && ortY<src.length) { 
					dst[y][x] = src[ortY][ortX]; 
					count++; 
				} else { 
					dst[y][x] = Double.NaN; 
				}
					
			}
		}
		//System.out.println("Translated " + count + " points");
		
	}

	public static void run(final JFrame f, final int width, final int height) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				f.setTitle(f.getClass().getSimpleName());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				f.setLocation(50, 50);
				f.setVisible(true);
			}
		});
	}
}