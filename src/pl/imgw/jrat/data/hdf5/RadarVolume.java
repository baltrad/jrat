/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import pl.imgw.jrat.util.MessageLogger;


/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RadarVolume extends OdimH5{

    // where
    protected Double lon;
    protected Double lat;
    protected Double height;

    // how
    protected Double beamwidth;
    protected Integer startepoch;
    protected Integer endepoch;
    protected String software;
    protected String sw_version;
    protected String system;
    protected Double wavelength;
    
    /**
     * 
     * @param verbose
     */
    public void displayGeneralObjectInfo(boolean verbose) {
        String message = "Volume file loaded:\n";
        message += "Data source:\t" + getSource() + "\n";
        message += "System:\t\t" + getSystem() + "\n";
        message += "Software:\t" + getSoftware() + " " + getSw_version() + "\n";
        message += "Scan time:\t" + getFullDate() + "\n";
        MessageLogger.showMessage(message, verbose);
    }
    
    /**
     * @return the lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     * @param lon
     *            the lon to set
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * @return the lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     * @param lat
     *            the lat to set
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * @return the height
     */
    public Double getHeight() {
        return height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(Double height) {
        this.height = height;
    }
    
    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system
     *            the system to set
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the software
     */
    public String getSoftware() {
        return software;
    }

    /**
     * @param software
     *            the software to set
     */
    public void setSoftware(String software) {
        this.software = software;
    }

    /**
     * @return the sw_version
     */
    public String getSw_version() {
        return sw_version;
    }

    /**
     * @param sw_version
     *            the sw_version to set
     */
    public void setSw_version(String sw_version) {
        this.sw_version = sw_version;
    }

    /**
     * @return the beamwidth
     */
    public Double getBeamwidth() {
        return beamwidth;
    }

    /**
     * @param beamwidth
     *            the beamwidth to set
     */
    public void setBeamwidth(Double beamwidth) {
        this.beamwidth = beamwidth;
    }

    /**
     * @return the wavelength
     */
    public Double getWavelength() {
        return wavelength;
    }

    /**
     * @param wavelength
     *            the wavelength to set
     */
    public void setWavelength(Double wavelength) {
        this.wavelength = wavelength;
    }

    /**
     * @return the startepoch
     */
    public Integer getStartepoch() {
        return startepoch;
    }

    /**
     * @param startepoch the startepoch to set
     */
    public void setStartepoch(Integer startepoch) {
        this.startepoch = startepoch;
    }

    /**
     * @return the endepoch
     */
    public Integer getEndepoch() {
        return endepoch;
    }

    /**
     * @param endepoch the endepoch to set
     */
    public void setEndepoch(Integer endepoch) {
        this.endepoch = endepoch;
    }
    
    
    
}
