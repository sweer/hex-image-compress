Lossless compression of hexagonally sampled images using a multiresolution filter bank 
==================
See hexagonal_image_compression.pdf article for details of the problem and the solution.

This is an Eclipse project. It uses Java 6 and JUnit 4.

There are plenty of unit tests, run them after each change. 
The overall API is implemented by pipeline/Encoder and pipeline/Decoder classes. 
For usag examples see test/pipeline/DecoderTest.testEncodeDecodeLena100(), 
main/DemoImageSet, other demo programs in main package.   

This software package includes BitInputStream and BitOutputStream 
implementation by Owen Astrachan and Range Coder implementation 
by Joe Halliwell. The author is very grateful to them.  


License
==================

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. 


Possibly relevant notes
==================

2D arrays are addressed [y][x] like matrices. 
Methods having x and y arguments have XY in their names to denote order of arguments.
Each hexagonally sampled image is stored in a rectangular 2D array (a Lattice) big enough for it to fit.
Missing points are represented by Double.NaN or QuantizedLattice.NAN. 
Some methods are public only to make them accessible by tests.


TODO
==================

- Replace long with byte in LongLattice
- Add checks at the library boundary for null & 0 length arrays, values out of 0..255 range
- Verify it on 1x1, 2x2, 3x3 images
- Simplify coding NAN sequences in the start and end of a row, so that it takes less bytes

