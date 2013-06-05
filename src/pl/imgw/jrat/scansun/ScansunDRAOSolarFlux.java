/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.AplicationConstans.DATA;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DATE_SEPARATOR;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DATE_TIME_FORMAT_LONG;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DRAO_SOLARFLUXFILE_BASENAME;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DRAO_SOLARFLUXFILE_EXT;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_RESULTSFILE_DELIMITER;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.imgw.jrat.tools.in.FilePatternFilter;
import pl.imgw.jrat.tools.in.RegexFileFilter;
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

public class ScansunDRAOSolarFlux {

    private static Log log = LogManager.getLogger();
	private static ScansunDRAOSolarFlux manager = new ScansunDRAOSolarFlux();

	public static ScansunDRAOSolarFlux getManager() {
		return manager;
	}

	class SolarFlux {
		Date date;
		double julianDay;
		double carringtonRotation;
		double observedFlux;
		double adjustedFlux;
		double URSIFlux;

		SolarFlux() {
		}

		Date getDate() {
			return date;
		}

		ScansunDay getDay() {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			return new ScansunDay(cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
		}

		double getAdjustedFlux() {
			return adjustedFlux;
		}

		void parseLine(String line) {
			String[] header = manager.getStringHeader(
					SCANSUN_RESULTSFILE_DELIMITER).split(
					SCANSUN_RESULTSFILE_DELIMITER);
			String[] words = line.split(SCANSUN_RESULTSFILE_DELIMITER);

			if (words.length != header.length) {
				log.printMsg(
						"SCANSUN: ScansunDRAOSolarFlux parseLine error", Log.TYPE_ERROR, Log.MODE_VERBOSE);
				return;
			}
			try {

				date = SCANSUN_DATE_TIME_FORMAT_LONG.parse(words[0]
						+ SCANSUN_DATE_SEPARATOR + words[1]);
				julianDay = Double.parseDouble(words[2]);
				carringtonRotation = Double.parseDouble(words[3]);
				observedFlux = Double.parseDouble(words[4]);
				adjustedFlux = Double.parseDouble(words[5]);
				URSIFlux = Double.parseDouble(words[6]);
			} catch (ParseException e) {
				log
						.printMsg(
								"SCANSUN: ScansunDRAOSolarFlux parseLine wrong format: ",
								Log.TYPE_WARNING, Log.MODE_VERBOSE);
				return;
			}
		}

	}

	private String getStringHeader(String delimiter) {
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

	public double getSolarFlux(ScansunDay day) {

		double solarFlux = 0.0;

		FilePatternFilter filter = new RegexFileFilter();
		List<File> fileList = filter.getFileList(DATA + "/*");

		String filenameRegex = SCANSUN_DRAO_SOLARFLUXFILE_BASENAME + "_\\d{8}."
				+ SCANSUN_DRAO_SOLARFLUXFILE_EXT;
		Pattern p = Pattern.compile(filenameRegex);
		String filename = null;
		int i = 0;
		for (File f : fileList) {
			Matcher m = p.matcher(f.getName());
			while (m.find()) {
				filename = m.group();
				i++;
			}
		}

		if (filename == null || i != 1) {
			log.printMsg(
					"SCANSUN: DRAO results file problem.", Log.TYPE_ERROR, Log.MODE_VERBOSE);
		}

		File file = null;
		for (File f : fileList) {
			if (f.getName().equals(filename)) {
				file = f;
				break;
			}
		}

		if (file == null) {
			log.printMsg(
					"SCANSUN: DRAO results file problem.", Log.TYPE_ERROR, Log.MODE_VERBOSE);
		}

		int beginIndex = SCANSUN_DRAO_SOLARFLUXFILE_BASENAME.length() + 1;
		int endIndex = beginIndex + 8;
		String date = file.getName().substring(beginIndex, endIndex);
		log.printMsg(
				"SCANSUN: DRAO results file date is " + date, Log.TYPE_NORMAL, Log.MODE_VERBOSE);
		try {
			Scanner scanner = new Scanner(file);

			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.startsWith("#"))
					continue;

				SolarFlux sf = manager.new SolarFlux();
				// SolarFlux sf = new SolarFlux();
				sf.parseLine(line);

				if (sf.getDay().equals(day))
					solarFlux += sf.getAdjustedFlux();
			}
			scanner.close();
			solarFlux /= 3.0;
		} catch (FileNotFoundException e) {
		}

		return solarFlux;
	}
}
