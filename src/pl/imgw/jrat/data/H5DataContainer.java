/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ncsa.hdf.hdf5lib.exceptions.HDF5AttributeException;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
import pl.imgw.jrat.tools.out.LogsType;
import ch.systemsx.cisd.hdf5.IHDF5Reader;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class H5DataContainer implements DataContainer {

    public static final String INT = "INTEGER";
    public static final String LONG = "LONG";
    public static final String FLOAT = "FLOAT";
    public static final String DOUBLE = "DOUBLE";
    public static final String STRING = "STRING";
    
    protected IHDF5Reader reader;
    protected Map<String, ArrayData> arrayList;

    private String name;
    private Date date;
    private String format;
    
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
    public ArrayData getArray(String name) {
        
        Iterator<String> itr = arrayList.keySet().iterator();
        while(itr.hasNext()) {
            String key = itr.next();
            if(key.contains(name))
                return arrayList.get(key);
        }
        return null;
    }

    /**
     * 
     * Receiving attribute value from product in given path and given name.
     * 
     * @param path
     *            e.g. /book/author
     * @param name
     *            use empty string if not needed
     * @return null if attribute not find
     */
    @Override
    public Object getAttributeValue(String path, String name) {

        try {
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
                    "Attribute '" + name + "' in '" + path
                            + "' has unknown format: " + type, Logging.ERROR);
        } catch (HDF5AttributeException e) {
            LogHandler.getLogs().displayMsg(
                    "Attribute '" + name + "' in '" + path
                            + "' does not exist", Logging.ERROR);
        }
        return null;
    }

    
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
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
     * @see pl.imgw.jrat.data.ProductDataContainer#getArrayList()
     */
    @Override
    public void setArrayList(Map<String, ArrayData> arrayList) {
        this.arrayList = arrayList;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.SimpleContainer#printAllAttributes()
     */
    @Override
    public void printAllAttributes() {
        // TODO Auto-generated method stub
        LogHandler.getLogs().displayMsg("Not implemented yet", Logging.SILENT);
    }

    protected void finalize() {
        reader.close();
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.DataContainer#printGeneralIfnormation()
     */
    @Override
    public void printGeneralIfnormation() {
        String msg = "This is HDF5 file format";
    }
    
}
