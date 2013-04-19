/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.*;
import java.text.*;
import java.io.*;

import pl.imgw.jrat.scansun.ScansunConstants.Sites;
import pl.imgw.jrat.tools.out.*;
import static pl.imgw.jrat.scansun.ScansunConstants.*;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunResultsPrinter {

	private ScansunResultParsedParameters params;
	private Set<File> resultsFiles;
	private Set<String> resultsSiteNames;

	public ScansunResultsPrinter(ScansunResultParsedParameters params) {
		this.params = params;

		resultsFiles = readResultsFiles();
		resultsSiteNames = readResultsSiteNames(resultsFiles);
	}

	private Set<File> readResultsFiles() {
		Set<File> files = new HashSet<File>();

		File folder = new File(ScansunFileHandler.getScansunPath());

		if (!folder.isDirectory()) {
			return files;
		}

		for (File file : folder.listFiles()) {
			if (file.isFile()
					&& file.getName().endsWith(
							ScansunFileHandler.getResultsfile())) {
				files.add(file);
			}
		}

		return files;
	}

	private Set<String> readResultsSiteNames(Set<File> files) {

		Set<String> siteNames = new TreeSet<String>();

		for (File file : files) {
			try {

				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith("#"))
						continue;

					ScansunEvent event = ScansunEvent.parseLine(line);
					siteNames.add(event.getSiteName());
				}

				scanner.close();
			} catch (FileNotFoundException e) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: Results file not found: " + file,
						Logging.WARNING);
			}
		}

		return siteNames;
	}

	public Set<File> getResultsFiles() {
		return resultsFiles;
	}

	public Set<String> getResultsSiteNames() {
		return resultsSiteNames;
	}

	public void printResults() {

		if (params.allSites()) {
			for (String siteName : resultsSiteNames) {
				printResults(siteName, resultsFiles);
			}
		} else {
			printResults(params.getSiteName(), resultsFiles);
		}

	}

	enum TimesIndicator {
		FIRST, LAST
	}

	enum SunIndicator {
		SUNRISE, SUNSET
	}

	public void printResults(String siteName, Set<File> files) {

		ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();

		if (files.isEmpty()) {
			printer.println("# No results found");
			return;
		}

		SortedSet<ScansunDay> days = getDays(siteName, files);

		Map<ScansunDay, Integer> events125 = getDetectionHistogram(siteName,
				files, EventType.EVENT, PulseDuration.SHORT);
		Map<ScansunDay, Integer> events250 = getDetectionHistogram(siteName,
				files, EventType.EVENT, PulseDuration.LONG);

		Map<ScansunDay, Integer> solarRays125 = getDetectionHistogram(siteName,
				files, EventType.SOLARRAY, PulseDuration.SHORT);
		Map<ScansunDay, Integer> solarRays250 = getDetectionHistogram(siteName,
				files, EventType.SOLARRAY, PulseDuration.LONG);

		Map<ScansunDay, Double> firstTime = getTimes(siteName, files,
				TimesIndicator.FIRST);
		Map<ScansunDay, Double> sunriseTime = getSunTimes(siteName, days,
				SunIndicator.SUNRISE);
		Map<ScansunDay, Double> lastTime = getTimes(siteName, files,
				TimesIndicator.LAST);
		Map<ScansunDay, Double> sunsetTime = getSunTimes(siteName, days,
				SunIndicator.SUNSET);

		Map<ScansunDay, Double> firstElevation = getElevations(siteName, files,
				TimesIndicator.FIRST);
		Map<ScansunDay, Double> lastElevation = getElevations(siteName, files,
				TimesIndicator.LAST);

		Map<ScansunDay, Double> firstAzimuth = getAzimuths(siteName, files,
				TimesIndicator.FIRST);
		Map<ScansunDay, Double> sunriseAzimuth = getSunAzimuths(siteName, days,
				SunIndicator.SUNRISE);
		Map<ScansunDay, Double> lastAzimuth = getAzimuths(siteName, files,
				TimesIndicator.LAST);
		Map<ScansunDay, Double> sunsetAzimuth = getSunAzimuths(siteName, days,
				SunIndicator.SUNSET);

		Map<ScansunDay, Double> minSolarPower = getMinSolarPowers(siteName,
				files);
		Map<ScansunDay, Double> maxSolarPower = getMaxSolarPowers(siteName,
				files);
		Map<ScansunDay, Double> meanSolarPower = getMeanSolarPowers(siteName,
				files);
		Map<ScansunDay, List<Double>> coefficients = getFitCoefficients(
				siteName, files);
		Map<ScansunDay, Double> solarFlux = getSolarFlux(siteName, files);
		Map<ScansunDay, Double> solarFluxDRAO = getSolarFluxDRAO(days);

		if (days.isEmpty()) {
			printer.println("# No results found for sitename = " + siteName);
			return;
		}

		StringBuilder header = new StringBuilder();
		header.append("#");
		for (int i = 0; i < 50; i++) {
			header.append("=");
		}
		header.append("\n#Sitename = ");
		header.append(siteName);
		header.append("\n#");
		header.append("\tdate "); // 1
		header.append("\tdayOfYear "); // 2
		header.append("\teventsNumber "); // 3
		header.append("\teventsNumber125 ");// 4
		header.append("\teventsNumber250 ");// 5
		header.append("\tsolarRaysNumber "); // 6
		header.append("\tsolarRaysNumber125 ");// 7
		header.append("\tsolarRaysNumber250 ");// 8
		header.append("\tefficiency ");// 9
		header.append("\tminDetectionTime ");// 10
		header.append("\tsunriseTime ");// 11
		header.append("\tmaxDetectionTime ");// 12
		header.append("\tsunsetTime ");// 13
		header.append("\tminDetectionElevation ");// 14
		header.append("\tmaxDetectionElevation ");// 15
		header.append("\tminDetectionAzimuth ");// 16
		header.append("\tsunriseAzimuth ");// 17
		header.append("\tmaxDetectionAzimuth ");// 18
		header.append("\tsunsetAzimuth ");// 19
		header.append("\tminSolarPower ");// 20
		header.append("\tmaxSolarPower ");// 21
		header.append("\tmeanSolarPower ");// 22
		header.append("\tax ");// 23
		header.append("\tay ");// 24
		header.append("\tbx ");// 25
		header.append("\tby ");// 26
		header.append("\tc");// 27
		header.append("\tpeakSolarPower ");// 28
		header.append("\tsolarFlux");// 29
		header.append("\tsolarFluxDRAO10.7cm");// 30
		header.append("\tsolarFluxDRAOAdjusted");// 31
		header.append("\tsolarFluxDRAOAdjusteddB");// 32
		printer.println(header.toString());

		DecimalFormat df = new DecimalFormat("#.##");

		for (ScansunDay day : days) {
			StringBuilder line = new StringBuilder();
			line.append(day.toString() + "\t");
			line.append(day.getDayOfYear() + "\t");

			int eventsNumber = events125.get(day) + events250.get(day);
			line.append(eventsNumber + "\t");
			line.append(events125.get(day) + "\t");
			line.append(events250.get(day) + "\t");

			int solarRaysNumber = 0;
			if (solarRays125.get(day) != null)
				solarRaysNumber += solarRays125.get(day);
			if (solarRays250.get(day) != null)
				solarRaysNumber += solarRays250.get(day);

			line.append(solarRaysNumber + "\t");
			line.append(solarRays125.get(day) + "\t");
			line.append(solarRays250.get(day) + "\t");

			line.append(df
					.format(((double) solarRaysNumber / eventsNumber) * 100.0)
					+ "\t");

			line.append(df.format(firstTime.get(day)) + "\t");
			line.append(df.format(sunriseTime.get(day)) + "\t");
			line.append(df.format(lastTime.get(day)) + "\t");
			line.append(df.format(sunsetTime.get(day)) + "\t");
			line.append(df.format(firstElevation.get(day)) + "\t");
			line.append(df.format(lastElevation.get(day)) + "\t");
			line.append(df.format(firstAzimuth.get(day)) + "\t");
			line.append(df.format(sunriseAzimuth.get(day)) + "\t");
			line.append(df.format(lastAzimuth.get(day)) + "\t");
			line.append(df.format(sunsetAzimuth.get(day)) + "\t");
			line.append(df.format(minSolarPower.get(day)) + "\t");
			line.append(df.format(maxSolarPower.get(day)) + "\t");
			line.append(df.format(meanSolarPower.get(day)) + "\t");

			double ax = coefficients.get(day).get(0);
			double ay = coefficients.get(day).get(1);
			double bx = coefficients.get(day).get(2);
			double by = coefficients.get(day).get(3);
			double c = coefficients.get(day).get(4);

			double P0 = c - bx * bx / (4.0 * ax) - by * by / (4.0 * ay);

			line.append(df.format(ax) + "\t");
			line.append(df.format(ay) + "\t");
			line.append(df.format(bx) + "\t");
			line.append(df.format(by) + "\t");
			line.append(df.format(c) + "\t");
			line.append(df.format(P0) + "\t");
			line.append(df.format(solarFlux.get(day)) + "\t");

			double sfDRAO = solarFluxDRAO.get(day);
			line.append(df.format(sfDRAO) + "\t");
			double sfDRAOAdjusted = ScansunEquations
					.solarFluxDRAOAdjustment(sfDRAO);
			line.append(df.format(sfDRAOAdjusted) + "\t");
			line.append(df.format(ScansunEquations.toDecibelScale(
					sfDRAOAdjusted, 1.0)) + "\t");

			printer.println(line.toString());
		}

	}

	protected SortedSet<ScansunDay> getDays(String siteName, Set<File> files) {

		SortedSet<ScansunDay> days = new TreeSet<ScansunDay>();

		for (File file : files) {
			try {
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith("#"))
						continue;

					ScansunEvent event = ScansunEvent.parseLine(line);
					if (event.getSiteName().equals(siteName))
						days.add(event.getDay());
				}

				scanner.close();
			} catch (FileNotFoundException e) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: Results file not found: " + file,
						Logging.WARNING);
			}
		}

		return days;
	}

	protected Map<ScansunDay, Integer> getDetectionHistogram(String siteName,
			Set<File> files, EventType et, PulseDuration pd) {
		Map<ScansunDay, Integer> histogram = new HashMap<ScansunDay, Integer>();

		for (File file : files) {
			try {

				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith("#"))
						continue;

					ScansunEvent event = ScansunEvent.parseLine(line);

					if (event.getSiteName().equalsIgnoreCase(siteName)) {
						if (et.isSolarRay() == event.isSolarRay()) {
							if (pd == event.getPulseDuration()) {
								Integer n = histogram.get(event.getDay());
								histogram.put(event.getDay(), n == null ? 1
										: n + 1);
							}
						}
					} // siteName
				} // while

				scanner.close();
			} catch (FileNotFoundException e) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: Results file not found: " + file,
						Logging.WARNING);
			}
		}

		return histogram;
	}

	protected Integer getTotalNumber(String siteName, Set<File> files,
			EventType t) {

		int number = 0;

		Map<ScansunDay, Integer> histogram125 = getDetectionHistogram(siteName,
				files, t, PulseDuration.SHORT);
		Map<ScansunDay, Integer> histogram250 = getDetectionHistogram(siteName,
				files, t, PulseDuration.LONG);

		for (ScansunDay day : getDays(siteName, files)) {

			if (histogram125.get(day) != null)
				number += histogram125.get(day);

			if (histogram250.get(day) != null)
				number += histogram250.get(day);
		}

		return number;
	}

	protected Integer getDetectionHistogramMax(String siteName,
			Set<File> files, EventType t) {

		int max = 0;

		Map<ScansunDay, Integer> histogram125 = getDetectionHistogram(siteName,
				files, t, PulseDuration.SHORT);
		Map<ScansunDay, Integer> histogram250 = getDetectionHistogram(siteName,
				files, t, PulseDuration.LONG);

		for (ScansunDay day : getDays(siteName, files)) {
			if (histogram125.get(day) + histogram250.get(day) > max)
				max = histogram125.get(day) + histogram250.get(day);
		}

		return max;
	}

	private Map<ScansunDay, Double> getSunTimes(String siteName,
			SortedSet<ScansunDay> days, SunIndicator si) {

		Map<ScansunDay, Double> sunTimes = new HashMap<ScansunDay, Double>();

		double longitude = Sites.getLongitude(siteName);
		double latitude = Sites.getLatitude(siteName);
		double altitude = Sites.getAltitude(siteName);

		ScansunSpa spa = new ScansunSpa(longitude, latitude, altitude);

		for (ScansunDay day : days) {
			if (si == SunIndicator.SUNRISE)
				sunTimes.put(day, spa.getSunriseTime(day));
			else if (si == SunIndicator.SUNSET)
				sunTimes.put(day, spa.getSunsetTime(day));
		}

		return sunTimes;
	}

	private Map<ScansunDay, Double> getSunAzimuths(String siteName,
			SortedSet<ScansunDay> days, SunIndicator si) {

		Map<ScansunDay, Double> sunAzimuths = new HashMap<ScansunDay, Double>();

		double longitude = Sites.getLongitude(siteName);
		double latitude = Sites.getLatitude(siteName);
		double altitude = Sites.getAltitude(siteName);

		ScansunSpa spa = new ScansunSpa(longitude, latitude, altitude);

		for (ScansunDay day : days) {
			if (si == SunIndicator.SUNRISE)
				sunAzimuths.put(day, spa.getSunriseAzimuth(day));
			else if (si == SunIndicator.SUNSET)
				sunAzimuths.put(day, spa.getSunsetAzimuth(day));
		}

		return sunAzimuths;
	}

	private Map<ScansunDay, Double> getTimes(String siteName, Set<File> files,
			TimesIndicator ti) {

		Map<ScansunDay, Double> times = new HashMap<ScansunDay, Double>();

		for (File file : files) {
			try {

				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith("#"))
						continue;

					ScansunEvent event = ScansunEvent.parseLine(line);

					if (event.getSiteName().equalsIgnoreCase(siteName)
							&& event.isSolarRay()) {

						Double t = times.get(event.getDay());
						if (t == null) {
							times.put(event.getDay(), event.getTime());
						} else {
							if (ti == TimesIndicator.FIRST) {
								if (t > event.getTime())
									times.put(event.getDay(), event.getTime());
							} else if (ti == TimesIndicator.LAST) {
								if (t < event.getTime())
									times.put(event.getDay(), event.getTime());
							}
						}
					}
				}

				scanner.close();
			} catch (FileNotFoundException e) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: Results file not found: " + file,
						Logging.WARNING);
			}
		}

		return times;
	}

	private Map<ScansunDay, Double> getElevations(String siteName,
			Set<File> files, TimesIndicator ti) {
		Map<ScansunDay, Double> elevations = new HashMap<ScansunDay, Double>();

		for (File file : files) {
			try {

				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith("#"))
						continue;

					ScansunEvent event = ScansunEvent.parseLine(line);

					if (event.getSiteName().equalsIgnoreCase(siteName)
							&& event.isSolarRay()) {

						Double e = elevations.get(event.getDay());
						if (e == null) {
							elevations.put(event.getDay(),
									event.getRadarElevation());
						} else {
							if (ti == TimesIndicator.FIRST) {
								if (e > event.getRadarElevation())
									elevations.put(event.getDay(),
											event.getRadarElevation());
							} else if (ti == TimesIndicator.LAST) {
								if (e < event.getRadarElevation())
									elevations.put(event.getDay(),
											event.getRadarElevation());
							}
						}
					}
				}

				scanner.close();
			} catch (FileNotFoundException e) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: Results file not found: " + file,
						Logging.WARNING);
			}
		}

		return elevations;
	}

	private Map<ScansunDay, Double> getAzimuths(String siteName,
			Set<File> files, TimesIndicator ti) {
		Map<ScansunDay, Double> azimuths = new HashMap<ScansunDay, Double>();

		for (File file : files) {
			try {

				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith("#"))
						continue;

					ScansunEvent event = ScansunEvent.parseLine(line);

					if (event.getSiteName().equalsIgnoreCase(siteName)
							&& event.isSolarRay()) {

						Double a = azimuths.get(event.getDay());
						if (a == null) {
							azimuths.put(event.getDay(),
									event.getRadarAzimuth());
						} else {
							if (ti == TimesIndicator.FIRST) {
								if (a > event.getRadarAzimuth())
									azimuths.put(event.getDay(),
											event.getRadarAzimuth());
							} else if (ti == TimesIndicator.LAST) {
								if (a < event.getRadarAzimuth())
									azimuths.put(event.getDay(),
											event.getRadarAzimuth());
							}
						}
					}
				}

				scanner.close();
			} catch (FileNotFoundException e) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: Results file not found: " + file,
						Logging.WARNING);
			}
		}

		return azimuths;
	}

	private Map<ScansunDay, List<Double>> getSolarPowers(String siteName,
			Set<File> files) {

		Map<ScansunDay, List<Double>> solarPowers = new HashMap<ScansunDay, List<Double>>();

		for (File file : files) {
			try {

				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith("#"))
						continue;

					ScansunEvent event = ScansunEvent.parseLine(line);

					if (event.getSiteName().equalsIgnoreCase(siteName)
							&& event.isSolarRay()) {

						List<Double> p = solarPowers.get(event.getDay());
						if (p == null)
							p = new ArrayList<Double>();

						p.add(event.getMeanSolarPower());
						solarPowers.put(event.getDay(), p);
					}
				}

				scanner.close();
			} catch (FileNotFoundException e) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: Results file not found: " + file,
						Logging.WARNING);
			}
		}

		return solarPowers;
	}

	private Map<ScansunDay, Double> getMeanSolarPowers(String siteName,
			Set<File> files) {

		Map<ScansunDay, Double> meanSolarPowers = new HashMap<ScansunDay, Double>();
		Map<ScansunDay, List<Double>> solarPowers = getSolarPowers(siteName,
				files);

		for (ScansunDay day : solarPowers.keySet()) {

			List<Double> p = solarPowers.get(day);
			Iterator<Double> itr = p.iterator();

			double v = 0.0;
			while (itr.hasNext()) {
				v += itr.next();
			}
			meanSolarPowers.put(day, v / p.size());
		}

		return meanSolarPowers;
	}

	private Map<ScansunDay, Double> getMinSolarPowers(String siteName,
			Set<File> files) {

		Map<ScansunDay, Double> minSolarPowers = new HashMap<ScansunDay, Double>();
		Map<ScansunDay, List<Double>> solarPowers = getSolarPowers(siteName,
				files);

		for (ScansunDay day : solarPowers.keySet()) {

			List<Double> p = solarPowers.get(day);
			Iterator<Double> itr = p.iterator();

			double min = itr.next();
			while (itr.hasNext()) {
				double v = itr.next();
				if (v < min)
					min = v;
			}
			minSolarPowers.put(day, min);
		}

		return minSolarPowers;
	}

	private Map<ScansunDay, Double> getMaxSolarPowers(String siteName,
			Set<File> files) {

		Map<ScansunDay, Double> maxSolarPowers = new HashMap<ScansunDay, Double>();

		Map<ScansunDay, List<Double>> solarPowers = getSolarPowers(siteName,
				files);

		for (ScansunDay day : solarPowers.keySet()) {

			List<Double> p = solarPowers.get(day);
			Iterator<Double> itr = p.iterator();

			double max = itr.next();
			while (itr.hasNext()) {
				double v = itr.next();
				if (v > max)
					max = v;
			}
			maxSolarPowers.put(day, max);
		}

		return maxSolarPowers;
	}

	protected Double getMeanSolarPower(String siteName, Set<File> files) {

		double meanPower = 0.0;

		for (ScansunDay day : getDays(siteName, files)) {
			if (getMeanSolarPowers(siteName, files).get(day) != null)
				meanPower += getMeanSolarPowers(siteName, files).get(day);
		}

		return meanPower / getDays(siteName, files).size();
	}

	protected Double getMinSolarPower(String siteName, Set<File> files) {

		double minPower = 0.0;

		for (ScansunDay day : getDays(siteName, files)) {
			minPower += getMinSolarPowers(siteName, files).get(day);
		}

		return minPower / getDays(siteName, files).size();
	}

	protected Double getMaxSolarPower(String siteName, Set<File> files) {

		double maxPower = 0.0;

		for (ScansunDay day : getDays(siteName, files)) {
			maxPower += getMaxSolarPowers(siteName, files).get(day);
		}

		return maxPower / getDays(siteName, files).size();
	}

	private Map<ScansunDay, List<Double>> getFitCoefficients(String siteName,
			Set<File> files) {

		Map<ScansunDay, List<Double>> coefficients = new HashMap<ScansunDay, List<Double>>();

		Map<ScansunDay, List<ScansunEvent>> events = new HashMap<ScansunDay, List<ScansunEvent>>();

		for (File file : files) {
			try {

				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith("#"))
						continue;

					ScansunEvent event = ScansunEvent.parseLine(line);

					if (event.getSiteName().equalsIgnoreCase(siteName)
							&& event.isSolarRay()) {

						List<ScansunEvent> e = events.get(event.getDay());
						if (e == null)
							e = new ArrayList<ScansunEvent>();

						e.add(event);
						events.put(event.getDay(), e);
					}
				}

				scanner.close();
			} catch (FileNotFoundException e) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: Results file not found: " + file,
						Logging.WARNING);
			}
		}

		for (ScansunDay day : events.keySet()) {

			ScansunPowerFit llsFitCoefficients = new ScansunPowerFit();

			for (ScansunEvent event : events.get(day)) {
				llsFitCoefficients.addData(
						ScansunEquations.Ps(event.getMeanSolarPower(),
								event.getSunElevation()),
						event.getAzimuthOffset(), event.getElevationOffset());
			}

			llsFitCoefficients.calculate();

			coefficients.put(day, llsFitCoefficients.toArray());
		}

		return coefficients;
	}

	private Map<ScansunDay, Double> getSolarFlux(String siteName,
			Set<File> files) {

		Map<ScansunDay, Double> solarFlux = new HashMap<ScansunDay, Double>();

		double deltaS = Math.toRadians(ScansunConstants.DELTA_S);
		double deltaR = Math.toRadians(ScansunOptionsHandler.getOptions()
				.getRadarParsedParameters(siteName).getBeamwidth());

		double L0 = ScansunEquations.L0(deltaR, deltaS);

		double deltaA = Math.toRadians(1.0);
		double La = ScansunEquations.La(L0, deltaR, deltaA);

		double lambda = ScansunOptionsHandler.getOptions()
				.getRadarParsedParameters(siteName).getWavelength() / 100.0;
		double GdBi = ScansunOptionsHandler.getOptions()
				.getRadarParsedParameters(siteName).getAntennaGain();
		double Ae = ScansunEquations.Ae(lambda, GdBi);

		Map<ScansunDay, List<Double>> coefficients = getFitCoefficients(
				siteName, files);

		for (ScansunDay day : coefficients.keySet()) {
			double ax = coefficients.get(day).get(0);
			double ay = coefficients.get(day).get(1);
			double bx = coefficients.get(day).get(2);
			double by = coefficients.get(day).get(3);
			double c = coefficients.get(day).get(4);

			double P0 = c - bx * bx / (4.0 * ax) - by * by / (4.0 * ay);

			solarFlux.put(day, ScansunEquations.solarFlux(P0, La, Ae));
		}

		return solarFlux;
	}

	private Map<ScansunDay, Double> getSolarFluxDRAO(Set<ScansunDay> days) {

		Map<ScansunDay, Double> solarFlux = new HashMap<ScansunDay, Double>();

		for (ScansunDay day : days) {
			solarFlux.put(day,
					ScansunDRAOSolarFlux.getManager().getSolarFlux(day));
		}

		return solarFlux;
	}

	public void printRawResults() {

		if (params.allSites()) {
			for (String siteName : resultsSiteNames) {
				printRawResults(siteName, resultsFiles);
			}
		} else {
			printRawResults(params.getSiteName(), resultsFiles);
		}

	}

	protected void printRawResults(String siteName, Set<File> files) {

		ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();

		boolean noResults = true;
		if (files.isEmpty()) {
			printer.println("# No results found");
			return;
		}

		boolean printHeader = true;

		for (File file : files) {

			if (printHeader) {
				StringBuilder header = new StringBuilder();
				header.append("#");
				for (int i = 0; i < 200; i++) {
					header.append("=");
				}
				header.append("\n#" + ScansunEvent.getStringHeader("\t") + "\n");
				printer.println(header.toString());
				printHeader = false;
			}

			try {
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {

					String line = scanner.nextLine();

					if (line.startsWith("#"))
						continue;

					ScansunEvent event = ScansunEvent.parseLine(line);

					if (event.getSiteName().equalsIgnoreCase(siteName)) {
						noResults = false;
						printer.println(event.toStringWithDelimiter("\t"));
					}
				}

				scanner.close();
			} catch (FileNotFoundException e) {
				LogHandler.getLogs().displayMsg(
						"SCANSUN: Results file not found: " + file,
						Logging.WARNING);
			}
		}

		if (noResults) {
			System.out.println("# No results matching selected parameters");
		}
		printer.print("\n");

	}
}