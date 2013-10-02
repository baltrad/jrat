/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import pl.imgw.jrat.scansun.ScansunException;
import pl.imgw.jrat.scansun.data.ScansunParameters;
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
public class ScansunParametersParser {

	private static Log log = LogManager.getLogger();

	private static final double MIN_HEIGHT_MIN = 0;
	private static final double MAX_HEIGHT_MIN = 100;
	private static final double MIN_RANGE_MIN = 0;
	private static final double MAX_RANGE_MIN = 250;
	private static final double MIN_THRESHOLD_FRACTION = 0;
	private static final double MAX_THRESHOLD_FRACTION = 1;
	private static final double MIN_ANGLE_DIFFERENCE = 0;
	private static final double MAX_ANGLE_DIFFERENCE = 5;
	private static final double MIN_MEAN_POWER_WIDTH_FACTOR = 0.01;
	private static final double MAX_MEAN_POWER_WIDTH_FACTOR = 1;

	private static ScansunParametersParser parser = new ScansunParametersParser();

	public static ScansunParametersParser getParser() {
		return parser;
	}

	private ScansunParametersParser() {

	}

	private static final String HEIGHT_MIN = "hmin=";
	private static final String RANGE_MIN = "rmin=";
	private static final String THRESHOLD_FRACTION = "tf=";
	private static final String ANGLE_DIFFERENCE = "ad=";
	private static final String MEAN_POWER_WIDTH_FACTOR = "mpwf=";

	private static final String errorMsg = "SCANSUN: Arguments are incorrect";

	public ScansunParameters parseParameters(String[] par)
			throws ScansunException {

		ScansunParameters params = new ScansunParameters();

		if (par == null || par.length == 0) {
			throw new ScansunException(
					"SCANSUN: Cannot parse parameters, string array is empty");
		}

		for (int i = 0; i < par.length; i++) {

			if (par[i].isEmpty()) {
				throw new ScansunException(errorMsg + ": string " + i
						+ " is empty");
			}

			if (par[i].startsWith(HEIGHT_MIN)) {
				setHeightMin(params, par[i].substring(HEIGHT_MIN.length()));
			} else if (par[i].startsWith(RANGE_MIN)) {
				setRangeMin(params, par[i].substring(RANGE_MIN.length()));
			} else if (par[i].startsWith(THRESHOLD_FRACTION)) {
				setThresholdFraction(params,
						par[i].substring(THRESHOLD_FRACTION.length()));
			} else if (par[i].startsWith(ANGLE_DIFFERENCE)) {
				setAngleDifference(params,
						par[i].substring(ANGLE_DIFFERENCE.length()));
			} else if (par[i].startsWith(MEAN_POWER_WIDTH_FACTOR)) {
				setMeanPowerWidthFactor(params,
						par[i].substring(MEAN_POWER_WIDTH_FACTOR.length()));
			} else if (par[i].contains("=")) {
				log.printMsg(errorMsg + " (" + par[i] + ")", Log.TYPE_WARNING,
						Log.MODE_VERBOSE);

				throw new ScansunException("Cannot parse parameter: " + par[i]);
			}
		}

		return params;
	}

	private void setHeightMin(ScansunParameters params, String word)
			throws ScansunException {
		Double heightMin = null;
		try {
			heightMin = Double.parseDouble(word);
		} catch (NumberFormatException e) {
			throw new ScansunException(heightMin
					+ " is not a valid heightMin value");
		}

		if (heightMin == null || heightMin < MIN_HEIGHT_MIN
				|| heightMin > MAX_HEIGHT_MIN) {
			throw new ScansunException(heightMin
					+ " is not a valid heightMin value");
		}

		params.setHeightMin(heightMin);
	}

	private void setRangeMin(ScansunParameters params, String word)
			throws ScansunException {
		Double rangeMin = null;
		try {
			rangeMin = Double.parseDouble(word);
		} catch (NumberFormatException e) {
			throw new ScansunException(rangeMin
					+ " is not a valid rangeMin value");
		}

		if (rangeMin == null || rangeMin < MIN_RANGE_MIN
				|| rangeMin > MAX_RANGE_MIN) {
			throw new ScansunException(rangeMin
					+ " is not a valid rangeMin value");
		}

		params.setRangeMin(rangeMin);
	}

	private void setThresholdFraction(ScansunParameters params, String word)
			throws ScansunException {
		Double thresholdFraction = null;
		try {
			thresholdFraction = Double.parseDouble(word);
		} catch (NumberFormatException e) {
			throw new ScansunException(thresholdFraction
					+ " is not a valid thresholdFraction value");
		}

		if (thresholdFraction == null
				|| thresholdFraction < MIN_THRESHOLD_FRACTION
				|| thresholdFraction > MAX_THRESHOLD_FRACTION) {
			throw new ScansunException(thresholdFraction
					+ " is not a valid thresholdFraction value");
		}

		params.setThresholdFraction(thresholdFraction);
	}

	private void setAngleDifference(ScansunParameters params, String word)
			throws ScansunException {
		Double angleDifference = null;
		try {
			angleDifference = Double.parseDouble(word);
		} catch (NumberFormatException e) {
			throw new ScansunException(angleDifference
					+ " is not a valid angleDifference value");
		}

		if (angleDifference == null || angleDifference < MIN_ANGLE_DIFFERENCE
				|| angleDifference > MAX_ANGLE_DIFFERENCE) {
			throw new ScansunException(angleDifference
					+ " is not a valid angleDifference value");
		}

		params.setAngleDifference(angleDifference);
	}

	private void setMeanPowerWidthFactor(ScansunParameters params, String word)
			throws ScansunException {
		Double meanPowerWidthFactor = null;
		try {
			meanPowerWidthFactor = Double.parseDouble(word);
		} catch (NumberFormatException e) {
			throw new ScansunException(meanPowerWidthFactor
					+ " is not a valid meanPowerWidthFactor value");
		}

		if (meanPowerWidthFactor == null
				|| meanPowerWidthFactor < MIN_MEAN_POWER_WIDTH_FACTOR
				|| meanPowerWidthFactor > MAX_MEAN_POWER_WIDTH_FACTOR) {
			throw new ScansunException(meanPowerWidthFactor
					+ " is not a valid meanPowerWidthFactor value");
		}

		params.setMeanPowerWidthFactor(meanPowerWidthFactor);
	}

	public static void printHelp() {

		StringBuilder msg = new StringBuilder("SCANSUN algorithm usage:\n");

		msg.append("jrat --scansun <args> -i files/folder(s) [-v]\n");
		msg.append("<args> ");
		msg.append("[hmin=] [rmin=] [tf=] [ad=] [mpwf=]");
		msg.append("\n");
		msg.append("hmin: minimal height in km, from " + MIN_HEIGHT_MIN
				+ " to " + MAX_HEIGHT_MIN + "\n");
		msg.append("rmin: minimal range in km, from " + MIN_RANGE_MIN + " to "
				+ MAX_RANGE_MIN + "\n");
		msg.append("tf: threshold fraction, from " + MIN_THRESHOLD_FRACTION
				+ " to " + MAX_THRESHOLD_FRACTION + "\n");
		msg.append("ad: angle difference in degrees, from "
				+ MIN_ANGLE_DIFFERENCE + " to " + MAX_ANGLE_DIFFERENCE + "\n");
		msg.append("mpwf: mean power width factor, from "
				+ MIN_MEAN_POWER_WIDTH_FACTOR + " to "
				+ MAX_MEAN_POWER_WIDTH_FACTOR + "\n");
		msg.append("jrat --scansun-help print this message\n\n");
		msg.append("jrat --scansun-result prints results.");
		msg.append("\nuse jrat --help to print general jrat help message");

		System.out.println(msg);
	}

}
