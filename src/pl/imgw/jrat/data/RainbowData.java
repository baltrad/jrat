/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import static pl.imgw.jrat.tools.out.LogsType.*;

import java.util.HashMap;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowData implements ProductContainer {

    protected HashMap<String, ArrayData> arrayList = new HashMap<String, ArrayData>();
    protected Document attribues;

    /**
     * @param attribues
     *            the attribues to set
     */
    public void setAttribues(Document attribues) {
        this.attribues = attribues;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductDataContainer#setArrayList(java.util.List)
     */
    @Override
    public void setArrayList(HashMap<String, ArrayData> arrayList) {
        this.arrayList = arrayList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductDataContainer#getArrayList()
     */
    @Override
    public HashMap<String, ArrayData> getArrayList() {
        return arrayList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductDataContainer#getArray(int)
     */
    @Override
    public ArrayData getArray(String name) {
        return arrayList.get(name);
    }

    /**
     * 
     * Receiving attribute value from product in given path and if necessary
     * also given name. When the value is store as an attribute it is necessary
     * to point exact attribute in the node by this name e.g.
     * {@code <book type="fiction"/>} so getAttributeValue("/book", "type") will
     * return "fiction"
     * 
     * @param path
     *            e.g. /book/author
     * @param name
     *            use empty string if not needed
     * @return empty string if attribute not find
     */
    public String getRainbowAttributeValue(String path, String name) {
        try {
            return (String) getAttributeValue(path, name);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(
                    "Attribute '" + name + "' in path '" + path + "' was not find",
                    WARNING);
            return "";
        }
    }
    
    @Override
    public Object getAttributeValue(String path, String name) {

        if (attribues == null) {
            LogHandler.getLogs().displayMsg("Attributes not initialized", ERROR);
            return "";
        }

        try {
            if (path.startsWith("/"))
                path = path.substring(1);

            String[] parents = path.split("/");

            Node element = attribues.getDocumentElement();
            if (parents.length > 1) {
                for (int i = 1; i < parents.length; i++) {
                    element = findNode(element, parents[i]);
                    if (element == null)
                        return "";
                }
            }

            if (name.isEmpty()) {
                String value = element.getFirstChild().getNodeValue();
                return (value != null) ? value : "";
            }
            else {
                String value = element.getAttributes().getNamedItem(name)
                        .getNodeValue();
                return (value != null) ? value : "";
            }
        } catch (DOMException e) {
            LogHandler.getLogs().displayMsg(
                    "Parsing XML in file " + name
                            + " failed, for more details see log file", ERROR);
            LogHandler.getLogs().saveErrorLogs(this.getClass().getName(),
                    e.getLocalizedMessage());

        } catch (NullPointerException e) {
            LogHandler.getLogs().displayMsg(
                    "File " + name + " is not initialized", ERROR);
            LogHandler.getLogs().saveErrorLogs(this.getClass().getName(),
                    e.getLocalizedMessage());
        }
        return "";
    }

    protected Node findNode(Node element, String name) {
        for (Node child = element.getFirstChild(); child != null; child = child
                .getNextSibling()) {
            // System.out.println(child.getNodeName());
            if (child.getNodeName().matches(name))
                return child;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.SimpleContainer#printAllAttributes()
     */
    @Override
    public void printAllAttributes() {
        // TODO Auto-generated method stub
        
    }
    

}
