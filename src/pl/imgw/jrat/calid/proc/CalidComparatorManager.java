/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import java.util.Map;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.CalidSingleResultContainer;
import pl.imgw.jrat.calid.data.PolarVolumesPair;
import pl.imgw.jrat.calid.data.RadarsPair;

/**
 * 
 * Sets CALID parameters
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidComparatorManager {

    private CalidParameters cmdLineParams;
    private Map<String, CalidParameters> optionFileParameters;

    public void setParsedParameters(CalidParameters params) {
        this.cmdLineParams = params;
    }

    public void setOptionFileParameters(
            Map<String, CalidParameters> optionFileParameters) {
        this.optionFileParameters = optionFileParameters;
    }

    public CalidParameters getParsedParameters() throws CalidException {
        if (cmdLineParams == null)
            throw new CalidException("Parsed parameters are not set");
        return cmdLineParams;
    }

    public CalidParameters getOptionFilePairParameters(RadarsPair pair)
            throws CalidException {
        if (pair == null)
            throw new CalidException("Pair is not set");

        if (optionFileParameters == null && cmdLineParams == null)
            throw new CalidException("Parameters are not set");

        if (optionFileParameters != null
                && optionFileParameters.containsKey(pair.getBothSources())) {
            return optionFileParameters.get(pair.getBothSources());
        }

        if (cmdLineParams == null)
            throw new CalidException("Parameters for this pair are not set");
        return cmdLineParams;

    }

    /**
     * Based on option file parsed parameters and command line parsed parameters
     * 
     * @param pair
     * @return
     */
    public CalidParameters getPairParameters(PolarVolumesPair pair) {

        CalidParameters params = new CalidParameters(getElevation(pair), getDistance(pair),
                getMaxRange(pair), getReflectivity(pair));
        
        return params;
    }
    
    public CalidSingleResultContainer compare(PolarVolumesPair pair)
            throws CalidException {

        Double elevation = getElevation(pair);
        Double reflectivity = getReflectivity(pair);
        Integer maxRange = getMaxRange(pair);
        Integer distance = getDistance(pair);
        CalidParameters params = new CalidParameters(elevation, distance,
                maxRange, reflectivity);
        CalidSingleResultContainer results = new CalidSingleResultContainer(
                params, pair);
        results.setCoords();
        CalidComparator.putResult(results);
        return results;
    }

    
    private Double getElevation(PolarVolumesPair pair) throws CalidException{
        
        Double ele = ElevationValidator.getElevation(pair,
                getOptionFilePairParameters(pair));
        return ele != null ? ele : ElevationValidator.getElevation(pair,
                cmdLineParams);    
    }
    
    /**
     * @param pair
     * @return
     */
    private Double getReflectivity(PolarVolumesPair pair) {
        if(!getOptionFilePairParameters(pair).isReflectivityDefault())
            return getOptionFilePairParameters(pair).getReflectivity();
        return cmdLineParams.getReflectivity();
    }
    
    /**
     * @param pair
     * @return
     */
    private Integer getMaxRange(PolarVolumesPair pair) throws CalidException{
        if(!getOptionFilePairParameters(pair).isMaxRangeDefault())
            return getOptionFilePairParameters(pair).getMaxRange();
        return cmdLineParams.getMaxRange();
    }
    
    /**
     * @param pair
     * @return
     */
    private Integer getDistance(PolarVolumesPair pair) {
        if(!getOptionFilePairParameters(pair).isDistanceDefault())
            return getOptionFilePairParameters(pair).getDistance();
        return cmdLineParams.getDistance();
    }

}
