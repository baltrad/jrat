/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.awt.geom.Point2D;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimH5Volume implements VolumeContainer {

    H5Data data = null;

    public OdimH5Volume(H5Data data) {
        if (((String) data.getAttributeValue("/what", "object"))
                .matches("PVOL"))
            this.data = data;
    }

    @SuppressWarnings("unused")
    private OdimH5Volume() {
        // hiding constructor
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getLon()
     */
    @Override
    public Double getLon() {

        return (Double) data.getAttributeValue("/where", "lon");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getLat()
     */
    @Override
    public Double getLat() {

        return (Double) data.getAttributeValue("/where", "lat");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getHeight()
     */
    @Override
    public int getHeight() {
        return (Integer) data.getAttributeValue("/where", "height");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getSiteName()
     */
    @Override
    public String getSiteName() {

        return (String) data.getAttributeValue("/what", "source");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getScan(double)
     */
    @Override
    public ScanContainer getScan(final double elevation) {
        ScanContainer scan = new ScanContainer() {

            String dataset = "/" + getDatasetByElevation(elevation);

            @Override
            public Date getStartTime() {
                try {
                    String time = (String) data.getAttributeValue(dataset
                            + "/what", "starttime");
                    String date = (String) data.getAttributeValue(dataset
                            + "/what", "startdate");
                    return formatSecondPrecision.parse(date + time);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public double getRScale() {
                return (Double) data.getAttributeValue(dataset + "/where",
                        "rscale");
            }

            @Override
            public int getNRays() {
                return (Integer) data.getAttributeValue(dataset + "/where",
                        "nrays");
            }

            @Override
            public int getNBins() {
                return (Integer) data.getAttributeValue(dataset + "/where",
                        "nbins");
            }

            @Override
            public double getElevation() {
                return elevation;
            }

            @Override
            public ArrayData getArray() {
                return data.getArray(dataset + "/data1/data");
            }

            @Override
            public Point2D.Double getCoordinates() {
                return new Point2D.Double(getLon(), getLat());
            }
        };
        return scan;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getTime()
     */
    @Override
    public Date getTime() {
        String time = (String) data.getAttributeValue("/what", "time");
        String date = (String) data.getAttributeValue("/what", "date");
        try {
            return formatMinutePrecision.parse(date
                    + time.substring(0, time.length() - 2));
        } catch (ParseException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#isValid()
     */
    @Override
    public boolean isValid() {
        if (data != null)
            return true;
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getTimeSec()
     */
    @Override
    public Date getTimeSec() {
        String time = (String) data.getAttributeValue("/what", "time");
        String date = (String) data.getAttributeValue("/what", "date");
        try {
            return formatSecondPrecision.parse(date + time);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 
     * Receive name of the group which contains data from given elevation
     * 
     * @param elevation in degrees (e.g. 0.5, 3.4)
     * @return empty string if not find
     */
    private String getDatasetByElevation(double elevation) {
        List<String> attrs = data.reader.getAllGroupMembers("/");
        Iterator<String> i = attrs.iterator();
        while(i.hasNext()) {
            String group = i.next();
            if(group.contains("dataset")) {
                double ele = (Double) data.getAttributeValue("/" + group + "/where", "elangle");
                if(ele == elevation)
                    return group;
            }
        }
        return "";
    }
    
}
