/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.spa;

import org.joda.time.DateTime;

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
public class ScansunSolarPositionAlgorithmParameters {

	private static Log log = LogManager.getLogger();

	private int year;// 4-digit year
	private static final int YEAR_MIN = -2000;
	private static final int YEAR_MAX = 6000;

	private int month; // 2-digit month
	private static final int MONTH_MIN = 1;
	private static final int MONTH_MAX = 12;

	private int dayOfMonth; // 2-digit dayOfMonth
	private static final int DAY_MIN = 1;
	private static final int DAY_MAX = 31;

	private int hourOfDay;// Observer local hourOfDay
	private static final int HOUR_MIN = 0;
	private static final int HOUR_MAX = 24;

	private int minute;// Observer local minute
	private static final int MINUTE_MIN = 0;
	private static final int MINUTE_MAX = 59;

	private int second;// Observer local second
	private static final int SECOND_MIN = 0;
	private static final int SECOND_MAX = 59;

	private double timezone; // Observer time zone (negative west of Greenwich)
	private static final double TIMEZONE_RANGE = 18.0;

	private double longitude;// Observer longitude (negative west of Greenwich)
	private static final double LONGITUDE_RANGE = 180.0;

	private double latitude;// Observer latitude (negative south of equator)
	private static final double LATITUDE_RANGE = 90.0;

	private double altitude;// Observer elevation [meters]
	private static final double ALTITUDE_MIN = -6500000.0;

	private double slope;// Surface slope (measured from the horizontal plane)
	private static final double SLOPE_RANGE = 360.0;

	private double pressure;// Annual average local pressure [millibars]
	private static final double PRESSURE_MIN = 0.0;
	private static final double PRESSURE_MAX = 5000.0;

	private double temperature;// Annual average local temperature [Celsius]
	private static final double TEMPERATURE_MIN = -273.0;
	private static final double TEMPERATURE_MAX = 6000.0;

	private double atmosphericRefraction;// Atmospheric refraction at sunrise
											// and sunset (0.5667 deg is
											// typical)
	private static final double ATMOSPHERIC_REFRACTION_RANGE = 5.0;

	/*
	 * Difference between earth rotation time and terrestrial time. It is
	 * derived from observation only and is reported in this bulletin:
	 * http://maia.usno.navy.mil/ser7/ser7.dat, where delta_t = 32.184 +
	 * (TAI-UTC) + DUT1
	 */
	private double deltaT;
	private static final double DELTA_T_RANGE = 8000.0;

	/*
	 * Surface azimuth rotation (measured from south to projection of surface
	 * normal on horizontal plane, negative west)
	 */
	private double azimuthRotation;
	private static final double AZIMUTH_ROTATION_RANGE = 360.0;

	public void setYear(int year) {
		this.year = year;
	}

	public int getYear() {
		return year;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getMonth() {
		return month;
	}

	public void setDayOfMonth(int day) {
		this.dayOfMonth = day;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setHourOfDay(int hour) {
		this.hourOfDay = hour;
	}

	public int getHourOfDay() {
		return hourOfDay;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getMinute() {
		return minute;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getSecond() {
		return second;
	}

	public void setTimezone(double timezone) {
		this.timezone = timezone;
	}

	public double getTimezone() {
		return timezone;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	public double getSlope() {
		return slope;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public double getPressure() {
		return pressure;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setAtmosphericRefraction(double atmosphericRefraction) {
		this.atmosphericRefraction = atmosphericRefraction;
	}

	public double getAtmosphericRefraction() {
		return atmosphericRefraction;
	}

	public void setDeltaT(double deltaT) {
		this.deltaT = deltaT;
	}

	public double getDeltaT() {
		return deltaT;
	}

	public void setAzimuthRotation(double azimuthRotation) {
		this.azimuthRotation = azimuthRotation;
	}

	public double getAzimuthRotation() {
		return azimuthRotation;
	}

	public ScansunSolarPositionAlgorithmParameters() {
	}

	public ScansunSolarPositionAlgorithmParameters(
			ScansunSolarPositionAlgorithmParameters source) {
		this.year = source.year;
		this.month = source.month;
		this.dayOfMonth = source.dayOfMonth;
		this.hourOfDay = source.hourOfDay;
		this.minute = source.minute;
		this.second = source.second;
		this.timezone = source.timezone;

		this.longitude = source.longitude;
		this.latitude = source.latitude;
		this.altitude = source.altitude;
		this.slope = source.slope;

		this.pressure = source.pressure;
		this.temperature = source.temperature;
		this.atmosphericRefraction = source.atmosphericRefraction;
		this.deltaT = source.deltaT;

		this.azimuthRotation = source.azimuthRotation;
	}

	public void setDateTime(DateTime dateTime) {
		this.year = dateTime.getYear();
		this.month = dateTime.getMonthOfYear(); // +1
		this.dayOfMonth = dateTime.getDayOfMonth();
		this.hourOfDay = dateTime.getHourOfDay();
		this.minute = dateTime.getMinuteOfHour();
		this.second = dateTime.getSecondOfMinute();
		this.timezone = 0.0; // UTC
	}

	public boolean isValid() {

		if ((year < YEAR_MIN) || (year > YEAR_MAX)) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - year is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if ((month < MONTH_MIN) || (month > MONTH_MAX)) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - month is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if ((dayOfMonth < DAY_MIN) || (dayOfMonth > DAY_MAX)) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - dayOfMonth is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if ((hourOfDay < HOUR_MIN) || (hourOfDay > HOUR_MAX)) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - hourOfDay is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if ((minute < MINUTE_MIN) || (minute > MINUTE_MAX)) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - minute is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if ((second < SECOND_MIN) || (second > SECOND_MAX)) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - second is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if (Math.abs(timezone) > TIMEZONE_RANGE) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - timezone is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if (Math.abs(longitude) > LONGITUDE_RANGE) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - longitude is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);

			return false;
		}

		if (Math.abs(latitude) > LATITUDE_RANGE) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - latitude is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if (altitude < ALTITUDE_MIN) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - altitude is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if ((pressure < PRESSURE_MIN) || (pressure > PRESSURE_MAX)) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - pressure is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if ((temperature <= TEMPERATURE_MIN) || (temperature > TEMPERATURE_MAX)) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - temperature is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if (Math.abs(slope) > SLOPE_RANGE) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - slope is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if (Math.abs(atmosphericRefraction) > ATMOSPHERIC_REFRACTION_RANGE) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - atmospheric refraction is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if (Math.abs(deltaT) > DELTA_T_RANGE) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - latitude is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		if (Math.abs(azimuthRotation) > AZIMUTH_ROTATION_RANGE) {
			log.printMsg(
					"SCANSUN: Solar Position Algorithm - azimuth rotation is not valid",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		}

		return true;
	}

}
