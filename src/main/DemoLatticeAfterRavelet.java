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

import pipeline.Encoder;

import lattice.DoubleLattice;
import lattice.LongLattice;

import util.ImageUtils;
import display.HexagonalDisplay;
import filter.H2O;
import filter.HexByteRavelet;

/**
 * Here we showcase O2H results on a hexagonal display
 * Copyright (C) 2012 Aleksejs Truhans
 **/ 
public class DemoLatticeAfterRavelet extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Drawer drawer;

	
	
	public DemoLatticeAfterRavelet(final BufferedImage[] images) {
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
//			File file = new File("images/test/horiz.png");
//			File file = new File("images/test/bird.png");
			images[0] = ImageIO.read(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		double[][] src = ImageUtils.imageRasterToDoubleArray(images[0]);

		DoubleLattice doubleLattice = H2O.o2h(src);
		HexByteRavelet ravelet = new HexByteRavelet(LongLattice.fromLattice(doubleLattice));
		ravelet.encode();

        int sizex = images[0].getWidth()*6; 
        int sizey = images[0].getHeight()*6; 
        
		double[][] dst = new double[sizey][sizex];
		HexagonalDisplay.render(ravelet.getDequantizedLattice().getData(), dst);
		
        images[1] = new BufferedImage(sizex, sizey, BufferedImage.TYPE_BYTE_GRAY);
        ImageUtils.doubleArrayToImageRaster(images[1], dst);

		run(new DemoLatticeAfterRavelet(images), sizex + 300, sizey + 100);
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