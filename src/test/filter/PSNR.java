package test.filter;
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

public class PSNR {
	public static double ofArray(double[] original, double[] distorted) { 
		double maxOriginal = 0; 
		double sumSquares = 0; 
		
		for (int i=0; i<original.length; i++) {
			double absOriginal = Math.abs(original[i]); 
			if (absOriginal > maxOriginal) maxOriginal = absOriginal; 
			double diff = original[i] - distorted[i]; 
			sumSquares += diff*diff; 
		}
		
		return 10*Math.log10(maxOriginal*maxOriginal*original.length/sumSquares); 
	}

	public static double ofNoise(double[] noise, double absMaxOfOriginal) { 
	    double sumSquares = 0; 
	    for (int i=0; i<noise.length; i++) {
            sumSquares += noise[i]*noise[i]; 
        }
		return 10 * Math.log10(absMaxOfOriginal * absMaxOfOriginal
				* noise.length / sumSquares);
	}
}
