/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.view;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.joda.time.LocalDate;

import pl.imgw.jrat.scansun.ScansunException;
import pl.imgw.jrat.scansun.data.ScansunMeanPowerCalibrationMode;
import pl.imgw.jrat.scansun.data.ScansunPowerFitSolution;
import pl.imgw.jrat.scansun.data.ScansunPulseDuration;
import pl.imgw.jrat.scansun.data.ScansunResultContainer;
import pl.imgw.jrat.scansun.data.ScansunResultParameters;
import pl.imgw.jrat.scansun.data.ScansunSite;
import pl.imgw.jrat.scansun.data.ScansunEvent;
import pl.imgw.jrat.scansun.data.ScansunEventType;
import pl.imgw.jrat.scansun.proc.ScansunCalculator;
import pl.imgw.jrat.scansun.proc.ScansunLinearPowerFitSolver;
import pl.imgw.jrat.scansun.proc.ScansunPowerFitSolver;
import pl.imgw.jrat.scansun.proc.ScansunRadarParametersFileHandler;
import pl.imgw.jrat.scansun.proc.ScansunResultFileGetter;
import pl.imgw.jrat.scansun.proc.ScansunResultParametersParser;
import pl.imgw.jrat.scansun.proc.ScansunSolarFluxFileHandler;
import pl.imgw.jrat.scansun.spa.ScansunSolarPositionCalculator;
import pl.imgw.jrat.scansun.spa.ScansunSolarPositionCalculator.SuntimeIndicator;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;
import static pl.imgw.jrat.scansun.data.ScansunConstants.COMMENT;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunResultsPrinter {

	protected static Log log = LogManager.getLogger();

	private Set<File> files;
	protected ScansunResultParameters params;
	protected ScansunResultContainer eventsContainer;
	protected Map<ScansunSite, SortedSet<LocalDate>> sitedays;

	private static final String DATE = "date";
	private static final String DAY_OF_YEAR = "dayOfYear";
	private static final String EVENTS_NUMBER = "eventsNumber";
	private static final String EVENTS_NUMBER_FROM_SHORT_SCAN = "eventsNumberFromShortScan";
	private static final String EVENTS_NUMBER_FROM_LONG_SCAN = "eventsNumberFromLongScan";
	private static final String SOLAR_RAY_NUMBER = "solarRaysNumber";
	private static final String SOLAR_RAY_NUMBER_FROM_SHORT_SCAN = "solarRaysNumberFromShortScan";
	private static final String SOLAR_RAY_NUMBER_FROM_LONG_SCAN = "solarRaysNumberFromLongScan";
	private static final String EFFICIENCY = "efficiency";

	private static final String SUNRISE_DAYTIME = "sunriseTime";
	private static final String SUNSET_DAYTIME = "sunsetTime";

	private static final String SUNRISE_AZIMUTH = "sunriseAzimuth";
	private static final String SUNSET_AZIMUTH = "sunsetAzimuth";

	private static final String PEAK_SOLAR_POWER = "peakSolarPower";
	private static final String SITE_SOLAR_FLUX = "solarFlux";
	private static final String SITE_SOLAR_FLUX_DB = "solarFluxdB";
	private static final String DRAO_SOLAR_FLUX_OBSERVED = "DRAOsolarFluxObserved";
	private static final String DRAO_SOLAR_FLUX_ADJUSTED = "DRAOsolarFluxAdjusted";
	private static final String DRAO_SOLAR_FLUX_ADJUSTED_DB = "DRAOsolarFluxAdjustedDB";

	private static final String DELIMITER = "\t";
	private static final String NEWLINE = "\n";
	private static final String PADDING = "=";

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(
			"#.###");

	protected ResultPrinter printer;

	public ScansunResultsPrinter(String[] args) {
		ScansunResultParameters params = null;

		try {
			params = ScansunResultParametersParser.getParser().parseParameters(
					args);
		} catch (ScansunException e) {
			throw e;
		}

		this.params = params;
		this.files = ScansunResultFileGetter.getResultFiles(params
				.getResultFolder());
		this.eventsContainer = ScansunResultContainer.readEvents(files);
		this.sitedays = eventsContainer.getSitedays();
	}

	public ScansunResultContainer getEventsContainer() {
		return eventsContainer;
	}

	public void generateResults() {
		printer = ResultPrinterManager.getManager().getPrinter();

		if (params.allAvailableSites()) {
			for (ScansunSite site : eventsContainer.getSites()) {
				printResults(site);
			}
		} else {
			printResults(params.getSite());
		}
	}

	public void printResults(ScansunSite site) {

		if (sitedays.get(site) == null) {
			printer.println(COMMENT + "No results found for sitename = "
					+ site.getSiteName());
			return;
		}

		Map<LocalDate, Set<ScansunEvent>> eventsByDayFromShortScan = eventsContainer
				.getEvents()
				.byEventType(ScansunEventType.NON_SOLAR)
				.byPulseDuration(ScansunPulseDuration.SHORT)
				.byMeanPowerCalibrationMode(
						ScansunMeanPowerCalibrationMode.CALIBRATED).asMap()
				.get(site);

		Map<LocalDate, Set<ScansunEvent>> eventsByDayFromLongScan = eventsContainer
				.getEvents()
				.byEventType(ScansunEventType.NON_SOLAR)
				.byPulseDuration(ScansunPulseDuration.LONG)
				.byMeanPowerCalibrationMode(
						ScansunMeanPowerCalibrationMode.CALIBRATED).asMap()
				.get(site);

		Map<LocalDate, Set<ScansunEvent>> solarRaysByDayFromShortScan = eventsContainer
				.getEvents()
				.byEventType(ScansunEventType.SOLAR_RAY)
				.byPulseDuration(ScansunPulseDuration.SHORT)
				.byMeanPowerCalibrationMode(
						ScansunMeanPowerCalibrationMode.CALIBRATED).asMap()
				.get(site);

		Map<LocalDate, Set<ScansunEvent>> solarRaysByDayFromLongScan = eventsContainer
				.getEvents()
				.byEventType(ScansunEventType.SOLAR_RAY)
				.byPulseDuration(ScansunPulseDuration.LONG)
				.byMeanPowerCalibrationMode(
						ScansunMeanPowerCalibrationMode.CALIBRATED).asMap()
				.get(site);

		Map<LocalDate, Double> sunriseTimeByDay = getSunDaytimesByDay(site,
				SuntimeIndicator.SUNRISE);
		Map<LocalDate, Double> sunsetTimeByDay = getSunDaytimesByDay(site,
				SuntimeIndicator.SUNSET);

		Map<LocalDate, Double> sunriseAzimuthByDay = getSunAzimuthsByDay(site,
				SuntimeIndicator.SUNRISE);
		Map<LocalDate, Double> sunsetAzimuthByDay = getSunAzimuthsByDay(site,
				SuntimeIndicator.SUNSET);

		Map<LocalDate, ScansunPowerFitSolution> fitCoefficientsByDay = getFitCoefficientsByDay(site);
		Map<LocalDate, Double> solarFluxByDay = getSiteSolarFluxByDay(site,
				fitCoefficientsByDay);

		Map<LocalDate, Double> solarFluxObservedDRAOByDay = getDRAOSolarFluxObservedByDay(site);
		Map<LocalDate, Double> solarFluxAdjustedDRAOByDay = getDRAOSolarFluxAdjustedByDay(site);

		StringBuilder header = new StringBuilder(COMMENT);
		header.append(COMMENT);
		for (int i = 0; i < 80; i++) {
			header.append(PADDING);
		}
		header.append(NEWLINE + COMMENT + "sitename = " + site.getSiteName());
		header.append(NEWLINE + COMMENT + getResultsHeader(DELIMITER));
		printer.println(header.toString());

		for (LocalDate day : sitedays.get(site)) {
			StringBuilder line = new StringBuilder();

			line.append(day + DELIMITER);
			line.append(day.getDayOfYear() + DELIMITER);

			int eventsNumberFromShortScan = 0;
			if (eventsByDayFromShortScan != null
					&& eventsByDayFromShortScan.get(day) != null) {
				eventsNumberFromShortScan += eventsByDayFromShortScan.get(day)
						.size();
			}

			int eventsNumberFromLongScan = 0;
			if (eventsByDayFromLongScan != null
					&& eventsByDayFromLongScan.get(day) != null) {
				eventsNumberFromLongScan += eventsByDayFromLongScan.get(day)
						.size();
			}
			int eventsNumber = eventsNumberFromShortScan
					+ eventsNumberFromLongScan;
			line.append(eventsNumber + DELIMITER);
			line.append(eventsNumberFromShortScan + DELIMITER);
			line.append(eventsNumberFromLongScan + DELIMITER);

			int solarRaysNumberFromShortScan = 0;
			if (solarRaysByDayFromShortScan != null
					&& solarRaysByDayFromShortScan.get(day) != null) {
				solarRaysNumberFromShortScan += solarRaysByDayFromShortScan
						.get(day).size();
			}
			int solarRaysNumberFromLongScan = 0;
			if (solarRaysByDayFromLongScan != null
					&& solarRaysByDayFromLongScan.get(day) != null) {
				solarRaysNumberFromLongScan += solarRaysByDayFromLongScan.get(
						day).size();
			}
			int solarRaysNumber = solarRaysNumberFromShortScan
					+ solarRaysNumberFromLongScan;

			line.append(solarRaysNumber + DELIMITER);
			line.append(solarRaysNumberFromShortScan + DELIMITER);
			line.append(solarRaysNumberFromLongScan + DELIMITER);

			line.append(DECIMAL_FORMAT
					.format(((double) solarRaysNumber / eventsNumber) * 100.0)
					+ DELIMITER);

			line.append(DECIMAL_FORMAT.format(sunriseTimeByDay.get(day))
					+ DELIMITER);
			line.append(DECIMAL_FORMAT.format(sunsetTimeByDay.get(day))
					+ DELIMITER);
			line.append(DECIMAL_FORMAT.format(sunriseAzimuthByDay.get(day))
					+ DELIMITER);
			line.append(DECIMAL_FORMAT.format(sunsetAzimuthByDay.get(day))
					+ DELIMITER);

			line.append(fitCoefficientsByDay.get(day).toString(DELIMITER,
					DECIMAL_FORMAT)
					+ DELIMITER);
			line.append(DECIMAL_FORMAT.format(fitCoefficientsByDay.get(day)
					.calculateP0dBm()) + DELIMITER);

			line.append(DECIMAL_FORMAT.format(solarFluxByDay.get(day))
					+ DELIMITER);
			line.append(DECIMAL_FORMAT.format(ScansunCalculator.toDecibelScale(
					solarFluxByDay.get(day), 1.0)) + DELIMITER);

			line.append(DECIMAL_FORMAT.format(solarFluxObservedDRAOByDay
					.get(day)) + DELIMITER);
			line.append(DECIMAL_FORMAT.format(solarFluxAdjustedDRAOByDay
					.get(day)) + DELIMITER);
			line.append(DECIMAL_FORMAT.format(ScansunCalculator.toDecibelScale(
					solarFluxAdjustedDRAOByDay.get(day), 1.0)) + DELIMITER);

			printer.println(line.toString());
		}

	}

	public Map<LocalDate, Double> getSunDaytimesByDay(ScansunSite site,
			SuntimeIndicator suntimeIndicator) {

		Map<LocalDate, Double> suntimes = new HashMap<>();

		ScansunSolarPositionCalculator solarPositionCalculator = new ScansunSolarPositionCalculator(
				site);

		for (LocalDate day : sitedays.get(site)) {
			if (suntimeIndicator == SuntimeIndicator.SUNRISE)
				suntimes.put(day,
						solarPositionCalculator.calculateSunriseTime(day));
			else if (suntimeIndicator == SuntimeIndicator.SUNSET)
				suntimes.put(day,
						solarPositionCalculator.calculateSunsetTime(day));
		}

		return suntimes;
	}

	public Map<LocalDate, Double> getSunAzimuthsByDay(ScansunSite site,
			SuntimeIndicator suntimeIndicator) {

		Map<LocalDate, Double> sunazimuths = new HashMap<>();

		ScansunSolarPositionCalculator solarPositionCalculator = new ScansunSolarPositionCalculator(
				site);

		for (LocalDate day : sitedays.get(site)) {
			if (suntimeIndicator == SuntimeIndicator.SUNRISE)
				sunazimuths.put(day,
						solarPositionCalculator.calculateSunriseAzimuth(day));
			else if (suntimeIndicator == SuntimeIndicator.SUNSET)
				sunazimuths.put(day,
						solarPositionCalculator.calculateSunsetAzimuth(day));
		}

		return sunazimuths;
	}

	public Map<LocalDate, ScansunPowerFitSolution> getFitCoefficientsByDay(
			ScansunSite site) {

		Map<LocalDate, ScansunPowerFitSolution> fitCoefficients = new HashMap<>();

		ScansunMeanPowerCalibrationMode meanPowerCalibrationMode = ScansunRadarParametersFileHandler
				.getHandler().getRadarParameters().get(site)
				.meanPowerCalibrationMode();

		for (LocalDate day : sitedays.get(site)) {

			ScansunPowerFitSolver solver = new ScansunLinearPowerFitSolver();

			for (ScansunEvent event : eventsContainer.getEvents()
					.byLocalDate(day).bySite(site)
					.byEventType(ScansunEventType.SOLAR_RAY)
					.byMeanPowerCalibrationMode(meanPowerCalibrationMode)) {

				solver.addData(ScansunCalculator.calculatePs(
						event.getMeanPower(), event.getSunElevation()), event
						.getAzimuthOffset(), event.getElevationOffset());
			}

			ScansunPowerFitSolution solution = solver.hasDataPoints() ? solver
					.solve() : ScansunPowerFitSolution.NO_SOLUTION;
			fitCoefficients.put(day, solution);
		}

		return fitCoefficients;
	}

	public Map<LocalDate, Double> getSiteSolarFluxByDay(ScansunSite site,
			Map<LocalDate, ScansunPowerFitSolution> fitCoefficientsByDay) {

		Map<LocalDate, Double> solarFlux = new HashMap<>();

		for (LocalDate day : sitedays.get(site)) {
			double La = ScansunRadarParametersFileHandler.getHandler()
					.getRadarParameters().get(site).calculateLa();
			double Ae = ScansunRadarParametersFileHandler.getHandler()
					.getRadarParameters().get(site).calculateAe();

			solarFlux.put(day, fitCoefficientsByDay.get(day)
					.calculateSolarFluxDecibel(La, Ae));
		}

		return solarFlux;
	}

	public Map<LocalDate, Double> getDRAOSolarFluxObservedByDay(ScansunSite site) {
		Map<LocalDate, Double> solarFluxMap = new HashMap<>();

		for (LocalDate day : sitedays.get(site)) {
			solarFluxMap.put(day, ScansunSolarFluxFileHandler.getHandler()
					.getSolarFluxObserved(day));
		}

		return solarFluxMap;
	}

	public Map<LocalDate, Double> getDRAOSolarFluxAdjustedByDay(ScansunSite site) {
		Map<LocalDate, Double> solarFluxMap = new HashMap<>();

		for (LocalDate day : sitedays.get(site)) {
			solarFluxMap.put(day, ScansunSolarFluxFileHandler.getHandler()
					.getSolarFluxAdjusted(day));
		}

		return solarFluxMap;
	}

	public String getResultsHeader(String delimiter) {
		StringBuilder header = new StringBuilder();

		header.append(DATE + delimiter);
		header.append(DAY_OF_YEAR + delimiter);
		header.append(EVENTS_NUMBER + delimiter);
		header.append(EVENTS_NUMBER_FROM_SHORT_SCAN + delimiter);
		header.append(EVENTS_NUMBER_FROM_LONG_SCAN + delimiter);
		header.append(SOLAR_RAY_NUMBER + delimiter);
		header.append(SOLAR_RAY_NUMBER_FROM_SHORT_SCAN + delimiter);
		header.append(SOLAR_RAY_NUMBER_FROM_LONG_SCAN + delimiter);
		header.append(EFFICIENCY + delimiter);

		header.append(SUNRISE_DAYTIME + delimiter);
		header.append(SUNSET_DAYTIME + delimiter);
		header.append(SUNRISE_AZIMUTH + delimiter);
		header.append(SUNSET_AZIMUTH + delimiter);

		header.append(ScansunPowerFitSolution.toStringHeader(delimiter)
				+ delimiter);
		header.append(PEAK_SOLAR_POWER + delimiter);

		header.append(SITE_SOLAR_FLUX + delimiter);
		header.append(SITE_SOLAR_FLUX_DB + delimiter);

		header.append(DRAO_SOLAR_FLUX_OBSERVED + delimiter);
		header.append(DRAO_SOLAR_FLUX_ADJUSTED + delimiter);
		header.append(DRAO_SOLAR_FLUX_ADJUSTED_DB);

		return header.toString();
	}

	public void printRawResults(ScansunSite site) {
		if (sitedays.get(site) == null) {
			printer.println(COMMENT + "No results found for sitename = "
					+ site.getSiteName());
			return;
		}

		StringBuilder header = new StringBuilder();
		header.append("#");
		for (int i = 0; i < 200; i++) {
			header.append("=");
		}
		header.append("\n#" + (new ScansunEvent()).toStringHeader(DELIMITER)
				+ NEWLINE);
		printer.println(header.toString());

		boolean noResults = true;
		Set<ScansunEvent> events = eventsContainer
				.getEvents()
				.bySite(site)
				.byMeanPowerCalibrationMode(
						ScansunMeanPowerCalibrationMode.CALIBRATED);

		if (events.size() != 0) {
			noResults = false;
		}

		for (ScansunEvent event : events) {
			printer.println(event.toString(DELIMITER));
		}

		if (noResults) {
			log.printMsg("SCANSUN: no results matching selected parameters",
					Log.TYPE_WARNING, Log.MODE_VERBOSE);
		}
		printer.print(NEWLINE);
	}

}
