/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DATE_SEPARATOR;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DATE_TIME_FORMAT_LONG;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import pl.imgw.jrat.tools.in.LineParseable;
import pl.imgw.jrat.tools.in.LineParseableFactory;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * Class containing one solar flux measurement.
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunSolarFluxObservation implements LineParseable {
    private Date date;
    private double julianDay;
    private double carringtonRotation;
    private double observedFlux;
    private double adjustedFlux;
    private double URSIFlux;

    private static final Date DATE_DEFAULT = getDateDefault();
    private static final double JULIANDAY_DEFAULT = 0;
    private static final double CARRINGTONROTATION_DEFAULT = 0;
    private static final double OBSERVEDFLUX_DEFAULT = 64.0;
    private static final double ADJUSTEDFLUX_DEFAULT = 64.0;
    private static final double URSIFLUX_DEFAULT = 64.0;

    public static final ScansunSolarFluxObservation WILDCARD_SCANSUN_SOLARFLUX_OBSERVATION = getWildcardScansunSolarFluxObservation();

    public ScansunSolarFluxObservation() {
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public Date getDate() {
	return date;
    }

    public void setJulianDay(double julianDay) {
	this.julianDay = julianDay;
    }

    public void setCarringtonRotation(double carringtonRotation) {
	this.carringtonRotation = carringtonRotation;
    }

    public void setObservedFlux(double observedFlux) {
	this.observedFlux = observedFlux;
    }

    public void setAdjustedFlux(double adjustedFlux) {
	this.adjustedFlux = adjustedFlux;
    }

    public void setURSIFlux(double uRSIFlux) {
	URSIFlux = uRSIFlux;
    }

    public ScansunDay getDay() {
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);

	return new ScansunDay(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
    }

    public double getAdjustedFlux() {
	return adjustedFlux;
    }

    public static class ScansunSolarFluxObservationFactory implements LineParseableFactory<ScansunSolarFluxObservation> {

	@Override
	public ScansunSolarFluxObservation create() {
	    return new ScansunSolarFluxObservation();
	}

    }

    @Override
    public String getStringHeaderWithDelimiter(String delimiter) {
	String header = new String();
	header += "date" + delimiter;
	header += "time" + delimiter;
	header += "julianDay" + delimiter;
	header += "carringtonRotation" + delimiter;
	header += "observedFlux" + delimiter;
	header += "adjustedFlux" + delimiter;
	header += "URSIFlux";

	return header;
    }

    @Override
    public void parseValues(String[] words) {
	try {
	    this.date = SCANSUN_DATE_TIME_FORMAT_LONG.parse(words[0] + SCANSUN_DATE_SEPARATOR + words[1]);
	    this.julianDay = Double.parseDouble(words[2]);
	    this.carringtonRotation = Double.parseDouble(words[3]);
	    this.observedFlux = Double.parseDouble(words[4]);
	    this.adjustedFlux = Double.parseDouble(words[5]);
	    this.URSIFlux = Double.parseDouble(words[6]);
	} catch (ParseException e) {
	    LogHandler.getLogs().displayMsg("SCANSUN: ScansunSolarFluxObservation.parseValues() wrong format.", Logging.WARNING);
	    return;
	}

    }

    private static Date getDateDefault() {
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.DAY_OF_MONTH, ScansunDay.WILDCARD_DAY.getDayOfMonth());
	cal.set(Calendar.MONTH, ScansunDay.WILDCARD_DAY.getMonth());
	cal.set(Calendar.YEAR, ScansunDay.WILDCARD_DAY.getYear());
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);

	return cal.getTime();
    }

    private static ScansunSolarFluxObservation getWildcardScansunSolarFluxObservation() {
	ScansunSolarFluxObservation sfo = new ScansunSolarFluxObservation();
	sfo.setDate(DATE_DEFAULT);
	sfo.setJulianDay(JULIANDAY_DEFAULT);
	sfo.setCarringtonRotation(CARRINGTONROTATION_DEFAULT);
	sfo.setObservedFlux(OBSERVEDFLUX_DEFAULT);
	sfo.setAdjustedFlux(ADJUSTEDFLUX_DEFAULT);
	sfo.setURSIFlux(URSIFLUX_DEFAULT);

	return sfo;
    }
}