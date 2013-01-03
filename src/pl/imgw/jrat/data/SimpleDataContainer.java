/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class SimpleDataContainer implements DataContainer {

    protected Map<String, ArrayData> arrayList = new HashMap<String, ArrayData>();
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.DataContainer#getArrayList()
     */
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


    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.DataContainer#getAttributeValue(java.lang.String, java.lang.String)
     */
    @Override
    public Object getAttributeValue(String path, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.DataContainer#getArray(java.lang.String)
     */
    @Override
    public ArrayData getArray(String name) {
        // TODO Auto-generated method stub
        return arrayList.get(name);
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.DataContainer#printAllAttributes()
     */
    @Override
    public void printAllAttributes() {
        // TODO Auto-generated method stub
        System.out.println("Not implemented yet");

    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.DataContainer#printGeneralIfnormation()
     */
    @Override
    public void printGeneralIfnormation() {
        // TODO Auto-generated method stub
        System.out.println("Not implemented yet");
        
    }

}
