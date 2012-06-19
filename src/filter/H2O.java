package filter;

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

import static java.lang.Math.sqrt;
import lattice.DoubleLattice;
import filter.InterpolatingArray.Factory;
import filter.RectangleDiagonal.DiagonalParameters;

/**
 * Implementation of H2O transformation described in L.Condat,
 * B.Forster-Heinlein, D. Van De Ville,
 * "Reversible Hexagonal-Orthogonal Grid Conversion by 1-D Filtering", IEEE
 * ICIP, 2007, vol.II pg. 73-76
 * 
 * Copyright (C) 2012 Aleksejs Truhans
 **/
public class H2O {

    public final static double M1A = 1 - sqrt(2 / sqrt(3));
    public final static double M2B = sqrt(sqrt(3) / 2) - 1;
    public final static double M3C = sqrt(2 / sqrt(3)) - 1 - 1 / sqrt(3);

    /**
     * @author Aleksejs Truhans
     */
    public static enum Direction {
        O2H, H2O, TEST_O2H, TEST_H2O;
    }

    /**
     * Use it to convert from orthogonal to hexagonal lattice.
     * o2h doesn't change the passed raster.
     **/
    public static DoubleLattice o2h(double[][] raster) {
        H2ORunner h2o = new H2ORunner(SincRepeater.getFactory());
        h2o.setData(raster, Direction.O2H);
        return h2o.o2hBody();
    }

    /**
     * Use it to convert from hexagonal to orthogonal lattice.
     * o2h doesn't change the passed raster.
     **/
    public static DoubleLattice h2o(DoubleLattice source) {
        H2ORunner h2o = new H2ORunner(SincRepeater.getFactory());
        h2o.setData(source, Direction.H2O);
        return h2o.h2oBody();
    }

    /**
     * @author Aleksejs Truhans
     */
    public static class H2ORunner {

        private final Factory interpolatingArrayFactory;
        private double[][] data;
        private int minx;
        private int miny;
        private int maxx;
        private int maxy;
        private int ox;
        private int oy;
        private Direction direction;

        public H2ORunner(Factory interpolatingArrayFactory) {
            this.interpolatingArrayFactory = interpolatingArrayFactory;
        }

        public void setData(DoubleLattice source, Direction direction) {
            setData(source.getData(), direction);
            ox += source.ox;
            oy += source.oy;
        }

        public void setData(double[][] source, Direction adirection) {
            int w = source[0].length;
            int h = source.length;
            // Will use this workaround buffer until I find
            // better way to predict image expansion due to shearings
            int bufferZone = (h + w) / 10;
            int xd = (int) (-(M3C + M2B) * h + 1) + bufferZone;
            int ydup = (int) (-M2B * (h + w - 1) / sqrt(2) + 1) + bufferZone;
            int yddown = (int) (-M1A * w + 1) + bufferZone;

            if (adirection == Direction.TEST_O2H || adirection == Direction.TEST_H2O) {
                xd = w / 2 + 2;
                ydup = h / 4 + 2;
                yddown = h / 4 + 2;
                direction = (adirection == Direction.TEST_O2H) ? Direction.O2H : Direction.H2O;
            } else {
                direction = adirection;
            }

            int nw = w + xd;
            int nh = h + ydup + yddown;

            data = new double[nh][nw];

            ox = direction == Direction.O2H ? xd : 0;
            oy = direction == Direction.O2H ? yddown : ydup;
            minx = ox;
            miny = oy;
            maxx = minx + w - 1;
            maxy = miny + h - 1;

            for (int y = 0; y < nh; y++) {
                for (int x = 0; x < nw; x++) {
                    if (y >= miny && x >= minx && y <= maxy && x <= maxx) {
                        data[y][x] = source[y - oy][x - ox];
                    } else {
                        data[y][x] = Double.NaN;
                    }

                }
            }
        }

        private DoubleLattice o2hBody() {
            shearRightM3(-H2O.M3C);
            shearDownRightM2(-H2O.M2B);
            shearUpM1(-H2O.M1A);
            return getResult();
        }

        private DoubleLattice h2oBody() {
            shearUpM1(H2O.M1A);
            shearDownRightM2(H2O.M2B);
            shearRightM3(H2O.M3C);
            return getResult();
        }

        public void shearRightM3(double shift) {
            for (int y = miny; y <= maxy; y++) {
                if (y - oy == 0)
                    continue; // shift by 0 means no shift

                double[] currentRow = data[y];

                int x0 = minx;
                while (Double.isNaN(currentRow[x0]))
                    x0++;

                int x1 = maxx;
                while (Double.isNaN(currentRow[x1]) && x1 >= x0)
                    x1--;

                if (x1 < x0)
                    continue; // Nothing to shift

                double[] oldRow = new double[x1 - x0 + 1];
                for (int x = x0; x <= x1; x++) {
                    oldRow[x - x0] = currentRow[x];
                }
                InterpolatingArray sia = interpolatingArrayFactory.getInstance(oldRow);

                double rowShift = shift * (y - oy);
                int shiftInArray = (int) rowShift;
                rowShift -= shiftInArray;
                if (minx > (x0 - shiftInArray))
                    minx = x0 - shiftInArray;
                if (maxx < (x1 - shiftInArray))
                    maxx = x1 - shiftInArray;
                if (minx < 0 || maxx >= data[0].length) {
                    throw new IllegalArgumentException("Shearing " + direction + " by " + shift
                            + " wouldn't fit into the data lattice");
                }

                for (int x = 0; x < oldRow.length; x++) {
                    currentRow[x + x0 - shiftInArray] = sia.get(rowShift + x);
                }

                for (int x = 0; x < shiftInArray; x++) {
                    currentRow[x1 - x] = Double.NaN;
                }

                for (int x = shiftInArray + 1; x <= 0; x++) {
                    currentRow[x0 - x] = Double.NaN;
                }
            }

            squeezeMinMax();
        }

        public DoubleLattice getResult() {
            int w = maxx - minx + 1;
            int h = maxy - miny + 1;
            double[][] result = new double[h][w];
            for (int y = miny; y <= maxy; y++) {
                for (int x = minx; x <= maxx; x++) {
                    result[y - miny][x - minx] = data[y][x];
                }
            }
            return new DoubleLattice(result, ox - minx, oy - miny);
        }

        public double[][] getRawData() {
            return data;
        }

        public void shearDownRightM2(double shift) {
            final int height = maxy - miny + 1;
            final int width = maxx - minx + 1;
            int ODiagonalNumber = ox - minx + oy - miny;
            int newmaxx = maxx;
            int newmaxy = maxy;
            int newminx = minx;
            int newminy = miny;

            for (int i = 0; i < (height + width - 1); i++) {
                int diagonalCoordinate = i - ODiagonalNumber;
                if (diagonalCoordinate == 0)
                    continue; // shift by 0 means no shift

                DiagonalParameters dp = DiagonalParameters.getDiagonalParameters(width, height, i);

                int j0 = 0;
                while (j0 < dp.diagonalLength
                        && Double.isNaN(data[dp.startY - j0 + miny][dp.startX + j0 + minx])) {
                    j0++;
                }

                int j1 = dp.diagonalLength - 1;
                while (j1 >= j0 && Double.isNaN(data[dp.startY - j1 + miny][dp.startX + j1 + minx])) {
                    j1--;
                }

                if (j1 < j0)
                    continue; // Nothing to shift

                double[] oldDiagonal = new double[j1 - j0 + 1];

                for (int j = j0; j <= j1; j++) {
                    oldDiagonal[j - j0] = data[dp.startY - j + miny][dp.startX + j + minx];
                }

                InterpolatingArray sia = interpolatingArrayFactory.getInstance(oldDiagonal);

                double diagonalShift = shift * diagonalCoordinate;
                int shiftInArray = (int) diagonalShift;
                diagonalShift -= shiftInArray;
                int j0new = j0 - shiftInArray;
                int j1new = j1 - shiftInArray;

                if (newmaxy < (dp.startY - j0new + miny))
                    newmaxy = dp.startY - j0new + miny;
                if (newminy > (dp.startY - j1new + miny))
                    newminy = dp.startY - j1new + miny;

                if (newminx > (dp.startX + j0new + minx))
                    newminx = dp.startX + j0new + minx;
                if (newmaxx < (dp.startX + j1new + minx))
                    newmaxx = dp.startX + j1new + minx;

                if (newminy < 0 || newmaxy >= data.length || newminx < 0
                        || newmaxx >= data[0].length) {
                    throw new IllegalArgumentException("Shearing " + direction + " by " + shift
                            + " wouldn't fit into the data lattice");
                }

                for (int j = 0; j < oldDiagonal.length; j++) {
                    data[dp.startY - (j + j0 - shiftInArray) + miny][dp.startX
                            + (j + j0 - shiftInArray) + minx] = sia.get(diagonalShift + j);
                }

                for (int j = 0; j < shiftInArray; j++) {
                    data[dp.startY - (j1 - j) + miny][dp.startX + (j1 - j) + minx] = Double.NaN;
                }

                for (int j = shiftInArray + 1; j <= 0; j++) {
                    data[dp.startY - (j0 - j) + miny][dp.startX + (j0 - j) + minx] = Double.NaN;
                }
            }

            maxx = newmaxx;
            maxy = newmaxy;
            miny = newminy;
            minx = newminx;

            squeezeMinMax();
        }

        private void squeezeMinMax() {
            minx = firstNonNaNHorizontal(minx, +1);
            maxx = firstNonNaNHorizontal(maxx, -1);
            miny = firstNonNaNVertical(miny, +1);
            maxy = firstNonNaNVertical(maxy, -1);
        }

        private int firstNonNaNVertical(int y0, int direction) {
            int y = y0;
            for (; y >= miny && y <= maxy; y += direction) {
                for (int x = minx; x <= maxx; x++) {
                    if (!Double.isNaN(data[y][x]))
                        return y;
                }
            }
            return y;
        }

        private int firstNonNaNHorizontal(int x0, int direction) {
            int x = x0;
            for (; x >= minx && x <= maxx; x += direction) {
                for (int y = miny; y <= maxy; y++) {
                    if (!Double.isNaN(data[y][x]))
                        return x;
                }
            }
            return x;
        }

        public void shearUpM1(double shift) {
            for (int x = minx; x <= maxx; x++) {
                if (x - ox == 0)
                    continue; // shift by 0 means no shift

                int y0 = miny;
                while (Double.isNaN(data[y0][x]))
                    y0++;

                int y1 = maxy;
                while (Double.isNaN(data[y1][x]) && y1 >= y0)
                    y1--;

                if (y1 < y0)
                    continue; // Nothing to shift

                double[] oldColumn = new double[y1 - y0 + 1];
                for (int y = y0; y <= y1; y++) {
                    oldColumn[y - y0] = data[y][x];
                }
                InterpolatingArray sia = interpolatingArrayFactory.getInstance(oldColumn);

                double columnShift = shift * (x - ox);
                int shiftInArray = (int) columnShift;
                columnShift -= shiftInArray;
                if (miny > (y0 - shiftInArray))
                    miny = y0 - shiftInArray;
                if (maxy < (y1 - shiftInArray))
                    maxy = y1 - shiftInArray;
                if (miny < 0 || maxy >= data.length) {
                    throw new IllegalArgumentException("Shearing " + direction + " by " + shift
                            + " wouldn't fit into the data lattice");
                }

                for (int y = 0; y < oldColumn.length; y++) {
                    data[y + y0 - shiftInArray][x] = sia.get(columnShift + y);
                }

                for (int y = 0; y < shiftInArray; y++) {
                    data[y1 - y][x] = Double.NaN;
                }

                for (int y = shiftInArray + 1; y <= 0; y++) {
                    data[y0 - y][x] = Double.NaN;
                }
            }

            squeezeMinMax();
        }
    }
}
