/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import pl.imgw.jrat.scansun.ScansunConstants.PulseDuration;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunContainer {
    
    private static Log log = LogManager.getLogger();

	private ArrayList<ScansunEvent> list;
	private Integer listSize;
	private boolean hasResults;
	private boolean hasSolarRays;

	public ScansunContainer() {
		this.list = new ArrayList<ScansunEvent>();
		this.hasResults = false;
		this.hasSolarRays = false;
	}

	public boolean hasResults() {
		return hasResults;
	}

	public boolean hasSolarRays() {
		return hasSolarRays;
	}

	public ArrayList<ScansunEvent> getList() {
		return list;
	}

	public Integer getListSize() {
		return listSize;
	}

	public void save() {

		if (!this.hasResults) {
			log.printMsg("SCANSUN: no results found",
					Log.TYPE_WARNING, Log.MODE_VERBOSE);
			return;
		}

		Iterator<ScansunEvent> itr = list.iterator();
		while (itr.hasNext()) {
			if (!ScansunFileHandler.saveResult(itr.next())) {
				log.printMsg("SCANSUN: Cannot save result",
						Log.TYPE_ERROR, Log.MODE_VERBOSE);
			}
		}

		log.printMsg("SCANSUN: Saving results completed",
				Log.TYPE_NORMAL, Log.MODE_VERBOSE);
	}

	public void resetContainer() {
		list = new ArrayList<ScansunEvent>();
		hasResults = false;
		hasSolarRays = false;
	}

	public void add(double radarElevation, double radarAzimuth,
			double sunElevation, double sunAzimuth, double meanPower,
			Date date, PulseDuration pd, double beamwidth, double wavelength,
			String siteName, double longitude, double latitude,
			double altitude, boolean isSolarRay) {

		list.add(new ScansunEvent(radarElevation, radarAzimuth, sunElevation,
				sunAzimuth, meanPower, date, isSolarRay, siteName, longitude,
				latitude, altitude, pd));
		listSize = list.size();
		hasResults = true;

		if (isSolarRay) {
			hasSolarRays = true;
		}
	}

}
