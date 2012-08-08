/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import static pl.imgw.jrat.tools.out.LogsType.ERROR;
import static pl.imgw.jrat.tools.out.LogsType.WARNING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.data.RawByteDataArray;
import pl.imgw.jrat.data.WZData;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class WZFileParser implements FileParser {

    private final String XML_WZ_TAG = "wz";
    private final String XML_DATAMAP_TAG = "datamap";
    private final String XML_LAYER_TAG = "layer";
    private final String XML_PARAMETER_TAG = "par";
    private final String XML_INFO_TAG = "info";

    private HashMap<Integer, DataBufferContainer> blobs;
    private WZData data = null;
    
    private final int CPX = 0;
    private final int SPE = 1;
    private int type = -1;

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
            if (!strLine.contains("xml")) {
                in.close();
                return false;
            }
            strLine = br.readLine();
            in.close();
            if (strLine.contains("wz") && strLine.contains("complex")) {
                type = CPX;
                return true;
            } else if (strLine.contains("wz") && strLine.contains("simple")) {
                type = SPE;
                return true;
            }
            return false;
        } catch (Exception e) {// Catch exception if any
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
            LogHandler.getLogs()
                    .displayMsg(
                            "'" + file.getName()
                                    + "' is not a valid WZ complex format",
                            WARNING);
            return false;
        }

        int cols = 0, rows = 0, nodata = 0, belowth = 0;
        HashMap<String, List<Param>> layers = new HashMap<String, List<Param>>();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            XMLStreamReader xml = XMLInputFactory.newInstance()
                    .createXMLStreamReader(fileInputStream);
            String attr = "";

            while (xml.hasNext()) {
                int event = xml.next();
                if (event == XMLStreamConstants.END_ELEMENT) {
                    if (xml.getLocalName().matches(XML_WZ_TAG)) {
                        break;
                    }
                }
                if (event != XMLStreamConstants.START_ELEMENT) {
                    continue;
                }
                // System.out.println(xml.getLocalName() + ", type: "
                // + xml.getEventType());

                if (xml.getLocalName().matches(XML_DATAMAP_TAG)) {
                    for (int i = 0; i < xml.getAttributeCount(); i++) {

                        try {
                            if (xml.getAttributeLocalName(i).matches("rows")) {
                                rows = Integer.parseInt(xml
                                        .getAttributeValue(i));
                            } else if (xml.getAttributeLocalName(i).matches(
                                    "cols")) {
                                cols = Integer.parseInt(xml
                                        .getAttributeValue(i));
                            } else if (xml.getAttributeLocalName(i).matches(
                                    "nodata")) {
                                nodata = Integer.parseInt(xml
                                        .getAttributeValue(i));
                            } else if (xml.getAttributeLocalName(i).matches(
                                    "belowthreshold")) {
                                belowth = Integer.parseInt(xml
                                        .getAttributeValue(i));
                            }

                        } catch (NumberFormatException e) {
                            break;
                        }

                    }
                }

                if (type == CPX && xml.getLocalName().matches(XML_LAYER_TAG)) {
                    double layermin = 0;
                    double layermax = 0;
                    for (int i = 0; i < xml.getAttributeCount(); i++) {
                        try {
                            if (xml.getAttributeLocalName(i).matches(
                                    "minheight")) {
                                layermin = Double.parseDouble(xml
                                        .getAttributeValue(i));
                            } else if (xml.getAttributeLocalName(i).matches(
                                    "maxheight")) {
                                layermax = Double.parseDouble(xml
                                        .getAttributeValue(i));
                            }

                        } catch (NumberFormatException e) {
                            break;
                        }

                    }
                    List<Param> layer = getLayersParam(xml);
                    layers.put("FL" + convertMetersToFeet(layermin) + "-FL"
                            + convertMetersToFeet(layermax), layer);
                }

                if (type == SPE) {
                    layers.put("WZ", null);
                }
                
                // continuation
            }

            fileInputStream.close();
            xml.close();

            if (layers.isEmpty())
                return false;

            fileInputStream = new FileInputStream(file);
            int file_len = (int) file.length();
            byte[] file_buf = new byte[file_len];
            fileInputStream.read(file_buf, 0, file_len);
            fileInputStream.close();
            RainbowBlobHandler rp = new RainbowBlobHandler(file_buf);
            blobs = rp.getAllRainbowDataBlobs();
            if (blobs == null || blobs.isEmpty()) {
                return false;
            }

            Map<String, ArrayData> arrays = new TreeMap<String, ArrayData>();;
            // System.out.println("Number of blobs: " + blobs.size());
            if (type == CPX) {
                Iterator<String> il = layers.keySet().iterator();
                while (il.hasNext()) {
                    String name = il.next();
                    List<Param> layer = layers.get(name);
                    Iterator<Param> ip = layer.iterator();
                    // System.out.println("layer: " + name);
                    while (ip.hasNext()) {
                        Param par = ip.next();
                        // System.out.println("blobid=" + par.blobid);
                        byte[][] infDataBuff = rp.inflateDataSection(
                                blobs.get(par.blobid), cols, rows, 8);

                        if (infDataBuff == null)
                            continue;

                        RawByteDataArray rbdc = new RawByteDataArray();
                        rbdc.setByteData(infDataBuff);
                        rbdc.setGain(par.factor);
                        rbdc.setOffset(par.offset);
                        arrays.put(name + ":" + par.name, rbdc);
                        // System.out.println(name + ":" + par.name +
                        // ", factor: " +
                        // par.factor + ", offset: " + par.offset);
                        // saveTXTResults(rbdc, name + ":" + par.name, new
                        // File("/home/lwojtas/Desktop"));
                    }
                }
            } else if (type == SPE) {
                byte[][] infDataBuff = rp.inflateDataSection(
                        blobs.get(0), cols, rows, 8);
                RawByteDataArray rbdc = new RawByteDataArray();
                rbdc.setByteData(infDataBuff);
                arrays.put("WZ", rbdc);
            }
            data = new WZData();
            data.setBelowth(belowth);
            data.setNodata(nodata);
            data.setArrayList(arrays);

        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + " was not found", ERROR);
            LogHandler.getLogs().saveErrorLogs(
                    RainbowImageParser.class.getSimpleName(),
                    e.getLocalizedMessage());
            return false;
        } catch (XMLStreamException e) {
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + " is not a XML format", ERROR);
            LogHandler.getLogs().saveErrorLogs(
                    RainbowImageParser.class.getSimpleName(),
                    e.getLocalizedMessage());
            return false;
        } catch (FactoryConfigurationError e) {
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + " cannot be initialized", ERROR);
            LogHandler.getLogs().saveErrorLogs(
                    RainbowImageParser.class.getSimpleName(),
                    e.getLocalizedMessage());
            return false;
        } catch (IOException e) {
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + " cannot be initialized", ERROR);
            LogHandler.getLogs().saveErrorLogs(
                    RainbowImageParser.class.getSimpleName(),
                    e.getLocalizedMessage());
            return false;
        }

        return true;

    }

    private void saveTXTResults(RawByteDataArray data, String fileName,
            File dest) {
        int xMax = data.getSizeX();
        int yMax = data.getSizeY();

        try {
            // Create file
            FileWriter fstream = new FileWriter(new File(dest, fileName));
            BufferedWriter out = new BufferedWriter(fstream);
            // Close the output stream

            for (int y = 0; y < yMax; y++) {
                for (int x = 0; x < xMax; x++) {
                    out.write(data.getPoint(x, y) + " ");
                }
                out.write("\n");
            }
            out.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    /*
     * function converts Meters to Feet.
     */
    private int convertMetersToFeet(double meters) {
        int toFeet = (int) (Math.round(meters * 3.2808 / 100));
        return toFeet;
    }

    /**
     * @param xml
     * @return
     * @throws XMLStreamException
     */
    private List<Param> getLayersParam(XMLStreamReader xml)
            throws XMLStreamException {

        List<Param> params = new ArrayList<Param>();
        while (xml.hasNext()) {
            int event = xml.next();
            if (event == XMLStreamConstants.END_ELEMENT) {
                if (xml.getLocalName().matches(XML_LAYER_TAG)) {
                    break;
                }
            }

            if (event != XMLStreamConstants.START_ELEMENT) {
                continue;
            }
            // System.out.println(xml.getLocalName() + "; type: "
            // + xml.getEventType());

            if (xml.getLocalName().matches(XML_PARAMETER_TAG)) {

                Param par = new Param();
                for (int i = 0; i < xml.getAttributeCount(); i++) {
                    try {
                        if (xml.getAttributeLocalName(i).matches("blobid")) {
                            par.blobid = Integer.parseInt(xml
                                    .getAttributeValue(i));
                        } else if (xml.getAttributeLocalName(i).matches("name")) {
                            par.name = xml.getAttributeValue(i);
                        } else if (xml.getAttributeLocalName(i).matches(
                                "threshold")) {
                            par.threshold = Double.parseDouble(xml
                                    .getAttributeValue(i));
                        }
                    } catch (NumberFormatException e) {
                        break;
                    }
                }

                while (xml.hasNext()) {
                    event = xml.next();
                    if (event == XMLStreamConstants.END_ELEMENT) {
                        if (xml.getLocalName().matches(XML_PARAMETER_TAG)) {
                            break;
                        }
                    }
                    if (!xml.isStartElement())
                        continue;
                    // System.out.println(xml.getLocalName() + "; type: "
                    // + xml.getEventType());

                    if (xml.getLocalName().matches(XML_INFO_TAG)) {
                        for (int i = 0; i < xml.getAttributeCount(); i++) {
                            try {
                                if (xml.getAttributeLocalName(i).matches(
                                        "factor")) {
                                    par.factor = Double.parseDouble(xml
                                            .getAttributeValue(i));
                                } else if (xml.getAttributeLocalName(i)
                                        .matches("offset")) {
                                    par.offset = Double.parseDouble(xml
                                            .getAttributeValue(i));
                                }
                            } catch (NumberFormatException e) {

                            }
                        }
                    }
                }
                params.add(par);
            }

        }

        return params;
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
        int blobid;
        String name;
        double factor;
        double offset;
        double threshold;
    }

}
