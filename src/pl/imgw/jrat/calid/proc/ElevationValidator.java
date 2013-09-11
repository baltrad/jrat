/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.PolarVolumesPair;
import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.ScanContainer;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ElevationValidator {

    private static final String PAIR_IS_NULL_EXC = "CALID Cannot validate elevation: pair is null";
    private static final String PARAMS_ARE_NULL_EXC = "CALID Cannot validate elevation: parameters are null";
    
    public static boolean isValid(PolarVolumesPair pair, double elevation) throws CalidException {

        if(pair == null)
            throw new CalidException(PAIR_IS_NULL_EXC);
        
        if (pair.getVol1().getScan(elevation) == null)
            return false;
        if (pair.getVol2().getScan(elevation) == null)
            return false;

        return true;

    }
    
    /**
     * 
     * @return elevation set as --calid parameter.
     *         <p>
     *         If elevation was not set as parameter, but volumes data was
     *         loaded then the lowest elevation available in both volumes is
     *         return
     *         <p>
     *         <b>null</b> if elevation was not set as parameter and no volume data was
     *         loaded,
     *         <p>
     *         also <b>null</b> if elevation was set as parameter but volumes do not
     *         contain this elevation,
     *         <p>
     *         also <b>null</b> if either elevation was no set as parameter or no
     *         volumes data was loaded.
     */ 
    public static Double getElevation(PolarVolumesPair pair,
            CalidParameters params) throws CalidException{

        if(pair == null)
            throw new CalidException(PAIR_IS_NULL_EXC);
        
        if(params == null)
            throw new CalidException(PARAMS_ARE_NULL_EXC);
        
        if (!params.isElevationDefault() && params.getElevation() != null) {

            if (pair.getVol1().getScan(params.getElevation()) == null)
                return null;
            if (pair.getVol2().getScan(params.getElevation()) == null)
                return null;

            return params.getElevation();
        }
        if (params.isElevationDefault()) {
            double ele1 = getLowestElevation(pair.getVol1());
            double ele2 = getLowestElevation(pair.getVol2());
            if (ele1 == ele2)
                return ele1;
            else
                throw new CalidException(
                        "CALID: Cannot find coresponding elevations for: "
                                + pair);
        }
        return null;

    }

    private static double getLowestElevation(PolarData vol) {
        double ele = 90;
        for (ScanContainer scan : vol.getAllScans()) {
            if (scan.getElevation() < ele)
                ele = scan.getElevation();
        }
        return ele;
    }
    
}
