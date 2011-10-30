/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RadarProduct extends OdimH5 {

    //where
    protected String projdef;
    protected Long xsize;
    protected Long ysize;
    protected Double xscale;
    protected Double yscale;
    protected Double LL_lon;
    protected Double LL_lat;
    protected Double UL_lon;
    protected Double UL_lat;
    protected Double UR_lon;
    protected Double UR_lat;
    protected Double LR_lon;
    protected Double LR_lat;
    
    /**
     * @return the projdef
     */
    public String getProjdef() {
        return projdef;
    }
    /**
     * @param projdef the projdef to set
     */
    public void setProjdef(String projdef) {
        this.projdef = projdef;
    }
    /**
     * @return the xsize
     */
    public Long getXsize() {
        return xsize;
    }
    /**
     * @param xsize the xsize to set
     */
    public void setXsize(Long xsize) {
        this.xsize = xsize;
    }
    /**
     * @return the ysize
     */
    public Long getYsize() {
        return ysize;
    }
    /**
     * @param ysize the ysize to set
     */
    public void setYsize(Long ysize) {
        this.ysize = ysize;
    }
    /**
     * @return the xscale
     */
    public Double getXscale() {
        return xscale;
    }
    /**
     * @param xscale the xscale to set
     */
    public void setXscale(Double xscale) {
        this.xscale = xscale;
    }
    /**
     * @return the yscale
     */
    public Double getYscale() {
        return yscale;
    }
    /**
     * @param yscale the yscale to set
     */
    public void setYscale(Double yscale) {
        this.yscale = yscale;
    }
    /**
     * @return the lL_lon
     */
    public Double getLL_lon() {
        return LL_lon;
    }
    /**
     * @param lL_lon the lL_lon to set
     */
    public void setLL_lon(Double lL_lon) {
        LL_lon = lL_lon;
    }
    /**
     * @return the lL_lat
     */
    public Double getLL_lat() {
        return LL_lat;
    }
    /**
     * @param lL_lat the lL_lat to set
     */
    public void setLL_lat(Double lL_lat) {
        LL_lat = lL_lat;
    }
    /**
     * @return the uL_lon
     */
    public Double getUL_lon() {
        return UL_lon;
    }
    /**
     * @param uL_lon the uL_lon to set
     */
    public void setUL_lon(Double uL_lon) {
        UL_lon = uL_lon;
    }
    /**
     * @return the uL_lat
     */
    public Double getUL_lat() {
        return UL_lat;
    }
    /**
     * @param uL_lat the uL_lat to set
     */
    public void setUL_lat(Double uL_lat) {
        UL_lat = uL_lat;
    }
    /**
     * @return the uR_lon
     */
    public Double getUR_lon() {
        return UR_lon;
    }
    /**
     * @param uR_lon the uR_lon to set
     */
    public void setUR_lon(Double uR_lon) {
        UR_lon = uR_lon;
    }
    /**
     * @return the uR_lat
     */
    public Double getUR_lat() {
        return UR_lat;
    }
    /**
     * @param uR_lat the uR_lat to set
     */
    public void setUR_lat(Double uR_lat) {
        UR_lat = uR_lat;
    }
    /**
     * @return the lR_lon
     */
    public Double getLR_lon() {
        return LR_lon;
    }
    /**
     * @param lR_lon the lR_lon to set
     */
    public void setLR_lon(Double lR_lon) {
        LR_lon = lR_lon;
    }
    /**
     * @return the lR_lat
     */
    public Double getLR_lat() {
        return LR_lat;
    }
    /**
     * @param lR_lat the lR_lat to set
     */
    public void setLR_lat(Double lR_lat) {
        LR_lat = lR_lat;
    }
    
}
