/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.data.RainbowDataContainer;
import pl.imgw.jrat.data.RawByteDataArray;
import pl.imgw.jrat.tools.out.LogHandler;




/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Rainbow53ImageParser implements FileParser{

    private static final String BLOBID = "blobid";

    private RainbowBlobHandler rp;
    private HashMap<Integer, DataBufferContainer> blobs;
    private RainbowDataContainer data = null;
    
    private static final String PRODUCT = "product";
    private static final String SIZE_X = "rows";
    private static final String SIZE_Y = "columns";
    private static final String DEPTH = "depth";
    private static final String DATAMAP = "datamap";
    private static final String FLAGMAP = "flagmap";

    /* (non-Javadoc)
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
            if (!strLine.contains(PRODUCT)) {
                return false;
            }
//            if (strLine.contains("version=\"5.3"))
//                return true;
            return true;
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
        if(!isValid(file)) {
            LogHandler.getLogs().displayMsg("'" + file.getName()
                    + "' is not a valid RAINBOW 5.3 format", WARNING);   
        
            return false;
        }
        
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            XMLStreamReader xml = XMLInputFactory.newInstance()
                    .createXMLStreamReader(fileInputStream);
            data = null;
            String type = "";

            HashSet<Param> params = new HashSet<Param>();
            
            while (xml.hasNext()) {
                
                int sizeX = 0, sizeY = 0, depth = 0;
                int blobid = -1;
                
                int event = xml.next();

                if (event == XMLStreamConstants.END_ELEMENT) {

                    if (xml.getLocalName().matches(PRODUCT)) {
                        break;
                    }
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String element = xml.getLocalName();
//                    System.out.println("1: " + element);
                    boolean found = false;
                    
                    if (element.matches(FLAGMAP)) {
                        type = FLAGMAP;
                        // System.out.println("2: " + type);
                        found = true;
                    } else if (element.matches(DATAMAP)) {
                        type = DATAMAP;
                        // System.out.println("2: " + type);
                        found = true;
                    }
                    
                    if (found) {
                        for (int i = 0; i < xml.getAttributeCount(); i++) {
                            if (xml.getAttributeLocalName(i).matches(BLOBID)) {
                                try {
                                    blobid = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
//                                    System.out.println("blobid: " + blobid);
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
                            } else if (xml.getAttributeLocalName(i).matches(SIZE_X)) {
                                try {
                                    sizeX = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
//                                    System.out.println("sizeX: " + sizeX);
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
                                    SIZE_Y)) {
                                try {
                                    sizeY = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
//                                    System.out.println("sizeY: " + sizeY);
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
                        
                        if (sizeX != 0 && blobid != -1) {
                            Param param = new Param();
                            param.depth=depth;
                            param.sizeX=sizeX;
                            param.sizeY=sizeY;
                            param.blobid=blobid;
                            param.type=type;
                            params.add(param);
                            
                        }
                        
                    }

                }
            }
            
            fileInputStream.close();
            xml.close();
            
            if(params.isEmpty())
                return false;
            
            fileInputStream = new FileInputStream(file);
            int file_len = (int) file.length();
            byte[] file_buf = new byte[file_len];
            fileInputStream.read(file_buf, 0, file_len);
            fileInputStream.close();
            rp = new RainbowBlobHandler(file_buf);
            blobs = rp.getAllRainbowDataBlobs();
            if (blobs == null || blobs.isEmpty()) {
                return false;
            }
            
            data = new RainbowDataContainer();
            Iterator<Param> itr = params.iterator();
            while(itr.hasNext()) {
                Param p = itr.next();
                byte[][] infDataBuff = rp.inflateDataSection(
                        blobs.get(p.blobid), p.sizeX, p.sizeY, p.depth);
                RawByteDataArray array = new RawByteDataArray(infDataBuff);
                array.setGain(0.5);
                array.setOffset(-31.5);
                data.getArrayList().put(p.blobid + "_" + p.type, array);
            }
            
            if (data != null && rp != null) {
                data.setAttribues(rp.parseXML());
            }
            
        } catch (FileNotFoundException e) {
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + " was not found", ERROR);
            LogHandler.getLogs().saveErrorLogs(
                    Rainbow53ImageParser.class.getSimpleName(),
                    e.getLocalizedMessage());
            return false;
        } catch (XMLStreamException e) {
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + " is not a XML format", ERROR);
            LogHandler.getLogs().saveErrorLogs(
                    Rainbow53ImageParser.class.getSimpleName(),
                    e.getLocalizedMessage());
            return false;
        } catch (FactoryConfigurationError e) {
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + " cannot be initialized", ERROR);
            LogHandler.getLogs().saveErrorLogs(
                    Rainbow53ImageParser.class.getSimpleName(),
                    e.getLocalizedMessage());
            return false;
        } catch (IOException e) {
            LogHandler.getLogs().displayMsg(
                    "File " + file.getName() + " cannot be initialized", ERROR);
            LogHandler.getLogs().saveErrorLogs(
                    Rainbow53ImageParser.class.getSimpleName(),
                    e.getLocalizedMessage());
            return false;
        }

//        System.out.println("ilosc blobow: " + getProduct().getArrayList().size());
        LogHandler.getLogs().displayMsg("File " + file.getName() + " initialized",
                WARNING);
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
    
    class Param{
        int sizeX;
        int sizeY;
        int depth;
        int blobid;
        String type;
    }
    
}
