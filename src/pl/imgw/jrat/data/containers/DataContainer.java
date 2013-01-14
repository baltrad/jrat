/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.containers;

import java.util.Map;
import java.util.TreeMap;

import pl.imgw.jrat.data.arrays.ArrayData;

/**
 * 
 * Simple container for products that has array data list and set of attributes.
 * It also has ability to print all attributes as an original tree
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface DataContainer {

    /**
     * Receiving list of arrays available in the product, where key reflects the
     * name of dataset in original file with full path if available and value is
     * an object of <code>ArrayData</code>
     * 
     * @return
     */
    public Map<String, ArrayData> getArrayList();

    /**
     * Setting list of arrays, should be done while initializing the container.
     * Key must reflect the name of dataset in original file with full path if
     * available
     * 
     * @param arrayList
     */
    public void setArrayList(Map<String, ArrayData> arrayList);

    public Object getAttributeValue(String path, String name);

    /**
     * Receiving an array of data, 
     * 
     * @param name
     *            path or name of an array in original file
     * @return null if array matching given name not find
     */
    public ArrayData getArray(String name);

    /**
     * Print all attributes to console as a tree
     */
    public void printAllAttributes();
    
    /**
     * Print general information about the product, like: date, source etc. 
     */
    public void printGeneralIfnormation();

}
