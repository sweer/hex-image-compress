package util;
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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import lattice.LongLattice;

public class ArrayUtils {

    public static double[] subtract2DArraysAs1D(double[][] a, double[][] b) {
        int height = a.length;
        int width = a[0].length;
        double[] result = new double[height * width];
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[i++] = a[y][x] - b[y][x];
            }
        }
        return result;
    }

    public static long[][] copyOfArray(long[][] src) {
        long[][] dst = new long[src.length][];
        for (int i = 0; i < src.length; i++) {
            dst[i] = Arrays.copyOf(src[i], src[i].length);
        }
        return dst;
    }

    
    public static boolean arraysAreCloseEnough(double[][] expected, double[][] actual, double delta) {
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                if (Double.isNaN(expected[i][j]) && Double.isNaN(actual[i][j]))
                    continue;
                if (Double.isNaN(expected[i][j]) || Double.isNaN(actual[i][j])
                        || Math.abs(expected[i][j] - actual[i][j]) > delta) {
                    System.out.println("arraysAreCloseEnough failed at YX[" + i + "," + j + "], value "
                            + actual[i][j] + " differs from expected " + expected[i][j]);
                    return false;
                }
            }
        }
        return true;
    }

    public static void saveArrayAsTextFile(double[] noise, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);

            for (double d : noise) {
                bw.write(String.format("%09.5f\n", d));
            }

            bw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean arraysAreCloseEnough(long[][] expected, long[][] actual) {
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                if (expected[i][j] != actual[i][j]) {
                    System.out.println("arraysAreCloseEnough failed at YX[" + i + "," + j
                            + "], actual value " + actual[i][j] + " differs from expected "
                            + expected[i][j]);
                    return false;
                }
            }
        }
        return true;
    }

    public static void print2dArray(long[][] data) {
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                if (data[y][x] != LongLattice.NAN) {
                    System.out.print((int) data[y][x] + " ");
                } else {
                    System.out.print("NAN ");
                }
            }
            System.out.println();
        }
    }

    public static void saveArrayAsTextFileSkipNAN(long[] data, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);

            for (long l : data) {
                if (l != LongLattice.NAN) {
                    bw.write(Long.toString(l));
                    bw.write('\n');
                }
            }

            bw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveArrayAsTextFileSkipNAN(long[][] data, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);

            for (long a[] : data) {
                for (long l : a) {
                    if (l != LongLattice.NAN) {
                        bw.write(Long.toString(l));
                        bw.write(' ');
                    }
                }
                bw.write('\n');
            }

            bw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static byte[] joinArrays(List<byte[]> data) {
        int size = 0;
        for (byte[] a : data) { 
            size += a.length;
        }
        int i = 0; 
        byte[] result = new byte[size];
        for (byte[] a : data) { 
            for (int j = 0; j < a.length; j++) { 
                result[i++] = a[j];
            }
        }
        return result;
    }

    public static byte[] intAsByteArray(int n) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        try {
            dataOut.writeInt(n);
            return byteOut.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
