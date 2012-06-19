package lattice;
/* 
 * Copyright (C) 2012 Aleksejs Truhans
 * 
 * This program is free software: you can redistribute it and/or modify
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

/**
 * The lattice allows storing hexagonally sampled image in a rectangular array. 
 * Since sampling rectangular image with corners (0,0), (0,100), (100,100), (100,0) 
 * gives hexagonal coordinates like (0,0), (0,-50), (100,-50), (100,0), we 
 * store it shifted so that (ox,oy) shows where the (0,0) is in the array. 
 * Array cells having no samples for them are assigned Double.NaN
 * 
 * I prefer using primitives for speed, though it makes me suffer from impossibility  
 * to override method return type. 
 * 
 * Copyright (C) 2012 Aleksejs Truhans
 **/
public class Lattice {

	public final int ox;
	public final int oy;
	public Lattice(int ox, int oy) {
		super();
		this.ox = ox;
		this.oy = oy;
	}

}