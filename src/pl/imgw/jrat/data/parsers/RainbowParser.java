/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import static pl.imgw.jrat.output.LogsType.ERROR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowParser implements FileParser {

    private static final String BLOBID = "blobid";
    private RainbowFieldsNameForParser fields;

    private RainbowBlobHandler rp;
    private HashMap<Integer, DataBufferContainer> blobs;
    private RainbowImage data = null;

    public RainbowParser(RainbowFieldsNameForParser fields) {
        this.fields = fields;
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

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            XMLStreamReader xml = XMLInputFactory.newInstance()
                    .createXMLStreamReader(fileInputStream);
            data = null;
            int sizeX = 0, sizeY = 0, depth = 0;
            int blobid = -1;
            String type = "";

            HashSet<Param> params = new HashSet<Param>();
            
            while (xml.hasNext()) {
                int event = xml.next();

                if (event == XMLStreamConstants.END_ELEMENT) {

                    if (xml.getLocalName().matches(fields.getProduct())) {
                        break;
                    }
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String element = xml.getLocalName();
//                    System.out.println("1: " + element);
                    boolean found = false;
                    for (int i = 0; i < fields.getTags().length; i++) {
                        if (element.matches(fields.getTags()[i])) {
                            type = fields.getTags()[i];
//                            System.out.println("2: " + type);
                            found = true;
                        }
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
                            } else if (xml.getAttributeLocalName(i).matches(
                                    fields.getX())) {
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
                                    fields.getY())) {
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
                                    fields.getD())) {
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
                        
                        if (sizeX != 0 && sizeY != 0 && blobid != -1) {
//                            System.out.println("3: ustawiam parametry");
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
            
            data = new RainbowImage();
            Iterator<Param> itr = params.iterator();
            while(itr.hasNext()) {
                Param p = itr.next();
                byte[][] infDataBuff = rp.inflateDataSection(
                        blobs.get(p.blobid), p.sizeX, p.sizeY, p.depth);
                data.getArrayList().put(p.type, new ByteDataContainer(infDataBuff));
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

        // TODO Auto-generated method stub
        return false;
    }

    public boolean initiazlizeRainbowAttributes() {
        if (data != null && rp != null) {
            data.setAttribues(rp.parseXML());
            return true;
        }
        return false;

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#getProduct()
     */
    @Override
    public ProductDataContainer getProduct() {
        // TODO Auto-generated method stub
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
