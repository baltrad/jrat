/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.RawByteDataArray;
import pl.imgw.jrat.data.FloatDataArray;
import pl.imgw.jrat.data.H5Data;
import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.tools.out.LogHandler;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Reader;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimH5Parser implements FileParser {

    public final static String FLOAT_SYMBOL = "FLOAT";
    public final static String INT_SYMBOL = "INTEGER";
    
    private H5Data h5data;
    IHDF5Reader reader;
    private static final String ROOT = "/";
    private static final String DATA = "data";
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.parsers.FileParser#isValid(java.io.File)
     */
    @Override
    public boolean isValid(File file) {
        return HDF5Factory.isHDF5File(file);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#initialize(java.io.File)
     */
    @Override
    public boolean initialize(File file) {
       
        if(!isValid(file)) {
            LogHandler.getLogs().displayMsg("'" + file.getName()
                    + "' is not a valid HDF5 file", WARNING);
            return false;
        }
        
        try {
            reader = HDF5Factory.openForReading(file);
            h5data = new H5Data();
            h5data.setReader(reader);
            LogHandler.getLogs().displayMsg("File " + file.getName() + " initialized",
                    WARNING);
            readDatasets();
        } catch (Exception e) {
//            e.printStackTrace();
            LogHandler.getLogs().displayMsg("Initialization of ODIM file " + file.getName()
                    + " failed", ERROR);
            LogHandler.getLogs().saveErrorLogs(this.getClass().getName(),
                    "Initialization of file " + file.getName() + " failed");
            return false;
        }
        return true;
    }

    private void readDatasets() {

        HashMap<String, ArrayData> arrayList = new HashMap<String, ArrayData>();
        Set<String> paths = new LinkedHashSet<String>();
        paths.add(ROOT);
        paths = getDataPathSet(paths);
        Iterator<String> i = paths.iterator();
        int index = 0;
        while (i.hasNext()) {
            String path = i.next();
            String type = reader.getDataSetInformation(path).toString();
            if(type.contains(FLOAT_SYMBOL)) {
//                System.out.println(path);
                FloatDataArray adc = new FloatDataArray(reader.readFloatMatrix(path));
                adc.transpose();
                arrayList.put(path, adc);
                index++;
            } else if(type.contains(INT_SYMBOL)) {
                RawByteDataArray adc = new RawByteDataArray();
                adc.setIntData(reader.readIntMatrix(path));
                arrayList.put(path, adc);
                index++;
            }
            
        }
        h5data.setArrayList(arrayList);
        
    }

    private Set<String> getDataPathSet(Set<String> paths) {
        Iterator<String> itr = paths.iterator();
        while (itr.hasNext()) {
            String path = itr.next();
            if (!reader.exists(path))
                paths.remove(path);
            if (reader.isDataSet(path))
                continue;
            paths.remove(path);
            List<String> list = reader.getAllGroupMembers(path);
            Iterator<String> i = list.iterator();
            while (i.hasNext()) {
                String name = i.next();
                if (name.contains(DATA)) {
                    paths.add(path + name + "/");
                    getDataPathSet(paths);
                }
            }
        }
        return paths;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#getProduct()
     */
    @Override
    public DataContainer getProduct() {
        return h5data;
    }

}
