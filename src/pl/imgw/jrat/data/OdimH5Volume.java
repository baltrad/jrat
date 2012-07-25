/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.util.Date;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimH5Volume implements VolumeContainer {

    H5Data data;
    
    public OdimH5Volume(H5Data data) {
        this.data = data;
    }
    
    @SuppressWarnings("unused")
    private OdimH5Volume() {
        //hiding constructor
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.VolumeContainer#getLon()
     */
    @Override
    public Double getLon() {
        
        return (Double) data.getAttributeValue("/where", "lon");
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.VolumeContainer#getLat()
     */
    @Override
    public Double getLat() {
        
        return (Double) data.getAttributeValue("/where", "lat");
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.VolumeContainer#getHeight()
     */
    @Override
    public int getHeight() {
        return (Integer) data.getAttributeValue("/where", "height");
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.VolumeContainer#getSiteName()
     */
    @Override
    public String getSiteName() {
        
        return (String) data.getAttributeValue("/what", "source");
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.VolumeContainer#getScan(double)
     */
    @Override
    public ScanContainer getScan(double elevation) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.VolumeContainer#getTime()
     */
    @Override
    public Date getTime() {
        // TODO Auto-generated method stub
        return null;
    }

}
