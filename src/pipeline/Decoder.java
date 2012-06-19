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
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import lattice.DoubleLattice;
import lattice.LongLattice;
import lattice.LongLattice.Run;

import rangecoder.Compressor;

import util.ImageUtils;
import filter.H2O;
import filter.HexByteRavelet;

/**
 * Decodes byte stream into a lattice. @see Encoder for byte stream description.
 * 
 * @see DecoderTest.testEncodeDecodeLena100() for usage example
 * 
 * 
 **/
public class Decoder {

	/**
	 * Decodes byte stream into a hexagonal lattice. @see Encoder for byte
	 * stream description.
	 **/
	public static LongLattice decodeToHexagonalLattice(byte[] source) {
		DecodeRunner decoder = new DecodeRunner(source);
		return decoder.decode();
	}

	/**
	 * Decodes byte stream into an orthogonal array. @see Encoder for byte
	 * stream description.
	 **/
	public static double[][] decodeToImageInArray(byte[] encoded) {
		LongLattice lattice = decodeToHexagonalLattice(encoded);
		return hexagonalLatticeToImageInArray(lattice);
	}

	public static BufferedImage hexagonalLatticeToBufferedImage(
			LongLattice source) {
		double[][] imageInArray = hexagonalLatticeToImageInArray(source);
		BufferedImage image = new BufferedImage(imageInArray[0].length,
				imageInArray.length, BufferedImage.TYPE_BYTE_GRAY);
		ImageUtils.doubleArrayToImageRaster(image, imageInArray);
		return image;
	}

	public static double[][] hexagonalLatticeToImageInArray(LongLattice source) {
		DoubleLattice doubleLattice = H2O.h2o(source.getDequantizedLattice());
		return doubleLattice.getData();
	}

	/**
	 * @author HP
	 */
	public static class DecodeRunner {
		/**
		 * @uml.property name="lattice"
		 * @uml.associationEnd
		 */
		private LongLattice lattice;
		private final ByteArrayInputStream byteIn;
		private final DataInputStream dataIn;
		/**
		 * @uml.property name="ravelet"
		 * @uml.associationEnd
		 */
		private HexByteRavelet ravelet;

		public DecodeRunner(byte[] source) {
			byteIn = new ByteArrayInputStream(source);
			dataIn = new DataInputStream(byteIn);
		}

		public void createLatticeFromCompressedHeader() {
			try {
				int length = dataIn.readInt();

				byte[] compressedHeader = new byte[length];
				int bytesRead = dataIn.read(compressedHeader);
				if (bytesRead != length)
					throw new IllegalStateException(
							"Not enough data for compressed header of length "
									+ length);

				byte[] decompressedHeader = Compressor
						.decompress(compressedHeader);
				lattice = decodeHeader(decompressedHeader);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		private void loadLatticeData() {
			try {
				int length = dataIn.readInt();

				byte[] compressedLatticeData = new byte[length];
				int bytesRead = dataIn.read(compressedLatticeData);
				if (bytesRead != length)
					throw new IllegalStateException(
							"Not enough data for compressed lattice of length "
									+ length);

				byte[] decompressedLatticeData = Compressor
						.decompress(compressedLatticeData);
				ravelet.loadDataAsByteStream(decompressedLatticeData);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

		}

		private LongLattice decode() {
			createLatticeFromCompressedHeader();
			ravelet = new HexByteRavelet(lattice);
			loadLatticeData();
			ravelet.loadNanRuns(decodeNanRuns(readNanRuns()));
			loadRemainderBits();
			ravelet.decode();
			return ravelet;
		}

		private void loadRemainderBits() {
			try {
				int length = dataIn.readInt();

				byte[] remainderBits = new byte[length];
				int bytesRead = dataIn.read(remainderBits);
				if (bytesRead != length)
					throw new IllegalStateException(
							"Not enough data for remainder bits of length "
									+ length);
				ravelet.loadRemainderBitStream(Compressor.decompress(remainderBits));
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		private byte[] readNanRuns() {
			try {
				int length = dataIn.readInt();

				byte[] nanRuns = new byte[length];
				int bytesRead = dataIn.read(nanRuns);
				if (bytesRead != length)
					throw new IllegalStateException(
							"Not enough data for NaN runs of length " + length);
				return nanRuns;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		public static LongLattice decodeHeader(byte[] source) {
			ByteArrayInputStream byteIn = new ByteArrayInputStream(source);
			DataInputStream dataIn = new DataInputStream(byteIn);
			try {
				int ox = dataIn.readShort();
				int oy = dataIn.readShort();
				int sizex = dataIn.readShort();
				int sizey = dataIn.readShort();
				double min = dataIn.readDouble();
				double factor = dataIn.readDouble();

				short[][] NANPositionStream = new short[sizey][2];
				for (short a[] : NANPositionStream) {
					a[0] = dataIn.readShort();
					a[1] = dataIn.readShort();
				}

				LongLattice lattice = new LongLattice(new long[sizey][sizex],
						ox, oy, min, factor);
				lattice.loadNANPositionStream(NANPositionStream);
				return lattice;

			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * @return
		 * @uml.property name="lattice"
		 */
		public LongLattice getLattice() {
			return lattice;
		}

		/**
		 * @return
		 * @uml.property name="ravelet"
		 */
		public HexByteRavelet getRavelet() {
			return ravelet;
		}

		public static List<Run> decodeNanRuns(byte[] source) {
			ByteArrayInputStream byteIn = new ByteArrayInputStream(source);
			DataInputStream dataIn = new DataInputStream(byteIn);
			List<Run> result = new LinkedList<Run>();
			try {
				while (true) {
					result.add(new Run(dataIn.readShort(), dataIn.readShort(),
							dataIn.readShort()));
				}
			} catch (EOFException e) {
				return result;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
