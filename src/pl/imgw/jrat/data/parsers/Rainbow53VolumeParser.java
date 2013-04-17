/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.NORMAL;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import pl.imgw.jrat.data.arrays.RainbowVolumeDataArray;
import pl.imgw.jrat.data.containers.DataContainer;
import pl.imgw.jrat.data.containers.RainbowBlobContainer;
import pl.imgw.jrat.data.containers.RainbowBlobHandler;
import pl.imgw.jrat.data.containers.RainbowDataContainer;
import pl.imgw.jrat.data.containers.RainbowVolume;
import pl.imgw.jrat.data.containers.VolumeContainer;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Rainbow53VolumeParser implements VolumeParser {

    private static final String BLOBID = "blobid";

    private RainbowBlobHandler rp;
    private HashMap<Integer, RainbowBlobContainer> blobs;
    private RainbowDataContainer data = null;

    private static final String VOLUME = "volume";
    private static final String SLICE = "slice";
    private static final String RAYS = "rays";
    private static final String BINS = "bins";
    private static final String DEPTH = "depth";
    private static final String RAWDATA = "rawdata";
    private static final String RAYINFO = "rayinfo";
    
    private int type = 0;

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#isValid(java.io.File)
     */
    @Override
    public boolean isValid(File file) {
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine = br.readLine();
            in.close();
            if (!strLine.contains(VOLUME)) {
                return false;
            }
            if (strLine.contains("version=\"5.3")) {
                type = RainbowDataContainer.VOLUME53;
                return true;
            }
            if (strLine.contains("version=\"5.2")) {
                type = RainbowDataContainer.VOLUME52;
                return true;
            }
            return false;
        } catch (Exception e) {// Catch exception if any
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + ": parsing error", ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
            return false;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#initialize(java.io.File)
     */
    @Override
    public boolean initialize(File file) {

        if (file == null) {
            return false;
        }
        if (!isValid(file)) {
            LogHandler.getLogs().displayMsg(
                    "'" + file.getName()
                            + "' is not a valid RAINBOW 5.x volume format",
                    WARNING);

            return false;
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);

            HashMap<Integer, Param> params = setParams(fileInputStream);
            
            if (params.isEmpty())
                return false;

            fileInputStream = new FileInputStream(file);
            
            rp = new RainbowBlobHandler();
            blobs = rp.getAllRainbowDataBlobs(fileInputStream);
            if (blobs == null || blobs.isEmpty()) {
                LogHandler.getLogs().displayMsg(
                        "[1]'" + file.getName() + "' failed to parse the file",
                        WARNING);
                return false;
            }
            
            data = new RainbowDataContainer();
            data.setAttribues(rp.getDoc());
            data.setType(type);
            
            String min, max;
            double mind, maxd;
            
            Iterator<Integer> itr = params.keySet().iterator();
            while (itr.hasNext()) {
                
                int refid = itr.next();
                Param p = params.get(refid);
                try {
                    min = data.getRainbowAttributeValue(
                            "/volume/scan/slice:refid=" + refid
                                    + "/slicedata/rawdata", "min");
                    max = data.getRainbowAttributeValue(
                            "/volume/scan/slice:refid=" + refid
                                    + "/slicedata/rawdata", "max");
                    mind = Double.parseDouble(min);
                    maxd = Double.parseDouble(max);
                    maxd = (maxd - mind) / 254;
                    mind -= 0.5;
                    
                } catch (NumberFormatException e) {
                    continue;
                }
                RainbowVolumeDataArray array = new RainbowVolumeDataArray();
                array.initialize(p.bins, p.rays);
                array.setBlobdata(blobs.get(p.blobidraw));
                array.setBlobray(blobs.get(p.blobidray));
                array.setGain(maxd);
                array.setOffset(mind);
                data.getArrayList().put(p.blobidraw + "", array);
            }
            /*
            Iterator<Integer> itr = params.keySet().iterator();
            while (itr.hasNext()) {
                Param p = params.get(itr.next());
                byte[][] infDataBuff = rp.inflateDataSection(
                        blobs.get(p.blobidraw), p.bins, p.rays, p.depthraw);
                byte[][] infRayInfo = rp.inflateDataSection(
                        blobs.get(p.blobidray), p.rays, 1, p.depthray);

                // System.out.println("depth=" + p.depthraw + ", " +
                // p.depthray);

                int shiftX = rp.firstAzimuth(blobs.get(p.blobidray)
                        .getDataBuffer(), p.rays);
                ;
                // System.out.println("shift=" + shiftX);

                // System.out.println("size=" + infRayInfo.length + ", " +
                // infRayInfo[0].length + " shift=" + shiftX);

                RawByteDataArrayWithTransposition array = new RawByteDataArrayWithTransposition(
                        infDataBuff);
                array.setGain(0.5);
                array.setOffset(-31.5);
                array.setXShift(shiftX);
                array.setTranspose(true);
                data.getArrayList().put(p.blobidraw + "", array);
            }
*/
//            if (data != null && rp != null) {
//                data.setAttribues(rp.parseXML());
//                data.setType(RainbowDataContainer.VOLUME);
//            }

        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + " was not found", ERROR);
            LogHandler.getLogs().saveErrorLogs(this, e);
            return false;
        }

        // System.out.println("ilosc blobow: " +
        // getProduct().getArrayList().size());
        LogHandler.getLogs().displayMsg(
                "File " + file.getName() + " initialized", NORMAL);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#getProduct()
     */
    @Override
    public DataContainer getProduct() {
        return data;
    }

    class Param {
        int rays;
        int bins;
        int depthraw;
        int depthray;
        int blobidraw = -1;
        int blobidray = -1;

        public boolean isValid() {
            if (rays > 0 && bins > 0 && depthraw > 0 && depthray > 0
                    && blobidraw != -1 && blobidray != -1) {
                return true;
            } else
                return false;
        }

    }

    private HashMap<Integer, Param> setParams(FileInputStream fis) {
        XMLStreamReader xml;
        HashMap<Integer, Param> params = new HashMap<Integer, Param>();
        try {
            xml = XMLInputFactory.newInstance().createXMLStreamReader(fis);

            data = null;
            Param param = null;
            int sliceid = 0;


            while (xml.hasNext()) {

                int rays = 0, bins = 0, depth = 0;
                int blobid = -1;

                int event = xml.next();

                if (event == XMLStreamConstants.END_ELEMENT) {

                    if (xml.getLocalName().matches(VOLUME)) {
                        break;
                    }
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String element = xml.getLocalName();
                    // System.out.println("1: " + element);

                    if (element.matches(SLICE)) {
                        sliceid = -1;
                        param = new Param();
                        for (int i = 0; i < xml.getAttributeCount(); i++) {
                            if (xml.getAttributeLocalName(i).matches("refid")) {

                                try {
                                    sliceid = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler
                                            .getLogs()
                                            .saveErrorLogs(
                                                    "XML parsing error: "
                                                            + this.getClass()
                                                                    .getName(),
                                                    xml.getAttributeValue(i)
                                                            + " is not a number");
                                }
                            }
                        }
                        continue;
                    }

                    if (element.matches(RAWDATA) || element.matches(RAYINFO)) {
                        // System.out.println("2: " + type);

                        for (int i = 0; i < xml.getAttributeCount(); i++) {
                            if (xml.getAttributeLocalName(i).matches(BLOBID)) {
                                try {
                                    blobid = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                    // System.out.println("blobid: " + blobid);
                                } catch (NumberFormatException e) {
                                    LogHandler
                                            .getLogs()
                                            .saveErrorLogs(
                                                    "XML parsing error: "
                                                            + this.getClass()
                                                                    .getName(),
                                                    xml.getAttributeValue(i)
                                                            + " is not a number");
                                }
                            } else if (xml.getAttributeLocalName(i).matches(
                                    RAYS)) {
                                try {
                                    rays = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                    // System.out.println("sizeX: " + sizeX);
                                } catch (NumberFormatException e) {
                                    LogHandler
                                            .getLogs()
                                            .saveErrorLogs(
                                                    "XML parsing error: "
                                                            + this.getClass()
                                                                    .getName(),
                                                    xml.getAttributeValue(i)
                                                            + " is not a number");
                                }
                            } else if (xml.getAttributeLocalName(i).matches(
                                    BINS)) {
                                try {
                                    bins = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                    // System.out.println("sizeY: " + sizeY);
                                } catch (NumberFormatException e) {
                                    LogHandler
                                            .getLogs()
                                            .saveErrorLogs(
                                                    "XML parsing error: "
                                                            + this.getClass()
                                                                    .getName(),
                                                    xml.getAttributeValue(i)
                                                            + " is not a number");
                                }
                            } else if (xml.getAttributeLocalName(i).matches(
                                    DEPTH)) {
                                try {
                                    depth = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler
                                            .getLogs()
                                            .saveErrorLogs(
                                                    "XML parsing error: "
                                                            + this.getClass()
                                                                    .getName(),
                                                    xml.getAttributeValue(i)
                                                            + " is not a number");
                                }
                            }
                        }

                        if (element.matches(RAWDATA)) {
                            param.rays = rays;
                            param.bins = bins;
                            param.depthraw = depth;
                            param.blobidraw = blobid;

                        } else if (element.matches(RAYINFO)) {
                            param.depthray = depth;
                            param.blobidray = blobid;
                        }

                        if (param.isValid() && sliceid != -1) {
                            params.put(sliceid, param);
                        }

                    }

                }
            }

            fis.close();
            xml.close();
        } catch (XMLStreamException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (FactoryConfigurationError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return params;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.parsers.VolumeParser#getVolume()
     */
    @Override
    public VolumeContainer getVolume() {
        return new RainbowVolume(data);
    }

}
