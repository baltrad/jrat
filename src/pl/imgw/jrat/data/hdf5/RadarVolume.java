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
public class RadarVolume extends OdimH5 {

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

    public OdimH5Dataset getDataset(double elevation) {

        for (int i = 0; i < getDatasetSize(); i++) {
            if (getDataset()[i].getElangle() == elevation)
                return getDataset()[i];
        }

        return null;
    }

    /**
     * Longitude position of the radar antenna (degrees). Fraction of a degree
     * are given in decimal notation.
     * 
     * @return the lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     * 
     * Longitude position of the radar antenna (degrees). Fraction of a degree
     * are given in decimal notation.
     * 
     * @param lon
     *            the lon to set
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * Latitude position of the radar antenna (degrees). Fraction of a degree
     * are given in decimal notation.
     * 
     * @return the lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Latitude position of the radar antenna (degrees). Fraction of a degree
     * are given in decimal notation.
     * 
     * @param lat
     *            the lat to set
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * Height of the centre of the antenna in meters above sea level
     * 
     * @return the height
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Height of the centre of the antenna in meters above sea level
     * 
     * @param height
     *            the height to set
     */
    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * Radar System abbreviations
     * 
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * Radar System abbreviation
     * 
     * @param system
     *            the system to set
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * Processing Software abbreviation
     * 
     * @return the software
     */
    public String getSoftware() {
        return software;
    }

    /**
     * Processing Software abbreviation
     * 
     * @param software
     *            the software to set
     */
    public void setSoftware(String software) {
        this.software = software;
    }

    /**
     * Software version in string format, e.g. "5.1"
     * 
     * @return the sw_version
     */
    public String getSw_version() {
        return sw_version;
    }

    /**
     * Software version in string format, e.g. "5.1"
     * 
     * @param sw_version
     *            the sw_version to set
     */
    public void setSw_version(String sw_version) {
        this.sw_version = sw_version;
    }

    /**
     * The radar's half-power beamwidth (degrees)
     * 
     * @return the beamwidth
     */
    public Double getBeamwidth() {
        return beamwidth;
    }

    /**
     * The radar's half-power beamwidth (degrees)
     * 
     * @param beamwidth
     *            the beamwidth to set
     */
    public void setBeamwidth(Double beamwidth) {
        this.beamwidth = beamwidth;
    }

    /**
     * Wavelenght in cm
     * 
     * @return the wavelength
     */
    public Double getWavelength() {
        return wavelength;
    }

    /**
     * Wavelenght in cm
     * 
     * @param wavelength
     *            the wavelength to set
     */
    public void setWavelength(Double wavelength) {
        this.wavelength = wavelength;
    }

    /**
     * Seconds after a standard 1970 epoch for which the starting time of the
     * data/product is valid.
     * 
     * @return the startepoch
     */
    public Integer getStartepoch() {
        return startepoch;
    }

    /**
     * Seconds after a standard 1970 epoch for which the starting time of the
     * data/product is valid.
     * 
     * @param startepoch
     *            the startepoch to set
     */
    public void setStartepoch(Integer startepoch) {
        this.startepoch = startepoch;
    }

    /**
     * Seconds after a standard 1970 epoch for which the ending time of the
     * data/product is valid.
     * 
     * @return the endepoch
     */
    public Integer getEndepoch() {
        return endepoch;
    }

    /**
     * Seconds after a standard 1970 epoch for which the ending time of the
     * data/product is valid.
     * 
     * @param endepoch
     *            the endepoch to set
     */
    public void setEndepoch(Integer endepoch) {
        this.endepoch = endepoch;
    }

}
