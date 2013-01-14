/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.arrays;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class FloatDataArray extends ArrayData implements Cloneable {

    private float[][] data;

    public FloatDataArray() {

    }

    /**
     * @param array
     */
    public FloatDataArray(float[][] data) {
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
        this.data = new float[sizeX][sizeY];

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
        return (short) data[x][y];
    }

    public boolean setFloatPoint(int x, int y, float value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return false;
        }
        data[x][y] = value;
        return true;
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
        data[x][y] = value;
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
        return int2byte((short) data[x][y]);
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
        data[x][y] = unsignedByte2Int(value);
        return true;
    }
     */

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
        return data[x][y];
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
        data[x][y] = (float) value;
        return true;
    }
     */

    public void multiArrayCopy(float[][] source, float[][] destination) {
        for (int a = 0; a < source.length; a++) {
            System.arraycopy(source[a], 0, destination[a], 0, source[a].length);
        }
    }

    public Object clone() {

        float[][] array = new float[sizeX][sizeY];
        multiArrayCopy(data, array);

        ArrayData dc = new FloatDataArray(array);

        return dc;
    }
    
    public void transpose() {
        float[][] array = new float[sizeY][sizeX];
        for(int x = 0; x < sizeX; x++)
            for(int y = 0; y < sizeY; y++)
                array[y][x] = data[x][y];
        
        data = array;
        this.sizeX = data.length;
        this.sizeY = data[0].length;
    }

}
