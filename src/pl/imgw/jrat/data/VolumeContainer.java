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
public interface VolumeContainer {

    /**
     * Get longitude of site location
     * 
     * @return null if not find
     */
    public Double getLon();

    /**
     * Get latitude of site location
     * 
     * @return null if not find
     */
    public Double getLat();

    /**
     * Get height above see level in meters of the site
     * 
     * @return -1 if not find
     */
    public int getHeight();

    /**
     * Get site name
     * 
     * @return empty string if not find
     */
    public String getSiteName();

    /**
     * Get scan from this volume for given elevation
     * 
     * @param elevation
     *            as a degree e.g. 0.5
     * @return null if not find
     */
    public ScanContainer getScan(double elevation);

    /**
     * Get time of this volume according to scan scheduler
     * 
     * @return null if not find
     */
    public Date getTime();

}
