/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.wz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.RawByteDataArray;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CompressedArray extends ArrayData {

    private byte[] compressedArray;

    public CompressedArray(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    
    public byte[][] getData() {
        return decompress(compressedArray, sizeX, sizeY);
    }

    public void setData(byte[][] data) {
        if (data == null)
            return;
        this.compressedArray = compress(data);
        this.sizeX = data.length;
        this.sizeY = data[0].length;
    }

    private byte[] compress(byte[][] data) {

        if (data == null) {
            return null;
        }
        int xMax = data.length;
        int yMax = data[0].length;

        int length = xMax * yMax;
        byte[] input = new byte[length];

        for (int y = 0; y < yMax; y++) {
            for (int x = 0; x < xMax; x++) {
                input[(y * xMax) + x] = data[x][y];
            }
        }

        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);

        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();

        // Create an expandable byte array to hold the compressed data.
        // You cannot use an array that's the same size as the orginal because
        // there is no guarantee that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            return null;
        }
        return bos.toByteArray();
    }

    private byte[][] decompress(byte[] data, int xsize, int ysize) {

        if (data == null || xsize == 0 || ysize == 0)
            return null;

        byte[][] output = new byte[xsize][ysize];

        // Create the decompressor and give it the data to compress
        Inflater decompressor = new Inflater();
        decompressor.setInput(data);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

        // Decompress the data
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
            }
        }
        try {
            bos.close();
        } catch (IOException e) {
        }

        // Get the decompressed data
        byte[] decompressedData = bos.toByteArray();

        for (int y = 0; y < xsize; y++) {
            for (int x = 0; x < ysize; x++) {
                output[x][y] = decompressedData[(y * xsize) + x];
            }
        }

        return output;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayData#initialize(int, int)
     */
    @Override
    public void initialize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.compressedArray = compress(new byte[sizeX][sizeY]);

    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayData#getRawIntPoint(int, int)
     */
    @Override
    public short getRawIntPoint(int x, int y) {
        RawByteDataArray data = new RawByteDataArray(decompress(
                compressedArray, sizeX, sizeY));
        return data.getRawIntPoint(x, y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayData#setRawIntPoint(int, int, short)
     */
    @Override
    public boolean setRawIntPoint(int x, int y, short value) {
        RawByteDataArray data = new RawByteDataArray(decompress(
                compressedArray, sizeX, sizeY));
        if (data.setRawIntPoint(x, y, value)) {
            this.compressedArray = compress(data.getByteData());
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayData#getRawBytePoint(int, int)
     */
    @Override
    public byte getRawBytePoint(int x, int y) {
        RawByteDataArray data = new RawByteDataArray(decompress(
                compressedArray, sizeX, sizeY));
        return data.getRawBytePoint(x, y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayData#setRawBytePoint(int, int, byte)
     */
    @Override
    public boolean setRawBytePoint(int x, int y, byte value) {
        RawByteDataArray data = new RawByteDataArray(decompress(
                compressedArray, sizeX, sizeY));
        if (data.setRawBytePoint(x, y, value)) {
            this.compressedArray = compress(data.getByteData());
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayData#getPoint(int, int)
     */
    @Override
    public double getPoint(int x, int y) {
        RawByteDataArray data = new RawByteDataArray(decompress(
                compressedArray, sizeX, sizeY));
        return data.getPoint(x, y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayData#setPoint(int, int, double)
     */
    @Override
    public boolean setPoint(int x, int y, double value) {
        RawByteDataArray data = new RawByteDataArray(decompress(
                compressedArray, sizeX, sizeY));
        if (data.setPoint(x, y, value)) {
            this.compressedArray = compress(data.getByteData());
            return true;
        }
        return false;
    }

}
