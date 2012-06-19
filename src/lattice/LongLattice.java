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

import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import util.ArrayUtils;

/**
 * LongLattice is scaled so that the lowest possible value is 0, the
 * highest 255. All the NaNs are replaced by NAN.
 */
public class LongLattice extends Lattice {

    public static final int SHIFT = 0;
    public static final long NAN = Long.MIN_VALUE;
    final public double min;
    final public double factor;
    protected long[][] data;
    private short[][] NANPositions;

    public static class Run {
        public short x;
        public short y;
        public short length;

        public Run(short x, short y, short length) {
            this.x = x;
            this.y = y;
            this.length = length;
        }
    }

    public LongLattice(long[][] data, int ox, int oy, double min, double factor) {
        super(ox, oy);
        this.data = data;
        this.min = min;
        this.factor = factor;
    }

    public LongLattice(LongLattice lattice) {
        this(lattice.data, lattice.ox, lattice.oy, lattice.min, lattice.factor);
    }

    public DoubleLattice getDequantizedLattice() {
        double descaled[][] = new double[data.length][data[0].length];

        for (int y = 0; y < data.length; y++) {
            long[] rowSrc = data[y];
            double[] rowDst = descaled[y];
            for (int x = 0; x < rowSrc.length; x++) {
                long b = rowSrc[x];
                if (b == NAN) {
                    rowDst[x] = NaN;
                } else {
                    rowDst[x] = (b + SHIFT) / factor + min;
                }
            }
        }
        return new DoubleLattice(descaled, ox, oy);
    }

    public long[][] getData() {
        return data;
    }

    public static LongLattice fromLattice(DoubleLattice source) {
        double[][] data = source.getData();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int y = 0; y < data.length; y++) {
            double[] row = data[y];
            for (int x = 0; x < data[0].length; x++) {
                double d = row[x];
                if (isNaN(d))
                    continue;
                if (d > max)
                    max = d;
                if (d < min)
                    min = d;
            }
        }

        long scaled[][] = new long[data.length][data[0].length];
        double range = max - min;
        double factor = 255 / range;
        for (int y = 0; y < data.length; y++) {
            double[] rowSrc = data[y];
            long[] rowDst = scaled[y];
            for (int x = 0; x < data[0].length; x++) {
                double d = rowSrc[x];
                if (isNaN(d)) {
                    rowDst[x] = NAN;
                } else {
                    rowDst[x] = (long) (Math.round((rowSrc[x] - min) * factor) - SHIFT);
                }
            }
        }

        return new LongLattice(scaled, source.ox, source.oy, min, factor);
    }

    public short[][] getNANPositions() {
        if (NANPositions == null) {
            initializeNANPositions();
        }
        return NANPositions;
    }

    private void initializeNANPositions() {
        int sizey = data.length;
        int sizex = data[0].length;
        NANPositions = new short[sizey][2];
        for (int y = 0; y < sizey; y++) {
            short x = 0;
            while (x < sizex && data[y][x] == NAN) {
                x++;
            }
            NANPositions[y][0] = (short) (x - 1);

            x = (short) (sizex - 1);
            while (x >= 0 && data[y][x] == NAN) {
                x--;
            }
            NANPositions[y][1] = (short) (x + 1);
        }
    }

    public short[][] getNANPositionStream() {
        short[][] source = getNANPositions();
        int size = source.length;
        short[][] result = new short[size][2];
        result[0][0] = source[0][0];
        result[0][1] = source[0][1];
        for (int i = 1; i < size; i++) {
            result[i][0] = (short) (source[i][0] - source[i - 1][0]);
            result[i][1] = (short) (source[i][1] - source[i - 1][1]);
        }
        return result;
    }

    public void loadNANPositionStream(short[][] source) {
        int size = data.length;
        if (size != source.length)
            throw new IllegalArgumentException("Parameter length = " + source.length
                    + ", data length = " + data.length);

        NANPositions = new short[size][2];
        NANPositions[0][0] = source[0][0];
        NANPositions[0][1] = source[0][1];
        for (int y = 0; y < size; y++) {
            if (y > 0) {
                NANPositions[y][0] = (short) (source[y][0] + NANPositions[y - 1][0]);
                NANPositions[y][1] = (short) (source[y][1] + NANPositions[y - 1][1]);
            }
            for (int x = 0; x <= NANPositions[y][0]; x++) {
                data[y][x] = NAN;
            }
            for (int x = NANPositions[y][1]; x < data[y].length; x++) {
                data[y][x] = NAN;
            }
        }
    }

    public byte[] getDataAsByteStream() {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        short NANPositions[][] = getNANPositions();

        for (int y = 0; y < data.length; y++) {
            long row[] = data[y];
            for (int x = 0; x < row.length; x++) {
                if (x <= NANPositions[y][0] || x >= NANPositions[y][1])
                    continue;

                long d = row[x];
                if (d == NAN) {
                    d = 0;
                }
                byteOut.write((byte) d);
            }
        }
        return byteOut.toByteArray();
    }

    public void loadDataAsByteStream(byte[] source) {
        int n = 0;
        short NANPositions[][] = getNANPositions();

        for (int y = 0; y < data.length; y++) {
            long row[] = data[y];
            for (int x = 0; x < row.length; x++) {
                if (x <= NANPositions[y][0] || x >= NANPositions[y][1])
                    continue;

                row[x] = source[n++] & 0xff;
            }
        }

    }

    public List<Run> getNanRuns() {

        short NANPositions[][] = getNANPositions();
        List<Run> result = new LinkedList<Run>();
        for (int y = 0; y < data.length; y++) {
            long row[] = data[y];
            boolean insideRun = false;
            int runStart = -1;
            for (int x = 0; x < row.length; x++) {
                if (x <= NANPositions[y][0] || x >= NANPositions[y][1])
                    continue;

                if (row[x] == NAN) {
                    if (!insideRun) {
                        insideRun = true;
                        runStart = x;
                    }
                    continue;
                }

                if (insideRun) {
                    result.add(new Run((short) runStart, (short) y, (short) (x - runStart)));
                    insideRun = false;
                }
            }
        }
        return result;
    }

    public void loadNanRuns(List<Run> runs) {
        for (Run run : runs) {
            for (int x = run.x; x < run.x + run.length; x++) {
                data[run.y][x] = NAN;
            }
        }
    }

    public static LongLattice createCopyOf(LongLattice lattice) {
        long[][] srcData = lattice.getData();
        long[][] dstData = ArrayUtils.copyOfArray(srcData);
        return new LongLattice(dstData, lattice.ox, lattice.oy, lattice.min, lattice.factor);
    }
}
