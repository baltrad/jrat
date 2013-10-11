/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.joda.time.LocalDate;

import pl.imgw.jrat.scansun.ScansunException;
import pl.imgw.jrat.scansun.data.ScansunSolarFluxObservation;
import pl.imgw.jrat.scansun.data.ScansunSolarFluxObservation.ScansunSolarFluxObservationFactory;
import pl.imgw.jrat.tools.in.LineParseTool;
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
public class ScansunSolarFluxFileHandler {

	private static Log log = LogManager.getLogger();

	private File datafile = null;

	public static final String SOLARFLUXFILE_BASENAME = "DRAO-solar-flux-table";
	public static final String SOLARFLUXFILE_EXT = "data";
	public static final String SOLARFLUXFILE_DELIMITER = ";";

	private static ScansunSolarFluxFileHandler handler = new ScansunSolarFluxFileHandler();

	private ScansunSolarFluxFileHandler() {
	}

	public static ScansunSolarFluxFileHandler getHandler() {
		return handler;
	}

	public void setDatafile(String fileName) throws ScansunException {

		File solarFluxDataFile = new File(fileName);

		if (solarFluxDataFile.isFile()) {
			datafile = solarFluxDataFile;
		} else {
			throw new ScansunException("Scansun DRAO file: "
					+ solarFluxDataFile + " cannot be found");
		}

		if (!isDatafileProper())
			throw new ScansunException("Scansun DRAO file: "
					+ solarFluxDataFile + " is not valid");
	}

	private boolean isDatafileProper() {

		// int beginIndex = SOLARFLUXFILE_BASENAME.length() + 1;
		// int endIndex = beginIndex + 8;
		// String date = datafile.getName().substring(beginIndex, endIndex);
		// log.printMsg("SCANSUN: DRAO solar flux file date is: " + date,
		// Log.TYPE_NORMAL, Log.MODE_VERBOSE);

		boolean result = true;

		Scanner scanner = null;
		try {
			scanner = new Scanner(datafile);
		} catch (FileNotFoundException e) {
			log.printMsg("SCANSUN: DRAO solar flux file " + datafile.getName()
					+ "cannot be found", Log.TYPE_ERROR, Log.MODE_VERBOSE);
		}

		String line = scanner.nextLine();

		if (!line.startsWith(COMMENT)) {
			result = false;
		}

		if (!ScansunSolarFluxObservation.checkHeader(line.substring(1))) {
			log.printMsg("SCANSUN: DRAO solar flux file header is not valid.",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
		}

		scanner.close();

		return result;
	}

	private Set<ScansunSolarFluxObservation> solarFluxObservationsFinder(
			LocalDate day) {
		Set<ScansunSolarFluxObservation> sfos = new HashSet<ScansunSolarFluxObservation>();

		Scanner scanner;
		try {
			scanner = new Scanner(datafile);
		} catch (FileNotFoundException e) {
			log.printMsg("SCANSUN: DRAO solar flux file " + datafile.getName()
					+ "cannot be found", Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return null;
		}

		while (scanner.hasNext()) {
			String line = scanner.nextLine();

			if (line.startsWith(COMMENT)) {
				continue;
			}

			ScansunSolarFluxObservation solarFluxObservation = LineParseTool
					.parseLine(line, new ScansunSolarFluxObservationFactory(),
							SOLARFLUXFILE_DELIMITER);

			if (solarFluxObservation.getFluxdate().equals(day)) {
				sfos.add(solarFluxObservation);
			}
		}

		scanner.close();

		return sfos;
	}

	public Double getSolarFluxObserved(LocalDate day) {
		double solarFlux = 0.0;
		int n = 0;

		for (ScansunSolarFluxObservation sfo : solarFluxObservationsFinder(day)) {
			solarFlux += sfo.getFluxobsflux();
			n++;
		}

		if (!(solarFlux > 0)) {
			log.printMsg("SCANSUN: no DRAO solar flux data for date " + day,
					Log.TYPE_WARNING, Log.MODE_VERBOSE);
		}

		return solarFlux /= n;
	}

	public Double getSolarFluxAdjusted(LocalDate day) {
		double solarFlux = 0.0;
		int n = 0;

		for (ScansunSolarFluxObservation sfo : solarFluxObservationsFinder(day)) {
			solarFlux += sfo.getFluxadjflux();
			n++;
		}

		return solarFlux /= n;
	}
}
