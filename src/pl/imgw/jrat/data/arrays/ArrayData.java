/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.arrays;

/**
 * 
 * This is a container of array data, standard format of array data is unsigned
 * byte. All values are stored as number from 0 to 255.
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public abstract class ArrayData {
    
    protected int sizeX;
    protected int sizeY;
    
    public static double NODATA_POINT = -9999;
    public static byte NODATA_RAW_BYTE_POINT = 0;
    public static short NODATA_RAW_INT_POINT = -1;
    
    
    /**
     * 
     * Initialize new array of size sizeX and sizeY
     * 
     * @param sizeX
     *            X-dimension array size

     * @param sizeY
     *            Y-dimension array size
     */
    public abstract void initialize(int sizeX, int sizeY);

    /**
     * 
     * Receiving value from the array in point (x, y). If x or y is out of
     * bounds return value will be <code>NODATA_RAW_INT_POINT</code>..
     * 
     * @param x X-dimension (range bin)
     * @param y Y-dimension (azimuth gate) 
     * @return returns signed short from 0 to 255, if x or y is out of bounds returns
     *         <code>NODATA_RAW_INT_POINT</code>.
     */
    public abstract short getRawIntPoint(int x, int y);

    /**
     * Setting value in the array in point (x, y). If x or y is out of bounds it
     * returns false, otherwise it returns true.
     * 
     * @param x 
     * @param y 
     * @param value
     * @return true if successful, false if x or y is out of bounds
    public abstract boolean setRawIntPoint(int x, int y, short value);
     */

    /**
     * Receiving value from the array in point (x, y). If x or y is out of
     * bounds return value will be <code>NODATA_RAW_BYTE_POINT</code>.
     * 
     * @param x X-dimension (range bin)
     * @param y Y-dimension (azimuth gate)
     * @return unsigned byte, if x or y is out of bounds returns
     *         <code>NODATA_RAW_BYTE_POINT</code>
     */
    public abstract byte getRawBytePoint(int x, int y);

    /**
     * Setting value in the array in point (x, y).The value must be an unsigned
     * byte. If x or y is out of bounds it returns false, otherwise it returns
     * true
     * 
     * @param x 
     * @param y 
     * @param value
     * @return true if successful, false if x or y is out of bounds
    public abstract boolean setRawBytePoint(int x, int y, byte value);
     */

    /**
     * Receiving real value from the array in point (x, y). If x or y is out of
     * bounds return value will be <code>NODATA_POINT</code>..
     * 
     * @param x X-dimension (range bin)
     *            
     * @param y Y-dimension (azimuth gate)
     *            
     * @return if x or y is out of bounds returns <code>NODATA_POINT</code>.
     */
    public abstract double getPoint(int x, int y);

    /**
     * Setting value in the array in point (x, y). If x or y is out of bounds it
     * returns false, otherwise it returns true
     * 
     * @param x
     * @param y
     * @param value
     * @return true if successful, false if x or y is out of bounds
    public abstract boolean setPoint(int x, int y, double value);
     */

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }
    
    protected byte int2byte(short i) {
        if (i > 255)
            return 0;
        return (byte) ((i & 0x000000ff));

    }

    protected int unsignedByte2Int(byte b) {
        return (int) b & 0xFF;
    }

}
