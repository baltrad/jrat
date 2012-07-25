/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.util.Date;

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
    public Date getTime();

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
    public int getRScale();

    /**
     * Data of this scan
     * 
     * @return
     */
    public ArrayData getArray();

}
