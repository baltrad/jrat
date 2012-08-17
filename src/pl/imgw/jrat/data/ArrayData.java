/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

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
    
    /**
     * 
     * Initialize new array of size sizeX and sizeY
     * 
     * @param sizeX
     *            width of new array
     * @param sizeY
     *            height of new array
     */
    public abstract void initialize(int sizeX, int sizeY);

    /**
     * 
     * Receiving value from the array in point (x, y). If x or y is out of
     * bounds return value will be -1
     * 
     * @param x ray number
     * @param y bin number
     * @return returns integer from 0 to 255, if x or y is out of bounds returns
     *         -1
     */
    public abstract short getRawIntPoint(int x, int y);

    /**
     * Setting value in the array in point (x, y). If x or y is out of bounds it
     * returns false, otherwise it returns true.
     * 
     * @param x ray number
     * @param y bin number
     * @param value
     * @return true if successful, false if x or y is out of bounds
     */
    public abstract boolean setRawIntPoint(int x, int y, short value);

    /**
     * Receiving value from the array in point (x, y). If x or y is out of
     * bounds return value will be 0.
     * 
     * @param x ray number
     * @param y bin number
     * @return unsigned byte, if x or y is out of bounds returns 0
     */
    public abstract byte getRawBytePoint(int x, int y);

    /**
     * Setting value in the array in point (x, y).The value must be an unsigned
     * byte. If x or y is out of bounds it returns false, otherwise it returns
     * true
     * 
     * @param x ray number
     * @param y bin number
     * @param value
     * @return true if successful, false if x or y is out of bounds
     */
    public abstract boolean setRawBytePoint(int x, int y, byte value);

    /**
     * Receiving real value from the array in point (x, y). If x or y is out of
     * bounds return value will be -9999.
     * 
     * @param x
     *            ray number
     * @param y
     *            bin number
     * @return if x or y is out of bounds returns -9999
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
     */
    public abstract boolean setPoint(int x, int y, double value);

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
