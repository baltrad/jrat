/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.containers;

import java.awt.geom.Point2D;
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
public interface ScanContainer {

    /**
     * Time of this scan
     * 
     * @return
     */
    public Date getStartTime();

    /**
     * Elevation of this scan
     * 
     * @return
     */
    public double getElevation();

    /**
     * Number of bins in this scan
     */
    public int getNBins();

    /**
     * Number of rays in this scan
     * 
     * @return
     */
    public int getNRays();

    /**
     * Resolution of one bin in meters
     * 
     * @return
     */
    public double getRScale();

    /**
     * The antenna speed in revolutions per minute, positive for clockwise scan-
     * ning, negative for counter-clockwise scanning
     * 
     * @return
     */
    public double getRPM();
    
    /**
     * Coefficient ’b’ in y=ax+b used to convert to unit. Default value is
     * 0.0.
     * 
     * @return
     */
    public double getOffset();
    
    /**
     * Coefficient ’a’ in y=ax+b used to convert to unit. Default value is
     * 1.0.
     * 
     * @return
     */
    public double getGain();
    
    /**
     * Raw value used to denote areas void of data (never radiated).
     * <p>
     * Note that this Attribute is always a float even if the data in question
     * is in another format.
     * 
     * @return
     */
    public double getNodata();
    
    /**
     * Raw value used to denote areas below the measurement detection threshold
     * (radiated but nothing detected).
     * <p>
     * Note that this Attribute is always a float even if the data in question
     * is in another format.
     * 
     * @return
     */
    public double getUndetect();
    
    
    /**
     * Data of this scan
     * 
     * @return
     */
    public ArrayData getArray();

    /**
     * Geographical coordinates of the site
     * @return
     */
    public Point2D.Double getCoordinates();
    
}
