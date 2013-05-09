package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.tools.out.Logging.ERROR;
import pl.imgw.jrat.scansun.ScansunConstants.Sites;
import pl.imgw.jrat.scansun.ScansunEvent.PulseDuration;
import pl.imgw.jrat.tools.out.LogHandler;

public class ScansunEventSiteCharacteristics {

    private String siteName;
    private double longitude;
    private double latitude;
    private double altitude;
    private PulseDuration pulseDuration;

    public ScansunEventSiteCharacteristics(String siteName, double longitude, double latitude, double altitude,
	    PulseDuration pulseDuration) {
	this.siteName = siteName;
	this.longitude = longitude;
	this.latitude = latitude;
	this.altitude = altitude;
	this.pulseDuration = pulseDuration;
    }

    public String getSiteName() {
	return siteName;
    }

    public double getLongitude() {
	return longitude;
    }

    public double getLatitude() {
	return latitude;
    }

    public double getAltitude() {
	return altitude;
    }

    public PulseDuration getPulseDuration() {
	return pulseDuration;
    }

    public static String getStringHeaderWithDelimiter(String delimiter) {
	String header = new String();

	header += "siteName" + delimiter;
	header += "longitude" + delimiter;
	header += "latitude" + delimiter;
	header += "altitude" + delimiter;
	header += "pulseDuration" + delimiter;

	return header;
    }

    public String toStringWithDelimiter(String delimiter) {
	String result = new String();

	result += Sites.toStringWithDelimiter(siteName, delimiter);
	result += pulseDuration + delimiter;

	return result;
    }

    public static ScansunEventSiteCharacteristics parseValues(String[] words) {

	if (words.length != getStringHeaderWithDelimiter(" ").split(" ").length) {
	    LogHandler.getLogs().displayMsg(
		    "SCANSUN: ScansunEventAngleCharacteristics.parseValues() error: words array wrong length", ERROR);
	    return null;
	}

	String siteName = words[0];
	double longitude = Double.parseDouble(words[1]);
	double latitude = Double.parseDouble(words[2]);
	double altitude = Double.parseDouble(words[3]);
	PulseDuration pulseDuration = PulseDuration.parseString(words[4]);

	return new ScansunEventSiteCharacteristics(siteName, longitude, latitude, altitude, pulseDuration);
    }

}
