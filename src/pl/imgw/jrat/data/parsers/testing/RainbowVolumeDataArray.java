/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers.testing;

import pl.imgw.jrat.data.ArrayData;

/**
 *
 *  /Class description/
 *  
 *  usunąć settery z interfejsu
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowVolumeDataArray extends ArrayData implements Cloneable  {

    private static final double AZIMUTH_STEP = 0;
    
    protected BlobContainer blobdata;
    protected BlobContainer blobray;
    
    protected double offset = 0;
    protected double gain = 0;
    
    private short azimuthZero = -1;
    
    private int getPosition(int x, int y) {
        
        System.out.println("size x and y " + getSizeX() + " " + getSizeY());
        
        if(azimuthZero == -1)
            setAzimuthZero();
        
        return shiftAzimuth(x) * sizeY + y;
        
    }
    
    private int shiftAzimuth(int x) {
        x += azimuthZero;
        return x % getSizeX();
    }
    
    public double getAzimuth(int ray) {
        byte[] azimuths = blobray.getDecompressed();
        int a = unsignedShortToInt(new byte[] { azimuths[ray * 2],
                azimuths[ray * 2 + 1] });
        return a * AZIMUTH_STEP;
    }
    
    /**
     * 
     */
    private void setAzimuthZero() {
        byte[] azimuths = blobray.getDecompressed();
        int azimuth = 2 * Short.MAX_VALUE;
        int a = 0;
        for (short i = 0; i < sizeX; i++) {
            a = unsignedShortToInt(new byte[] { azimuths[i * 2],
                    azimuths[i * 2 + 1] });
            if (a < azimuth) {
                azimuth = a;
                azimuthZero = i;
            }
        }
    }

    /**
     * Converts a two byte array to an integer
     * @param b a byte array of length 2
     * @return an int representing the unsigned short
     */
    private int unsignedShortToInt(byte[] b) {
        int i = 0;
        i |= b[0] & 0xFF;
        i <<= 8;
        i |= b[1] & 0xFF;
        return i;
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#initialize(int, int)
     */
    @Override
    public void initialize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        
    }

    /**
     * @param blobdata the blobdata to set
     */
    public void setBlobdata(BlobContainer blobdata) {
        this.blobdata = blobdata;
    }



    /**
     * @param blobray the blobray to set
     */
    public void setBlobray(BlobContainer blobray) {
        this.blobray = blobray;
    }



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



    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#getRawIntPoint(int, int)
     */
    @Override
    public short getRawIntPoint(int x, int y) {
        int p = unsignedByte2Int(blobdata.getDecompressed()[getPosition(x, y)]);
        return (short) p;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#setRawIntPoint(int, int, short)
     */
    @Override
    public boolean setRawIntPoint(int x, int y, short value) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#getRawBytePoint(int, int)
     */
    @Override
    public byte getRawBytePoint(int x, int y) {
        return blobdata.getDecompressed()[getPosition(x, y)];
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#setRawBytePoint(int, int, byte)
     */
    @Override
    public boolean setRawBytePoint(int x, int y, byte value) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#getPoint(int, int)
     */
    @Override
    public double getPoint(int x, int y) {
        short value = getRawIntPoint(x, y);
        if (gain == 0)
            return value;
        return raw2real(value);
    }

    private double raw2real(int x) {
        return gain * x + offset;
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#setPoint(int, int, double)
     */
    @Override
    public boolean setPoint(int x, int y, double value) {
        // TODO Auto-generated method stub
        return false;
    }

    
    
}
