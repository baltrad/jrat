/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class WZData implements DataContainer {
    
    private double nodata = -9999;
    private double belowth = -9999;

    protected Map<String, ArrayData> arrayList;

    /**
     * Key set contains all names of parameters and layer height
     * 
     * @return if container is empty, empty set is returned
     */
    public Set<String> getKeySet() {
        if (arrayList != null)
            return arrayList.keySet();
        return new HashSet<String>();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductContainer#getArrayList()
     */
    @Override
    public Map<String, ArrayData> getArrayList() {
        return arrayList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductContainer#setArrayList(java.util.HashMap)
     */
    @Override
    public void setArrayList(Map<String, ArrayData> arrayList) {
        this.arrayList = arrayList;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pl.imgw.jrat.data.ProductContainer#getAttributeValue(java.lang.String,
     * java.lang.String)
     */
    @Override
    public Object getAttributeValue(String path, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductContainer#getArray(java.lang.String)
     */
    @Override
    public ArrayData getArray(String name) {
        return arrayList.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductContainer#printAllAttributes()
     */
    @Override
    public void printAllAttributes() {
        // TODO Auto-generated method stub

    }

    /**
     * @return the belowth
     */
    public double getBelowth() {
        return belowth;
    }

    /**
     * @param belowth the belowth to set
     */
    public void setBelowth(double belowth) {
        this.belowth = belowth;
    }

    /**
     * @return the nodata
     */
    public double getNodata() {
        return nodata;
    }

    /**
     * @param nodata the nodata to set
     */
    public void setNodata(double nodata) {
        this.nodata = nodata;
    }

}
