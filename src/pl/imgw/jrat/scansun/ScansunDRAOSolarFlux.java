/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.tools.in.FilePatternFilter;
import pl.imgw.jrat.tools.in.LineParseTool;
import pl.imgw.jrat.tools.in.RegexFileFilter;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

import static pl.imgw.jrat.AplicationConstans.*;
import static pl.imgw.jrat.process.CommandLineArgsParser.SCANSUN_DRAO;
import static pl.imgw.jrat.scansun.ScansunConstants.*;
import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.NORMAL;
import static pl.imgw.jrat.tools.out.Logging.WARNING;
import pl.imgw.jrat.scansun.ScansunSolarFluxObservation.ScansunSolarFluxObservationFactory;

/**
 * 
 * Class used to extract solar flux values from separate file.
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunDRAOSolarFlux {

    // private File resultsFile;
    private List<ScansunSolarFluxObservation> solarFluxes;
    private static ScansunDRAOSolarFlux manager = new ScansunDRAOSolarFlux();

    public static ScansunDRAOSolarFlux getManager() {
	return manager;
    }

    private ScansunDRAOSolarFlux() {
    }

    private static File getResultsFile(String filename) {

	File file = new File(filename);

	String filenameRegex = SCANSUN_DRAO_SOLARFLUXFILE_BASENAME + "_\\d{8}." + SCANSUN_DRAO_SOLARFLUXFILE_EXT;
	Pattern p = Pattern.compile(filenameRegex);
	Matcher m = p.matcher(file.getName());

	if (!m.matches()) {
	    LogHandler.getLogs().displayMsg("SCANSUN: DRAO results file problem.", Logging.ERROR);
	    return null;
	}

	int beginIndex = SCANSUN_DRAO_SOLARFLUXFILE_BASENAME.length() + 1;
	int endIndex = beginIndex + 8;
	String date = file.getName().substring(beginIndex, endIndex);
	LogHandler.getLogs().displayMsg("SCANSUN: DRAO results file date is " + date, Logging.NORMAL);

	return file;
    }

    private static ArrayList<ScansunSolarFluxObservation> getSolarFluxes(File resultFile) {
	ArrayList<ScansunSolarFluxObservation> solarFluxes = new ArrayList<>();

	try {
	    Scanner scanner = new Scanner(resultFile);

	    while (scanner.hasNext()) {
		String line = scanner.nextLine();
		if (line.startsWith("#"))
		    continue;

		ScansunSolarFluxObservation solarFluxObservation = LineParseTool.parseLine(line, new ScansunSolarFluxObservationFactory());
		solarFluxes.add(solarFluxObservation);

	    }
	    scanner.close();
	} catch (FileNotFoundException e) {
	    LogHandler.getLogs().displayMsg("SCANSUN: DRAO results file not found.", Logging.ERROR);
	}

	return solarFluxes;
    }

    private static List<ScansunSolarFluxObservation> getDefaultSolarFluxes() {

	List<ScansunSolarFluxObservation> solarFluxes = new ArrayList<>();

	solarFluxes.add(ScansunSolarFluxObservation.WILDCARD_SCANSUN_SOLARFLUX_OBSERVATION);

	return solarFluxes;
    }

    public double getSolarFlux(ScansunDay day) {

	double solarFlux = 0.0;
	int n = 0;

	for (ScansunSolarFluxObservation sfo : solarFluxes) {
	    if (sfo.getDay().equals(day)) {
		solarFlux += sfo.getAdjustedFlux();
		n++;
	    }
	}

	return solarFlux /= n;
    }

    public double getAdjustedSolarFlux(ScansunDay day) {
	return (0.71 * (getSolarFlux(day) - 64.0) + 126.0);
    }

    public static boolean withDRAOFileHandling(CommandLine cmd) {

	LogHandler.getLogs().displayMsg("SCANSUN: DRAO solar flux file found - using real Sun solar flux values", NORMAL);

	File resultFile = getResultsFile(cmd.getOptionValue(SCANSUN_DRAO));
	if (resultFile == null) {
	    LogHandler.getLogs().displayMsg("SCANSUN: DRAO result file error", ERROR);
	    return false;
	}

	manager.solarFluxes = getSolarFluxes(resultFile);
	if (manager.solarFluxes == null) {
	    LogHandler.getLogs().displayMsg("SCANSUN: reading DRAO solar fluxes error", ERROR);
	    return false;
	}

	return true;
    }

    public static boolean withoutDRAOFileHandling() {
	LogHandler.getLogs().displayMsg("SCANSUN: no DRAO solar flux file in input - using quiet Sun solar flux values", NORMAL);

	manager.solarFluxes = getDefaultSolarFluxes();

	if (manager.solarFluxes == null) {
	    LogHandler.getLogs().displayMsg("SCANSUN: loading default DRAO solar fluxes rror", ERROR);
	    return false;
	}

	return true;
    }
}