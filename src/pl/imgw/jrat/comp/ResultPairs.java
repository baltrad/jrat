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
    
    HashMap<Date, List<RayBinData>> data;
    
    public ResultPairs() {
        data = new HashMap<Date, List<RayBinData>>();
    }
    
    /**
     * @param date
     * @param raybin
     * @return
     */
    public boolean add(Date date, List<RayBinData> raybin) {
        if (data.containsKey(date)) {
            return false;
        }
        data.put(date, raybin);
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
