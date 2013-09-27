/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import pl.imgw.jrat.tools.in.LineParseable;
import pl.imgw.jrat.tools.in.LineParseableFactory;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunSolarFluxObservation implements LineParseable {

	private LocalDate date;
	private LocalTime time;
	private double julianDay;
	private double carringtonRotation;
	private double observedFlux;
	private double adjustedFlux;
	private double URSIFlux;

	public static final String DELIMITER = ";";

	private static final String DATE = "date";
	private static final String TIME = "time";
	private static final String JULIAN_DAY = "julianDay";
	private static final String CARRINGTON_ROTATION = "carringtonRotation";
	private static final String OBSERVED_FLUX = "observedFlux";
	private static final String ADJUSTED_FLUX = "adjustedFlux";
	private static final String URSI_FLUX = "ursiFlux";

	private ScansunSolarFluxObservation() {

	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setJulianDay(double julianDay) {
		this.julianDay = julianDay;
	}

	public double getJulianDay() {
		return julianDay;
	}

	public void setCarringtonRotation(double carringtonRotation) {
		this.carringtonRotation = carringtonRotation;
	}

	public double getCarringtonRotation() {
		return carringtonRotation;
	}

	public double getObservedFlux() {
		return observedFlux;
	}

	public void setObservedFlux(double observedFlux) {
		this.observedFlux = observedFlux;
	}

	public double getAdjustedFlux() {
		return adjustedFlux;
	}

	public void setAdjustedFlux(double adjustedFlux) {
		this.adjustedFlux = adjustedFlux;
	}

	public double getURSIFlux() {
		return URSIFlux;
	}

	public void setURSIFlux(double uRSIFlux) {
		URSIFlux = uRSIFlux;
	}

	public static class ScansunSolarFluxObservationFactory implements
			LineParseableFactory<ScansunSolarFluxObservation> {

		@Override
		public ScansunSolarFluxObservation create() {
			return new ScansunSolarFluxObservation();
		}
	}

	public static boolean checkHeader(String line) {
		String header = new ScansunSolarFluxObservation().lineHeader(DELIMITER);

		return line.equalsIgnoreCase(header);
	}

	@Override
	public String lineHeader(String delimiter) {
		StringBuilder header = new StringBuilder();

		header.append(DATE + delimiter);
		header.append(TIME + delimiter);
		header.append(JULIAN_DAY + delimiter);
		header.append(CARRINGTON_ROTATION + delimiter);
		header.append(OBSERVED_FLUX + delimiter);
		header.append(ADJUSTED_FLUX + delimiter);
		header.append(URSI_FLUX);

		return header.toString();
	}

	@Override
	public void parseLine(String[] words) {
		this.setDate(new LocalDate(words[0]));
		this.setTime(new LocalTime(words[1]));
		this.setJulianDay(Double.parseDouble(words[2]));
		this.setCarringtonRotation(Double.parseDouble(words[3]));
		this.setObservedFlux(Double.parseDouble(words[4]));
		this.setAdjustedFlux(Double.parseDouble(words[5]));
		this.setURSIFlux(Double.parseDouble(words[6]));
	}

}
