/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import static pl.imgw.jrat.tools.out.LogsType.ERROR;

import java.util.Date;

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

    private RainbowData data;

    public RainbowCMAX(RainbowData data) {
        ((RawByteDataContainer)data.arrayList.get("datamap")).setGain(0.5);
        ((RawByteDataContainer)data.arrayList.get("datamap")).setOffset(-31.5);
        this.data = data;
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
        String value = data.getRainbowAttributeValue("/product/data/sensorinfo",
                "name");
        return (value != null) ? value : "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ImageContainer#getData()
     */
    @Override
    public ArrayData getData() {
        return data.arrayList.get("datamap");
    }

    public ArrayData getFlag() {
        // TODO Auto-generated method stub
        return data.arrayList.get("flagmap");
    }

}
