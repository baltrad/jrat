/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunSpa {

	private Double longitude;
	private Double latitude;
	private Double altitude;

	private static final double DELTA_T = 67.0;
	private static final double PRESSURE = 820.0;
	private static final double TEMPERATURE = 20.0;
	private static final double SLOPE = 0.0;
	private static final double AZIMUTH_ROTATION = 0.0;
	private static final double ATMOSPHERIC_REFRACTION = 0.5667;

	private ScansunSpaData spaData;

	public ScansunSpa(double longitude, double latitude, double altitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;

		int year = 1970;
		int month = 0;
		int day = 1;
		int hour = 0;
		int minute = 0;
		int second = 0;
		double timezone = 0.0;

		this.spaData = new ScansunSpaData(year, month, day, hour, minute,
				second, timezone, longitude, latitude, altitude, SLOPE,
				PRESSURE, TEMPERATURE, ATMOSPHERIC_REFRACTION, DELTA_T,
				AZIMUTH_ROTATION);
	}

	public Double getSunElevation(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		double timezone = 0.0; // cal.get(Calendar.ZONE_OFFSET) / 3600000.0;

		this.spaData = new ScansunSpaData(year, month, day, hour, minute,
				second, timezone, longitude, latitude, altitude, SLOPE,
				PRESSURE, TEMPERATURE, ATMOSPHERIC_REFRACTION, DELTA_T,
				AZIMUTH_ROTATION);

		spaData.initialize();

		return spaData.getElevation();
	}

	public Double getSunAzimuth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		double timezone = 0.0; // cal.get(Calendar.ZONE_OFFSET) / 3600000.0;

		this.spaData = new ScansunSpaData(year, month, day, hour, minute,
				second, timezone, longitude, latitude, altitude, SLOPE,
				PRESSURE, TEMPERATURE, ATMOSPHERIC_REFRACTION, DELTA_T,
				AZIMUTH_ROTATION);

		spaData.initialize();

		return spaData.getAzimuth();
	}

	public Double getSunriseTime(ScansunDay day) {

		int hour = 0;
		int minute = 0;
		int second = 0;
		double timezone = 0.0;

		this.spaData = new ScansunSpaData(day.getYear(), day.getMonth() + 1,
				day.getDayOfMonth(), hour, minute, second, timezone, longitude,
				latitude, altitude, SLOPE, PRESSURE, TEMPERATURE,
				ATMOSPHERIC_REFRACTION, DELTA_T, AZIMUTH_ROTATION);

		spaData.initialize();

		return spaData.getSunriseTime();
	}

	public Double getSunriseAzimuth(ScansunDay day) {

		double timezone = 0.0;

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

			this.spaData = new ScansunSpaData(day.getYear(),
					day.getMonth() + 1, day.getDayOfMonth(), hour, minute,
					second, timezone, longitude, latitude, altitude, SLOPE,
					PRESSURE, TEMPERATURE, ATMOSPHERIC_REFRACTION, DELTA_T,
					AZIMUTH_ROTATION);
			spaData.initialize();

			cal.add(Calendar.MINUTE, 1);
		} while (spaData.getElevation() < 0.0);

		return spaData.getAzimuth();
	}

	public Double getSunsetTime(ScansunDay day) {

		int hour = 12; // arbitrary
		int minute = 0;
		int second = 0;
		double timezone = 0.0;

		this.spaData = new ScansunSpaData(day.getYear(), day.getMonth() + 1,
				day.getDayOfMonth(), hour, minute, second, timezone, longitude,
				latitude, altitude, SLOPE, PRESSURE, TEMPERATURE,
				ATMOSPHERIC_REFRACTION, DELTA_T, AZIMUTH_ROTATION);

		spaData.initialize();

		return spaData.getSunsetTime();
	}

	public Double getSunsetAzimuth(ScansunDay day) {

		double timezone = 0.0;

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

			this.spaData = new ScansunSpaData(day.getYear(),
					day.getMonth() + 1, day.getDayOfMonth(), hour, minute,
					second, timezone, longitude, latitude, altitude, SLOPE,
					PRESSURE, TEMPERATURE, ATMOSPHERIC_REFRACTION, DELTA_T,
					AZIMUTH_ROTATION);
			spaData.initialize();

			cal.add(Calendar.MINUTE, -1);
		} while (spaData.getElevation() < 0.0);

		return spaData.getAzimuth();
	}

}