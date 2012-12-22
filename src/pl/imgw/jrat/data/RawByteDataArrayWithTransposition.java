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
public class RawByteDataArrayWithTransposition extends RawByteDataArray {

    protected int xShift = 0;
    protected boolean transpose = false;
    
    /**
     * @param infDataBuff
     */
    public RawByteDataArrayWithTransposition(byte[][] data) {
        if (data != null) {
            this.sizeX = data.length;
            this.sizeY = data[0].length;
            this.data = data;
        }
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.RawByteDataArray#getPoint(int, int)
     */
    @Override
    public double getPoint(int x, int y) {
        if(transpose) {
            x = shiftX(x);
            return super.getPoint(y, x);
        } else {
            return super.getPoint(x, y);
        }
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.RawByteDataArray#getRawIntPoint(int, int)
     */
    @Override
    public short getRawIntPoint(int x, int y) {
        if (transpose) {
            x = shiftX(x);
            return super.getRawIntPoint(y, x);
        } else
            return super.getRawIntPoint(x, y);
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.RawByteDataArray#getRawBytePoint(int, int)
     */
    @Override
    public byte getRawBytePoint(int x, int y) {
        if (transpose) {
            x = shiftX(x);
            return super.getRawBytePoint(y, x);
        }
        return super.getRawBytePoint(x, y);
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.RawByteDataArray#setPoint(int, int, double)
     */
    @Override
    public boolean setPoint(int x, int y, double value) {
        if (transpose) {
            return super.setPoint(y, x, value);
        } else
        return super.setPoint(x, y, value);
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.RawByteDataArray#setRawBytePoint(int, int, byte)
     */
    @Override
    public boolean setRawBytePoint(int x, int y, byte value) {
        if (transpose) {
            return super.setRawBytePoint(y, x, value);
        } else
            return super.setRawBytePoint(x, y, value);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.RawByteDataArray#setRawIntPoint(int, int, short)
     */
    @Override
    public boolean setRawIntPoint(int x, int y, short value) {
        if (transpose) {
            return super.setRawIntPoint(y, x, value);
        } else
            return super.setRawIntPoint(x, y, value);
    }
    
    /**
     * @return the transpose
     */
    public boolean isTranspose() {
        return transpose;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ArrayData#getSizeX()
     */
    @Override
    public int getSizeX() {
        if (transpose)
            return super.getSizeY();
        return super.getSizeX();
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#getSizeY()
     */
    @Override
    public int getSizeY() {
        if (transpose)
            return super.getSizeX();
        return super.getSizeY();
    }
    
    /**
     * @param transpose the transpose to set
     */
    public void setTranspose(boolean transpose) {
        this.transpose = transpose;
    }
    
    /**
     * @param xShift
     */
    public void setXShift(int xShift) {
        this.xShift = xShift;
    }
    
    
    private int shiftX(int x) {
        x += xShift;
        return x % getSizeX();
    }
    
    
}
