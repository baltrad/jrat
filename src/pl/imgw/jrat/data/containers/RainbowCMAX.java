/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.containers;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.util.Date;
import java.util.Iterator;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.arrays.RawByteDataArray;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowCMAX implements ImageContainer {

    private RainbowDataContainer data;

    public RainbowCMAX(RainbowDataContainer data) {
        if (data.getRainbowAttributeValue("/product", "name").matches("CMAX")) {
            this.data = data;
            ((RawByteDataArray) getData()).setGain(0.5);
            ((RawByteDataArray) getData()).setOffset(-31.5);
        }
    }

    @SuppressWarnings("unused")
    private RainbowCMAX() {
        // hidding default constructor
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
        String value = data.getRainbowAttributeValue(
                "/product/data/radarpicture/datamap", "columns");
        int i = -1;
        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LogHandler.getLogs().displayMsg(
                    "Projection parameter is not available for this image",
                    ERROR);
            LogHandler.getLogs().saveErrorLogs(this.getClass().getName(),
                    e.getLocalizedMessage());
        }
        return i;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getYSize()
     */
    @Override
    public int getYSize() {
        String value = data.getRainbowAttributeValue(
                "/product/data/radarpicture/datamap", "rows");
        int i = -1;
        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LogHandler.getLogs().displayMsg(
                    "Projection parameter is not available for this image",
                    ERROR);
            LogHandler.getLogs().saveErrorLogs(this.getClass().getName(),
                    e.getLocalizedMessage());
        }
        return i;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getProjDef()
     */
    @Override
    public String getProjDef() {
        String proj = "";
        String par = data.getRainbowAttributeValue(
                "/product/data/radarpicture/projection", "type");
        if (!par.isEmpty())
            proj += ("+proj=" + par);

        par = data.getRainbowAttributeValue(
                "/product/data/radarpicture/projection/lon_0", "");
        if (!par.isEmpty())
            proj += (" +lon_0=" + par);

        par = data.getRainbowAttributeValue(
                "/product/data/radarpicture/projection/lat_0", "");
        if (!par.isEmpty())
            proj += (" +lat_0=" + par);

        par = data.getRainbowAttributeValue(
                "/product/data/radarpicture/projection/ellps", "");
        if (!par.isEmpty())
            proj += (" " + par);

        par = data.getRainbowAttributeValue(
                "/product/data/radarpicture/projection/lat_0", "");
        if (!par.isEmpty())
            proj += (" +lat_0=" + par);

        proj += " +a=6371000";

        return proj;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getXScale()
     */
    @Override
    public double getXScale() {
        String value = data.getRainbowAttributeValue(
                "/product/data/viewparams/disphorres", "");
        value = value.replace("@", "");
        double i = -1.0;
        try {
            i = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LogHandler.getLogs().displayMsg(
                    "Projection parameter is not available for this image",
                    ERROR);
            LogHandler.getLogs().saveErrorLogs(this.getClass().getName(),
                    e.getLocalizedMessage());
        }
        return i * 1000;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getYScale()
     */
    @Override
    public double getYScale() {
        String value = data.getRainbowAttributeValue(
                "/product/data/viewparams/disphorres", "");
        value = value.replace("@", "");
        double i = -1.0;
        try {
            i = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LogHandler.getLogs().displayMsg(
                    "Projection parameter is not available for this image",
                    ERROR);
            LogHandler.getLogs().saveErrorLogs(this.getClass().getName(),
                    e.getLocalizedMessage());
        }
        return i * 1000;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getSourceName()
     */
    @Override
    public String getSourceName() {
        String value = data.getRainbowAttributeValue(
                "/product/data/sensorinfo", "name");
        return (value != null) ? value : "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getData()
     */
    @Override
    public ArrayData getData() {
        Iterator<String> itr = data.arrayList.keySet().iterator();
        while(itr.hasNext()) {
            String name = itr.next();
            if(name.contains("datamap")) {
                return data.arrayList.get(name);
            }
        }
        return null;
    }

    public ArrayData getFlag() {
        Iterator<String> itr = data.arrayList.keySet().iterator();
        while(itr.hasNext()) {
            String name = itr.next();
            if(name.contains("flagmap")) {
                return data.arrayList.get(name);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#isValid()
     */
    @Override
    public boolean isValid() {
        if (data != null)
            return true;
        return false;
    }

}