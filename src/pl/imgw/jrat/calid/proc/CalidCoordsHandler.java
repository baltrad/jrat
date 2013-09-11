/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import java.util.List;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidCoordsLoader;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.PairedPoint;
import pl.imgw.jrat.calid.data.PolarVolumesPair;
import pl.imgw.jrat.calid.data.RadarsPair;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidCoordsHandler {

    private static Log log = LogManager.getLogger();
    
    /**
     * 
     * Receives coordinates of overlapping points from file, or if corresponding
     * file does not exist calculates them. Prints exception messages.
     * 
     * @param params
     * @param pair
     * @return <b>null</b> if cannot get
     */
    public static List<PairedPoint> getCoords(CalidParameters params,
            RadarsPair pair) throws CalidException {

        List<PairedPoint> list = null;

        list = loadCoords(params, pair);

        if (list != null)
            return list;

        if (!(pair instanceof PolarVolumesPair)) {
            throw new CalidException(
                    "Cannot receive coordinates without polar data for " + pair);
        }

        list = CalidCoordsCalc.calculateCoords(params, (PolarVolumesPair) pair);

        return list;
    }

    /**
     * Receives coordinates from file. Prints exception messages
     * 
     * @param params
     * @param pair 
     * @return <b>null</b> if cannot load
     */
    protected static List<PairedPoint> loadCoords(CalidParameters params, RadarsPair pair) {

        try {
            return CalidCoordsLoader.loadCoords(params, pair);
        } catch (CalidException e) {
            log.printMsg(e.getMessage(), Log.TYPE_WARNING, Log.MODE_VERBOSE);
        }
        return null;

    }

}
