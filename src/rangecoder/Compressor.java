package rangecoder;
/**
 * Implementation of Range Coding Compression/Decompression
 * Range encoding is a lossless data compression method defined by G N N Martin in his
 * 1979 paper on "Range encoding: an algorithm for removing redundancy from a 
 * digitized message". 
 * 
 * This code was written to provide simple data compression for J2ME.
 * 
 * Based on reference materials and code from  http://www.bodden.de/studies/ac/
 *
 * The homepage for this software is http://winterwell.com/software/compressor.php
 *
 * (c) 2008 Joe Halliwell <joe.halliwell@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class Compressor {
	
	/* Tailored to int as 32bit signed */
	private final static int FIRST_QUARTER = 0x200000;
	private final static int THIRD_QUARTER = 0x600000;
	private final static int HALF          = 0x400000;
	private final static int HIGH          = 0x7fffff;
	private final static int INITIAL_READ  = 23;
	
	public static byte[] compress(byte[] in) {
		
		class BitOutputBuffer {
			ByteArrayOutputStream buf;
			byte[] currentByte;
			byte currentBit;
			BitOutputBuffer() {
				buf = new ByteArrayOutputStream();
				currentByte = new byte[1];
				currentByte[0] = 0;
				currentBit = 0;
			}
			void writeBit(byte bit) throws IOException {
				currentByte[0] = (byte) ((currentByte[0]) << 1);
				currentByte[0] += bit;
				currentBit+=1;
				if (currentBit==8) {
					buf.write(currentByte);
					currentByte[0] = 0;
					currentBit = 0;
				}
			}
			void flush() throws IOException {
				/* Pad the buffer with zeros */
				while (currentBit!=0) {
					writeBit((byte) 0);
				}
				buf.flush();
			}
			
			byte[] toByteArray() {
				try {
					buf.flush();
					return buf.toByteArray();
				}
				catch (IOException e) {
					return null;
				}
			}
			
		}
		
		BitOutputBuffer bitBuf = new BitOutputBuffer();
		
		int low = 0, high = HIGH, total;
		int mLow = low, mHigh = high, mStep = 0;
		int mScale = 0;
		int current = 0;
		
		/* Initialize frequency table */
		int[] freq = new int[257];
		for (int i=0; i<257; i++) freq[i] = 1;
		total = 257;
		
		try {
			
			for (int i=0; i<in.length + 1; i++) {
				
				if (i == in.length) {
					/* Encode terminator if necessary */
					low = total - 1;
					high = total;
				}
				else {
					/* Otherwise retrieve cumulative freq */
					current = in[i] & 0xff; // Get unsigned value
					low = 0;
					for (int j=0; j<current; j++) {
						low += freq[j];
					}
					high = low + freq[current];
				}
				
				/* 2. Update the coder */
				mStep = ( mHigh - mLow + 1 ) / total;
				mHigh = (mLow + mStep * high) - 1;
				mLow = mLow + mStep * low;
				
				/* Renormalize if possible */
				while( (mHigh < HALF) || (mLow >= HALF) )
				{
					if( mHigh < HALF )
					{
						bitBuf.writeBit((byte) 0);
						mLow = mLow * 2;
						mHigh = mHigh * 2 + 1;
						
						/* Perform e3 mappings */
						for(; mScale > 0; mScale-- )
							bitBuf.writeBit((byte) 1);
					}
					else if( mLow >= HALF )
					{
						bitBuf.writeBit((byte) 1);
						mLow = ( mLow - HALF ) * 2;
						mHigh = ( mHigh - HALF ) * 2 + 1;
						
						/* Perform e3 mappings */
						for(; mScale > 0; mScale-- )
							bitBuf.writeBit((byte) 0);
					}
				}
				
				while((FIRST_QUARTER <= mLow) && (mHigh < THIRD_QUARTER)) {
					mScale++;
					mLow = ( mLow - FIRST_QUARTER ) * 2;
					mHigh = ( mHigh - FIRST_QUARTER ) * 2 + 1;
				}		
				
				/* 3. Update model */
				freq[current]+=1;
				total+=1;
				
			}
			/* Finish encoding */
			if( mLow < FIRST_QUARTER ) {
				/* Case: mLow < FirstQuarter < Half <= mHigh */
				bitBuf.writeBit((byte) 0);
				/* Perform e3-scaling */
				for( int i=0; i<mScale+1; i++ ) 
					bitBuf.writeBit((byte) 1);
			} else {
				/* Case: mLow < Half < ThirdQuarter <= mHigh */
				bitBuf.writeBit((byte) 1 );
			}
			bitBuf.flush();
		}
		catch (IOException e) {
			return null;
		}
		return bitBuf.toByteArray();
	}
	
	public static byte[] decompress(byte[] in) {
		
		class BitInputBuffer {
			byte[] source;
			int bytep = 0, bitp = 0;
			byte currentByte = 0;
			BitInputBuffer(byte [] source) {
				this.source = source;
				currentByte = source[0];// & 0xff;
			}
			int readBit() {
				int result = (currentByte >> 7) & 1;
				currentByte = (byte) (currentByte << 1); 
				if (bitp++==7) {
					bytep++;
					if (bytep > source.length - 1) {
						currentByte = 0;
					}
					else {
						currentByte = source[bytep];
						bitp = 0;
					}
				}
				return result;
			}
		}
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		/* Initialise frequency table */
		
		int[] freq = new int[257];
		for (int i=0; i<257; i++) freq[i] = 1;
		int total = 257;
		int current = 0;
		int value;
		int low = 0, high = HIGH;
		int mLow = low, mHigh = high, mStep = 0, mScale = 0, mBuffer = 0;
		BitInputBuffer inbuf = new BitInputBuffer(in);
		/*	Fill buffer with bits from the input stream */

		for( int i=0; i<INITIAL_READ; i++ ) {
			mBuffer = 2 * mBuffer;
			mBuffer += inbuf.readBit();
		}
			
		while(true) {
			/* 1. Retrieve current byte */
			mStep = ( mHigh - mLow + 1 ) / total;
			value = ( mBuffer - mLow ) / mStep;
			low = 0;
			for (current=0; current<256 && low + freq[current] <= value; current++)
				low += freq[current];
			
			if (current==256) break;
			
			buf.write(current);
			high = low + freq[current];
			
			/* 2. Update the decoder */
			mHigh = mLow + mStep * high - 1; // interval open at the top => -1
			
			/* Update lower bound */
			mLow = mLow + mStep * low;
			
			/* e1/e2 mapping */
			while( ( mHigh < HALF ) || ( mLow >= HALF ) )
			{
				if( mHigh < HALF )
				{
					mLow = mLow * 2;
					mHigh = ((mHigh * 2) + 1);
					mBuffer = (2 * mBuffer);
				}
				else if( mLow >= HALF )
				{
					mLow = 2 * ( mLow - HALF );
					mHigh = 2 * ( mHigh - HALF ) + 1;
					mBuffer = 2 * ( mBuffer - HALF );
				}
	
				mBuffer += inbuf.readBit();
				mScale = 0;
			}
			
			/* e3 mapping */
			while( ( FIRST_QUARTER <= mLow ) && ( mHigh < THIRD_QUARTER ) )
			{
				mScale++;
				mLow = 2 * ( mLow - FIRST_QUARTER );
				mHigh = 2 * ( mHigh - FIRST_QUARTER ) + 1;
				mBuffer = 2 * ( mBuffer - FIRST_QUARTER ) ;
				mBuffer += inbuf.readBit();
			}
			
			/* 3. Update frequency table */
			freq[current]+=1;
			total+=1;
		}
		
		return buf.toByteArray();
	}
}

