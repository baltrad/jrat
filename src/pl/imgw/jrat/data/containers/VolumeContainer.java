/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.containers;

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
     * @return null if not found
     */
    public Double getLon();

    /**
     * Get latitude of site location
     * 
     * @return null if not found
     */
    public Double getLat();

    /**
     * Get height above see level in meters of the site
     * 
     * @return null if not found
     */
    public Double getHeight();

    /**
     * Get site name
     * 
     * @return empty string if not found
     */
    public String getSiteName();

    /**
     * Get scan from this volume for given elevation
     * 
     * @param elevation
     *            as a degree e.g. 0.5
     * @return null if not found
     */
    public ScanContainer getScan(double elevation);

    public List<ScanContainer> getAllScans();
    
    /**
     * Get time of this volume according to scan scheduler with minute precision
     * 
     * @return null if not found
     */
    public Date getTime();
    
    /**
     * Get time of this volume according to scan scheduler with second precision
     * 
     * @return null if not found
     */
    public Date getTimeSec();
    
    /**
     * Wavelength in cm
     * 
     * @return null if not found
     */
    public Double getWavelength();

    /**
     * Pulsewidth in μs
     * 
     * @return null if not found
     */
    public Double getPulsewidth();

    /**
     * The radar’s half-power beamwidth (degrees)
     * 
     * @return null if not found
     */
    public Double getBeamwidth();

}
