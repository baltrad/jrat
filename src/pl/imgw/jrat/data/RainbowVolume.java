/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.awt.geom.Point2D;
import java.util.Date;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowVolume implements VolumeContainer {

    RainbowData data = null;

    public RainbowVolume(RainbowData data) {
        if (((String) data.getAttributeValue("/volume", "type"))
                .matches("vol"))
            this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#isValid()
     */
    @Override
    public boolean isValid() {
        if(data != null)
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
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getLat()
     */
    @Override
    public Double getLat() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.VolumeContainer#getHeight()
     */
    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0;
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
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public int getNRays() {
                String rays = data.getRainbowAttributeValue(path + "/rawdata", "rays");
                try {
                    return Integer.parseInt(rays);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            
            @Override
            public int getNBins() {
                String bins = data.getRainbowAttributeValue(path + "/rawdata", "bins");
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
                
                if(blobid.isEmpty())
                    return null;
                
                RawByteDataArray array = (RawByteDataArray) data
                        .getArray(blobid + "_rawdata");
                
                String min = data.getRainbowAttributeValue(path + "/rawdata",
                        "min");
                String max = data.getRainbowAttributeValue(path + "/rawdata",
                        "max");
                try {
                    double mind = Double.parseDouble(min);
                    double maxd = Double.parseDouble(max);
                    array.setOffset(mind - 0.5);
                    array.setGain((maxd - mind)/254);
                } catch (NumberFormatException e) {

                }

                return array;
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

    /* (non-Javadoc)
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

        return "";
    }
    
}
