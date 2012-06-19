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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Helper for demonstrating classes 
 * Copyright (C) 2012 Aleksejs Truhans
 **/ 
public class Drawer extends JPanel {

	private static final long serialVersionUID = 1L;

	public Drawer(BufferedImage[] images) {
		this.images = images;
	}

	private final BufferedImage[] images;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(images[0], 0, 0, images[0].getWidth(null),
				images[0].getHeight(null), 0, 0, images[0].getWidth(null),
				images[0].getHeight(null), null);
		g2.drawImage(images[1], images[0].getWidth(null), 0,
				images[1].getWidth(null) + images[0].getWidth(null),
				images[1].getHeight(null), 0, 0, images[1].getWidth(null),
				images[1].getHeight(null), null);
	}
}
