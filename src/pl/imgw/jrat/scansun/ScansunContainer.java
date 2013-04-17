/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.*;

import pl.imgw.jrat.scansun.ScansunConstants.PulseDuration;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunContainer {

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
			LogHandler.getLogs().displayMsg("SCANSUN: no results found",
					Logging.WARNING);
			return;
		}

		Iterator<ScansunEvent> itr = list.iterator();
		while (itr.hasNext()) {
			if (!ScansunFileHandler.saveResult(itr.next())) {
				LogHandler.getLogs().displayMsg("SCANSUN: Cannot save result",
						LogHandler.ERROR);
			}
		}

		LogHandler.getLogs().displayMsg("SCANSUN: Saving results completed",
				LogHandler.NORMAL);
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
