/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.SliderUI;

import pl.imgw.jrat.parsers.RainbowVolumeDataArray;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowVolume implements VolumeContainer {

    protected RainbowDataContainer data = null;
    protected Map<Double, ScanContainer> scans = new HashMap<Double, ScanContainer>();

    public RainbowVolume(RainbowDataContainer data) {
        if (((String) data.getAttributeValue("/volume", "type")).matches("vol"))
            this.data = data;

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
     * @see pl.imgw.jrat.data.VolumeContainer#getLon()
     */
    @Override
    public Double getLon() {
        String lon = data
                .getRainbowAttributeValue("/volume/sensorinfo/lon", "");

        try {
            return Double.parseDouble(lon);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getLat()
     */
    @Override
    public Double getLat() {
        String lat = data
                .getRainbowAttributeValue("/volume/sensorinfo/lat", "");

        try {
            return Double.parseDouble(lat);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getHeight()
     */
    @Override
    public int getHeight() {
        String alt = data
                .getRainbowAttributeValue("/volume/sensorinfo/alt", "");

        try {
            return Integer.parseInt(alt);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getSiteName()
     */
    @Override
    public String getSiteName() {
        return data.getRainbowAttributeValue("/volume/sensorinfo", "name");
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
        
        if (getSlicedataPath(elevation) == null) {
            return null;
        }

        ScanContainer scan = new ScanContainer() {
            String path = getSlicedataPath(elevation);

            @Override
            public Date getStartTime() {
                try {
                    String date = data.getRainbowAttributeValue(path, "date")
                            .replaceAll("-", "");
                    String time = data.getRainbowAttributeValue(path, "time")
                            .replaceAll(":", "");
                    return formatSecondPrecision.parse(date
                            + time.substring(0, time.length()));
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public double getRScale() {
                String rscale = data.getRainbowAttributeValue(
                        "/volume/scan/pargroup/rangestep", "");
                try {
                    double range = Double.parseDouble(rscale);
                    return range * 1000;
                } catch (NumberFormatException e) {
                    return 0;
                }
            }

            @Override
            public int getNRays() {
                String rays = data.getRainbowAttributeValue(path + "/rawdata",
                        "rays");
                try {
                    return Integer.parseInt(rays);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }

            @Override
            public int getNBins() {
                String bins = data.getRainbowAttributeValue(path + "/rawdata",
                        "bins");
                try {
                    return Integer.parseInt(bins);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }

            @Override
            public double getElevation() {
                return elevation;
            }

            @Override
            public ArrayData getArray() {
                String blobid = data.getRainbowAttributeValue(
                        path + "/rawdata", "blobid");

                if (blobid.isEmpty())
                    return null;

                RainbowVolumeDataArray array = (RainbowVolumeDataArray) data
                        .getArray(blobid);

                String min = data.getRainbowAttributeValue(path + "/rawdata",
                        "min");
                String max = data.getRainbowAttributeValue(path + "/rawdata",
                        "max");
                try {
                    double mind = Double.parseDouble(min);
                    double maxd = Double.parseDouble(max);
                    array.setOffset(mind - 0.5);
                    array.setGain((maxd - mind) / 254);
                } catch (NumberFormatException e) {

                }

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
        try {
            String date = data.getRainbowAttributeValue("/volume/scan", "date")
                    .replaceAll("-", "");
            String time = data.getRainbowAttributeValue("/volume/scan", "time")
                    .replaceAll(":", "");
            return formatMinutePrecision.parse(date
                    + time.substring(0, time.length() - 2));
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getTimeSec()
     */
    @Override
    public Date getTimeSec() {
        try {
            String date = data.getRainbowAttributeValue("/volume/scan", "date")
                    .replaceAll("-", "");
            String time = data.getRainbowAttributeValue("/volume/scan", "time")
                    .replaceAll(":", "");
            return formatSecondPrecision.parse(date
                    + time.substring(0, time.length()));
        } catch (Exception e) {
            return null;
        }
    }

    private String getSlicedataPath(double elevation) {
        int size = data.getArrayList().size();
        int refid = -1;
        for (int i = 0; i < size; i++) {
            String posangle = data.getRainbowAttributeValue(
                    "/volume/scan/slice:refid=" + i + "/posangle", "");
            if (posangle.matches(Double.toString(elevation))) {
                refid = i;
                break;
            }
        }

        if (refid != -1) {
            return "/volume/scan/slice:refid=" + refid + "/slicedata";
        }

        LogHandler.getLogs().displayMsg(
                "Elevation " + elevation + " not found in " + getVolId(),
                LogHandler.WARNING);

        return null;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.VolumeContainer#getVolId()
     */
    @Override
    public String getVolId() {
        String id = "'Rb5 vol ";
        id += getSiteName();
        id += " ";
        id += formatMinutePrecision.format(getTime());
        id += "'";
        return id;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.VolumeContainer#getAllScans()
     */
    @Override
    public List<ScanContainer> getAllScans() {

        int size = data.getArrayList().size();

        if (scans.size() < size) {

            for (int i = 0; i < size; i++) {
                String posangle = data.getRainbowAttributeValue(
                        "/volume/scan/slice:refid=" + i + "/posangle", "");
                double ele = Double.parseDouble(posangle);
                getScan(ele);
            }
        }
        return new ArrayList<ScanContainer>(scans.values());
    }

}
