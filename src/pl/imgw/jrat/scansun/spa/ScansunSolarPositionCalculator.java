/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.spa;

import java.util.ListIterator;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pl.imgw.jrat.scansun.data.ScansunSite;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunSolarPositionCalculator {

	private static final double DELTA_T = 67.0;
	private static final double PRESSURE = 820.0;
	private static final double TEMPERATURE = 20.0;
	private static final double SLOPE = 0.0;
	private static final double AZIMUTH_ROTATION = 0.0;
	private static final double ATMOSPHERIC_REFRACTION = 0.5667;

	ScansunSolarPositionAlgorithmParameters params;

	public enum SuntimeIndicator {
		SUNRISE, SUNSET
	}

	public ScansunSolarPositionCalculator(double longitude, double latitude,
			double altitude) {
		params = new ScansunSolarPositionAlgorithmParameters();
		params.setLongitude(longitude);
		params.setLatitude(latitude);
		params.setAltitude(altitude);
		params.setSlope(SLOPE);
		params.setPressure(PRESSURE);
		params.setTemperature(TEMPERATURE);
		params.setAtmosphericRefraction(ATMOSPHERIC_REFRACTION);
		params.setDeltaT(DELTA_T);
		params.setAzimuthRotation(AZIMUTH_ROTATION);
	}

	public ScansunSolarPositionCalculator(ScansunSite site) {
		params = new ScansunSolarPositionAlgorithmParameters();
		params.setLongitude(site.getLongitude());
		params.setLatitude(site.getLatitude());
		params.setAltitude(site.getAltitude());
		params.setSlope(SLOPE);
		params.setPressure(PRESSURE);
		params.setTemperature(TEMPERATURE);
		params.setAtmosphericRefraction(ATMOSPHERIC_REFRACTION);
		params.setDeltaT(DELTA_T);
		params.setAzimuthRotation(AZIMUTH_ROTATION);
	}

	public Double calculateSunElevation(DateTime dateTime) {
		params.setDateTime(dateTime);

		ScansunSolarPositionAlgorithmSolver solver = new ScansunSolarPositionAlgorithmSolver(
				params);
		solver.calculate();

		return solver.getElevation();
	}

	public Double calculateSunAzimuth(DateTime dateTime) {
		params.setDateTime(dateTime);

		ScansunSolarPositionAlgorithmSolver solver = new ScansunSolarPositionAlgorithmSolver(
				params);
		solver.calculate();

		return solver.getAzimuth();
	}

	public Double calculateSunriseTime(LocalDate day) {

		DateTime dateTime = new DateTime(day.getYear(), day.getMonthOfYear(),
				day.getDayOfMonth(), 12, 0, 0);
		// params.setTimezone(0.0); // UTC
		params.setDateTime(dateTime);

		ScansunSolarPositionAlgorithmSolver solver = new ScansunSolarPositionAlgorithmSolver(
				params);
		solver.calculate();

		return solver.getSunriseTime();
	}

	public Double calculateSunsetTime(LocalDate day) {

		DateTime dateTime = new DateTime(day.getYear(), day.getMonthOfYear(),
				day.getDayOfMonth(), 12, 0, 0);
		// params.setTimezone(0.0); // UTC
		params.setDateTime(dateTime);

		ScansunSolarPositionAlgorithmSolver solver = new ScansunSolarPositionAlgorithmSolver(
				params);
		solver.calculate();

		return solver.getSunsetTime();
	}

	public ListIterator<DateTime> minuteIterator(final DateTime start) {
		return new ListIterator<DateTime>() {

			@Override
			public void add(DateTime arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean hasNext() {
				return start != null;
			}

			@Override
			public boolean hasPrevious() {
				return start != null;
			}

			@Override
			public DateTime next() {
				return start.plusMinutes(1);
			}

			@Override
			public int nextIndex() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public DateTime previous() {
				return start.minusMinutes(1);
			}

			@Override
			public int previousIndex() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub

			}

			@Override
			public void set(DateTime arg0) {
				// TODO Auto-generated method stub

			}
		};
	}

	public Double calculateSunriseAzimuth(LocalDate day) {

		DateTime start = new DateTime(day.getYear(), day.getMonthOfYear(),
				day.getDayOfMonth(), 12, 0, 0);

		ListIterator<DateTime> itr = minuteIterator(start);

		params.setDateTime(start);
		ScansunSolarPositionAlgorithmSolver solver = new ScansunSolarPositionAlgorithmSolver(
				params);
		solver.calculate();

		while (itr.hasPrevious() && solver.getElevation() < 0.0) {
			DateTime currentDateTime = (DateTime) itr.previous();

			params.setDateTime(currentDateTime);
			solver.setSolarPositionAlgorithmParameters(params);
			solver.calculate();
		}

		return solver.getAzimuth();
	}

	public Double calculateSunsetAzimuth(LocalDate day) {

		DateTime start = new DateTime(day.getYear(), day.getMonthOfYear(),
				day.getDayOfMonth(), 12, 0, 0);

		ListIterator<DateTime> itr = minuteIterator(start);

		params.setDateTime(start);
		ScansunSolarPositionAlgorithmSolver solver = new ScansunSolarPositionAlgorithmSolver(
				params);
		solver.calculate();

		while (itr.hasNext() && solver.getElevation() < 0.0) {
			DateTime currentDateTime = (DateTime) itr.next();

			params.setDateTime(currentDateTime);
			solver.setSolarPositionAlgorithmParameters(params);
			solver.calculate();
		}

		return solver.getAzimuth();
	}

}