/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DATE_TIME_FORMAT;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DECIMAL_FORMAT;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_RESULTSFILE_DELIMITER;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

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
public class ScansunEvent {

    private static Log log = LogManager.getLogger();

    private double radarElevation;
    private double radarAzimuth;
    private double sunElevation;
    private double sunAzimuth;
    private double meanPower;
    private Date date;
    private boolean isSolarRay;

    private String siteName;
    private double longitude;
    private double latitude;
    private double altitude;
    private PulseDuration pulseDuration;

    private ScansunEvent() {
    }

    public ScansunEvent(double radarElevation, double radarAzimuth,
            double sunElevation, double sunAzimuth, double meanPower,
            Date date, boolean isSolarRay, String siteName, double longitude,
            double latitude, double altitude, PulseDuration pulseDuration) {
        this.radarElevation = radarElevation;
        this.radarAzimuth = radarAzimuth;
        this.sunElevation = sunElevation;
        this.sunAzimuth = sunAzimuth;
        this.meanPower = meanPower;
        this.date = date;
        this.isSolarRay = isSolarRay;

        this.siteName = siteName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.pulseDuration = pulseDuration;
    }

    public double getRadarElevation() {
        return radarElevation;
    }

    public double getRadarAzimuth() {
        return radarAzimuth;
    }

    public double getSunElevation() {
        return sunElevation;
    }

    public double getSunAzimuth() {
        return sunAzimuth;
    }

    public double getMeanSolarPower() {
        return meanPower;
    }

    public Date getDate() {
        return date;
    }

    public boolean isSolarRay() {
        return isSolarRay;
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

    public double getElevationOffset() {
        return (radarElevation - sunElevation);
    }

    public double getAzimuthOffset() {
        return (radarAzimuth - sunAzimuth);
    }

    public String toStringWithDelimiter(String delimiter) {

        String eventString = new String();
        eventString += SCANSUN_DECIMAL_FORMAT.format(radarElevation)
                + delimiter;
        eventString += SCANSUN_DECIMAL_FORMAT.format(radarAzimuth) + delimiter;
        eventString += SCANSUN_DECIMAL_FORMAT.format(sunElevation) + delimiter;
        eventString += SCANSUN_DECIMAL_FORMAT.format(sunAzimuth) + delimiter;
        eventString += SCANSUN_DECIMAL_FORMAT.format(meanPower) + delimiter;
        eventString += SCANSUN_DATE_TIME_FORMAT.format(date) + delimiter;
        eventString += (new Boolean(isSolarRay)) + delimiter;
        eventString += siteName + delimiter;
        eventString += SCANSUN_DECIMAL_FORMAT.format(longitude) + delimiter;
        eventString += SCANSUN_DECIMAL_FORMAT.format(latitude) + delimiter;
        eventString += SCANSUN_DECIMAL_FORMAT.format(altitude) + delimiter;
        eventString += pulseDuration + delimiter;

        return eventString;
    }

    public static String getStringHeader(String delimiter) {
        String header = new String();
        header += "radarElevation" + delimiter;
        header += "radarAzimuth" + delimiter;
        header += "sunElevation" + delimiter;
        header += "sunAzimuth" + delimiter;
        header += "meanPower" + delimiter;
        header += SCANSUN_DATE_TIME_FORMAT.toPattern() + delimiter;
        header += "isSolarRay" + delimiter;
        header += "siteName" + delimiter;
        header += "longitude" + delimiter;
        header += "latitude" + delimiter;
        header += "altitude" + delimiter;
        header += "pulseDuration" + delimiter;

        return header;
    }

    public static ScansunEvent parseLine(String line) {
        ScansunEvent event = new ScansunEvent();

        String[] header = getStringHeader(SCANSUN_RESULTSFILE_DELIMITER).split(
                SCANSUN_RESULTSFILE_DELIMITER);
        String[] words = line.split(SCANSUN_RESULTSFILE_DELIMITER);

        if (words.length != header.length) {
            log.printMsg("SCANSUN: ScansunEvent parseLine error.",
                    Log.TYPE_ERROR, Log.MODE_VERBOSE);
            return null;
        }
        try {
            event.radarElevation = Double.parseDouble(words[0]);
            event.radarAzimuth = Double.parseDouble(words[1]);
            event.sunElevation = Double.parseDouble(words[2]);
            event.sunAzimuth = Double.parseDouble(words[3]);
            event.meanPower = Double.parseDouble(words[4]);
            event.date = SCANSUN_DATE_TIME_FORMAT.parse(words[5]);
            event.isSolarRay = Boolean.parseBoolean(words[6]);
            event.siteName = words[7];
            event.longitude = Double.parseDouble(words[8]);
            event.latitude = Double.parseDouble(words[9]);
            event.altitude = Double.parseDouble(words[10]);
            event.pulseDuration = PulseDuration.parseString(words[11]);

        } catch (ParseException e) {
            log.printMsg("SCANSUN: parseLine wrong format.", Log.TYPE_WARNING,
                    Log.MODE_VERBOSE);
            return null;
        }

        return event;
    }

    public ScansunDay getDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return new ScansunDay(cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
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

}