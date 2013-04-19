/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.AplicationConstans.*;
import static pl.imgw.jrat.scansun.ScansunConstants.*;
import static pl.imgw.jrat.tools.out.Logging.ERROR;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.*;

import pl.imgw.jrat.tools.in.FilePatternFilter;
import pl.imgw.jrat.tools.in.RegexFileFilter;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */

public class ScansunDRAOSolarFlux {

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
				LogHandler.getLogs().displayMsg(
						"SCANSUN: ScansunDRAOSolarFlux parseLine error", ERROR);
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
				LogHandler
						.getLogs()
						.displayMsg(
								"SCANSUN: ScansunDRAOSolarFlux parseLine wrong format: ",
								Logging.WARNING);
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
			LogHandler.getLogs().displayMsg(
					"SCANSUN: DRAO results file problem.", Logging.ERROR);
		}

		File file = null;
		for (File f : fileList) {
			if (f.getName().equals(filename)) {
				file = f;
				break;
			}
		}

		if (file == null) {
			LogHandler.getLogs().displayMsg(
					"SCANSUN: DRAO results file problem.", Logging.ERROR);
		}

		int beginIndex = SCANSUN_DRAO_SOLARFLUXFILE_BASENAME.length() + 1;
		int endIndex = beginIndex + 8;
		String date = file.getName().substring(beginIndex, endIndex);
		LogHandler.getLogs().displayMsg(
				"SCANSUN: DRAO results file date is " + date, Logging.NORMAL);
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
