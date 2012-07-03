/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.util.HashMap;

/**
 * 
 * Simple container for products that has array data and attributes
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface ProductDataContainer {

    /**
     * Receiving list of arrays available in the product.
     * @return
     */
    public HashMap<String, ArrayDataContainer> getArrayList ();
    
    /**
     * Setting list of arrays, should be done while initializing the container.
     * @param arrayList
     */
    public void setArrayList(HashMap<String, ArrayDataContainer> arrayList);
    
    /**
     * Receiving array data container with given index > 0.
     * 
     * @param index
     * @return
     */
    public ArrayDataContainer getArray(int index);

    /**
     * 
     * Receiving attribute value from product in given path and if necessary
     * also given name. When parsing XML and the value is store as an attribute
     * it is necessary to point exact attribute in the node by this name e.g.
     * {@code <book type="fiction"/>} so getAttributeValue("/book", "type") will return
     * "fiction"
     * 
     * @param path
     *            e.g. /book/author
     * @param name
     *            use empty string if not needed
     * @return null if attribute not find
     */
    public Object getAttributeValue(String path, String name);


}
