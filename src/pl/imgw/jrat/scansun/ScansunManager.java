/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.Calendar;
import java.util.Date;

import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.rainbow.RainbowScanArray;
import pl.imgw.jrat.scansun.ScansunConstants.PulseDuration;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunManager {

    private static Log log = LogManager.getLogger();
	private boolean valid = false;

	private ScansunManager() {
		valid = true;
	}

	private static ScansunManager manager = new ScansunManager();

	public static ScansunManager getScansunManager() {
		return manager;
	}

	/**
	 * @param product
	 */
	public ScansunContainer calculate(PolarData vol,
			ScansunParsedParameters params) {

		ScansunContainer sc = new ScansunContainer();

		log
				.printMsg(
						"Volume file info: site="
								+ vol.getSiteName()
								+ " date="
								+ ScansunConstants.SCANSUN_DATE_TIME_FORMAT_LONG.format(vol
										.getTimeSec()), Log.TYPE_NORMAL, Log.MODE_VERBOSE);

		for (ScanContainer scan : vol.getAllScans()) {

			if (scan.getElevation() < 1.0)
				continue;

			int rays = scan.getNRays();
			int bins = scan.getNBins();

			int binMin = binMin(scan, params);

			if (binMin > bins)
				continue;

			for (int rayNumber = 0; rayNumber < rays; rayNumber++) {

				double rayAzimuth = ((RainbowScanArray) scan.getArray())
						.getAzimuth(rayNumber);

				int deltaSeconds = (int) Math.floor(rayNumber
						/ (scan.getRPM() * 60.0));
				Date rayDate = scan.getStartTime();
				Calendar cal = Calendar.getInstance();
				cal.setTime(rayDate);
				cal.add(Calendar.SECOND, deltaSeconds);
				rayDate = cal.getTime();

				ScansunSpa spa = new ScansunSpa(scan.getCoordinates().x,
						scan.getCoordinates().y, vol.getHeight());

				double e = scan.getElevation() - spa.getSunElevation(rayDate);
				double a = rayAzimuth - spa.getSunAzimuth(rayDate);

				if (Math.sqrt(e * e + a * a) < params.getAngleDifference()) {

					log.printMsg(
							"Event found: " + rayDate.toString()
									+ " elevation = " + scan.getElevation()
									+ " azimuth = " + rayAzimuth, Log.TYPE_NORMAL, Log.MODE_VERBOSE);

					boolean isSolarRay = false;
					double meanSolarPower = 0.0;
					PulseDuration pd = scan.getRScale() < 750.0 ? PulseDuration.SHORT
							: PulseDuration.LONG;

					if (isReflectivityIncreasing(scan, rayNumber, binMin)) {
						if (isEchoNarrow(scan, rayNumber, binMin)) {

							meanSolarPower = meanSolarPower(scan, rayNumber,
									binMin, vol.getSiteName());

							int binCount = binCount(scan, rayNumber, binMin,
									meanSolarPower, vol.getSiteName());

							if (binCount > params.getThresholdFraction()
									* (bins - binMin)) {
								isSolarRay = true;
								log.printMsg(
										"Solar ray found: "
												+ rayDate.toString()
												+ " elevation = "
												+ scan.getElevation()
												+ " azimuth = " + rayAzimuth,
										Log.TYPE_NORMAL, Log.MODE_VERBOSE);
							} else {
								meanSolarPower = 0.0;
							}
						} // isEchoNarrow()
					}// isReflectivityIncreasing()

					// add event
					sc.add(scan.getElevation(), rayAzimuth,
							spa.getSunElevation(rayDate),
							spa.getSunAzimuth(rayDate), meanSolarPower,
							rayDate, pd, vol.getBeamwidth(),
							vol.getWavelength(), vol.getSiteName(),
							scan.getCoordinates().x, scan.getCoordinates().y,
							vol.getHeight(), isSolarRay);
				} // event
			} // rayNumber
		} // scan

		return sc;
	}

	/*
	 * calculates range corresponding to given elevation and height
	 * 
	 * Eq.(12) from Huuskonen, Holleman (2007)
	 */

	private int binMin(ScanContainer scan, ScansunParsedParameters params) {
		double rangeStep = scan.getRScale() / 1000.0;

		double range = ScansunEquations.range(scan.getElevation(),
				params.getHeightMin());

		int binMin = (int) Math.floor(range / rangeStep);

		if (params.getRangeMin() > binMin * rangeStep)
			binMin = (int) Math.floor(params.getRangeMin() / rangeStep);

		return binMin;
	}

	private boolean isReflectivityIncreasing(ScanContainer scan, int rayNumber,
			int binMin) {

		int bins = scan.getNBins();
		/*
		 * try { String filename = "test.dat"; FileWriter fout = new
		 * FileWriter(filename);
		 * 
		 * for (int i = binMin; i < scan.getNBins(); i++) fout.write(i + " " +
		 * scan.getArray().getPoint(i, rayNumber) + "\n"); fout.close(); } catch
		 * (IOException e) { System.out.println("ERROR!!!"); }
		 */
		int dBin = 20;

		if (scan.getArray().getPoint(binMin, rayNumber) > scan.getOffset()) {
			if (scan.getArray().getPoint(bins - dBin, rayNumber) > scan
					.getOffset()) {
				if (scan.getArray().getPoint(binMin, rayNumber) < scan
						.getArray().getPoint(bins - dBin, rayNumber))
					return true;
			}
		}

		return false;
	}

	public boolean isEchoNarrow(ScanContainer scan, int rayNumber, int binMin) {

		int rays = scan.getNRays();
		int bins = scan.getNBins();

		int rayNb = 2;
		int rayNumberPrevious = (rayNumber - rayNb + rays) % rays;
		int rayNumberNext = (rayNumber + rayNb) % rays;

		for (int binNumber = binMin; binNumber < bins; binNumber++) {
			double dBZprevious = scan.getArray().getPoint(binNumber,
					rayNumberPrevious);
			double dBZ = scan.getArray().getPoint(binNumber, rayNumber);
			double dBZnext = scan.getArray().getPoint(binNumber, rayNumberNext);

			boolean b1 = Math.abs(dBZprevious - scan.getOffset()) > 0.1 ? true
					: false;
			boolean b2 = dBZ > scan.getOffset() ? true : false;
			boolean b3 = Math.abs(dBZnext - scan.getOffset()) > 0.1 ? true
					: false;

			if (b1 && b2 && b3)
				return false;
			/*
			 * if (dBZ > scan.getOffset() && Math.abs(dBZprevious -
			 * scan.getOffset()) > 0.1 && Math.abs(dBZnext - scan.getOffset()) >
			 * 0.1) return false;
			 */
		}

		return true;
	}

	private double meanSolarPower(ScanContainer scan, int rayNumber,
			int binMin, String siteName) {

		int bins = scan.getNBins();

		double sum = 0.0;
		for (int binNumber = binMin; binNumber < bins; binNumber++) {
			double r = binNumber * (scan.getRScale() / 1000.0);
			double dBZ = scan.getArray().getPoint(binNumber, rayNumber);

			PulseDuration pd = scan.getRScale() < 750.0 ? PulseDuration.SHORT
					: PulseDuration.LONG;
			double radarConstant = ScansunEquations.radarConstant(
					ScansunOptionsHandler.getOptions()
							.getRadarParsedParameters(siteName), pd);
			double bandwidth = ScansunOptionsHandler.getOptions()
					.getRadarParsedParameters(siteName).getBandwidth(pd);

			double power = ScansunEquations.power(dBZ, r, radarConstant,
					bandwidth);

			sum += power;
		}

		return (sum / (bins - binMin));
	}

	private int binCount(ScanContainer scan, int rayNumber, int binMin,
			double meanPower, String siteName) {

		int count = 0;

		int bins = scan.getNBins();

		for (int binNumber = binMin; binNumber < bins; binNumber++) {
			double dBZ = scan.getArray().getPoint(binNumber, rayNumber);

			if (dBZ > scan.getOffset()) {
				double r = binNumber * (scan.getRScale() / 1000.0);
				PulseDuration pd = scan.getRScale() < 750.0 ? PulseDuration.SHORT
						: PulseDuration.LONG;
				double radarConstant = ScansunEquations.radarConstant(
						ScansunOptionsHandler.getOptions()
								.getRadarParsedParameters(siteName), pd);
				double bandwidth = ScansunOptionsHandler.getOptions()
						.getRadarParsedParameters(siteName).getBandwidth(pd);

				double power = ScansunEquations.power(dBZ, r, radarConstant,
						bandwidth);

				if (Math.abs((power - meanPower) / meanPower) < 1.0) // TODO
					count++;
			}
		}

		return count;
	}

	public boolean isValid() {
		return valid;
	}

}