/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface VolumeContainer {

    public SimpleDateFormat formatSecondPrecision = new SimpleDateFormat("yyyyMMddHHmmss");
    public SimpleDateFormat formatMinutePrecision = new SimpleDateFormat("yyyyMMddHHmm");
    
    /**
     * 
     * @return identification of the volume, should contain site name and date
     */
    public String getVolId();
    
    /**
     * Check if the container was initialize with correct product
     * 
     * @return true if the product is valid volume
     */
    public boolean isValid();

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

    public List<ScanContainer> getAllScans();
    
    /**
     * Get time of this volume according to scan scheduler with minute precision
     * 
     * @return null if not find
     */
    public Date getTime();
    
    /**
     * Get time of this volume according to scan scheduler with second precision
     * 
     * @return null if not find
     */
    public Date getTimeSec();

}
