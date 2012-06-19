package display;
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

import java.util.LinkedList;
import java.util.Queue;

import lattice.DoubleLattice;
import lattice.LongLattice;


/**
 * The class renders a hexagonally sampled image as 7x6 hexagons in display raster.  
 * Copyright (C) 2012 Aleksejs Truhans
 **/ 
public class HexagonalDisplay {
    /* 
     * Here we assume both src[][] and dst[][] are addressed src[y][x], 
     * like a matrix  
     */
    public static void render(double[][] src, double[][] dst) {
        Renderer renderer = new Renderer(src, dst);
        renderer.render();
    }

    public static void renderScaled(double[][] src, double[][] dst) {
        DoubleLattice lattice = new DoubleLattice(src, 0, 0); 
        long[][] scaledBytes = LongLattice.fromLattice(lattice).getData();
        double[][] scaledDouble = new double[scaledBytes.length][scaledBytes[0].length];
        for (int y=0; y<scaledBytes.length; y++) { 
            for (int x=0; x<scaledBytes[y].length; x++) { 
            	long n = scaledBytes[y][x];
                scaledDouble[y][x] = (n == LongLattice.NAN) ? Double.NaN : n; 
            }
        }
        render(scaledDouble, dst);
    }
    
    private static class Renderer {

        private final double[][] src;
        private final double[][] dst;
        private final int        srcWidth;
        private final int        srcHeight;
        private final int        dstWidth;
        private final int        dstHeight;
        private boolean          queued[][];
        private Queue<int[]>     queue;

        public Renderer(double[][] src, double[][] dst) {
            this.src = src;
            this.dst = dst;
            srcWidth = src[0].length;
            srcHeight = src.length;
            dstWidth = dst[0].length;
            dstHeight = dst.length;
        }

        public void render() {
            int midDstX = dstWidth / 2;
            int midDstY = dstHeight / 2;

            int midSrcX = srcWidth / 2;
            int midSrcY = srcHeight / 2;
            queued = new boolean[srcHeight][srcWidth];
            queue = new LinkedList<int[]>();
            int[] item = { midSrcX, midSrcY, midDstX, midDstY };
            queued[midSrcY][midSrcX] = true; 
            while (item != null) {
                renderSrcPointXY(item[0], item[1], item[2], item[3]);
                item = queue.poll();
            }
        }

        private void renderSrcPointXY(int srcX, int srcY, int dstX, int dstY) {

            if (Double.isNaN(src[srcY][srcX])) 
                return; 
            
            drawHexagonXY(src[srcY][srcX], dstX, dstY);
            
            for (int i = 0; i < neighbourRelativeSrcXY.length; i++) {
                int x = srcX + neighbourRelativeSrcXY[i][0];
                int y = srcY + neighbourRelativeSrcXY[i][1];

                if (x >= 0 && x < srcWidth && y >= 0 && y < srcHeight && !queued[y][x]) {
                    queue.add(new int[] { x, y, dstX + neighbourRelativeDstXY[i][0], dstY
                            + neighbourRelativeDstXY[i][1]});
                    queued[y][x] = true;
                }
            }
        }

        private void drawHexagonXY(double d, int centerX, int centerY) {
            for (int[] pointXY : hexagonRelativePointsXY) {
                final int y = centerY + pointXY[1];
                final int x = centerX + pointXY[0];
                if (y >= 0 && y < dstHeight && x >= 0 && x < dstWidth) {
                    dst[y][x] = d;
                }
            }
        }



//@formatter:off
        private static final int[][] neighbourRelativeSrcXY  = { 
            { -1, 1 }, { 0, 1 }, { 1, 1 },
            { -1, 0 }, { 0, 0 }, { 1, 0 },
            { -1, -1 }, { 0, -1 }, { 1, -1 } 
        };

        private static final int[][] neighbourRelativeDstXY  = { 
            { -3, 5 }, { 3, 5 }, { 9, 5 },
            { -6, 0 }, { 0, 0 }, { 6, 0 },
            { -9, -5 }, { -3, -5 }, { 3, -5 } 
        };

        private static final int[][] hexagonRelativePointsXY = { 
                                    { -1,  3 }, { 0,  3 }, 
                        { -2,  2 }, { -1,  2 }, { 0,  2 }, { 1,  2 }, 
            { -3,  1 }, { -2,  1 }, { -1,  1 }, { 0,  1 }, { 1,  1 }, { 2,  1 }, 
            { -3,  0 }, { -2,  0 }, { -1,  0 }, { 0,  0 }, { 1,  0 }, { 2,  0 }, 
            { -3, -1 }, { -2, -1 }, { -1, -1 }, { 0, -1 }, { 1, -1 }, { 2, -1 }, 
                        { -2, -2 }, { -1, -2 }, { 0, -2 }, { 1, -2 },                                          
                                    { -1, -3 }, { 0, -3 } 
        };
//@formatter:on

    }

}
