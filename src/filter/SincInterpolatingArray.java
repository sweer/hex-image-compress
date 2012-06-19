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

public abstract class SincInterpolatingArray extends InterpolatingArray {

    private static double sinc(double x) { 
    	if (Math.abs(x) < 0.000001) { 
    		return (1 - x*x/6); 
    	} else { 
    		return Math.sin(Math.PI * x)/ (Math.PI * x);
    	}
    }

    public SincInterpolatingArray(double[] points, int order) {
        super(points, order);
    }

    public abstract double get(int i);

    public SincInterpolatingArray(double[] points) {
        super(points);
    }

    @Override
    public double get(double x) { 
    	int length = points.length;
    	double result = 0; 
    	for (int i = -(length*order-1); i < length*order; i++) { 
    		result += get(i) * sinc(x - i); 
    	}
    	return result ;  
    }

}