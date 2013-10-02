/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import java.util.Iterator;
import java.util.List;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidDataSaver;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.CalidResultLoader;
import pl.imgw.jrat.calid.data.CalidSingleResultContainer;
import pl.imgw.jrat.calid.data.PairedPoint;
import pl.imgw.jrat.calid.data.PolarVolumesPair;
import pl.imgw.jrat.data.OutOfBoundsException;
import pl.imgw.jrat.data.ScanContainer;
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
public class CalidComparator {

	private static Log log = LogManager.getLogger();

	private static void calculateResult(CalidSingleResultContainer results) {

		results.resetDifferences();
		List<PairedPoint> points = results.getPairedPointsList();
		PolarVolumesPair pair = results.getPolarVolumePair();
		CalidParameters params = results.getParams();

		ScanContainer scan1;
		try {
			scan1 = pair.getVol1().getScan(params.getElevation());
		} catch (NullPointerException e) {
			throw new CalidException(pair.getVol1().getSiteName()
					+ " does not have " + params.getElevation() + " elevation");
		}

		ScanContainer scan2;
		try {
			scan2 = pair.getVol2().getScan(params.getElevation());
		} catch (NullPointerException e) {
			throw new CalidException(pair.getVol2().getSiteName()
					+ " does not have " + params.getElevation() + " elevation");
		}

		log.printMsg("CALID: Comparing data for " + pair, Log.TYPE_NORMAL,
				Log.MODE_VERBOSE);

		// CalidSingleResultContainer container = new
		// CalidSingleResultContainer();

		Iterator<PairedPoint> itr = points.iterator();
		while (itr.hasNext()) {
			PairedPoint coords = itr.next();
			double val1 = 0, val2 = 0;
			try {
				val1 = scan1.getArray().getPoint(coords.getBin1(),
						coords.getRay1());
				val2 = scan2.getArray().getPoint(coords.getBin2(),
						coords.getRay2());
			} catch (OutOfBoundsException e) {
				continue;
			}
			if (val1 == scan1.getOffset() || val2 == scan2.getOffset()) {
				if (val1 >= params.getReflectivity()) {
					results.r2understated();
				} else if (val2 >= params.getReflectivity()) {
					results.r1understated();
				}
			} else if (val1 >= params.getReflectivity()
					&& val2 >= params.getReflectivity()) {
				coords.setDifference(val1 - val2);
			}
		}
	}

	/**
	 * Loads from archive file or calculates results of comparison for given
	 * pair with given parameters.
	 * 
	 * @param pair
	 * @param params
	 * @return
	 */
	public static void putResult(CalidSingleResultContainer results) {

		if (!results.hasCoords())
			results.setCoords();

		if (!CalidResultLoader.loadSingleResult(results)) {

			calculateResult(results);
			CalidDataSaver.saveResults(results);
		}

		log.printMsg("CALID: Comparison completed", Log.TYPE_NORMAL,
				Log.MODE_VERBOSE);

	}

}
