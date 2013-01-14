/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.containers;

import java.util.Date;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimH5CompoImage implements ImageContainer {

    private OdimDataContainer data = new OdimDataContainer();
    private boolean valid = false;

    public OdimH5CompoImage(OdimDataContainer data) {
        
        if (((String) (data.getAttributeValue("/what", "object")))
                .matches("COMP")) {
            this.data = data;
            valid = true;
        } else {
            LogHandler.getLogs().displayMsg(
                    "This is not a valid ODIM Composite Image", LogHandler.WARNING);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getTime()
     */
    @Override
    public Date getTime() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getXSize()
     */
    @Override
    public int getXSize() {
        return (Integer) data.getAttributeValue("/where", "xsize");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getYSize()
     */
    @Override
    public int getYSize() {
        return (Integer) data.getAttributeValue("/where", "ysize");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getProjDef()
     */
    @Override
    public String getProjDef() {
        return (String) data.getAttributeValue("/where", "projdef");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getXScale()
     */
    @Override
    public double getXScale() {
        return (Double) data.getAttributeValue("/where", "xscale");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getYScale()
     */
    @Override
    public double getYScale() {
        return (Double) data.getAttributeValue("/where", "yscale");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getSourceName()
     */
    @Override
    public String getSourceName() {
        return (String) data.getAttributeValue("/what", "source");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getData()
     */
    @Override
    public ArrayData getData() {
        return data.getArray("/dataset1/data1/data/");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#isValid()
     */
    @Override
    public boolean isValid() {
        return valid;
    }

}
