/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.Calendar;
import java.util.Date;

import pl.imgw.jrat.data.arrays.RainbowVolumeDataArray;
import pl.imgw.jrat.data.containers.VolumeContainer;
import pl.imgw.jrat.data.containers.ScanContainer;
import pl.imgw.jrat.scansun.ScansunEvent.PulseDuration;
import pl.imgw.jrat.tools.out.LogHandler;

import static pl.imgw.jrat.tools.out.Logging.*;
import static pl.imgw.jrat.scansun.ScansunConstants.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunManager {

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
    public ScansunContainer calculate(VolumeContainer vol, ScansunParsedParameters params) {

	ScansunContainer scansunContainer = new ScansunContainer();

	LogHandler.getLogs().displayMsg(
		"Volume file info: site=" + vol.getSiteName() + " date=" + ScansunConstants.SCANSUN_DATE_TIME_FORMAT_LONG.format(vol.getTimeSec()), NORMAL);

	for (ScanContainer scan : vol.getAllScans()) {

	    if (scan.getElevation() < ELEVATION_THRESHOLD)
		continue;

	    int rays = scan.getNRays();
	    int bins = scan.getNBins();

	    int binMin = calculateBinMin(scan, params);

	    if (binMin > bins)
		continue;

	    for (int rayNumber = 0; rayNumber < rays; rayNumber++) {

		double rayAzimuth = ((RainbowVolumeDataArray) scan.getArray()).getAzimuth(rayNumber);

		int deltaSeconds = (int) Math.floor(rayNumber / (scan.getRPM() * 60.0));
		Date rayDate = scan.getStartTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(rayDate);
		cal.add(Calendar.SECOND, deltaSeconds);
		rayDate = cal.getTime();

		ScansunSolarPositionAlgorithm solarPositionAlgorithm = new ScansunSolarPositionAlgorithm(scan.getCoordinates().x, scan.getCoordinates().y,
			vol.getHeight());

		double elevationDifference = scan.getElevation() - solarPositionAlgorithm.calculateSunElevation(rayDate);
		double azimuthDifference = rayAzimuth - solarPositionAlgorithm.calculateSunAzimuth(rayDate);

		if (!areAntennaAndSunSligned(params, elevationDifference, azimuthDifference))
		    continue;

		LogHandler.getLogs().displayMsg("Event found: " + rayDate.toString() + " elevation = " + scan.getElevation() + " azimuth = " + rayAzimuth,
			NORMAL);

		PulseDuration pulseDuration = scan.getRScale() < 750.0 ? PulseDuration.SHORT : PulseDuration.LONG;

		ScansunEventAngleCharacteristics eventAngleCharacteristics = new ScansunEventAngleCharacteristics(scan.getElevation(), rayAzimuth,
			solarPositionAlgorithm.calculateSunElevation(rayDate), solarPositionAlgorithm.calculateSunAzimuth(rayDate));
		ScansunEventSiteCharacteristics eventSiteCharacteristics = new ScansunEventSiteCharacteristics(vol.getSiteName(), scan.getCoordinates().x,
			scan.getCoordinates().y, vol.getHeight(), pulseDuration);

		boolean isSolarRay = false;
		boolean isPowerCalibrated = ScansunOptionsHandler.getOptions().getRadarParsedParameters(vol.getSiteName()).areParametersProper();
		double meanPower = 0.0;

		if (isReflectivityIncreasing(scan, rayNumber, binMin) && isEchoNarrow(scan, rayNumber, binMin)) {

		    double[] powerByBin = calculatePowerByBin(scan, rayNumber, binMin, vol.getSiteName());

		    meanPower = calculateMeanSolarPower(powerByBin);

		    int binCount = calculateBinCount(scan, rayNumber, binMin, meanPower, powerByBin);

		    if (isBinCountAboveThreshold(params, bins, binMin, binCount)) {
			isSolarRay = true;
			LogHandler.getLogs().displayMsg(
				"Solar ray found: " + rayDate.toString() + " elevation = " + scan.getElevation() + " azimuth = " + rayAzimuth, NORMAL);
		    } else {
			meanPower = 0.0;
		    }
		}

		ScansunEvent event = new ScansunEvent();
		event.setAngleCharacteristics(eventAngleCharacteristics);
		event.setSiteCharacteristics(eventSiteCharacteristics);
		event.setSolarRayMode(isSolarRay);
		event.setDate(rayDate);
		event.setPowerCalibrationMode(isPowerCalibrated);
		event.setMeanPower(meanPower);

		scansunContainer.addEvent(event);
	    }
	}

	return scansunContainer;
    }

    private boolean areAntennaAndSunSligned(ScansunParsedParameters params, double deltaElevation, double deltaAzimuth) {
	return Math.sqrt(deltaElevation * deltaElevation + deltaAzimuth * deltaAzimuth) < params.getAngleDifference();
    }

    private boolean isBinCountAboveThreshold(ScansunParsedParameters params, int bins, int binMin, int binCount) {
	return binCount > params.getThresholdFraction() * (bins - binMin);
    }

    /*
     * calculates range corresponding to given elevation and height
     * 
     * Eq.(12) from Huuskonen, Holleman (2007)
     */

    private int calculateBinMin(ScanContainer scan, ScansunParsedParameters params) {
	double rangeStep = scan.getRScale() / 1000.0;

	double range = ScansunEquations.calculateRange(scan.getElevation(), params.getHeightMin());

	int binMin = (int) Math.floor(range / rangeStep);

	if (params.getRangeMin() > binMin * rangeStep)
	    binMin = (int) Math.floor(params.getRangeMin() / rangeStep);

	return binMin;
    }

    private boolean isReflectivityIncreasing(ScanContainer scan, int rayNumber, int binMin) {

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
	    if (scan.getArray().getPoint(bins - dBin, rayNumber) > scan.getOffset()) {
		if (scan.getArray().getPoint(binMin, rayNumber) < scan.getArray().getPoint(bins - dBin, rayNumber))
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
	    double dBZprevious = scan.getArray().getPoint(binNumber, rayNumberPrevious);
	    double dBZ = scan.getArray().getPoint(binNumber, rayNumber);
	    double dBZnext = scan.getArray().getPoint(binNumber, rayNumberNext);

	    boolean b1 = Math.abs(dBZprevious - scan.getOffset()) > 0.1 ? true : false;
	    boolean b2 = dBZ > scan.getOffset() ? true : false;
	    boolean b3 = Math.abs(dBZnext - scan.getOffset()) > 0.1 ? true : false;

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

    private double[] calculatePowerByBin(ScanContainer scan, int rayNumber, int binMin, String siteName) {

	int bins = scan.getNBins();

	double[] powers = new double[bins - binMin];

	double radarConstant, bandwidth;

	for (int i = 0; i < powers.length; i++) {
	    int binNumber = i + binMin;
	    double r = binNumber * (scan.getRScale() / 1000.0);
	    double dBZ = scan.getArray().getPoint(binNumber, rayNumber);

	    PulseDuration pd = scan.getRScale() < 750.0 ? PulseDuration.SHORT : PulseDuration.LONG;
	    radarConstant = ScansunEquations.calculateRadarConstant(ScansunOptionsHandler.getOptions().getRadarParsedParameters(siteName), pd);
	    bandwidth = ScansunOptionsHandler.getOptions().getRadarParsedParameters(siteName).getBandwidth().get(pd);

	    powers[i] = ScansunEquations.calculatePower(dBZ, r, radarConstant, bandwidth);
	}

	return powers;
    }

    private double calculateMeanSolarPower(double[] powerByBin) {

	double sum = 0.0;
	for (int i = 0; i < powerByBin.length; i++) {
	    sum += powerByBin[i];
	}

	return (sum / powerByBin.length);
    }

    private int calculateBinCount(ScanContainer scan, int rayNumber, int binMin, double meanPower, double[] powerByBin) {

	int count = 0;

	for (int i = 0; i < powerByBin.length; i++) {
	    int binNumber = i + binMin;
	    double dBZ = scan.getArray().getPoint(binNumber, rayNumber);

	    if (dBZ > scan.getOffset()) {
		if (Math.abs((powerByBin[i] - meanPower) / meanPower) < 1.0) // TODO
		    count++;
	    }
	}

	return count;
    }

    public boolean isValid() {
	return valid;
    }

}