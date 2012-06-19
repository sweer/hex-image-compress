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

import util.ImageUtils;
import display.HexagonalDisplay;
import filter.H2O;

/**
 * Here we showcase O2H results on a hexagonal display
 * Copyright (C) 2012 Aleksejs Truhans
 **/ 
public class DemoHexagonalDisplay extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Drawer drawer;

	
	
	public DemoHexagonalDisplay(final BufferedImage[] images) {
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

        int sizex = 600; 
        int sizey = 600; 
        
		double[][] dst = new double[sizey][sizex];

        HexagonalDisplay.render(hexLattice.getData(), dst);
        
        images[1] = new BufferedImage(sizex, sizey, BufferedImage.TYPE_BYTE_GRAY);
        
        ImageUtils.doubleArrayToImageRaster(images[1], dst);

		run(new DemoHexagonalDisplay(images), sizex + 300, sizey + 100);
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