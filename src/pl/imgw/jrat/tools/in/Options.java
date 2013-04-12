/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

import java.io.File;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 *
 *  Astract class for options handler.
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public abstract class Options {

    /**
     * Print sample option file
     */
    public abstract void printHelp();
    
    /**
     * Pointing to option file (xml format)
     * @return
     */
    protected abstract File getOptionFile();

    /**
     * This method reads options from XML file and return XML document object.
     * 
     * @return null if option file does not exist or is not well formatted
     */
    protected Document loadOptions() {
       
        if (getOptionFile() == null || !getOptionFile().exists())
            return null;

        Document doc = null;
        try {
            DOMParser parser = new DOMParser();
            parser.parse(getOptionFile().getPath());
            doc = parser.getDocument();
            LogHandler.getLogs().displayMsg(
                    "Parsing options file: " + getOptionFile(),
                    LogHandler.NORMAL);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(
                    "Parsing options file: " + getOptionFile() + " FAILED",
                    LogHandler.ERROR);
            return null;
        }
        return doc;
    }
    
    /**
     * Helper method
     * 
     * @param doc
     * @param argName
     * @return
     */
    protected String getElementByName(Document doc, String argName) {

        NodeList nodeList = null;
        nodeList = doc.getElementsByTagName(argName);
        return nodeList.item(0).getFirstChild().getNodeValue();
        
    }
    
    /**
     * Method retrieves attribute's value. Attribute is identified by its parent
     * Element and its name.
     * 
     * @param node
     *            root element
     * @param elemName
     *            name of the parent Element
     * @param atrName
     *            name of the attribute
     * @return attribute value
     */
    protected String getValueByName(Node node, String elemName,
            String atrName) {

        if(node == null)
            return null;
        
        String value = null;
        int type = node.getNodeType();
        if (type == Node.DOCUMENT_NODE) {
            value = getValueByName(((Document) node).getDocumentElement(),
                    elemName, atrName);
        }
        if (type == Node.ELEMENT_NODE) {

            if (elemName != null && atrName != null && node.getNodeName().equals(elemName)) {
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    value = getValueByName(attrs.item(i), elemName, atrName);
                    if (value != null) {
                        return value;
                    }
                }
            } else if (elemName != null && atrName == null && node.getNodeName().equals(elemName)
                    && node.hasChildNodes()) {
                return node.getFirstChild().getNodeValue();

            } else if(elemName == null && atrName != null) {
                if(!node.hasAttributes())
                    return null;
                
                return getValueByName(node.getAttributes().getNamedItem(atrName), null, atrName);
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
    
    

}
