/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import static pl.imgw.jrat.output.LogsType.ERROR;

import java.util.HashMap;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import pl.imgw.jrat.output.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowImage implements RainbowImageContainer {

    public static final int DATA = 1;
    public static final int FLAGS = 2;
    private static final String DATA_S = "datamap";
    private static final String FLAG_S = "flagmap";
    private HashMap<String, ArrayDataContainer> arrayList = new HashMap<String, ArrayDataContainer>();

    private Document attribues;

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
    public void setArrayList(HashMap<String, ArrayDataContainer> arrayList) {
        this.arrayList = arrayList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductDataContainer#getArrayList()
     */
    @Override
    public HashMap<String, ArrayDataContainer> getArrayList() {
        return arrayList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductDataContainer#getArray(int)
     */
    @Override
    public ArrayDataContainer getArray(int index) {
        if (index == DATA)
            return arrayList.get(DATA_S);
        if (index == FLAGS)
            return arrayList.get(FLAG_S);
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pl.imgw.jrat.data.ProductDataContainer#getAttributeValue(java.lang.String
     * , java.lang.String)
     */
    @Override
    public Object getAttributeValue(String path, String name) {

        if (attribues == null) {
            System.out.println("Attributes not initialized");
            return null;
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
                        return null;
                }
            }

            if (name.isEmpty())
                return element.getFirstChild().getNodeValue();
            else
                return element.getAttributes().getNamedItem(name)
                        .getNodeValue();
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
        return null;
    }

    private Node findNode(Node element, String name) {
        for (Node child = element.getFirstChild(); child != null; child = child
                .getNextSibling()) {
            // System.out.println(child.getNodeName());
            if (child.getNodeName().matches(name))
                return child;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.RainbowImageContainer#getImageData()
     */
    @Override
    public ArrayDataContainer getImageData() {
        return arrayList.get(DATA_S);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.RainbowImageContainer#getFlagsData()
     */
    @Override
    public ArrayDataContainer getFlagsData() {
        return arrayList.get(FLAG_S);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.RainbowImageContainer#getProjection()
     */
    @Override
    public String getProjection() {
        // TODO Auto-generated method stub
        return null;
    }

}
