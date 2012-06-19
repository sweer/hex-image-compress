package pipeline;

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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import lattice.DoubleLattice;
import lattice.LongLattice;
import lattice.LongLattice.Run;

import rangecoder.Compressor;
import util.ArrayUtils;
import util.ImageUtils;
import filter.H2O;
import filter.HexByteRavelet;

//@formatter:off
/**
 * The pipeline can process only grayscale images no more than 32767 pixels wide
 * and high. The generated compressed byte stream has the following format: 
 * - length of range coded header in bytes (int) 
 * - range coded header: 
 *     - quantized lattice params (shorts): ox, oy, sizex, sizey; (doubles): quantization min,factor. 
 *     - NAN positions: index of last from left side and last from right side 
 *         - 0-th row (shorts): from left, from right 
 *         - for each following image row it's stored as difference (what has to be added \
 *           to the previous row's value to get value for the current row) 
 *           (shorts): from left, from right 
 * - length of range coded lattice in bytes (int) 
 * - range coded lattice. NANs are replaced with 0. 
 * - length of NAN runs in bytes (int) 
 * - list of NAN runs. 
 *     - For each horizontal sequence of NANs in the lattice 
 *       not described yet by NAN positions: short x, short y of beginning and short length of the run 
 * - length of remainder bit planes in bytes (int) 
 *     - for each level 1 and higher cell all it's remainder bits packed into a bit stream 
 *     (so that 2 cells can fit into a single byte, for example)
 * 
 * @see Decoder for decoding functionality.
 * @see DecoderTest.testEncodeDecodeLena100() for usage example.
 */
public class Encoder {
//@formatter:on
    /**
     * @author Aleksejs Truhans
     */
    public static class Statistics {
        private int compressedHeaderSizeBytes;
        private int compressedLatticeSizeBytes;
        private int remainderBitsSizeBytes;
        private int NanRunsSizeBytes;

        public int getCompressedHeaderSizeBytes() {
            return compressedHeaderSizeBytes;
        }

        public int getCompressedLatticeSizeBytes() {
            return compressedLatticeSizeBytes;
        }

        public int getRemainderBitsSizeBytes() {
            return remainderBitsSizeBytes;
        }

        public int getNanRunsSizeBytes() {
            return NanRunsSizeBytes;
        }
    }

    private final LongLattice source;
    private final Statistics statistics = new Statistics();

    public Encoder(LongLattice source) {
        this.source = source;
    }

    public Encoder(BufferedImage sourceImage) {
        this(representImageAsHexagonalLattice(sourceImage));
    }

    public byte[] encode() {
        List<byte[]> data = new LinkedList<byte[]>();
        HexByteRavelet ravelet = new HexByteRavelet(source);
        ravelet.encode();

        byte[] compressedHeader = Compressor.compress(generateHeader(ravelet));
        statistics.compressedHeaderSizeBytes = compressedHeader.length;
        data.add(ArrayUtils.intAsByteArray(compressedHeader.length));
        data.add(compressedHeader);

        byte[] compressedLattice = Compressor.compress(ravelet.getDataAsByteStream());
        statistics.compressedLatticeSizeBytes = compressedLattice.length;
        data.add(ArrayUtils.intAsByteArray(compressedLattice.length));
        data.add(compressedLattice);

        List<Run> nanRuns = ravelet.getNanRuns();
        byte[] nanRunsBytes = generateNanRuns(nanRuns);
        statistics.NanRunsSizeBytes = nanRunsBytes.length;
        data.add(ArrayUtils.intAsByteArray(nanRunsBytes.length));
        data.add(nanRunsBytes);

        byte[] remainderBits = Compressor.compress(ravelet.getReminderBitStream());
        statistics.remainderBitsSizeBytes = remainderBits.length;
        data.add(ArrayUtils.intAsByteArray(remainderBits.length));
        data.add(remainderBits);

        return ArrayUtils.joinArrays(data);
    }

    public static byte[] generateNanRuns(List<LongLattice.Run> nanRuns) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);

            for (Run run : nanRuns) {
                dataOut.writeShort((short) run.x);
                dataOut.writeShort((short) run.y);
                dataOut.writeShort((short) run.length);
            }

            return byteOut.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static LongLattice representImageAsHexagonalLattice(BufferedImage sourceImage) {
        double[][] source = ImageUtils.imageRasterToDoubleArray(sourceImage);
        return representImageAsHexagonalLattice(source);
    }

    public static LongLattice representImageAsHexagonalLattice(double[][] source) {
        DoubleLattice hexDoubleLattice = H2O.o2h(source);
        LongLattice hexLongLattice = LongLattice.fromLattice(hexDoubleLattice);
        return hexLongLattice;
    }

    public static byte[] generateHeader(LongLattice lattice) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);

            dataOut.writeShort((short) lattice.ox);
            dataOut.writeShort((short) lattice.oy);
            dataOut.writeShort((short) lattice.getData()[0].length);
            dataOut.writeShort((short) lattice.getData().length);
            dataOut.writeDouble(lattice.min);
            dataOut.writeDouble(lattice.factor);

            short[][] NANPositions = lattice.getNANPositionStream();
            for (short a[] : NANPositions) {
                dataOut.writeShort(a[0]);
                dataOut.writeShort(a[1]);
            }
            return byteOut.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public LongLattice getLattice() {
        return source;
    }

}
