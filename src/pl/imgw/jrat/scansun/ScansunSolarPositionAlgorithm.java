/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunSolarPositionAlgorithm {

    private double longitude;
    private double latitude;
    private double altitude;

    private static final double DELTA_T = 67.0;
    private static final double PRESSURE = 820.0;
    private static final double TEMPERATURE = 20.0;
    private static final double SLOPE = 0.0;
    private static final double AZIMUTH_ROTATION = 0.0;
    private static final double ATMOSPHERIC_REFRACTION = 0.5667;

    private SolarPositionAlgorithmHandler handler = new SolarPositionAlgorithmHandler();

    public ScansunSolarPositionAlgorithm(double longitude, double latitude, double altitude) {
	this.longitude = longitude;
	this.latitude = latitude;
	this.altitude = altitude;

	this.handler = new SolarPositionAlgorithmHandler();
	this.handler.setLongitude(longitude);
	this.handler.setLatitude(latitude);
	this.handler.setAltitude(altitude);
	this.handler.setSlope(SLOPE);
	this.handler.setPressure(PRESSURE);
	this.handler.setTemperature(TEMPERATURE);
	this.handler.setAtmosphericRefraction(ATMOSPHERIC_REFRACTION);
	this.handler.setDeltaT(DELTA_T);
	this.handler.setAzimuthRotation(AZIMUTH_ROTATION);
    }

    public Double calculateSunElevation(Date date) {

	Calendar cal = Calendar.getInstance();
	cal.setTime(date);

	handler.setYear(cal.get(Calendar.YEAR));
	handler.setMonth(cal.get(Calendar.MONTH) + 1);
	handler.setDay(cal.get(Calendar.DAY_OF_MONTH));
	handler.setHour(cal.get(Calendar.HOUR_OF_DAY));
	handler.setMinute(cal.get(Calendar.MINUTE));
	handler.setSecond(cal.get(Calendar.SECOND));
	handler.setTimezone(0.0); // UTC
	handler.calculateSunPosition();

	return handler.getElevation();
    }

    public Double calculateSunAzimuth(Date date) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);

	handler.setYear(cal.get(Calendar.YEAR));
	handler.setMonth(cal.get(Calendar.MONTH) + 1);
	handler.setDay(cal.get(Calendar.DAY_OF_MONTH));
	handler.setHour(cal.get(Calendar.HOUR_OF_DAY));
	handler.setMinute(cal.get(Calendar.MINUTE));
	handler.setSecond(cal.get(Calendar.SECOND));
	handler.setTimezone(0.0); // UTC
	handler.calculateSunPosition();

	return handler.getAzimuth();
    }

    public Double calculateSunriseTime(ScansunDay day) {

	handler.setYear(day.getYear());
	handler.setMonth(day.getMonth() + 1);
	handler.setDay(day.getDayOfMonth());
	handler.setHour(12);// arbitrary within (0:24)
	handler.setMinute(0);
	handler.setSecond(0);
	handler.setTimezone(0.0); // UTC
	handler.calculateSunPosition();

	return handler.getSunriseTime();
    }

    public Double calculateSunriseAzimuth(ScansunDay day) {

	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.YEAR, day.getYear());
	cal.set(Calendar.MONTH, day.getMonth());
	cal.set(Calendar.DAY_OF_MONTH, day.getDayOfMonth());
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);

	do {
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    int minute = cal.get(Calendar.MINUTE);
	    int second = cal.get(Calendar.SECOND);

	    handler.setYear(day.getYear());
	    handler.setMonth(day.getMonth() + 1);
	    handler.setDay(day.getDayOfMonth());
	    handler.setHour(hour);
	    handler.setMinute(minute);
	    handler.setSecond(second);
	    handler.setTimezone(0.0); // UTC
	    handler.calculateSunPosition();

	    cal.add(Calendar.MINUTE, 1);
	} while (handler.getElevation() < 0.0);

	return handler.getAzimuth();
    }

    public Double calculateSunsetTime(ScansunDay day) {

	handler.setYear(day.getYear());
	handler.setMonth(day.getMonth() + 1);
	handler.setDay(day.getDayOfMonth());
	handler.setHour(12);// arbitrary within (0:24)
	handler.setMinute(0);
	handler.setSecond(0);
	handler.setTimezone(0.0); // UTC
	handler.calculateSunPosition();

	return handler.getSunsetTime();
    }

    public Double calculateSunsetAzimuth(ScansunDay day) {

	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.YEAR, day.getYear());
	cal.set(Calendar.MONTH, day.getMonth());
	cal.set(Calendar.DAY_OF_MONTH, day.getDayOfMonth());
	cal.set(Calendar.HOUR_OF_DAY, 23);
	cal.set(Calendar.MINUTE, 59);
	cal.set(Calendar.SECOND, 0);

	do {
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    int minute = cal.get(Calendar.MINUTE);
	    int second = cal.get(Calendar.SECOND);

	    handler.setYear(day.getYear());
	    handler.setMonth(day.getMonth() + 1);
	    handler.setDay(day.getDayOfMonth());
	    handler.setHour(hour);
	    handler.setMinute(minute);
	    handler.setSecond(second);
	    handler.setTimezone(0.0); // UTC
	    handler.calculateSunPosition();

	    cal.add(Calendar.MINUTE, -1);
	} while (handler.getElevation() < 0.0);

	return handler.getAzimuth();
    }

}