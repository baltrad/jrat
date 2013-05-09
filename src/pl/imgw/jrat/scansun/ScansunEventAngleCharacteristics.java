/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DECIMAL_FORMAT;
import static pl.imgw.jrat.tools.out.Logging.ERROR;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */

public class ScansunEventAngleCharacteristics {

    private double radarElevation;
    private double radarAzimuth;
    private double sunElevation;
    private double sunAzimuth;

    public ScansunEventAngleCharacteristics(double radarElevation, double radarAzimuth, double sunElevation,
	    double sunAzimuth) {
	this.radarElevation = radarElevation;
	this.radarAzimuth = radarAzimuth;
	this.sunElevation = sunElevation;
	this.sunAzimuth = sunAzimuth;
    }

    public void setRadarElevation(double radarElevation) {
	this.radarElevation = radarElevation;
    }

    public double getRadarElevation() {
	return radarElevation;
    }

    public void setRadarAzimuth(double radarAzimuth) {
	this.radarAzimuth = radarAzimuth;
    }

    public double getRadarAzimuth() {
	return radarAzimuth;
    }

    public void setSunElevation(double sunElevation) {
	this.sunElevation = sunElevation;
    }

    public double getSunElevation() {
	return sunElevation;
    }

    public void setSunAzimuth(double sunAzimuth) {
	this.sunAzimuth = sunAzimuth;
    }

    public double getSunAzimuth() {
	return sunAzimuth;
    }

    public static String getStringHeaderWithDelimiter(String delimiter) {
	String header = new String();

	header += "radarElevation" + delimiter;
	header += "radarAzimuth" + delimiter;
	header += "sunElevation" + delimiter;
	header += "sunAzimuth" + delimiter;

	return header;
    }

    public String toStringWithDelimiter(String delimiter) {
	String result = new String();

	result += SCANSUN_DECIMAL_FORMAT.format(radarElevation) + delimiter;
	result += SCANSUN_DECIMAL_FORMAT.format(radarAzimuth) + delimiter;
	result += SCANSUN_DECIMAL_FORMAT.format(sunElevation) + delimiter;
	result += SCANSUN_DECIMAL_FORMAT.format(sunAzimuth) + delimiter;

	return result;
    }

    public static ScansunEventAngleCharacteristics parseValues(String[] words) {

	if (words.length != getStringHeaderWithDelimiter(" ").split(" ").length) {
	    LogHandler.getLogs().displayMsg(
		    "SCANSUN: ScansunEventAngleCharacteristics.parseValues() error: words array wrong length", ERROR);
	    return null;
	}

	double radarElevation = Double.parseDouble(words[0]);
	double radarAzimuth = Double.parseDouble(words[1]);
	double sunElevation = Double.parseDouble(words[2]);
	double sunAzimuth = Double.parseDouble(words[3]);

	return new ScansunEventAngleCharacteristics(radarElevation, radarAzimuth, sunElevation, sunAzimuth);
    }
}
