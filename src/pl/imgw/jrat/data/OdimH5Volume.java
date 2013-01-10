/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.awt.geom.Point2D;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimH5Volume implements VolumeContainer {

    private H5DataContainer data = null;
    private boolean valid = false;

    private Map<Double, ScanContainer> scans = new HashMap<Double, ScanContainer>();
    
    public OdimH5Volume(H5DataContainer data) {
        if (((String) data.getAttributeValue("/what", "object"))
                .matches("PVOL")
                || ((String) data.getAttributeValue("/what", "object"))
                        .matches("SCAN")) {
            this.data = data;
            valid = true;
        } else {
            LogHandler.getLogs()
                    .displayMsg("This is not a valid ODIM Polar Volume",
                            LogHandler.WARNING);
        }
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
        
        String source = (String) data.getAttributeValue("/what", "source");

        String[] src1 = source.split(",");
        String tmp = "";

        for (String s : src1) {
            if (s.startsWith("PLC:")) {
                tmp = s.substring("PLC:".length());
                break;
            } else if (s.startsWith("WMO:")) {
                tmp = s.substring("WMO:".length());
            } else if (s.startsWith("RAD:")) {
                tmp = s.substring("RAD:".length());
            }
        }

        return tmp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getScan(double)
     */
    @Override
    public ScanContainer getScan(final double elevation) {
        
        if(scans.containsKey(elevation))
            return scans.get(elevation);

        if (getDatasetByElevation(elevation) == null) {
            return null;
        }

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
            public RawByteDataArray getArray() {

                RawByteDataArray array = (RawByteDataArray) data
                        .getArray(dataset + "/data1/data");
                array.setGain((Double) data.getAttributeValue(dataset
                        + "/data1/what", "gain"));
                array.setOffset((Double) data.getAttributeValue(dataset
                        + "/data1/what", "offset"));
                return array;
            }

            @Override
            public Point2D.Double getCoordinates() {
                return new Point2D.Double(getLon(), getLat());
            }
        };
        scans.put(elevation, scan);
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
        return valid;
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
     * @param elevation
     *            in degrees (e.g. 0.5, 3.4)
     * @return empty string if not find
     */
    private String getDatasetByElevation(double elevation) {
        List<String> attrs = data.reader.getAllGroupMembers("/");
        Iterator<String> i = attrs.iterator();
        while (i.hasNext()) {
            String group = i.next();
            if (group.contains("dataset")) {
                double ele = (Double) data.getAttributeValue("/" + group
                        + "/where", "elangle");
                if (ele == elevation)
                    return group;
            }
        }
        LogHandler.getLogs().displayMsg(
                "Elevation " + elevation + " not found in " + getVolId(),
                LogHandler.WARNING);

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getVolId()
     */
    @Override
    public String getVolId() {
        String id = "'HDF5 vol ";
        id += getSiteName();
        id += " ";
        id += formatMinutePrecision.format(getTime());
        id += "'";
        return id;
    }

    
    private double getElevation(String datasetName) {

        String[] parts = datasetName.split("/");
        String path = "/";
        for (String part : parts) {
            if (part.startsWith("dataset")) {
                path += part + "/where";
                return (Double) data.getAttributeValue(path, "elangle");
            }
        }
        return 0;
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.VolumeContainer#getAllScans()
     */
    @Override
    public List<ScanContainer> getAllScans() {
//        List<ScanContainer> scans = new ArrayList<ScanContainer>();
        for(String eleStr : data.getArrayList().keySet()) {
            double ele = getElevation(eleStr);
            getScan(ele);
        }
        return new ArrayList<ScanContainer>(scans.values());
    }

}
