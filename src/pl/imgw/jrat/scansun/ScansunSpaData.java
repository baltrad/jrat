/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

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
public class ScansunSpaData {

	/*
	 * 4-digit year, valid range: -2000 to 6000
	 */
	private int year;

	/*
	 * 2-digit month, valid range: 1 to 12
	 */
	private int month;

	/*
	 * 2-digit day, valid range: 1 to 31
	 */
	private int day;

	/*
	 * Observer local hour, valid range: 0 to 24, error code:
	 */
	private int hour;

	/*
	 * Observer local minute, valid range: 0 to 59
	 */
	private int minute;

	/*
	 * Observer local second, valid range: 0 to 59
	 */
	private int second;

	/*
	 * Observer time zone (negative west of Greenwich), valid range: -18 to 18
	 * hours
	 */
	private double timezone;

	/*
	 * Observer longitude (negative west of Greenwich), valid range: -180 to 180
	 * degrees
	 */
	private double longitude;

	/*
	 * Observer latitude (negative south of equator), valid range: -90 to 90
	 * degrees
	 */
	private double latitude;

	/*
	 * Observer elevation [meters], valid range: -6500000 or higher meters
	 */
	private double altitude;

	/*
	 * Surface slope (measured from the horizontal plane), valid range: -360 to
	 * 360 degrees
	 */
	private double slope;

	/*
	 * Annual average local pressure [millibars], valid range: 0 to 5000
	 * millibars
	 */
	private double pressure;

	/*
	 * Annual average local temperature [degrees Celsius], valid range: -273 to
	 * 6000 degrees Celsius
	 */
	private double temperature;

	/*
	 * Atmospheric refraction at sunrise and sunset (0.5667 deg is typical),
	 * valid range: -5 to 5 degrees
	 */
	private double atmosphericRefraction;

	/*
	 * Difference between earth rotation time and terrestrial time. It is
	 * derived from observation only and is reported in this bulletin:
	 * http://maia.usno.navy.mil/ser7/ser7.dat, where delta_t = 32.184 +
	 * (TAI-UTC) + DUT1, valid range: -8000 to 8000 seconds
	 */
	private double deltaT;

	/*
	 * Surface azimuth rotation (measured from south to projection of surface
	 * normal on horizontal plane, negative west), valid range: -360 to 360
	 * degrees
	 */
	private double azimuthRotation;

	public ScansunSpaData(int year, int month, int day, int hour, int minute,
			int second, double timezone, double longitude, double latitude,
			double altitude, double slope, double pressure, double temperature,
			double atmosphericRefraction, double deltaT, double azimuthRotation) {

		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.timezone = timezone;

		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.slope = slope;

		this.pressure = pressure;
		this.temperature = temperature;
		this.atmosphericRefraction = atmosphericRefraction;
		this.deltaT = deltaT;

		this.azimuthRotation = azimuthRotation;
	}

	/*
	 * Julian day
	 */
	private double jd;

	/*
	 * Julian century
	 */
	private double jc;

	/*
	 * Julian ephemeris day
	 */
	private double jde;

	/*
	 * Julian ephemeris century
	 */
	private double jce;

	/*
	 * Julian ephemeris millennium
	 */
	private double jme;

	/*
	 * earth heliocentric longitude [degrees]
	 */
	private double l;

	/*
	 * earth heliocentric latitude [degrees]
	 */
	private double b;

	/*
	 * earth radius vector [Astronomical Units, AU]
	 */
	private double r;

	/*
	 * geocentric longitude [degrees]
	 */
	private double theta;

	/*
	 * geocentric latitude [degrees]
	 */
	private double beta;

	/*
	 * mean elongation (moon-sun) [degrees]
	 */
	private double x0;

	/*
	 * mean anomaly (sun) [degrees]
	 */
	private double x1;

	/*
	 * mean anomaly (moon) [degrees]
	 */
	private double x2;

	/*
	 * argument latitude (moon) [degrees]
	 */
	private double x3;

	/*
	 * ascending longitude (moon) [degrees]
	 */
	private double x4;

	/*
	 * nutation longitude [degrees]
	 */
	private double deltaPsi;

	/*
	 * nutation obliquity [degrees]
	 */
	private double deltaEpsilon;

	/*
	 * ecliptic mean obliquity [arc seconds]
	 */
	private double epsilon0;

	/*
	 * ecliptic true obliquity [degrees]
	 */
	private double epsilon;

	/*
	 * aberration correction [degrees]
	 */
	private double deltaTau;

	/*
	 * apparent sun longitude [degrees]
	 */
	private double lamda;

	/*
	 * Greenwich mean sidereal time [degrees]
	 */
	private double nu0;

	/*
	 * Greenwich sidereal time [degrees]
	 */
	private double nu;

	/*
	 * geocentric sun right ascension [degrees]
	 */
	private double alpha;

	/*
	 * geocentric sun declination [degrees]
	 */
	private double delta;

	/*
	 * observer hour angle [degrees]
	 */
	private double h;

	/*
	 * sun equatorial horizontal parallax [degrees]
	 */
	private double xi;

	/*
	 * sun right ascension parallax [degrees]
	 */
	private double deltaAlpha;

	/*
	 * topocentric sun declination [degrees]
	 */
	private double deltaPrime;

	/*
	 * topocentric sun right ascension [degrees]
	 */
	private double alphaPrime;

	/*
	 * topocentric local hour angle [degrees]
	 */
	private double hPrime;

	/*
	 * topocentric elevation angle (uncorrected) [degrees]
	 */
	private double e0;

	/*
	 * atmospheric refraction correction [degrees]
	 */
	private double deltaE;

	/*
	 * topocentric elevation angle (corrected) [degrees]
	 */
	private double e;

	/*
	 * equation of time [minutes]
	 */
	private double eot;

	/*
	 * sunrise hour angle [degrees]
	 */
	private double srha;

	/*
	 * sunset hour angle [degrees]
	 */
	private double ssha;

	/*
	 * sun transit altitude [degrees]
	 */
	private double sta;

	/*
	 * topocentric zenith angle [degrees]
	 */
	private double zenith;

	/*
	 * topocentric azimuth angle (westward from south) [-180 to 180 degrees]
	 */
	private double azimuth180;

	/*
	 * topocentric azimuth angle (eastward from north) [ 0 to 360 degrees]
	 */
	private double azimuth;

	/*
	 * surface incidence angle [degrees]
	 */
	private double incidence;

	/*
	 * local sun transit time (or solar noon) [fractional hour]
	 */
	private double suntransit;

	/*
	 * local sunrise time (+/- 30 seconds) [fractional hour]
	 */
	private double sunrise;

	/*
	 * local sunset time (+/- 30 seconds) [fractional hour]
	 */
	private double sunset;

	/*
	 * Copy constructor
	 */
	public ScansunSpaData(ScansunSpaData source) {
		this.year = source.year;
		this.month = source.month;
		this.day = source.day;
		this.hour = source.hour;
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

		this.jd = source.jd;
		this.jc = source.jc;
		this.jde = source.jde;
		this.jce = source.jce;
		this.jme = source.jme;

		this.l = source.l;
		this.b = source.b;
		this.r = source.r;

		this.theta = source.theta;
		this.beta = source.beta;

		this.x0 = source.x0;
		this.x1 = source.x1;
		this.x2 = source.x2;
		this.x3 = source.x3;
		this.x4 = source.x4;

		this.deltaPsi = source.deltaPsi;
		this.deltaEpsilon = source.deltaEpsilon;

		this.epsilon0 = source.epsilon0;
		this.epsilon = source.epsilon;

		this.deltaTau = source.deltaTau;
		this.lamda = source.lamda;
		this.nu0 = source.nu0;
		this.nu = source.nu;

		this.alpha = source.alpha;
		this.delta = source.delta;
		this.h = source.h;
		this.xi = source.xi;

		this.deltaAlpha = source.deltaAlpha;
		this.deltaPrime = source.deltaPrime;
		this.alphaPrime = source.alphaPrime;
		this.hPrime = source.hPrime;

		this.e0 = source.e0;
		this.deltaE = source.deltaE;

		this.e = source.e;

		this.eot = source.eot;
		this.srha = source.srha;
		this.ssha = source.ssha;
		this.sta = source.sta;

		this.zenith = source.zenith;
		this.azimuth180 = source.azimuth180;
		this.azimuth = source.azimuth;
		this.incidence = source.incidence;
		this.suntransit = source.suntransit;
		this.sunrise = source.sunrise;
		this.sunset = source.sunset;
	}

	/*
	 * Initializes all intermediate fields
	 * 
	 * return void
	 */
	public void initialize() {

		if (isValid()) {

			julianDay();
			calculateGeocentricSunRightAscensionAndDeclination();

			observerHourAngle();
			sunEquatorialHorizontalParallax();

			sunRightAscensionParallaxAndTopocentricDec();

			topocentricSunRightAscension();
			topocentricLocalHourAngle();

			topocentricElevationAngle();
			atmosphericRefractionCorrection();
			topocentricElevationAngleCorrected();

			topocentricZenithAngle();
			topocentricAzimuthAngleNeg180Pos180();
			topocentricAzimuthAngleZero360();

			surfaceIncidenceAngle();

			calculateEotAndSunRiseTransitSet();
		}
	}

	/*
	 * topocentric elevation angle [degrees]
	 * 
	 * return double
	 */
	public double getElevation() {
		return e;
	}

	/*
	 * topocentric azimuth angle (eastward from north) [0 to 360 degrees]
	 * 
	 * return double
	 */
	public double getAzimuth() {
		return azimuth;
	}

	/*
	 * local sunrise time (+/- 30 seconds)
	 * 
	 * return double
	 */
	public double getSunriseTime() {
		return sunrise;
	}

	/*
	 * local sunset time (+/- 30 seconds)
	 * 
	 * return double
	 */
	public double getSunsetTime() {
		return sunset;
	}

	private static final double SUN_RADIUS = 0.26667;

	private enum TERM {
		TERM_A, TERM_B, TERM_C, TERM_COUNT;
	}

	private enum TERM_X {
		TERM_X0, TERM_X1, TERM_X2, TERM_X3, TERM_X4, TERM_X_COUNT;
	}

	private enum TERM_PSI {
		TERM_PSI_A, TERM_PSI_B, TERM_EPS_C, TERM_EPS_D, TERM_PE_COUNT;
	}

	private enum JD {
		JD_MINUS, JD_ZERO, JD_PLUS, JD_COUNT;
	}

	private enum SUN {
		SUN_TRANSIT, SUN_RISE, SUN_SET, SUN_COUNT;
	}

	private static final int TERM_Y_COUNT = TERM_X.TERM_X_COUNT.ordinal();

	private static final int[] L_SUBCOUNT = { 64, 34, 20, 7, 3, 1 };
	private static final int[] B_SUBCOUNT = { 5, 2 };
	private static final int[] R_SUBCOUNT = { 40, 10, 6, 2, 1 };

	/*
	 * Earth Periodic Terms
	 */
	private static final double[][][] L_TERMS = {
			{ { 175347046.0, 0, 0 }, { 3341656.0, 4.6692568, 6283.07585 },
					{ 34894.0, 4.6261, 12566.1517 },
					{ 3497.0, 2.7441, 5753.3849 }, { 3418.0, 2.8289, 3.5231 },
					{ 3136.0, 3.6277, 77713.7715 },
					{ 2676.0, 4.4181, 7860.4194 },
					{ 2343.0, 6.1352, 3930.2097 },
					{ 1324.0, 0.7425, 11506.7698 },
					{ 1273.0, 2.0371, 529.691 }, { 1199.0, 1.1096, 1577.3435 },
					{ 990, 5.233, 5884.927 }, { 902, 2.045, 26.298 },
					{ 857, 3.508, 398.149 }, { 780, 1.179, 5223.694 },
					{ 753, 2.533, 5507.553 }, { 505, 4.583, 18849.228 },
					{ 492, 4.205, 775.523 }, { 357, 2.92, 0.067 },
					{ 317, 5.849, 11790.629 }, { 284, 1.899, 796.298 },
					{ 271, 0.315, 10977.079 }, { 243, 0.345, 5486.778 },
					{ 206, 4.806, 2544.314 }, { 205, 1.869, 5573.143 },
					{ 202, 2.458, 6069.777 }, { 156, 0.833, 213.299 },
					{ 132, 3.411, 2942.463 }, { 126, 1.083, 20.775 },
					{ 115, 0.645, 0.98 }, { 103, 0.636, 4694.003 },
					{ 102, 0.976, 15720.839 }, { 102, 4.267, 7.114 },
					{ 99, 6.21, 2146.17 }, { 98, 0.68, 155.42 },
					{ 86, 5.98, 161000.69 }, { 85, 1.3, 6275.96 },
					{ 85, 3.67, 71430.7 }, { 80, 1.81, 17260.15 },
					{ 79, 3.04, 12036.46 }, { 75, 1.76, 5088.63 },
					{ 74, 3.5, 3154.69 }, { 74, 4.68, 801.82 },
					{ 70, 0.83, 9437.76 }, { 62, 3.98, 8827.39 },
					{ 61, 1.82, 7084.9 }, { 57, 2.78, 6286.6 },
					{ 56, 4.39, 14143.5 }, { 56, 3.47, 6279.55 },
					{ 52, 0.19, 12139.55 }, { 52, 1.33, 1748.02 },
					{ 51, 0.28, 5856.48 }, { 49, 0.49, 1194.45 },
					{ 41, 5.37, 8429.24 }, { 41, 2.4, 19651.05 },
					{ 39, 6.17, 10447.39 }, { 37, 6.04, 10213.29 },
					{ 37, 2.57, 1059.38 }, { 36, 1.71, 2352.87 },
					{ 36, 1.78, 6812.77 }, { 33, 0.59, 17789.85 },
					{ 30, 0.44, 83996.85 }, { 30, 2.74, 1349.87 },
					{ 25, 3.16, 4690.48 } },
			{ { 628331966747.0, 0, 0 }, { 206059.0, 2.678235, 6283.07585 },
					{ 4303.0, 2.6351, 12566.1517 }, { 425.0, 1.59, 3.523 },
					{ 119.0, 5.796, 26.298 }, { 109.0, 2.966, 1577.344 },
					{ 93, 2.59, 18849.23 }, { 72, 1.14, 529.69 },
					{ 68, 1.87, 398.15 }, { 67, 4.41, 5507.55 },
					{ 59, 2.89, 5223.69 }, { 56, 2.17, 155.42 },
					{ 45, 0.4, 796.3 }, { 36, 0.47, 775.52 },
					{ 29, 2.65, 7.11 }, { 21, 5.34, 0.98 },
					{ 19, 1.85, 5486.78 }, { 19, 4.97, 213.3 },
					{ 17, 2.99, 6275.96 }, { 16, 0.03, 2544.31 },
					{ 16, 1.43, 2146.17 }, { 15, 1.21, 10977.08 },
					{ 12, 2.83, 1748.02 }, { 12, 3.26, 5088.63 },
					{ 12, 5.27, 1194.45 }, { 12, 2.08, 4694 },
					{ 11, 0.77, 553.57 }, { 10, 1.3, 6286.6 },
					{ 10, 4.24, 1349.87 }, { 9, 2.7, 242.73 },
					{ 9, 5.64, 951.72 }, { 8, 5.3, 2352.87 },
					{ 6, 2.65, 9437.76 }, { 6, 4.67, 4690.48 } },
			{ { 52919.0, 0, 0 }, { 8720.0, 1.0721, 6283.0758 },
					{ 309.0, 0.867, 12566.152 }, { 27, 0.05, 3.52 },
					{ 16, 5.19, 26.3 }, { 16, 3.68, 155.42 },
					{ 10, 0.76, 18849.23 }, { 9, 2.06, 77713.77 },
					{ 7, 0.83, 775.52 }, { 5, 4.66, 1577.34 },
					{ 4, 1.03, 7.11 }, { 4, 3.44, 5573.14 },
					{ 3, 5.14, 796.3 }, { 3, 6.05, 5507.55 },
					{ 3, 1.19, 242.73 }, { 3, 6.12, 529.69 },
					{ 3, 0.31, 398.15 }, { 3, 2.28, 553.57 },
					{ 2, 4.38, 5223.69 }, { 2, 3.75, 0.98 } },
			{ { 289.0, 5.844, 6283.076 }, { 35, 0, 0 }, { 17, 5.49, 12566.15 },
					{ 3, 5.2, 155.42 }, { 1, 4.72, 3.52 },
					{ 1, 5.3, 18849.23 }, { 1, 5.97, 242.73 } },
			{ { 114.0, 3.142, 0 }, { 8, 4.13, 6283.08 }, { 1, 3.84, 12566.15 } },
			{ { 1, 3.14, 0 } } };

	private static final double[][][] B_TERMS = {
			{ { 280.0, 3.199, 84334.662 }, { 102.0, 5.422, 5507.553 },
					{ 80, 3.88, 5223.69 }, { 44, 3.7, 2352.87 },
					{ 32, 4, 1577.34 } },
			{ { 9, 3.9, 5507.55 }, { 6, 1.73, 5223.69 } } };

	private static final double[][][] R_TERMS = {
			{ { 100013989.0, 0, 0 }, { 1670700.0, 3.0984635, 6283.07585 },
					{ 13956.0, 3.05525, 12566.1517 },
					{ 3084.0, 5.1985, 77713.7715 },
					{ 1628.0, 1.1739, 5753.3849 },
					{ 1576.0, 2.8469, 7860.4194 }, { 925.0, 5.453, 11506.77 },
					{ 542.0, 4.564, 3930.21 }, { 472.0, 3.661, 5884.927 },
					{ 346.0, 0.964, 5507.553 }, { 329.0, 5.9, 5223.694 },
					{ 307.0, 0.299, 5573.143 }, { 243.0, 4.273, 11790.629 },
					{ 212.0, 5.847, 1577.344 }, { 186.0, 5.022, 10977.079 },
					{ 175.0, 3.012, 18849.228 }, { 110.0, 5.055, 5486.778 },
					{ 98, 0.89, 6069.78 }, { 86, 5.69, 15720.84 },
					{ 86, 1.27, 161000.69 }, { 65, 0.27, 17260.15 },
					{ 63, 0.92, 529.69 }, { 57, 2.01, 83996.85 },
					{ 56, 5.24, 71430.7 }, { 49, 3.25, 2544.31 },
					{ 47, 2.58, 775.52 }, { 45, 5.54, 9437.76 },
					{ 43, 6.01, 6275.96 }, { 39, 5.36, 4694 },
					{ 38, 2.39, 8827.39 }, { 37, 0.83, 19651.05 },
					{ 37, 4.9, 12139.55 }, { 36, 1.67, 12036.46 },
					{ 35, 1.84, 2942.46 }, { 33, 0.24, 7084.9 },
					{ 32, 0.18, 5088.63 }, { 32, 1.78, 398.15 },
					{ 28, 1.21, 6286.6 }, { 28, 1.9, 6279.55 },
					{ 26, 4.59, 10447.39 } },
			{ { 103019.0, 1.10749, 6283.07585 },
					{ 1721.0, 1.0644, 12566.1517 }, { 702.0, 3.142, 0 },
					{ 32, 1.02, 18849.23 }, { 31, 2.84, 5507.55 },
					{ 25, 1.32, 5223.69 }, { 18, 1.42, 1577.34 },
					{ 10, 5.91, 10977.08 }, { 9, 1.42, 6275.96 },
					{ 9, 0.27, 5486.78 } },
			{ { 4359.0, 5.7846, 6283.0758 }, { 124.0, 5.579, 12566.152 },
					{ 12, 3.14, 0 }, { 9, 3.63, 77713.77 },
					{ 6, 1.87, 5573.14 }, { 3, 5.47, 18849.23 } },
			{ { 145.0, 4.273, 6283.076 }, { 7, 3.92, 12566.15 } },
			{ { 4, 2.56, 6283.08 } } };

	/*
	 * Periodic Terms for the nutation in longitude and obliquity
	 */
	private static final int[][] Y_TERMS = { { 0, 0, 0, 0, 1 },
			{ -2, 0, 0, 2, 2 }, { 0, 0, 0, 2, 2 }, { 0, 0, 0, 0, 2 },
			{ 0, 1, 0, 0, 0 }, { 0, 0, 1, 0, 0 }, { -2, 1, 0, 2, 2 },
			{ 0, 0, 0, 2, 1 }, { 0, 0, 1, 2, 2 }, { -2, -1, 0, 2, 2 },
			{ -2, 0, 1, 0, 0 }, { -2, 0, 0, 2, 1 }, { 0, 0, -1, 2, 2 },
			{ 2, 0, 0, 0, 0 }, { 0, 0, 1, 0, 1 }, { 2, 0, -1, 2, 2 },
			{ 0, 0, -1, 0, 1 }, { 0, 0, 1, 2, 1 }, { -2, 0, 2, 0, 0 },
			{ 0, 0, -2, 2, 1 }, { 2, 0, 0, 2, 2 }, { 0, 0, 2, 2, 2 },
			{ 0, 0, 2, 0, 0 }, { -2, 0, 1, 2, 2 }, { 0, 0, 0, 2, 0 },
			{ -2, 0, 0, 2, 0 }, { 0, 0, -1, 2, 1 }, { 0, 2, 0, 0, 0 },
			{ 2, 0, -1, 0, 1 }, { -2, 2, 0, 2, 2 }, { 0, 1, 0, 0, 1 },
			{ -2, 0, 1, 0, 1 }, { 0, -1, 0, 0, 1 }, { 0, 0, 2, -2, 0 },
			{ 2, 0, -1, 2, 1 }, { 2, 0, 1, 2, 2 }, { 0, 1, 0, 2, 2 },
			{ -2, 1, 1, 0, 0 }, { 0, -1, 0, 2, 2 }, { 2, 0, 0, 2, 1 },
			{ 2, 0, 1, 0, 0 }, { -2, 0, 2, 2, 2 }, { -2, 0, 1, 2, 1 },
			{ 2, 0, -2, 0, 1 }, { 2, 0, 0, 0, 1 }, { 0, -1, 1, 0, 0 },
			{ -2, -1, 0, 2, 1 }, { -2, 0, 0, 0, 1 }, { 0, 0, 2, 2, 1 },
			{ -2, 0, 2, 0, 1 }, { -2, 1, 0, 2, 1 }, { 0, 0, 1, -2, 0 },
			{ -1, 0, 1, 0, 0 }, { -2, 1, 0, 0, 0 }, { 1, 0, 0, 0, 0 },
			{ 0, 0, 1, 2, 0 }, { 0, 0, -2, 2, 2 }, { -1, -1, 1, 0, 0 },
			{ 0, 1, 1, 0, 0 }, { 0, -1, 1, 2, 2 }, { 2, -1, -1, 2, 2 },
			{ 0, 0, 3, 2, 2 }, { 2, -1, 0, 2, 2 }, };

	private static final double[][] PE_TERMS = {
			{ -171996, -174.2, 92025, 8.9 }, { -13187, -1.6, 5736, -3.1 },
			{ -2274, -0.2, 977, -0.5 }, { 2062, 0.2, -895, 0.5 },
			{ 1426, -3.4, 54, -0.1 }, { 712, 0.1, -7, 0 },
			{ -517, 1.2, 224, -0.6 }, { -386, -0.4, 200, 0 },
			{ -301, 0, 129, -0.1 }, { 217, -0.5, -95, 0.3 }, { -158, 0, 0, 0 },
			{ 129, 0.1, -70, 0 }, { 123, 0, -53, 0 }, { 63, 0, 0, 0 },
			{ 63, 0.1, -33, 0 }, { -59, 0, 26, 0 }, { -58, -0.1, 32, 0 },
			{ -51, 0, 27, 0 }, { 48, 0, 0, 0 }, { 46, 0, -24, 0 },
			{ -38, 0, 16, 0 }, { -31, 0, 13, 0 }, { 29, 0, 0, 0 },
			{ 29, 0, -12, 0 }, { 26, 0, 0, 0 }, { -22, 0, 0, 0 },
			{ 21, 0, -10, 0 }, { 17, -0.1, 0, 0 }, { 16, 0, -8, 0 },
			{ -16, 0.1, 7, 0 }, { -15, 0, 9, 0 }, { -13, 0, 7, 0 },
			{ -12, 0, 6, 0 }, { 11, 0, 0, 0 }, { -10, 0, 5, 0 },
			{ -8, 0, 3, 0 }, { 7, 0, -3, 0 }, { -7, 0, 0, 0 }, { -7, 0, 3, 0 },
			{ -7, 0, 3, 0 }, { 6, 0, 0, 0 }, { 6, 0, -3, 0 }, { 6, 0, -3, 0 },
			{ -6, 0, 3, 0 }, { -6, 0, 3, 0 }, { 5, 0, 0, 0 }, { -5, 0, 3, 0 },
			{ -5, 0, 3, 0 }, { -5, 0, 3, 0 }, { 4, 0, 0, 0 }, { 4, 0, 0, 0 },
			{ 4, 0, 0, 0 }, { -4, 0, 0, 0 }, { -4, 0, 0, 0 }, { -4, 0, 0, 0 },
			{ 3, 0, 0, 0 }, { -3, 0, 0, 0 }, { -3, 0, 0, 0 }, { -3, 0, 0, 0 },
			{ -3, 0, 0, 0 }, { -3, 0, 0, 0 }, { -3, 0, 0, 0 }, { -3, 0, 0, 0 }, };

	/*
	 * limits degrees
	 * 
	 * return double
	 */
	private double limitDegrees(double degrees) {
		double limited;

		degrees /= 360.0;
		limited = 360.0 * (degrees - Math.floor(degrees));
		if (limited < 0)
			limited += 360.0;

		return limited;
	}

	/*
	 * limits degrees
	 * 
	 * return double
	 */
	private double limitDegrees180pm(double degrees) {
		double limited;

		degrees /= 360.0;
		limited = 360.0 * (degrees - Math.floor(degrees));
		if (limited < -180.0)
			limited += 360.0;
		else if (limited > 180.0)
			limited -= 360.0;

		return limited;
	}

	/*
	 * limits degrees
	 * 
	 * return double
	 */
	private double limitDegrees180(double degrees) {
		double limited;

		degrees /= 180.0;
		limited = 180.0 * (degrees - Math.floor(degrees));
		if (limited < 0)
			limited += 180.0;

		return limited;
	}

	/*
	 * limits value to 0 or 1
	 * 
	 * return double
	 */

	private double limitZero2one(double value) {
		double limited;

		limited = value - Math.floor(value);
		if (limited < 0)
			limited += 1.0;

		return limited;
	}

	/*
	 * limits minutes
	 * 
	 * return double
	 */
	private double limitMinutes(double minutes) {
		double limited = minutes;

		if (limited < -20.0)
			limited += 1440.0;
		else if (limited > 20.0)
			limited -= 1440.0;

		return limited;
	}

	/*
	 * calculates local hour
	 * 
	 * return double
	 */
	private double dayfracToLocalHr(double dayfrac, double timezone) {
		return 24.0 * limitZero2one(dayfrac + timezone / 24.0);
	}

	/*
	 * third order polynomial function
	 * 
	 * return double
	 */
	private double thirdOrderPolynomial(double a, double b, double c, double d,
			double x) {
		return ((a * x + b) * x + c) * x + d;
	}

	/*
	 * calculates julian day
	 * 
	 * return void
	 */
	private void julianDay() {
		double dayDecimal, julianDay, a;

		dayDecimal = day + (hour - timezone + (minute + second / 60.0) / 60.0)
				/ 24.0;

		if (month < 3) {
			month += 12;
			year--;
		}

		julianDay = Math.floor(365.25 * (year + 4716.0))
				+ Math.floor(30.6001 * (month + 1)) + dayDecimal - 1524.5;

		if (julianDay > 2299160.0) {
			a = Math.floor(year / 100);
			julianDay += (2 - a + Math.floor(a / 4));
		}

		jd = julianDay;
	}

	/*
	 * calculate julian century
	 * 
	 * return void
	 */
	private void julianCentury() {
		jc = (jd - 2451545.0) / 36525.0;
	}

	/*
	 * calculate julian ephemeris day
	 * 
	 * return void
	 */
	private void julianEphemerisDay() {
		jde = jd + deltaT / 86400.0;
	}

	/*
	 * calculate julian ephemeris century
	 * 
	 * return void
	 */
	private void julianEphemerisCentury() {
		jce = (jde - 2451545.0) / 36525.0;
	}

	/*
	 * calculate julian ephemeris millennium
	 * 
	 * return void
	 */
	private void julianEphemerisMillennium() {
		jme = jce / 10.0;
	}

	/*
	 * calculate earth periodic term summation
	 * 
	 * return earth periodic term summation as double
	 */
	private double earthPeriodicTermSummation(double[][] terms, int count) {
		int i;
		double sum = 0;

		for (i = 0; i < count; i++)

			sum += terms[i][TERM.TERM_A.ordinal()]
					* Math.cos(terms[i][TERM.TERM_B.ordinal()]
							+ terms[i][TERM.TERM_C.ordinal()] * jme);

		return sum;
	}

	/*
	 * calculates ?
	 * 
	 * return double
	 */
	private double earthValues(double[] termSum, int count) {
		int i;
		double sum = 0;

		for (i = 0; i < count; i++)
			sum += termSum[i] * Math.pow(jme, i);

		sum /= 1.0e8;

		return sum;
	}

	/*
	 * calculate earth heliocentric longitude
	 * 
	 * return void
	 */
	private void earthHeliocentricLongitude() {
		double[] sum = new double[L_SUBCOUNT.length];
		int i;

		for (i = 0; i < L_SUBCOUNT.length; i++)
			sum[i] = earthPeriodicTermSummation(L_TERMS[i], L_SUBCOUNT[i]);

		l = limitDegrees(Math.toDegrees(earthValues(sum, L_SUBCOUNT.length)));
	}

	/*
	 * calculates earth heliocentric latitude
	 * 
	 * return void
	 */
	private void earthHeliocentricLatitude() {
		double[] sum = new double[B_SUBCOUNT.length];
		int i;

		for (i = 0; i < B_SUBCOUNT.length; i++)
			sum[i] = earthPeriodicTermSummation(B_TERMS[i], B_SUBCOUNT[i]);

		b = Math.toDegrees(earthValues(sum, B_SUBCOUNT.length));
	}

	/*
	 * calculates earth radius vector
	 * 
	 * return void
	 */
	private void earthRadiusVector() {
		double[] sum = new double[R_SUBCOUNT.length];
		int i;

		for (i = 0; i < R_SUBCOUNT.length; i++)
			sum[i] = earthPeriodicTermSummation(R_TERMS[i], R_SUBCOUNT[i]);

		r = earthValues(sum, R_SUBCOUNT.length);
	}

	/*
	 * calculates geocentric longitude
	 * 
	 * return void
	 */
	private void geocentricLongitude() {
		theta = l + 180.0;

		if (theta >= 360.0)
			theta -= 360.0;
	}

	/*
	 * calculates geocentric latitude
	 * 
	 * return void
	 */
	private void geocentricLatitude() {
		beta = -b;
	}

	/*
	 * calculate mean elongation moon sun
	 * 
	 * return void
	 */
	private void meanElongationMoonSun() {
		x0 = thirdOrderPolynomial(1.0 / 189474.0, -0.0019142, 445267.11148,
				297.85036, jce);
	}

	/*
	 * calculate mean anomaly sun
	 * 
	 * return void
	 */
	private void meanAnomalySun() {
		x1 = thirdOrderPolynomial(-1.0 / 300000.0, -0.0001603, 35999.05034,
				357.52772, jce);
	}

	/*
	 * calculate mean anomaly moon
	 * 
	 * return void
	 */
	private void meanAnomalyMoon() {
		x2 = thirdOrderPolynomial(1.0 / 56250.0, 0.0086972, 477198.867398,
				134.96298, jce);
	}

	/*
	 * calculate argument latitude moon
	 * 
	 * return void
	 */
	private void argumentLatitudeMoon() {
		x3 = thirdOrderPolynomial(1.0 / 327270.0, -0.0036825, 483202.017538,
				93.27191, jce);
	}

	/*
	 * calculate ascending longitude moon
	 * 
	 * return void
	 */
	private void ascendingLongitudeMoon() {
		x4 = thirdOrderPolynomial(1.0 / 450000.0, 0.0020708, -1934.136261,
				125.04452, jce);
	}

	/*
	 * sums xy terms
	 * 
	 * return double
	 */
	private double xyTermSummation(int i, double[] x) {
		int j;
		double sum = 0;

		for (j = 0; j < TERM_Y_COUNT; j++)
			sum += x[j] * Y_TERMS[i][j];

		return sum;
	}

	/*
	 * calculate nutation longitude and obliquity
	 * 
	 * return void
	 */
	private void nutationLongitudeAndObliquity(double[] x) {
		int i;
		double xyTermSum, sumPsi = 0, sumEpsilon = 0;

		for (i = 0; i < Y_TERMS.length; i++) {
			xyTermSum = Math.toRadians(xyTermSummation(i, x));
			sumPsi += (PE_TERMS[i][TERM_PSI.TERM_PSI_A.ordinal()] + jce
					* PE_TERMS[i][TERM_PSI.TERM_PSI_B.ordinal()])
					* Math.sin(xyTermSum);
			sumEpsilon += (PE_TERMS[i][TERM_PSI.TERM_EPS_C.ordinal()] + jce
					* PE_TERMS[i][TERM_PSI.TERM_EPS_D.ordinal()])
					* Math.cos(xyTermSum);
		}

		deltaPsi = sumPsi / 36000000.0;
		deltaEpsilon = sumEpsilon / 36000000.0;
	}

	/*
	 * calculate ecliptic mean obliquity
	 * 
	 * return void
	 */
	private void eclipticMeanObliquity() {
		double u = jme / 10.0;

		epsilon0 = 84381.448
				+ u
				* (-4680.93 + u
						* (-1.55 + u
								* (1999.25 + u
										* (-51.38 + u
												* (-249.67 + u
														* (-39.05 + u
																* (7.12 + u
																		* (27.87 + u
																				* (5.79 + u * 2.45)))))))));
	}

	/*
	 * calculate ecliptic true obliquity
	 * 
	 * return void
	 */
	private void eclipticTrueObliquity() {
		epsilon = deltaEpsilon + epsilon0 / 3600.0;
	}

	/*
	 * calculate aberration correction
	 * 
	 * return void
	 */
	private void aberrationCorrection() {
		deltaTau = -20.4898 / (3600.0 * r);
	}

	/*
	 * calculate apparent sun longitude
	 * 
	 * return void
	 */
	private void apparentSunLongitude() {
		lamda = theta + deltaPsi + deltaTau;
	}

	/*
	 * calculate greenwich mean sidereal time
	 * 
	 * return void
	 */
	private void greenwichMeanSiderealTime() {
		nu0 = limitDegrees(280.46061837 + 360.98564736629 * (jd - 2451545.0)
				+ jc * jc * (0.000387933 - jc / 38710000.0));
	}

	/*
	 * calculate greenwich sidereal time
	 * 
	 * return void
	 */
	private void greenwichSiderealTime() {
		nu = nu0 + deltaPsi * Math.cos(Math.toRadians(epsilon));
	}

	/*
	 * calculate geocentric sun right ascension
	 * 
	 * return void
	 */
	private void geocentricSunRightAscension() {
		double lamdaRad = Math.toRadians(lamda);
		double epsilonRad = Math.toRadians(epsilon);

		alpha = limitDegrees(Math
				.toDegrees(Math.atan2(
						Math.sin(lamdaRad) * Math.cos(epsilonRad)
								- Math.tan(Math.toRadians(beta))
								* Math.sin(epsilonRad), Math.cos(lamdaRad))));
	}

	/*
	 * calculate geocentric sun declination
	 * 
	 * return void
	 */
	private void geocentricSunDeclination() {
		double betaRad = Math.toRadians(beta);
		double epsilonRad = Math.toRadians(epsilon);

		delta = Math.toDegrees(Math.asin(Math.sin(betaRad)
				* Math.cos(epsilonRad) + Math.cos(betaRad)
				* Math.sin(epsilonRad) * Math.sin(Math.toRadians(lamda))));
	}

	/*
	 * calculate observer hour angle
	 * 
	 * return void
	 */
	private void observerHourAngle() {
		h = limitDegrees(nu + longitude - alpha);
	}

	/*
	 * calculate sun equatorial horizontal parallax
	 * 
	 * return void
	 */
	private void sunEquatorialHorizontalParallax() {
		xi = 8.794 / (3600.0 * r);
	}

	/*
	 * calculate sun right ascension parallax and topocentric dec
	 * 
	 * return void
	 */
	private void sunRightAscensionParallaxAndTopocentricDec() {

		double deltaAlphaRad;
		double latitudeRad = Math.toRadians(latitude);
		double xiRad = Math.toRadians(xi);
		double hRad = Math.toRadians(h);
		double deltaRad = Math.toRadians(delta);
		double u = Math.atan(0.99664719 * Math.tan(latitudeRad));
		double y = 0.99664719 * Math.sin(u) + altitude * Math.sin(latitudeRad)
				/ 6378140.0;
		double x = Math.cos(u) + altitude * Math.cos(latitudeRad) / 6378140.0;

		deltaAlphaRad = Math.atan2(-x * Math.sin(xiRad) * Math.sin(hRad),
				Math.cos(deltaRad) - x * Math.sin(xiRad) * Math.cos(hRad));

		deltaPrime = Math.toDegrees(Math.atan2(
				(Math.sin(deltaRad) - y * Math.sin(xiRad))
						* Math.cos(deltaAlphaRad), Math.cos(deltaRad) - x
						* Math.sin(xiRad) * Math.cos(hRad)));

		deltaAlpha = Math.toDegrees(deltaAlphaRad);
	}

	/*
	 * calculate topocentric sun right ascension
	 * 
	 * return void
	 */
	private void topocentricSunRightAscension() {
		alphaPrime = alpha + deltaAlpha;
	}

	/*
	 * calculate topocentric local hour angle
	 * 
	 * return void
	 */
	private void topocentricLocalHourAngle() {
		hPrime = h - deltaAlpha;
	}

	/*
	 * calculate topocentric_elevation_angle
	 * 
	 * return void
	 */
	private void topocentricElevationAngle() {
		double latitudeRad = Math.toRadians(latitude);
		double deltaPrimeRad = Math.toRadians(deltaPrime);

		e0 = Math.toDegrees(Math.asin(Math.sin(latitudeRad)
				* Math.sin(deltaPrimeRad) + Math.cos(latitudeRad)
				* Math.cos(deltaPrimeRad) * Math.cos(Math.toRadians(hPrime))));
	}

	/*
	 * calculate atmospheric refraction correction
	 * 
	 * return void
	 */
	private void atmosphericRefractionCorrection() {
		deltaE = 0;

		if (e0 >= -1 * (SUN_RADIUS + atmosphericRefraction))
			deltaE = (pressure / 1010.0)
					* (283.0 / (273.0 + temperature))
					* 1.02
					/ (60.0 * Math.tan(Math.toRadians(e0 + 10.3 / (e0 + 5.11))));

	}

	/*
	 * calculate topocentric elevation angle corrected
	 * 
	 * return void
	 */
	private void topocentricElevationAngleCorrected() {
		e = e0 + deltaE;
	}

	/*
	 * calcluate topocentric zenith angle
	 * 
	 * return void
	 */
	private void topocentricZenithAngle() {
		zenith = 90.0 - e;
	}

	/*
	 * calculate topocentric azimuth angle (-180;180)
	 * 
	 * return void
	 */
	private void topocentricAzimuthAngleNeg180Pos180() {
		double hPrimeRad = Math.toRadians(hPrime);
		double latitudeRad = Math.toRadians(latitude);

		azimuth180 = Math.toDegrees(Math.atan2(
				Math.sin(hPrimeRad),
				Math.cos(hPrimeRad) * Math.sin(latitudeRad)
						- Math.tan(Math.toRadians(deltaPrime))
						* Math.cos(latitudeRad)));
	}

	/*
	 * calculate topocentric azimuth angle (0;360)
	 * 
	 * return void
	 */
	private void topocentricAzimuthAngleZero360() {
		azimuth = azimuth180 + 180.0;
	}

	/*
	 * calculate surface incidence angle
	 * 
	 * return void
	 */
	private void surfaceIncidenceAngle() {
		double zenithRad = Math.toRadians(zenith);
		double slopeRad = Math.toRadians(slope);

		incidence = Math.toDegrees(Math.acos(Math.cos(zenithRad)
				* Math.cos(slopeRad) + Math.sin(slopeRad) * Math.sin(zenithRad)
				* Math.cos(Math.toRadians(azimuth180 - azimuthRotation))));
	}

	/*
	 * calculate sun mean longitude
	 * 
	 * return double
	 */
	private double sunMeanLongitude() {
		return limitDegrees(280.4664567
				+ jme
				* (360007.6982779 + jme
						* (0.03032028 + jme
								* (1 / 49931.0 + jme
										* (-1 / 15300.0 + jme
												* (-1 / 2000000.0))))));
	}

	/*
	 * calculate equation of time
	 * 
	 * return void
	 */
	private void eot(double m) {
		eot = limitMinutes(4.0 * (m - 0.0057183 - alpha + deltaPsi
				* Math.cos(Math.toRadians(epsilon))));
	}

	/*
	 * calculate approx sun transit time
	 * 
	 * return double
	 */
	private double approxSunTransitTime(double alphaZero, double longitude,
			double nu) {
		return (alphaZero - longitude - nu) / 360.0;
	}

	/*
	 * calculate sun hour angle at rise set
	 * 
	 * return double
	 */
	private double sunHourAngleAtRiseSet(double latitude, double deltaZero,
			double h0prime) {
		double h0 = -99999;
		double latitudeRad = Math.toRadians(latitude);
		double deltaZeroRad = Math.toRadians(deltaZero);
		double argument = (Math.sin(Math.toRadians(h0prime)) - Math
				.sin(latitudeRad) * Math.sin(deltaZeroRad))
				/ (Math.cos(latitudeRad) * Math.cos(deltaZeroRad));

		if (Math.abs(argument) <= 1)
			h0 = limitDegrees180(Math.toDegrees(Math.acos(argument)));

		return h0;
	}

	/*
	 * calculate rts alpha delta prime
	 * 
	 * return double
	 */
	private double rtsAlphaDeltaPrime(double[] ad, double n) {
		double a = ad[JD.JD_ZERO.ordinal()] - ad[JD.JD_MINUS.ordinal()];
		double b = ad[JD.JD_PLUS.ordinal()] - ad[JD.JD_ZERO.ordinal()];

		if (Math.abs(a) >= 2.0)
			a = limitZero2one(a);
		if (Math.abs(b) >= 2.0)
			b = limitZero2one(b);

		return ad[JD.JD_ZERO.ordinal()] + n * (a + b + (b - a) * n) / 2.0;
	}

	/*
	 * calculate rts sun altitude
	 * 
	 * return double
	 */
	private double rtsSunAltitude(double latitude, double deltaPrime,
			double hPrime) {
		double latitudeRad = Math.toRadians(latitude);
		double deltaPrimeRad = Math.toRadians(deltaPrime);

		return Math.toDegrees(Math.asin(Math.sin(latitudeRad)
				* Math.sin(deltaPrimeRad) + Math.cos(latitudeRad)
				* Math.cos(deltaPrimeRad) * Math.cos(Math.toRadians(hPrime))));
	}

	/*
	 * calculate sun rise and set
	 * 
	 * return double
	 */
	private double sunRiseAndSet(double[] mRts, double[] hRts, double[] dPrime,
			double latitude, double[] hPrime, double h0prime, int sun) {
		return mRts[sun]
				+ (hRts[sun] - h0prime)
				/ (360.0 * Math.cos(Math.toRadians(dPrime[sun]))
						* Math.cos(Math.toRadians(latitude)) * Math.sin(Math
						.toRadians(hPrime[sun])));
	}

	/*
	 * alculate required SPA parameters to get the right ascension (alpha) and
	 * declination (delta)
	 * 
	 * return void
	 */
	private void calculateGeocentricSunRightAscensionAndDeclination() {

		double[] x = new double[TERM_X.TERM_X_COUNT.ordinal()];

		julianCentury();

		julianEphemerisDay();
		julianEphemerisCentury();
		julianEphemerisMillennium();

		earthHeliocentricLongitude();
		earthHeliocentricLatitude();
		earthRadiusVector();

		geocentricLongitude();
		geocentricLatitude();

		meanElongationMoonSun();
		meanAnomalySun();
		meanAnomalyMoon();
		argumentLatitudeMoon();
		ascendingLongitudeMoon();

		x[TERM_X.TERM_X0.ordinal()] = x0;
		x[TERM_X.TERM_X1.ordinal()] = x1;
		x[TERM_X.TERM_X2.ordinal()] = x2;
		x[TERM_X.TERM_X3.ordinal()] = x3;
		x[TERM_X.TERM_X4.ordinal()] = x4;

		nutationLongitudeAndObliquity(x);

		eclipticMeanObliquity();
		eclipticTrueObliquity();

		aberrationCorrection();
		apparentSunLongitude();
		greenwichMeanSiderealTime();
		greenwichSiderealTime();

		geocentricSunRightAscension();
		geocentricSunDeclination();
	}

	/*
	 * Calculate Equation of Time (EOT) and Sun Rise, Transit, & Set (RTS)
	 * 
	 * return void
	 */
	private void calculateEotAndSunRiseTransitSet() {

		double nu, m, h0, h0dfrac, n;
		double[] a = new double[JD.JD_COUNT.ordinal()]; // alpha
		double[] d = new double[JD.JD_COUNT.ordinal()]; // delta

		double[] mRts = new double[SUN.SUN_COUNT.ordinal()];
		double[] nuRts = new double[SUN.SUN_COUNT.ordinal()];
		double[] hRts = new double[SUN.SUN_COUNT.ordinal()];

		double[] aPrime = new double[SUN.SUN_COUNT.ordinal()];
		double[] dPrime = new double[SUN.SUN_COUNT.ordinal()];
		double[] hPrime = new double[SUN.SUN_COUNT.ordinal()];

		double h0prime = -1 * (SUN_RADIUS + atmosphericRefraction);
		int i;

		ScansunSpaData sunRts = new ScansunSpaData(this);
		// ScansunSpaData sunRts = this.clone();

		m = sunMeanLongitude();
		eot(m);

		sunRts.hour = 0;
		sunRts.minute = 0;
		sunRts.second = 0;
		sunRts.timezone = 0.0;

		sunRts.julianDay();
		sunRts.calculateGeocentricSunRightAscensionAndDeclination();

		nu = sunRts.nu;

		sunRts.deltaT = 0;
		sunRts.jd--;

		for (i = 0; i < JD.JD_COUNT.ordinal(); i++) {
			sunRts.calculateGeocentricSunRightAscensionAndDeclination();
			a[i] = sunRts.alpha;
			d[i] = sunRts.delta;
			sunRts.jd++;
		}

		mRts[SUN.SUN_TRANSIT.ordinal()] = approxSunTransitTime(
				a[JD.JD_ZERO.ordinal()], longitude, nu);
		h0 = sunHourAngleAtRiseSet(latitude, d[JD.JD_ZERO.ordinal()], h0prime);

		if (h0 >= 0) {

			// approx_sun_rise_and_set(m_rts, h0);
			h0dfrac = h0 / 360.0;

			mRts[SUN.SUN_RISE.ordinal()] = limitZero2one(mRts[SUN.SUN_TRANSIT
					.ordinal()] - h0dfrac);
			mRts[SUN.SUN_SET.ordinal()] = limitZero2one(mRts[SUN.SUN_TRANSIT
					.ordinal()] + h0dfrac);
			mRts[SUN.SUN_TRANSIT.ordinal()] = limitZero2one(mRts[SUN.SUN_TRANSIT
					.ordinal()]);

			for (i = 0; i < SUN.SUN_COUNT.ordinal(); i++) {

				nuRts[i] = nu + 360.985647 * mRts[i];

				n = mRts[i] + deltaT / 86400.0;
				aPrime[i] = rtsAlphaDeltaPrime(a, n);
				dPrime[i] = rtsAlphaDeltaPrime(d, n);

				hPrime[i] = limitDegrees180pm(nuRts[i] + longitude - aPrime[i]);

				hRts[i] = rtsSunAltitude(latitude, dPrime[i], hPrime[i]);
			}

			srha = hPrime[SUN.SUN_RISE.ordinal()];
			ssha = hPrime[SUN.SUN_SET.ordinal()];
			sta = hRts[SUN.SUN_TRANSIT.ordinal()];

			suntransit = dayfracToLocalHr(mRts[SUN.SUN_TRANSIT.ordinal()]
					- hPrime[SUN.SUN_TRANSIT.ordinal()] / 360.0, timezone);

			sunrise = dayfracToLocalHr(
					sunRiseAndSet(mRts, hRts, dPrime, latitude, hPrime,
							h0prime, SUN.SUN_RISE.ordinal()), timezone);

			sunset = dayfracToLocalHr(
					sunRiseAndSet(mRts, hRts, dPrime, latitude, hPrime,
							h0prime, SUN.SUN_SET.ordinal()), timezone);

		} else {
			srha = -99999;
			ssha = -99999;
			sta = -99999;

			suntransit = -99999;
			sunrise = -99999;
			sunset = -99999;
		}

	}

	/*
	 * 
	 */
	public boolean isValid() {

		if ((year < -2000) || (year > 6000)) {
			LogHandler.getLogs().displayMsg("Year not valid", ERROR);
			return false;
		}
		if ((month < 1) || (month > 12)) {
			LogHandler.getLogs().displayMsg("Month not valid", ERROR);
			return false;
		}
		if ((day < 1) || (day > 31)) {
			LogHandler.getLogs().displayMsg("Day not valid", ERROR);
			return false;
		}
		if ((hour < 0) || (hour > 24)) {
			LogHandler.getLogs().displayMsg("Hour not valid", ERROR);
			return false;
		}
		if ((minute < 0) || (minute > 59)) {
			LogHandler.getLogs().displayMsg("Minute not valid", ERROR);
			return false;
		}
		if ((second < 0) || (second > 59)) {
			LogHandler.getLogs().displayMsg("Second not valid", ERROR);
			return false;
		}
		if (Math.abs(timezone) > 18.0) {
			LogHandler.getLogs().displayMsg("Timezone not valid", ERROR);
			return false;
		}

		if (Math.abs(longitude) > 180.0) {
			LogHandler.getLogs().displayMsg("Longitude not valid", ERROR);
			return false;
		}
		if (Math.abs(latitude) > 90.0) {
			LogHandler.getLogs().displayMsg("Latitude not valid", ERROR);
			return false;
		}
		if (altitude < -6500000.0) {
			LogHandler.getLogs().displayMsg("Altitude not valid", ERROR);
			return false;
		}

		if ((pressure < 0.0) || (pressure > 5000.0)) {
			LogHandler.getLogs().displayMsg("Pressure not valid", ERROR);
			return false;
		}
		if ((temperature <= -273.0) || (temperature > 6000.0)) {
			LogHandler.getLogs().displayMsg("Temperature not valid", ERROR);
			return false;
		}
		if (Math.abs(slope) > 360.0) {
			LogHandler.getLogs().displayMsg("Slope not valid", ERROR);
			return false;
		}
		if (Math.abs(atmosphericRefraction) > 5.0) {
			LogHandler.getLogs().displayMsg("Atmospheric refraction not valid",
					ERROR);
			return false;
		}
		if (Math.abs(deltaT) > 8000.0) {
			LogHandler.getLogs().displayMsg("Delta_t not valid", ERROR);
			return false;
		}

		if (Math.abs(azimuthRotation) > 360.0) {
			LogHandler.getLogs()
					.displayMsg("Azimuth rotation not valid", ERROR);
			return false;
		}

		return true;
	}

}