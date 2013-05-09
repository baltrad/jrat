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
    Date date;
    double julianDay;
    double carringtonRotation;
    double observedFlux;
    double adjustedFlux;
    double URSIFlux;

    private ScansunSolarFluxObservation() {
    }

    public Date getDate() {
	return date;
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
	    LogHandler.getLogs().displayMsg("SCANSUN: ScansunSolarFluxObservation.parseValues() wrong format.",
		    Logging.WARNING);
	    return;
	}

    }

}