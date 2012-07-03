/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import static pl.imgw.jrat.output.LogsType.ERROR;
import static pl.imgw.jrat.output.LogsType.INITIATION;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import pl.imgw.jrat.data.ByteDataContainer;
import pl.imgw.jrat.data.ProductDataContainer;
import pl.imgw.jrat.data.RainbowImage;
import pl.imgw.jrat.output.LogHandler;



/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowImageParser implements FileParser {

    private static final String DATAMAP = "datamap";
    private static final String FLAGMAP = "flagmap";
    private static final String RADARPICT = "radarpicture";
    private static final String BLOBID = "blobid";
    private static final String ROWS = "rows";
    private static final String COLUMNS = "columns";
    private static final String DEPTH = "depth";
    
    private RainbowBlobHandler rp;
    
    private HashMap<Integer, DataBufferContainer> blobs;
    private RainbowImage data = null;
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.parsers.FileParser#initialize(java.io.File)
     */
    @Override
    public boolean initialize(File file) {
        
        if (file == null) {
            return false;
        }
        
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            XMLStreamReader xml = XMLInputFactory.newInstance()
                    .createXMLStreamReader(fileInputStream);
            data = null;
            int rows = 0, cols = 0, depth = 0;
            int blobid = -1;
            int frows = 0, fcols = 0, fdepth = 0;
            int fblobid = -1;
            while (xml.hasNext()) {
                int event = xml.next();

                if (event == XMLStreamConstants.END_ELEMENT) {

                    if (xml.getLocalName().matches(RADARPICT)) {
                        break;
                    }
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String element = xml.getLocalName();
                    if (element.matches(DATAMAP)) {
//                        System.out.println("znalazlem dane");
                        for (int i = 0; i < xml.getAttributeCount(); i++) {
                            if (xml.getAttributeLocalName(i).matches(BLOBID)) {
                                try {
                                    blobid = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler.getLogs().saveErrorLogs(
                                            "XML parsing error: " +
                                            this.getClass().getName(),
                                            xml.getAttributeValue(i)
                                                    + " is not a number");
                                }
                            } else if (xml.getAttributeLocalName(i).matches(
                                    ROWS)) {
                                try {
                                    rows = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler.getLogs().saveErrorLogs(
                                            "XML parsing error: " +
                                            this.getClass().getName(),
                                            xml.getAttributeValue(i)
                                                    + " is not a number");
                                }
                            } else if (xml.getAttributeLocalName(i).matches(
                                    COLUMNS)) {
                                try {
                                    cols = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler.getLogs().saveErrorLogs(
                                            "XML parsing error: " +
                                            this.getClass().getName(),
                                            xml.getAttributeValue(i)
                                                    + " is not a number");
                                }
                            } else if (xml.getAttributeLocalName(i).matches(
                                    DEPTH)) {
                                try {
                                    depth = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler.getLogs().saveErrorLogs(
                                            "XML parsing error: " +
                                            this.getClass().getName(),
                                            xml.getAttributeValue(i)
                                                    + " is not a number");
                                }
                            }
                        }
                    } if (element.matches(FLAGMAP)) {
//                        System.out.println("znalazlem flagi");
                        for (int i = 0; i < xml.getAttributeCount(); i++) {
                            if (xml.getAttributeLocalName(i).matches(BLOBID)) {
                                try {
                                    fblobid = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler.getLogs().saveErrorLogs(
                                            "XML parsing error: " +
                                            this.getClass().getName(),
                                            xml.getAttributeValue(i)
                                                    + " is not a number");
                                }
                            } else if (xml.getAttributeLocalName(i).matches(
                                    ROWS)) {
                                try {
                                    frows = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler.getLogs().saveErrorLogs(
                                            "XML parsing error: " +
                                            this.getClass().getName(),
                                            xml.getAttributeValue(i)
                                                    + " is not a number");
                                }
                            } else if (xml.getAttributeLocalName(i).matches(
                                    COLUMNS)) {
                                try {
                                    fcols = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler.getLogs().saveErrorLogs(
                                            "XML parsing error: " +
                                            this.getClass().getName(),
                                            xml.getAttributeValue(i)
                                                    + " is not a number");
                                }
                            } else if (xml.getAttributeLocalName(i).matches(
                                    DEPTH)) {
                                try {
                                    fdepth = (Integer.parseInt(xml
                                            .getAttributeValue(i)));
                                } catch (NumberFormatException e) {
                                    LogHandler.getLogs().saveErrorLogs(
                                            "XML parsing error: " +
                                            this.getClass().getName(),
                                            xml.getAttributeValue(i)
                                                    + " is not a number");
                                }
                            }
                        }
                    }
                    
                }
            }
            fileInputStream.close();
            xml.close();
            
            if (rows != 0 && cols != 0 && blobid != -1) {
                /*
                 * Reading blob section
                 */
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

                /*
                 * Receiving data arrays
                 */
                byte[][] infDataBuff = rp.inflateDataSection(
                        blobs.get(blobid), cols, rows, depth);
                data = new RainbowImage();
                
                data.getArrayList().put("data", new ByteDataContainer(infDataBuff));
                if (frows != 0 && fcols != 0 && fblobid != -1) {
                    // System.out.println("ustawiam flagi, depth=" + fdepth);
                    byte[][] infFlagMapBuff = rp.inflateDataSection(
                            blobs.get(fblobid), fcols, frows, fdepth);
                    data.getArrayList().put("flags", new ByteDataContainer(infFlagMapBuff));
                }
            }
            

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
        
        LogHandler.getLogs().displayMsg(
                "File " + file.getName() + " initialized.", INITIATION);
        return true;
    }
    
    public boolean initiazlizeRainbowAttributes() {
        if (data != null && rp != null) {
            data.setAttribues(rp.parseXML());
            return true;
        }
        return false;

    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.parsers.FileParser#getProduct()
     */
    @Override
    public ProductDataContainer getProduct() {
        return data;
    }
    
    
    
}
