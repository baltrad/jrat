/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.containers;

import java.util.Date;

import pl.imgw.jrat.data.arrays.ArrayData;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface ImageContainer {

    /**
     * Check if the container was initialize with correct product
     * 
     * @return true if the product is valid image
     */
    public boolean isValid();
    
    public Date getTime();

    /**
     * horizontal image size
     * 
     * @return -1 if not find
     */
    public int getXSize();

    /**
     * vertical image size
     * 
     * @return -1 if not find
     */
    public int getYSize();

    /**
     * projection definition according to proj4
     * 
     * @return empty string if not find
     */
    public String getProjDef();

    /**
     * horizontal resolution, horizontal size of single pixel in meters
     * 
     * @return -1 if not find
     */
    public double getXScale();

    /**
     * vertical resolution, vertical size of single pixel in meters
     * 
     * @return -1 if not find
     */
    public double getYScale();

    /**
     * name of the source
     * 
     * @return empty String if not find
     */
    public String getSourceName();

    /**
     * Get time of this image
     * 
     * @return null if not find
     */
    public ArrayData getData();

}
