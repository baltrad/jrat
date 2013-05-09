/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.tools.out.Logging.WARNING;
import pl.imgw.jrat.tools.out.LogHandler;

import static pl.imgw.jrat.scansun.ScansunConstants.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunParsedParameters {

    private static final String HEIGHT_MIN = "hmin=";
    private static final String RANGE_MIN = "rmin=";
    private static final String THRESHOLD_FRACTION = "tf=";
    private static final String ANGLE_DIFFERENCE = "ad=";
    private static final String MEAN_POWER_WIDTH_FACTOR = "mpwf=";

    private double heightMin;
    private double rangeMin;
    private double thresholdFraction;
    private double angleDifference;
    private double meanPowerWidthFactor;

    public ScansunParsedParameters() {
    }

    public boolean initialize(String[] par) {

	if (par == null) {
	    return false;
	}

	String errorMsg = "SCANSUN: Arguments for SCANSUN are incorrect";

	try {
	    for (int i = 0; i < par.length; i++) {

		if (par[i].startsWith(HEIGHT_MIN)) {
		    heightMin = Double.parseDouble(par[i].substring(HEIGHT_MIN.length()));
		} else if (par[i].startsWith(RANGE_MIN)) {
		    rangeMin = Double.parseDouble(par[i].substring(RANGE_MIN.length()));
		} else if (par[i].startsWith(THRESHOLD_FRACTION)) {
		    thresholdFraction = Double.parseDouble(par[i].substring(THRESHOLD_FRACTION.length()));
		} else if (par[i].startsWith(ANGLE_DIFFERENCE)) {
		    angleDifference = Double.parseDouble(par[i].substring(ANGLE_DIFFERENCE.length()));
		} else if (par[i].startsWith(MEAN_POWER_WIDTH_FACTOR)) {
		    meanPowerWidthFactor = Double.parseDouble(par[i].substring(MEAN_POWER_WIDTH_FACTOR.length()));
		} else {
		    LogHandler.getLogs().displayMsg(errorMsg + " (" + par[i] + ")", WARNING);
		    return false;
		}
	    }
	} catch (NumberFormatException e) {
	    LogHandler.getLogs().displayMsg(errorMsg + " (" + e.getLocalizedMessage() + ")", WARNING);
	    return false;
	} catch (ArrayIndexOutOfBoundsException e) {
	    LogHandler.getLogs().displayMsg(errorMsg, WARNING);
	    return false;
	}

	/*
	 * Checks if provided parameters are valid
	 */
	if (getHeightMin() < 0 || getHeightMin() > 100) {
	    LogHandler.getLogs().displayMsg(errorMsg + " (" + HEIGHT_MIN + getHeightMin() + ")", WARNING);
	    return false;
	}
	if (getRangeMin() < 0 || getRangeMin() > 250) {
	    LogHandler.getLogs().displayMsg(errorMsg + " (" + RANGE_MIN + getRangeMin() + ")", WARNING);
	    return false;
	}

	if (getThresholdFraction() < 0 || getThresholdFraction() > 1) {
	    LogHandler.getLogs().displayMsg(errorMsg + " (" + THRESHOLD_FRACTION + getThresholdFraction() + ")", WARNING);
	    return false;
	}

	if (getAngleDifference() < 0 || getAngleDifference() > 5) {
	    LogHandler.getLogs().displayMsg(errorMsg + " (" + ANGLE_DIFFERENCE + getAngleDifference() + ")", WARNING);
	    return false;
	}

	if (getMeanPowerFactor() < 0 || getMeanPowerFactor() > 0.1) {
	    LogHandler.getLogs().displayMsg(errorMsg + " (" + MEAN_POWER_WIDTH_FACTOR + getMeanPowerFactor() + ")", WARNING);
	    return false;
	}

	return true;
    }

    public static void printHelp() {

	String msg = "SCANSUN algorithm usage: jrat [options]\n";
	msg += "--scansun-help print this message\n\n";
	msg += "--scansun [<args>]." + "\n<args> " + "hmin=: sets minimal height in km, from 0 to 100\n" + "rmin=: sets minimal range in km, from 0 to 250\n"
		+ "tf=: sets threshold fraction, from 0 to 1\n" + "ad=: sets angle difference in degrees, from 0 to 5.0\n"
		+ "mpf: sets mean power factor, from 0 to 0.1\n";
	msg += "\nuse jrat --help to print more general help message";

	System.out.println(msg);
    }

    public double getHeightMin() {
	return heightMin;
    }

    public double getRangeMin() {
	return rangeMin;
    }

    public double getThresholdFraction() {
	return thresholdFraction;
    }

    public double getAngleDifference() {
	return angleDifference;
    }

    public double getMeanPowerFactor() {
	return meanPowerWidthFactor;
    }

}
