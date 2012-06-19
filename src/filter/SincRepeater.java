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

/**
 * 1D interpolator & extrapolator based on Whittaker-Shannon sinc-interpolation formula.
 * Given an array of function values it acts as if the array has been repeating periodically.
 * The number of period repetitions is defined by the order value. 
 * @see SincInterpolatingArray  
 */
public class SincRepeater extends SincInterpolatingArray {

    /**
	 * @uml.property  name="fACTORY"
	 * @uml.associationEnd  
	 */
    private static final Factory FACTORY = new Factory();

    public SincRepeater(double[] points, int order) {
        super(points, order);
    }

    public SincRepeater(double[] points) {
        super(points);
    }

    @Override
    public double get(int i) {
// @formatter:off
        //  1 2 3 4 5 6 1 2 3 4 5 6 1 2 3 4
        // -6-5-4-3-2-1 0 1 2 3 4 5 6 7
// @formatter:on
        int length = points.length;
        int j = (i % length + length) % length;
        return points[j];
    }

    /**
	 * @return
	 * @uml.property  name="fACTORY"
	 */
    public static Factory getFactory() {
        return FACTORY;
    }

    public static class Factory implements InterpolatingArray.Factory {

        @Override
        public SincRepeater getInstance(double[] points) {
            return new SincRepeater(points);
        }

    }

}
