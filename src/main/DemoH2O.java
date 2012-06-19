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
import filter.H2O;

/**
 * Demo of H2O transform there and back. @see filter.H2O
 * 
 * Copyright (C) 2012 Aleksejs Truhans
 **/ 
public class DemoH2O extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Drawer drawer;

	public DemoH2O(final BufferedImage[] images) {
		drawer = new Drawer(images);
		add(drawer);
	}

	public static void main(String[] args) {
		BufferedImage[] images = new BufferedImage[3];

		try {
            File file = new File("images/Lena200.jpg");
			images[0] = ImageIO.read(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	
		double[][] raster = ImageUtils.imageRasterToDoubleArray(images[0]);

		DoubleLattice hexLattice = H2O.o2h(raster);
		double[][] backRaster = H2O.h2o(hexLattice).getData();

		images[1] = new BufferedImage(images[0].getWidth(), images[0].getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		ImageUtils.doubleArrayToImageRaster(images[1], backRaster);
		
		run(new DemoH2O(images), 950, 700);
	}

	public static void run(final JFrame f, final int width, final int height) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				f.setTitle(f.getClass().getSimpleName());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				f.setLocation(400, 300);
				f.setVisible(true);
			}
		});
	}
}