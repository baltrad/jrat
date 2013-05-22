/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import pl.imgw.jrat.scansun.ScansunConstants.Sites;
import pl.imgw.jrat.scansun.ScansunEvent.EventType;
import pl.imgw.jrat.scansun.ScansunEvent.PulseDuration;
import pl.imgw.jrat.scansun.ScansunPowerFitSolver.ScansunPowerFitSolution;
import pl.imgw.jrat.tools.in.LineParseTool;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;
import pl.imgw.jrat.scansun.ScansunEvent.ScansunEventFactory;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunResultsPrinter {

    protected ScansunResultsParsedParameters params;
    protected final ScansunEventsContainer resultsEventsContainer;

    public ScansunResultsPrinter(ScansunResultsParsedParameters params) {
	this.params = params;
	this.resultsEventsContainer = readEvents();
    }

    private Set<File> readResultsFiles() {
	Set<File> files = new HashSet<File>();

	File folder = new File(ScansunFileHandler.getScansunPath());

	if (!folder.isDirectory()) {
	    return files;
	}

	for (File file : folder.listFiles()) {
	    if (file.isFile() && file.getName().endsWith(ScansunFileHandler.getResultsfile())) {
		files.add(file);
	    }
	}

	return files;
    }

    private ScansunEventsContainer readEvents() {

	ScansunEventsContainer eventsContainer = new ScansunEventsContainer();

	Set<File> resultsFiles = readResultsFiles();

	for (File file : resultsFiles) {
	    try {
		Scanner scanner = new Scanner(file);
		while (scanner.hasNext()) {
		    String line = scanner.nextLine();
		    if (line.startsWith("#"))
			continue;

		    eventsContainer.addEvent(LineParseTool.parseLine(line, new ScansunEventFactory()));
		}
		scanner.close();
	    } catch (FileNotFoundException e) {
		LogHandler.getLogs().displayMsg("SCANSUN: Results file not found: " + file, Logging.WARNING);
	    }
	}

	return eventsContainer;
    }

    public void printResults() {

	if (params.allSites()) {
	    for (String siteName : resultsEventsContainer.getSiteNames()) {
		printResults(siteName);
	    }
	} else {
	    printResults(params.getSiteName());
	}

    }

    enum SunTimeIndicator {
	SUNRISE, SUNSET
    }

    public static String getResultsHeaderWithDelimiter(String delimiter) {
	StringBuilder header = new StringBuilder();
	header.append("#");
	header.append("date" + delimiter); // 1
	header.append("dayOfYear" + delimiter); // 2
	header.append("eventsNumber" + delimiter); // 3
	header.append("eventsNumberFromShortScan" + delimiter);// 4
	header.append("eventsNumberFromLongScan" + delimiter);// 5
	header.append("solarRaysNumber" + delimiter); // 6
	header.append("solarRaysNumberFromShortScan" + delimiter);// 7
	header.append("solarRaysNumberFromLongScan" + delimiter);// 8
	header.append("efficiency" + delimiter);// 9

	header.append("sunriseTime" + delimiter);// 10
	header.append("sunsetTime" + delimiter);// 11

	header.append("sunriseAzimuth" + delimiter);// 12
	header.append("sunsetAzimuth" + delimiter);// 13

	header.append("ax" + delimiter);// 14
	header.append("ay" + delimiter);// 15
	header.append("bx" + delimiter);// 16
	header.append("by" + delimiter);// 17
	header.append("c" + delimiter);// 18
	header.append("peakSolarPower" + delimiter);// 19
	header.append("solarFluxdB" + delimiter);// 20
	header.append("solarFluxDRAO10.7cm" + delimiter);// 21
	header.append("solarFluxDRAOAdjusted" + delimiter);// 22
	header.append("solarFluxDRAOAdjusteddB" + delimiter);// 23
	return header.toString();
    }

    public void printResults(String siteName) {

	ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();

	SortedSet<ScansunDay> days = getDays(siteName);
	if (days.isEmpty()) {
	    printer.println("# No results found for sitename = " + siteName);
	    return;
	}

	Map<ScansunDay, Integer> eventsNumberByDayFromShortScan = getDetectionHistogramByDay(siteName, EventType.EVENT, PulseDuration.SHORT);
	Map<ScansunDay, Integer> eventsNumberByDayFromLongScan = getDetectionHistogramByDay(siteName, EventType.EVENT, PulseDuration.LONG);

	Map<ScansunDay, Integer> solarRaysNumberByDayFromShortScan = getDetectionHistogramByDay(siteName, EventType.SOLARRAY, PulseDuration.SHORT);
	Map<ScansunDay, Integer> solarRaysNumberByDayFromLongScan = getDetectionHistogramByDay(siteName, EventType.SOLARRAY, PulseDuration.LONG);

	Map<ScansunDay, Double> sunriseTimeByDay = getSunTimesByDay(siteName, SunTimeIndicator.SUNRISE);
	Map<ScansunDay, Double> sunsetTimeByDay = getSunTimesByDay(siteName, SunTimeIndicator.SUNSET);

	Map<ScansunDay, Double> sunriseAzimuthByDay = getSunAzimuthsByDay(siteName, SunTimeIndicator.SUNRISE);
	Map<ScansunDay, Double> sunsetAzimuthByDay = getSunAzimuthsByDay(siteName, SunTimeIndicator.SUNSET);

	Map<ScansunDay, ScansunPowerFitSolution> fitCoefficientsByDay = getFitCoefficientsByDay(siteName);
	Map<ScansunDay, Double> solarFluxByDay = getSolarFluxByDay(siteName);
	Map<ScansunDay, Double> solarFluxDRAOByDay = getSolarFluxDRAOByDay(days);
	Map<ScansunDay, Double> solarFluxDRAOAdjustedByDay = getSolarFluxDRAOAdjustedByDay(days);

	String delimiter = "\t";

	StringBuilder header = new StringBuilder();
	header.append("#");
	for (int i = 0; i < 50; i++) {
	    header.append("=");
	}
	header.append("\n#Sitename = " + siteName);
	header.append("\n" + getResultsHeaderWithDelimiter(delimiter));
	printer.println(header.toString());

	DecimalFormat df = new DecimalFormat("#.##");

	for (ScansunDay day : days) {
	    StringBuilder line = new StringBuilder();
	    line.append(day.toString() + delimiter);
	    line.append(day.getDayOfYear() + delimiter);

	    int eventsNumber = eventsNumberByDayFromShortScan.get(day) + eventsNumberByDayFromLongScan.get(day);
	    line.append(eventsNumber + delimiter);
	    line.append(eventsNumberByDayFromShortScan.get(day) + delimiter);
	    line.append(eventsNumberByDayFromLongScan.get(day) + delimiter);

	    int solarRaysNumber = 0;
	    if (solarRaysNumberByDayFromShortScan.get(day) != null)
		solarRaysNumber += solarRaysNumberByDayFromShortScan.get(day);
	    if (solarRaysNumberByDayFromLongScan.get(day) != null)
		solarRaysNumber += solarRaysNumberByDayFromLongScan.get(day);

	    line.append(solarRaysNumber + delimiter);
	    line.append(solarRaysNumberByDayFromShortScan.get(day) + delimiter);
	    line.append(solarRaysNumberByDayFromLongScan.get(day) + delimiter);

	    line.append(df.format(((double) solarRaysNumber / eventsNumber) * 100.0) + delimiter);

	    line.append(df.format(sunriseTimeByDay.get(day)) + delimiter);
	    line.append(df.format(sunsetTimeByDay.get(day)) + delimiter);
	    line.append(df.format(sunriseAzimuthByDay.get(day)) + delimiter);
	    line.append(df.format(sunsetAzimuthByDay.get(day)) + delimiter);

	    double ax = fitCoefficientsByDay.get(day).getAx();
	    line.append(df.format(ax) + delimiter);

	    double ay = fitCoefficientsByDay.get(day).getAy();
	    line.append(df.format(ay) + delimiter);

	    double bx = fitCoefficientsByDay.get(day).getBx();
	    line.append(df.format(bx) + delimiter);

	    double by = fitCoefficientsByDay.get(day).getBy();
	    line.append(df.format(by) + delimiter);

	    double c = fitCoefficientsByDay.get(day).getC();
	    line.append(df.format(c) + delimiter);

	    double P0dBm = fitCoefficientsByDay.get(day).calculateP0dBm();
	    line.append(df.format(P0dBm) + delimiter);

	    line.append(df.format(solarFluxByDay.get(day)) + delimiter);

	    double sfDRAO = solarFluxDRAOByDay.get(day);
	    line.append(df.format(sfDRAO) + delimiter);

	    double sfDRAOAdjusted = solarFluxDRAOAdjustedByDay.get(day);
	    line.append(df.format(sfDRAOAdjusted) + delimiter);

	    line.append(df.format(ScansunEquations.toDecibelScale(sfDRAOAdjusted, 1.0)) + delimiter);

	    printer.println(line.toString());
	}

    }

    public SortedSet<ScansunDay> getDays(String siteName) {

	SortedSet<ScansunDay> days = new TreeSet<ScansunDay>();

	for (ScansunEvent event : resultsEventsContainer.getEvents()) {
	    days.add(event.getDay());
	}

	return days;
    }

    public Map<ScansunDay, Integer> getDetectionHistogramByDay(String siteName, EventType eventType, PulseDuration pulseDuration) {
	Map<ScansunDay, Integer> histogram = new HashMap<ScansunDay, Integer>();

	for (ScansunDay day : getDays(siteName)) {
	    histogram.put(day, resultsEventsContainer.filterEvents().byDay(day).bySiteName(siteName).byEventType(eventType).byPulseDuration(pulseDuration)
		    .getEvents().size());
	}

	return histogram;
    }

    public int getTotalNumber(String siteName, EventType eventType) {

	int totalNumber = 0;

	for (ScansunDay day : getDays(siteName)) {
	    totalNumber += resultsEventsContainer.filterEvents().byDay(day).bySiteName(siteName).byEventType(eventType).byPulseDuration(PulseDuration.SHORT)
		    .getEvents().size();
	    totalNumber += resultsEventsContainer.filterEvents().byDay(day).bySiteName(siteName).byEventType(eventType).byPulseDuration(PulseDuration.LONG)
		    .getEvents().size();
	}

	return totalNumber;
    }

    public int getDetectionHistogramMax(String siteName, EventType eventType) {

	int maxNumber = 0;

	for (ScansunDay day : getDays(siteName)) {
	    int dayNumber = resultsEventsContainer.filterEvents().byDay(day).bySiteName(siteName).byEventType(eventType).byPulseDuration(PulseDuration.SHORT)
		    .getEvents().size()
		    + resultsEventsContainer.filterEvents().byDay(day).bySiteName(siteName).byEventType(eventType).byPulseDuration(PulseDuration.LONG)
			    .getEvents().size();
	    if (dayNumber > maxNumber) {
		maxNumber = dayNumber;
	    }
	}

	return maxNumber;
    }

    public Map<ScansunDay, Double> getSunTimesByDay(String siteName, SunTimeIndicator sunTimeIndicator) {

	Map<ScansunDay, Double> sunTimes = new HashMap<ScansunDay, Double>();

	double longitude = Sites.getLongitude(siteName);
	double latitude = Sites.getLatitude(siteName);
	double altitude = Sites.getAltitude(siteName);
	ScansunSolarPositionAlgorithm solarPositionAlgorithm = new ScansunSolarPositionAlgorithm(longitude, latitude, altitude);

	for (ScansunDay day : getDays(siteName)) {
	    if (sunTimeIndicator == SunTimeIndicator.SUNRISE)
		sunTimes.put(day, solarPositionAlgorithm.calculateSunriseTime(day));
	    else if (sunTimeIndicator == SunTimeIndicator.SUNSET)
		sunTimes.put(day, solarPositionAlgorithm.calculateSunsetTime(day));
	}

	return sunTimes;
    }

    public Map<ScansunDay, Double> getSunAzimuthsByDay(String siteName, SunTimeIndicator sunTimeIndicator) {

	Map<ScansunDay, Double> sunAzimuths = new HashMap<ScansunDay, Double>();

	double longitude = Sites.getLongitude(siteName);
	double latitude = Sites.getLatitude(siteName);
	double altitude = Sites.getAltitude(siteName);

	ScansunSolarPositionAlgorithm solarPositionAlgorithm = new ScansunSolarPositionAlgorithm(longitude, latitude, altitude);

	for (ScansunDay day : getDays(siteName)) {
	    if (sunTimeIndicator == SunTimeIndicator.SUNRISE)
		sunAzimuths.put(day, solarPositionAlgorithm.calculateSunriseAzimuth(day));
	    else if (sunTimeIndicator == SunTimeIndicator.SUNSET)
		sunAzimuths.put(day, solarPositionAlgorithm.calculateSunsetAzimuth(day));
	}

	return sunAzimuths;
    }

    public double getMeanPower(String siteName) {
	int n = 0;
	double meanPower = 0.0;

	boolean isMeanPowerCalibrated = ScansunOptionsHandler.getOptions().getRadarParsedParameters(siteName).areParametersProper();

	for (ScansunEvent event : resultsEventsContainer.filterEvents().bySiteName(siteName).byEventType(EventType.SOLARRAY)
		.byMeanPowerCalibrationMode(isMeanPowerCalibrated).getEvents()) {
	    meanPower += event.getMeanPower();
	    n++;
	}

	return meanPower / n;
    }

    public Map<ScansunDay, ScansunPowerFitSolution> getFitCoefficientsByDay(String siteName) {

	Map<ScansunDay, ScansunPowerFitSolution> fitCoefficients = new HashMap<ScansunDay, ScansunPowerFitSolution>();

	boolean areParametersProper = ScansunOptionsHandler.getOptions().getRadarParsedParameters(siteName).areParametersProper();

	for (ScansunDay day : getDays(siteName)) {

	    ScansunPowerFitSolver llsPowerFitSolver = new ScansunPowerFitSolver();

	    for (ScansunEvent event : resultsEventsContainer.filterEvents().byDay(day).bySiteName(siteName).byEventType(EventType.SOLARRAY)
		    .byMeanPowerCalibrationMode(areParametersProper).getEvents()) {
		llsPowerFitSolver.addData(ScansunEquations.calculatePs(event.getMeanPower(), event.getSunElevation()), event.getAzimuthOffset(),
			event.getElevationOffset());
	    }

	    ScansunPowerFitSolution solution = llsPowerFitSolver.hasDataPoints() ? llsPowerFitSolver.solve() : ScansunPowerFitSolution.NO_SOLUTION;
	    fitCoefficients.put(day, solution);
	}

	return fitCoefficients;
    }

    public Map<ScansunDay, Double> getSolarFluxByDay(String siteName) {

	Map<ScansunDay, Double> solarFlux = new HashMap<ScansunDay, Double>();

	Map<ScansunDay, ScansunPowerFitSolution> fitCoefficientsByDay = getFitCoefficientsByDay(siteName);

	for (ScansunDay day : getDays(siteName)) {
	    double La = ScansunOptionsHandler.getOptions().getRadarParsedParameters(siteName).calculateLa();
	    double Ae = ScansunOptionsHandler.getOptions().getRadarParsedParameters(siteName).calculateAe();

	    solarFlux.put(day, fitCoefficientsByDay.get(day).calculateSolarFlux(La, Ae));
	}

	return solarFlux;
    }

    public Map<ScansunDay, Double> getSolarFluxDRAOByDay(Set<ScansunDay> days) {
	Map<ScansunDay, Double> solarFlux = new HashMap<ScansunDay, Double>();

	for (ScansunDay day : days) {
	    solarFlux.put(day, ScansunDRAOSolarFlux.getManager().getSolarFlux(day));
	}

	return solarFlux;
    }

    public Map<ScansunDay, Double> getSolarFluxDRAOAdjustedByDay(Set<ScansunDay> days) {
	Map<ScansunDay, Double> solarFluxAdjusted = new HashMap<ScansunDay, Double>();

	for (ScansunDay day : days) {
	    solarFluxAdjusted.put(day, ScansunDRAOSolarFlux.getManager().getAdjustedSolarFlux(day));
	}

	return solarFluxAdjusted;
    }

    public void printRawResults() {

	if (params.allSites()) {
	    for (String siteName : resultsEventsContainer.getSiteNames()) {
		printRawResults(siteName);
	    }
	} else {
	    printRawResults(params.getSiteName());
	}

    }

    public void printRawResults(String siteName) {

	ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();

	StringBuilder header = new StringBuilder();
	header.append("#");
	for (int i = 0; i < 200; i++) {
	    header.append("=");
	}
	header.append("\n#" + (new ScansunEvent()).getStringHeaderWithDelimiter("\t") + "\n");
	printer.println(header.toString());

	boolean noResults = true;
	Set<ScansunEvent> events = resultsEventsContainer.filterEvents().bySiteName(siteName).getEvents();
	if (events.size() != 0) {
	    noResults = false;
	}

	for (ScansunEvent event : events) {
	    printer.println(event.toStringWithDelimiter("\t"));
	}

	if (noResults) {
	    System.out.println("# No results matching selected parameters");
	}
	printer.print("\n");
    }

}