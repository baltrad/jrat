/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.containers;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.arrays.RainbowVolumeDataArray;
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
        String lon = "";
        if (data.getType() == RainbowDataContainer.VOLUME53)
            lon = data.getRainbowAttributeValue("/volume/sensorinfo/lon", "");
        else if (data.getType() == RainbowDataContainer.VOLUME52)
            lon = data.getRainbowAttributeValue("/volume/radarinfo", "lon");
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
        String lat = "";
        if (data.getType() == RainbowDataContainer.VOLUME53)
            lat = data.getRainbowAttributeValue("/volume/sensorinfo/lat", "");
        else if (data.getType() == RainbowDataContainer.VOLUME52)
            lat = data.getRainbowAttributeValue("/volume/radarinfo", "lat");
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
    public Double getHeight() {
        String alt = "";
        if (data.getType() == RainbowDataContainer.VOLUME53)
            alt = data.getRainbowAttributeValue("/volume/sensorinfo/alt", "");
        else if (data.getType() == RainbowDataContainer.VOLUME52)
            alt = data.getRainbowAttributeValue("/volume/radarinfo", "alt");

        try {
            return Double.parseDouble(alt);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.containers.VolumeContainer#getWavelength()
     */
    @Override
    public Double getWavelength() {
        String wl = "";
        if (data.getType() == RainbowDataContainer.VOLUME53)
            wl = data.getRainbowAttributeValue("/volume/sensorinfo/wavelen", "");
        else if (data.getType() == RainbowDataContainer.VOLUME52)
            wl = data.getRainbowAttributeValue("/volume/radarinfo/wavelen", "");
        try {
            return Double.parseDouble(wl);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.containers.VolumeContainer#getPulsewidth()
     */
    @Override
    public Double getPulsewidth() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.containers.VolumeContainer#getBeamwidth()
     */
    @Override
    public Double getBeamwidth() {
        String bw = "";
        if (data.getType() == RainbowDataContainer.VOLUME53)
            bw = data.getRainbowAttributeValue("/volume/sensorinfo/beamwidth", "");
        else if (data.getType() == RainbowDataContainer.VOLUME52)
            bw = data.getRainbowAttributeValue("/volume/radarinfo/beamwidth", "");
        try {
            return Double.parseDouble(bw);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getSiteName()
     */
    @Override
    public String getSiteName() {
        if (data.getType() == RainbowDataContainer.VOLUME53)
            return data.getRainbowAttributeValue("/volume/sensorinfo", "name");

        else if (data.getType() == RainbowDataContainer.VOLUME52)
            return data.getRainbowAttributeValue("/volume/radarinfo/name", "");
        return "";
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

                return data.getArray(blobid);
            }

            @Override
            public Point2D.Double getCoordinates() {
                return new Point2D.Double(getLon(), getLat());
            }

            @Override
            public double getRPM() {
                String ant = data.getRainbowAttributeValue(
                        "/volume/scan/pargroup/antspeed", "");
                try {
                    return 60 / (360 / Double.parseDouble(ant));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }

            @Override
            public double getOffset() {

                return ((RainbowVolumeDataArray) getArray()).getOffset();
            }

            @Override
            public double getGain() {

                return ((RainbowVolumeDataArray) getArray()).getGain();
            }

            @Override
            public double getNodata() {
                return 0;
            }

            @Override
            public double getUndetect() {
                return 0;
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
