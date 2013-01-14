/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.arrays;

/**
 * 
 * Unsigned byte array data container. All values will be stored as
 * unsigned bytes from range 0 to 255.
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RawByteDataArray extends ArrayData implements Cloneable {

    protected byte[][] data = null;
    protected double offset = 0;
    protected double gain = 0;


    public void transpose() {
        byte[][] array = new byte[sizeY][sizeX];
        for(int x = 0; x < sizeX; x++)
            for(int y = 0; y < sizeY; y++)
                array[y][x] = data[x][y];
        
        data = array;
        this.sizeX = data.length;
        this.sizeY = data[0].length;
    }
    
    /**
     * @param offset
     *            the offset to set
     */
    public void setOffset(double offset) {
        this.offset = offset;
    }

    /**
     * @param gain
     *            the gain to set
     */
    public void setGain(double gain) {
        this.gain = gain;
    }

    /**
     * @return the offset
     */
    public double getOffset() {
        return offset;
    }

    /**
     * @return the gain
     */
    public double getGain() {
        return gain;
    }

    public RawByteDataArray() {

    }

    /**
     * @param infDataBuff
     */
    public RawByteDataArray(byte[][] data) {
        if (data != null) {
            this.sizeX = data.length;
            this.sizeY = data[0].length;
            this.data = data;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#initialize(int, int)
     */
    @Override
    public void initialize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.data = new byte[sizeX][sizeY];
    }

    public short[][] getShortData() {
        short[][] idata = new short[sizeX][sizeY];

        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++)
                idata[i][j] = (short) unsignedByte2Int(data[i][j]);

        return idata;
    }

    public byte[][] getByteData() {
        return data;
    }

    /**
     * Given value has to be from 0 to 255 range and byte should be unsigned.
     * Otherwise when converting to integer the value might be wrong.
     * 
     * @param data
     */
    public void setByteData(byte[][] data) {
        this.sizeX = data.length;
        this.sizeY = data[0].length;
        this.data = data;

    }

    /**
     * Given value will be converted to unsigned byte, it has to be from 0 to
     * 255 range. Otherwise value 0 will be set.
     * 
     * @param data
     */
    public void setIntData(int[][] data) {
        if (data != null) {
            int sizeX = data.length;
            int sizeY = data[0].length;
            initialize(sizeX, sizeY);
            for (int x = 0; x < sizeX; x++)
                for (int y = 0; y < sizeY; y++)
                    this.data[x][y] = int2byte((short) data[x][y]);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#getPoint(int, int)
     */
    @Override
    public short getRawIntPoint(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            // System.out.println(index ++);
            return -1;
        }
        return (short) unsignedByte2Int(data[x][y]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#setPoint(int, int, short)
    @Override
    public boolean setRawIntPoint(int x, int y, short value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return false;
        }
        data[x][y] = int2byte(value);

        return true;
    }
     */

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#getBytePoint(int, int)
     */
    @Override
    public byte getRawBytePoint(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            // System.out.println(index ++);
            return 0;
        }
        return data[x][y];
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#setBytePoint(int, int, byte)
    @Override
    public boolean setRawBytePoint(int x, int y, byte value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {

            return false;
        }
        data[x][y] = value;
        return true;
    }
     */

    private void multiArrayCopy(byte[][] source, byte[][] destination) {
        for (int a = 0; a < source.length; a++) {
            System.arraycopy(source[a], 0, destination[a], 0, source[a].length);
        }
    }

    public Object clone() {

        byte[][] array = new byte[sizeX][sizeY];
        multiArrayCopy(data, array);

        ArrayData dc = new RawByteDataArray(array);

        return dc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#getDoublePoint(int, int)
     */
    @Override
    public double getPoint(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            // System.out.println(index ++);
            return -9999;
        }
        short value = (short) unsignedByte2Int(data[x][y]);
        if (gain == 0)
            return value;
        return raw2real(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#setDoublePoint(int, int,
     * double)
    @Override
    public boolean setPoint(int x, int y, double value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return false;
        }
        if (gain == 0)
            data[x][y] = int2byte((short) value);
        else
            data[x][y] = int2byte(real2raw(value));
        return true;
    }
     */

    private short real2raw(double x) {
        if (gain == 0)
            return 0;
        return (short) ((1 / gain * (x - offset)) + 1);
    }

    private double raw2real(int x) {
        return gain * x + offset;
    }

}
