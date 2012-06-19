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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import lattice.LongLattice;

import bitstream.BitInputStream;
import bitstream.BitOutputStream;

/**
 * The idea of the wavelet we use belongs to Anatolijs Ressins (@artazor). Every
 * second point is a "center" point. Center point values get replaced by 1/4th
 * of itself + 1/8th of surrounding hexagon petals sum. After replacing center
 * values, points between centers (the hexagon petals) each have average of two
 * neighbor centers subtracted from them. Then the wavelet is applied to the
 * centers, thus scaling it by factor of two.
 * 
 * Copyright (C) 2012 Aleksejs Truhans
 */
public class HexByteRavelet extends LongLattice implements HexRavelet {

    private byte[][][] remainderBits; // remainderBits[n] are the bits that
                                      // dropped out when encoding level n

    public void setRemainderBits(byte[][][] remainderBits) {
        this.remainderBits = remainderBits;
    }

    public byte[][][] getRemainderBits() {
        return remainderBits;
    }

    public HexByteRavelet(LongLattice lattice) {
        super(lattice);
        remainderBits = new byte[getMaxLevel() + 1][data.length][data[0].length];
    }

    @Override
    public void encode() {
        int maxLength = Math.max(data[0].length, data.length);
        int level = 0;
        int halfStep = 1 << level;
        while (halfStep < maxLength) {
            encodeLevel(level);
            level++;
            halfStep = 1 << level;
        }
    }

    public final int getMaxLevel() {
        int length = Math.max(data[0].length, data.length);
        int level = 0;
        while (length > 0) {
            level++;
            length >>= 1;
        }
        return level - 1;
    }

    @Override
    public void encodeLevel(int level) {
        Stage stage = Stage.ENCODE;
        processCenters(level, stage);
        processBetween(level, Direction.ROW, stage);
        processBetween(level, Direction.COLUMN, stage);
        processBetween(level, Direction.DIAGONAL, stage);
    }

    @Override
    public void decode() {
        int maxLength = Math.max(data[0].length, data.length);

        int level = 0;
        int halfStep = 1 << level;
        while (halfStep < maxLength) {
            level++;
            halfStep = 1 << level;
        }

        level--;
        halfStep = 1 << level;
        while (halfStep > 0) {
            decodeLevel(level);
            level--;
            halfStep = 1 << level;
        }

    }

    @Override
    public void decodeLevel(int level) {
        Stage stage = Stage.DECODE;
        processBetween(level, Direction.ROW, stage);
        processBetween(level, Direction.COLUMN, stage);
        processBetween(level, Direction.DIAGONAL, stage);
        processCenters(level, stage);
    }

    private void processBetween(int level, Direction direction, Stage stage) {
        int step = 2 << level;
        int halfStep = step / 2;
        final int[] plusMinusOne = new int[] { -1, +1 };

        for (int y = direction.y0 * halfStep; y < data.length; y += step) {
            long row[] = data[y];
            for (int x = direction.x0 * halfStep; x < row.length; x += step) {
                if (row[x] == NAN)
                    continue;

                long sum = 0;
                int count = 0;
                for (int delta : plusMinusOne) {
                    long a = safeGetDataXY(x + delta * direction.xdelta * halfStep, y + delta
                            * direction.ydelta * halfStep);
                    if (a != NAN) {
                        sum += a;
                        count++;
                    }
                }
                if (count > 0) {
                    row[x] += stage.averageCoef * (sum / count);
                    if (row[x] < 0)
                        row[x] += 256;
                    if (row[x] > 255)
                        row[x] -= 256;
                }
            }
        }
    }

    private void processCenters(int level, Stage stage) {
        int step = 2 << level;
        for (int y = 0; y < data.length; y += step) {
            long row[] = data[y];
            for (int x = 0; x < row.length; x += step) {
                long d = row[x];
                if (d == NAN)
                    continue;
                if (stage == Stage.ENCODE) {
                    d -= stage.averageCoef * (sumPetalsXY(x, y, step / 2) / 2);
                    row[x] = d / 4;
                    remainderBits[level][y][x] = (byte) (d % 4);
                } else {
                    row[x] = d * 4 + remainderBits[level][y][x] - stage.averageCoef
                            * (sumPetalsXY(x, y, step / 2) / 2);
                }

            }
        }
    }

    private long sumPetalsXY(int x, int y, int step) {
        long sumPetals = 0;
        int count = 0;
        for (int i = 0; i < HEXAGON_TRAVERSAL_XY.length; i++) {
            long d = safeGetDataXY(x + HEXAGON_TRAVERSAL_XY[i][0] * step, y
                    + HEXAGON_TRAVERSAL_XY[i][1] * step);
            if (d != NAN) {
                sumPetals += d;
                count++;
            }
        }
        if (count > 0 && count < 6) {
            sumPetals = (sumPetals * 6) / count;
        }
        return sumPetals;
    }

    private long safeGetDataXY(int x, int y) {
        return (x >= 0 && x < data[0].length && y >= 0 && y < data.length) ? data[y][x] : NAN;
    }

    public int getPointLevelXY(int x, int y) {
        int level = 0;
        if (x == 0 && y == 0)
            return getMaxLevel();
        while ((x & 1) == 0 && (y & 1) == 0) {
            level++;
            x >>= 1;
            y >>= 1;
        }
        return level;
    }

    public byte[] getReminderBitStream() {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BitOutputStream bitOut = new BitOutputStream(byteOut);
        int step = 2;
        for (int y = 0; y < data.length; y += step) {
            long row[] = data[y];
            for (int x = 0; x < row.length; x += step) {
                int level = getPointLevelXY(x, y);
                for (int i = 1; i <= level; i++) {
                    bitOut.write(2, remainderBits[i - 1][y][x]);
                }
            }
        }
        bitOut.flush();
        return byteOut.toByteArray();
    }

    public void loadRemainderBitStream(byte[] source) {
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(source);
            BitInputStream bitIn = new BitInputStream(byteIn);
            int step = 2;
            for (int y = 0; y < data.length; y += step) {
                long row[] = data[y];
                for (int x = 0; x < row.length; x += step) {
                    int level = getPointLevelXY(x, y);
                    for (int i = 1; i <= level; i++) {
                        remainderBits[i - 1][y][x] = (byte) bitIn.read(2);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long[][] getHigherLevelLattice(int level) {
        int step = 1 << level;
        int sizey = (data.length - 1) / step + 1;
        int sizex = (data[0].length - 1) / step + 1;
        long[][] result = new long[sizey][sizex];
        for (int y = 0, resulty = 0; y < data.length; y += step, resulty++) {
            long row[] = data[y];
            for (int x = 0, resultx = 0; x < row.length; x += step, resultx++) {
                result[resulty][resultx] = row[x];
            }
        }
        return result;
    }
}
