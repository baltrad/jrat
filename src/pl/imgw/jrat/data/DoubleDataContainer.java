/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class DoubleDataContainer extends ArrayDataContainer implements
        Cloneable {

    private double[][] data;

    public DoubleDataContainer() {

    }

    /**
     * @param array
     */
    public DoubleDataContainer(double[][] data) {
        if (data != null) {
            this.sizeX = data.length;
            this.sizeY = data[0].length;
            this.data = data;
        }
    }

    public void setDoubleData(double[][] data) {
        this.sizeX = data.length;
        this.sizeY = data[0].length;
        this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#initialize(int, int)
     */
    @Override
    public void initialize(int sizeX, int sizeY) {
        this.data = new double[sizeX][sizeY];
        this.sizeX = sizeX;
        this.sizeY = sizeY;

    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#getIntPoint(int, int)
     */
    @Override
    public short getIntPoint(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            // System.out.println(index ++);
            return -1;
        }
        return (short) data[x][y];
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#setIntPoint(int, int, short)
     */
    @Override
    public boolean setIntPoint(int x, int y, short value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return false;
        }
        data[x][y] = value;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#getBytePoint(int, int)
     */
    @Override
    public byte getBytePoint(int x, int y) {
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
     */
    @Override
    public boolean setBytePoint(int x, int y, byte value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return false;
        }
        data[x][y] = unsignedByte2Int(value);
        return true;
    }

    public void multiArrayCopy(double[][] source, double[][] destination) {
        for (int a = 0; a < source.length; a++) {
            System.arraycopy(source[a], 0, destination[a], 0, source[a].length);
        }
    }

    public Object clone() {

        double[][] array = new double[sizeX][sizeY];
        multiArrayCopy(data, array);

        ArrayDataContainer dc = new DoubleDataContainer(array);

        return dc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayDataContainer#getDoublePoint(int, int)
     */
    @Override
    public double getDoublePoint(int x, int y) {
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
     */
    @Override
    public boolean setDoublePoint(int x, int y, double value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {

            return false;
        }
        data[x][y] = value;
        return true;
    }

}
