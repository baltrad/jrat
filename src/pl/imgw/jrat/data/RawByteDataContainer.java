/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;



/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RawByteDataContainer extends ArrayData implements Cloneable {

    private byte[][] data = null;
    private double offset = 0;
    private double gain = 0;
    
    /**
     * @param offset the offset to set
     */
    public void setOffset(double offset) {
        this.offset = offset;
    }

    /**
     * @param gain the gain to set
     */
    public void setGain(double gain) {
        this.gain = gain;
    }

    public RawByteDataContainer() {
        
    }
    
    /**
     * @param infDataBuff
     */
    public RawByteDataContainer(byte[][] data) {
        if (data != null) {
            this.sizeX = data.length;
            this.sizeY = data[0].length;
            this.data = data;
        }
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayDataContainer#initialize(int, int)
     */
    @Override
    public void initialize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.data = new byte[sizeX][sizeY];
    }


    public short[][] getData() {
        short[][] idata = new short[sizeX][sizeY];
        
        for(int i = 0; i < sizeX; i++)
            for(int j = 0; j < sizeY; j++)
                idata[i][j] = (short) unsignedByte2Int(data[i][j]);
        
        return idata;
    }

    public byte[][] getByteData() {
        return data;
    }

    public void setByteData(byte[][] data) {
        this.sizeX = data.length;
        this.sizeY = data[0].length;
        this.data = data;

    }

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
    
    /* (non-Javadoc)
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

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayDataContainer#setPoint(int, int, short)
     */
    @Override
    public boolean setRawIntPoint(int x, int y, short value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return false;
        }

        data[x][y] = int2byte(value);

        return true;
    }

    /* (non-Javadoc)
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

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayDataContainer#setBytePoint(int, int, byte)
     */
    @Override
    public boolean setRawBytePoint(int x, int y, byte value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            
            return false;
        }
        data[x][y] = value;
        return true;
    }

    public void multiArrayCopy(byte[][] source, byte[][] destination) {
        for (int a = 0; a < source.length; a++) {
            System.arraycopy(source[a], 0, destination[a], 0, source[a].length);
        }
    }
    
    public Object clone() {
        
        byte[][] array = new byte[sizeX][sizeY];
        multiArrayCopy(data, array);
        
        ArrayData dc = new RawByteDataContainer(array);

        return dc;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayDataContainer#getDoublePoint(int, int)
     */
    @Override
    public double getPoint(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            // System.out.println(index ++);
            return -9999;
        }
        short value = (short) unsignedByte2Int(data[x][y]);
        if(gain == 0)
            return value;
        return raw2dBZ(value);
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayDataContainer#setDoublePoint(int, int, double)
     */
    @Override
    public boolean setPoint(int x, int y, double value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return false;
        }
        if (gain == 0)
            data[x][y] = int2byte((short) value);
        else
            data[x][y] = int2byte(dBZ2raw(value));
        return true;
    }

    public short dBZ2raw(double x) {
        if (gain == 0)
            return 0;
        return (short) ((1 / gain * (x - offset)) + 1);
    }
    
    public double raw2dBZ(int x) {
        return gain * x + offset;
    }
    
}
