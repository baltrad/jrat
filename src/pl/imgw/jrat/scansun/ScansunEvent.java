/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.scansun.ScansunConstants.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import pl.imgw.jrat.tools.in.LineParseable;
import pl.imgw.jrat.tools.in.LineParseableFactory;
import pl.imgw.jrat.tools.out.LogHandler;
import static pl.imgw.jrat.tools.out.Logging.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunEvent implements LineParseable {

    private ScansunEventAngleCharacteristics angleCharacteristics;
    private ScansunEventSiteCharacteristics siteCharacteristics;

    private boolean isSolarRay;
    private Date date;
    private boolean isMeanPowerCalibrated;
    private double meanPower;

    public ScansunEvent() {
    }

    public void setAngleCharacteristics(ScansunEventAngleCharacteristics angleCharacteristics) {
	this.angleCharacteristics = angleCharacteristics;
    }

    public double getRadarElevation() {
	return angleCharacteristics.getRadarElevation();
    }

    public double getRadarAzimuth() {
	return angleCharacteristics.getRadarAzimuth();
    }

    public double getSunElevation() {
	return angleCharacteristics.getSunElevation();
    }

    public double getSunAzimuth() {
	return angleCharacteristics.getSunAzimuth();
    }

    public void setSiteCharacteristics(ScansunEventSiteCharacteristics siteCharacteristics) {
	this.siteCharacteristics = siteCharacteristics;
    }

    public String getSiteName() {
	return siteCharacteristics.getSiteName();
    }

    public double getLongitude() {
	return siteCharacteristics.getLongitude();
    }

    public double getLatitude() {
	return siteCharacteristics.getLatitude();
    }

    public double getAltitude() {
	return siteCharacteristics.getAltitude();
    }

    public PulseDuration getPulseDuration() {
	return siteCharacteristics.getPulseDuration();
    }

    public void setSolarRayMode(boolean isSolarRay) {
	this.isSolarRay = isSolarRay;
    }

    public boolean isSolarRay() {
	return isSolarRay;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public Date getDate() {
	return date;
    }

    public void setPowerCalibrationMode(boolean isPowerCalibrated) {
	this.isMeanPowerCalibrated = isPowerCalibrated;
    }

    public boolean isMeanPowerCalibrated() {
	return isMeanPowerCalibrated;
    }

    public void setMeanPower(double meanPower) {
	this.meanPower = meanPower;
    }

    public double getMeanPower() {
	return meanPower;
    }

    public double getElevationOffset() {
	return (angleCharacteristics.getRadarElevation() - angleCharacteristics.getSunElevation());
    }

    public double getAzimuthOffset() {
	return (angleCharacteristics.getRadarAzimuth() - angleCharacteristics.getSunAzimuth());
    }

    public String toStringWithDelimiter(String delimiter) {

	String result = new String();
	result += angleCharacteristics.toStringWithDelimiter(delimiter);
	result += siteCharacteristics.toStringWithDelimiter(delimiter);
	result += (new Boolean(isSolarRay)) + delimiter;
	result += SCANSUN_DATE_TIME_FORMAT.format(date) + delimiter;
	result += (new Boolean(isMeanPowerCalibrated)) + delimiter;
	result += SCANSUN_DECIMAL_FORMAT.format(meanPower) + delimiter;

	return result;
    }

    public String getStringHeaderWithDelimiter(String delimiter) {
	String header = new String();

	header += ScansunEventAngleCharacteristics.getStringHeaderWithDelimiter(delimiter);
	header += ScansunEventSiteCharacteristics.getStringHeaderWithDelimiter(delimiter);

	header += "isSolarRay" + delimiter;
	header += SCANSUN_DATE_TIME_FORMAT.toPattern() + delimiter;
	header += "isMeanPowerCalibrated" + delimiter;
	header += "meanPower" + delimiter;

	return header;
    }

    public ScansunDay getDay() {
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);

	return new ScansunDay(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
    }

    public Double getTime() {
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	int hour = cal.get(Calendar.HOUR_OF_DAY);
	int minute = cal.get(Calendar.MINUTE);
	int second = cal.get(Calendar.SECOND);
	int milisecond = cal.get(Calendar.MILLISECOND);

	return (hour + minute / 60.0 + second / 3600.0 + milisecond / 3600000.0);
    }

    public static class ScansunEventFactory implements LineParseableFactory<ScansunEvent> {

	@Override
	public ScansunEvent create() {
	    return new ScansunEvent();
	}

    }

    @Override
    public void parseValues(String[] words) {

	this.setAngleCharacteristics(ScansunEventAngleCharacteristics.parseValues(Arrays.copyOfRange(words, 0, 4)));
	this.setSiteCharacteristics(ScansunEventSiteCharacteristics.parseValues(Arrays.copyOfRange(words, 4, 9)));
	this.isSolarRay = Boolean.parseBoolean(words[9]);
	try {
	    this.date = SCANSUN_DATE_TIME_FORMAT.parse(words[10]);
	} catch (ParseException e) {
	    LogHandler.getLogs().displayMsg("SCANSUN: ScansunEvent.parseValues() date parsing error", ERROR);
	    e.printStackTrace();
	}
	this.isMeanPowerCalibrated = Boolean.parseBoolean(words[11]);
	this.meanPower = Double.parseDouble(words[12]);
    }

    enum EventType {
	EVENT(false), SOLARRAY(true);

	private boolean isSolarRay;

	private EventType(boolean isSolarRay) {
	    this.isSolarRay = isSolarRay;
	}

	public boolean isSolarRay() {
	    return isSolarRay;
	}

    }

    public enum PulseDuration {
	SHORT, LONG;

	public String toString() {
	    return name().toLowerCase();
	}

	public static PulseDuration parseString(String s) {
	    if (s.equalsIgnoreCase(SHORT.name()))
		return SHORT;
	    else if (s.equalsIgnoreCase(LONG.name()))
		return LONG;

	    return null;
	}
    }

}