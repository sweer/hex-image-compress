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

public class DoubleLattice extends Lattice {
	/**
	 * @uml.property  name="data"
	 */
	private final double[][] data;
	public DoubleLattice(double[][] data, int ox, int oy) {
		super(ox,oy);
		this.data = data;
	}
	
    public DoubleLattice(DoubleLattice lattice) {
        this(lattice.data, lattice.ox, lattice.oy);
    }
    
    /**
	 * @return
	 * @uml.property  name="data"
	 */
    public double[][] getData() {
        return data;
    }
}
