/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.arrays;

import pl.imgw.jrat.data.containers.RainbowBlobContainer;

/**
 * 
 * Range bins are stored in the array’s equivalent X-dimension and azimuth gates
 * in the Y-dimension
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowVolumeDataArray extends ArrayData implements Cloneable  {

    private static final double AZIMUTH_STEP = 0;
    
    protected RainbowBlobContainer blobdata;
    protected RainbowBlobContainer blobray;
    
    protected double offset = 0;
    protected double gain = 0;
    
    private short azimuthZero = -1;
    
    private int getPosition(int x, int y) {
        
        if(azimuthZero == -1)
            setAzimuthZero();
        
        return shiftAzimuth(y) * getSizeX() + x;
        
    }
    
    private int shiftAzimuth(int y) {
        y += azimuthZero;
        return y % getSizeY();
    }
    
    public double getAzimuth(int ray) {
        ray = shiftAzimuth(ray);
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
        for (short i = 0; i < getSizeY(); i++) {
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
    public void setBlobdata(RainbowBlobContainer blobdata) {
        this.blobdata = blobdata;
    }



    /**
     * @param blobray the blobray to set
     */
    public void setBlobray(RainbowBlobContainer blobray) {
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
        if (x < 0 || y < 0 || x >= getSizeX() || y >= getSizeY()) {
            // System.out.println(index ++);
            return -1;
        }
        int p = unsignedByte2Int(blobdata.getDecompressed()[getPosition(x, y)]);
        return (short) p;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#getRawBytePoint(int, int)
     */
    @Override
    public byte getRawBytePoint(int x, int y) {
        if (x < 0 || y < 0 || x >= getSizeX() || y >= getSizeY()) {
            // System.out.println(index ++);
            return 0;
        }
        return blobdata.getDecompressed()[getPosition(x, y)];
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.ArrayData#getPoint(int, int)
     */
    @Override
    public double getPoint(int x, int y) {
        if (x < 0 || y < 0 || x >= getSizeX() || y >= getSizeY()) {
            // System.out.println(index ++);
            return -9999;
        }
        short value = getRawIntPoint(x, y);
        if (gain == 0)
            return value;
        return raw2real(value);
    }

    private double raw2real(int x) {
        return gain * x + offset;
    }

    
    
}
