/**
 * (C) 2010 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.io.File;
import java.io.FileWriter;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * Handles options' file
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
@SuppressWarnings("deprecation")
public class XMLHandler {

    private static Log log = LogManager.getLogger();

    // XML settings
    private static final String XML_VERSION = "1.0";
    private static final String XML_ENCODING = "UTF-8";

    /**
     * 
     * This method reads options from XML file and return XML document object
     * 
     * @param msgl
     * @param verbose
     * @return XML document
     * 
     *         public static Document loadOptions(boolean verbose) {
     * 
     *         Document doc = null; try { DOMParser parser = new DOMParser();
     *         parser.parse(OPTION_XML_FILE); doc = parser.getDocument();
     *         MessageLogger.showMessage("Parsing options file: " +
     *         OPTION_XML_FILE, verbose); } catch (Exception e) {
     *         MessageLogger.showMessage("Failed to parse options file: " +
     *         e.getMessage(), true); } return doc; }
     * 
     *         /**
     * 
     *         This method reads Baltrad Feeder options from XML document
     * 
     * @param doc
     * @return
     * 
     *         public static RadarOptions[] getRadarOptions(Document doc) {
     * 
     *         NodeList radarList = doc.getElementsByTagName("radar"); int
     *         counter = radarList.getLength(); RadarOptions[] options = new
     *         RadarOptions[counter];
     * 
     *         for (int i = 0; i < counter; i++) {
     * 
     *         options[i] = new RadarOptions();
     * 
     *         options[i].setRadarName(radarList.item(i).getAttributes()
     *         .getNamedItem(NAME).getNodeValue()); options[i]
     *         .setDir(getValueByName(radarList.item(i), DIRECTORY, null));
     * 
     *         } return options; }
     */

    /**
     * Helper method
     * 
     * @param doc
     * @param argName
     * @return
     */
    public static String getElementByName(Document doc, String argName) {

        NodeList nodeList = null;
        nodeList = doc.getElementsByTagName(argName);
        return nodeList.item(0).getFirstChild().getNodeValue();
    }

    /**
     * If attribute not found, return empty string.
     * 
     * @param node
     * @param atrName
     * @return
     */
    public static String getAttributeValue(Node node, String atrName) {
        String value = "";

        if (node.hasAttributes()) {
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.item(i).getNodeName().matches(atrName)) {
                    value = attributes.item(i).getNodeValue();
                }
            }
        }

        return value;
    }

    /**
     * Method retrieves attribute's value of RAINBOW metadata header. Attribute
     * is identified by its parent Element and its siteName.
     * 
     * @param node
     *            List of top level nodes in XML document
     * @param elemName
     *            siteName of the parent Element
     * @param atrName
     *            siteName of the attribute
     * @return attribute value
     */
    public static String getValueByName(Node node, String elemName,
            String atrName) {

        String value = null;
        int type = node.getNodeType();
        if (type == Node.DOCUMENT_NODE) {
            value = getValueByName(((Document) node).getDocumentElement(),
                    elemName, atrName);
        }
        if (type == Node.ELEMENT_NODE) {

            if (atrName != null && node.getNodeName().equals(elemName)) {
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    value = getValueByName(attrs.item(i), elemName, atrName);
                    if (value != null) {
                        return value;
                    }
                }
            } else if (atrName == null && node.getNodeName().equals(elemName)
                    && node.hasChildNodes()) {
                return node.getFirstChild().getNodeValue();

            }
            if (node.hasChildNodes()) {
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    value = getValueByName(children.item(i), elemName, atrName);
                    if (value != null) {
                        return value;
                    }
                }
            }
        } else if (type == Node.ATTRIBUTE_NODE) {

            if (node.getNodeName().equals(atrName)) {
                return value = node.getNodeValue();
            }
        }

        return value;
    }

    public static Document loadXML(File file) {

        if (!file.exists()) {
            log.printMsg("XML file not found: " + file, Log.TYPE_WARNING,
                    Log.MODE_VERBOSE);
            return null;
        }

        Document doc = null;
        try {
            DOMParser parser = new DOMParser();
            parser.parse(file.getPath());
            doc = parser.getDocument();

        } catch (Exception e) {
            log.printMsg(e.getLocalizedMessage(), Log.TYPE_ERROR,
                    Log.MODE_VERBOSE);
        }
        return doc;
    }

    /**
     * Method saves XML document to file.
     * 
     * @param doc
     *            XML document
     * @param fileName
     *            Output file siteName
     */
    public static void saveXMLFile(Document doc, String fileName) {

        try {
            OutputFormat format = new OutputFormat(doc);
            format.setVersion(XML_VERSION);
            format.setEncoding(XML_ENCODING);
            format.setIndenting(true);
            XMLSerializer serializer = new XMLSerializer(format);
            FileWriter fw = new java.io.FileWriter(fileName);
            serializer.setOutputCharStream(fw);
            serializer.serialize(doc);
            fw.close();
            log.printMsg("XML: Data saved to " + fileName, Log.TYPE_NORMAL,
                    Log.MODE_VERBOSE);
        } catch (Exception e) {
            log.printMsg("Error while saving XML file: " + e.getMessage(),
                    Log.TYPE_ERROR, Log.MODE_VERBOSE);
        }

    }

}
