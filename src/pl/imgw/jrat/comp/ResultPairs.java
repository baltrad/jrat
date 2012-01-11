/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.comp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pl.imgw.jrat.data.hdf5.OdimH5;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ResultPairs {
    
    HashMap<String, List<RayBinData>> data;
    
    /**
     * @param date
     * @param raybin
     * @return
     */
    public boolean add(Date date, List<RayBinData> raybin) {
        String fulldate = OdimH5.getFullDateFormat().format(date);
        if (data.containsKey(fulldate)) {
            return false;
        }
        data.put(fulldate, raybin);
        return true;
    }
    
    /**
     * @return the mean
     */
    public Double getMean() {
        return null;
    }
    /**
     * @return the corelation
     */
    public Double getCorelation() {
        return null;
    }
    /**
     * @return the rootMeanSquare
     */
    public Double getRootMeanSquare() {
        return null;
    }
    /**
     * @return the meanSquaredError
     */
    public Double getMeanSquaredError() {
        return null;
    }
    
    
    
}
