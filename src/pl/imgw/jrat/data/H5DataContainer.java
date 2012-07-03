/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import static pl.imgw.jrat.data.ProductDataTypes.*;

import java.util.HashMap;
import java.util.Iterator;

import pl.imgw.jrat.output.LogHandler;
import pl.imgw.jrat.output.LogsType;
import ch.systemsx.cisd.hdf5.IHDF5Reader;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class H5DataContainer implements ProductDataContainer {

    private IHDF5Reader reader;
    private HashMap<String, ArrayDataContainer> arrayList;

    /**
     * @param reader
     *            the reader to set
     */
    public void setReader(IHDF5Reader reader) {
        this.reader = reader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.ProductDataContainer#getArray(int)
     */
    @Override
    public ArrayDataContainer getArray(int index) {
        String s = "dataset" + index;
        Iterator<String> itr = arrayList.keySet().iterator();
        while(itr.hasNext()) {
            String key = itr.next();
            if(key.contains(s))
                return arrayList.get(key);
        }
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

        String type = reader.getAttributeInformation(path, name).toString()
                .toUpperCase();

        if (type.contains(DOUBLE)) {
            return reader.getDoubleAttribute(path, name);
        } else if (type.contains(FLOAT)) {
            return reader.getDoubleAttribute(path, name);
        } else if (type.contains(INT)) {
            return reader.getIntAttribute(path, name);
        } else if (type.contains(LONG)) {
            return reader.getLongAttribute(path, name);
        } else if (type.contains(STRING)) {
            return reader.getStringAttribute(path, name);
        }

        LogHandler.getLogs().displayMsg(
                "Attribute " + name + " in " + path + " has unknown format: " + type,
                LogsType.ERROR);
        return null;
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

    public void setArrayList(HashMap<String, ArrayDataContainer> arrayList) {
        this.arrayList = arrayList;
    }


}
