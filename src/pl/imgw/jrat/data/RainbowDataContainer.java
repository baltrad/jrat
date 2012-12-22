/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
public class RainbowDataContainer implements DataContainer {

    protected Map<String, ArrayData> arrayList = new HashMap<String, ArrayData>();
    
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
    public void setArrayList(Map<String, ArrayData> arrayList) {
        this.arrayList = arrayList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductDataContainer#getArrayList()
     */
    @Override
    public Map<String, ArrayData> getArrayList() {
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
     * to point exact attribute in the node by this name e.g.</br></br>
     * 
     * 
     * {@code <book type="fiction"/>} in this case getAttributeValue("/book",
     * "type") will return "fiction" </br></br>
     * 
     * If there are more then one node with the same name in the path, you can
     * specify the correct one by pointing its argument name and value using ':'
     * and '=' e.g. </br></br>
     * 
     * {@code <slice refid="0">} </br> {@code <posangle>0.5</posangle>}</br> and
     * the argument for the method will be:</br></br>
     * 
     * {@code getAttributeValue("/slice:refid=0/posangle", "")}
     * 
     * @param path
     *            e.g. /book/author
     * @param name
     *            use empty string if not needed
     * @return empty string if attribute not find
     */
    public String getRainbowAttributeValue(String path, String name) {
        return (String) getAttributeValue(path, name);
        
    }

    @Override
    public Object getAttributeValue(String path, String name) {

        if (attribues == null) {
            LogHandler.getLogs()
                    .displayMsg("Attributes not initialized", ERROR);
            return "";
        }

        try {
            if (path.startsWith("/"))
                path = path.substring(1);

            String[] parents = path.split("/");

            Node element = attribues.getDocumentElement();
            if (parents.length > 1) {
                for (int i = 1; i < parents.length; i++) {
                    String argname = "";
                    String argval = "";
                    if (parents[i].contains(":") && parents[i].contains("=")) {
                        argname = parents[i].split(":")[1].split("=")[0];
                        argval = parents[i].split(":")[1].split("=")[1];
                        parents[i] = parents[i].split(":")[0];
                    }
                    element = findNode(element, parents[i], argname, argval);
                    if (element == null)
                        return "";
                }
            }

            if (name.isEmpty()) {
                String value = element.getFirstChild().getNodeValue();
                return (value != null) ? value : "";
            } else {
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
                    "Attribute '" + name + "' in path '" + path
                            + "' was not find", WARNING);
        }
        return "";
    }

    protected Node findNode(Node element, String name, String argname,
            String argvalue) {
        for (Node child = element.getFirstChild(); child != null; child = child
                .getNextSibling()) {
            // System.out.println(child.getNodeName());
            if (child.getNodeName().matches(name)) {
                if (!argname.isEmpty() && !argvalue.isEmpty()) {

                    NamedNodeMap atrs = child.getAttributes();
                    for (int i = 0; i < atrs.getLength(); i++) {
                        if (atrs.item(i).getNodeName().matches(argname)
                                && atrs.item(i).getNodeValue()
                                        .matches(argvalue)) {
                            return child;
                        }

                    }

                } else
                    return child;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.SimpleContainer#printAllAttributes()
     */
    @Override
    public void printAllAttributes() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.DataContainer#printGeneralIfnormation()
     */
    @Override
    public void printGeneralIfnormation() {
        // TODO Auto-generated method stub
        
    }

}
