package filter;
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

public abstract class InterpolatingArray {

    protected final double[] points;

    public abstract double get(double x);
    
    public abstract double get(int i);

    protected int order = 2;
    
    public InterpolatingArray(double[] points, int order) {
        this(points);
        this.order = order;
    }

    public InterpolatingArray(double[] points) {
        this.points = points;
    }
    
    public double[] getShiftedArray(double shift) { 
    	int length = points.length;
    	double[] result = new double[length]; 
    	for (int i = 0; i < length; i++) { 
    		result[i] = get(i+shift); 
    	}
    	return result;
    }

    public final int length() { 
    	return points.length;  
    }
    
    public interface Factory {
        InterpolatingArray getInstance(double[] points); 
    }

}