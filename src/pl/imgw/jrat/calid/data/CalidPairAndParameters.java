/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import pl.imgw.jrat.calid.CalidException;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidPairAndParameters {

    public CalidPairAndParameters() {
        
    }
    
    private RadarsPair pair;
    private CalidParameters params;
    
    public CalidPairAndParameters(CalidParameters params, RadarsPair pair) {
        
        this.pair = pair;
        this.params = params;
    }
    
    
    public RadarsPair getPair() {
        return pair;
    }
    
    public PolarVolumesPair getVolumesPair() {
        if (pair instanceof PolarVolumesPair)
            return (PolarVolumesPair) pair;
        else
            throw new CalidException("");
    }
    
    public void setSources(String source1, String source2) {
        pair = new RadarsPair(source1, source2);
    }


    /**
     * @return the params
     */
    public CalidParameters getParameters() {
        return params;
    }


    /**
     * @return
     */
    public boolean hasPolarData() {
        return pair instanceof PolarVolumesPair;
    }
    
    
}
