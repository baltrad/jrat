/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.imgw.jrat.tools.in.FilePatternFilter;
import pl.imgw.jrat.tools.in.LineParseTool;
import pl.imgw.jrat.tools.in.RegexFileFilter;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

import static pl.imgw.jrat.AplicationConstans.*;
import static pl.imgw.jrat.scansun.ScansunConstants.*;
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

    private File resultsFile;
    private ArrayList<ScansunSolarFluxObservation> solarFluxes;
    private static ScansunDRAOSolarFlux manager = new ScansunDRAOSolarFlux();

    public static ScansunDRAOSolarFlux getManager() {
	return manager;
    }

    private ScansunDRAOSolarFlux() {
	resultsFile = getResultsFile();
	solarFluxes = getSolarFluxes();
    }

    private File getResultsFile() {
	FilePatternFilter filter = new RegexFileFilter();
	List<File> fileList = filter.getFileList(DATA + "/*");

	String filenameRegex = SCANSUN_DRAO_SOLARFLUXFILE_BASENAME + "_\\d{8}." + SCANSUN_DRAO_SOLARFLUXFILE_EXT;
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
	    LogHandler.getLogs().displayMsg("SCANSUN: DRAO results file problem.", Logging.ERROR);
	}

	File resultsFile = null;
	for (File f : fileList) {
	    if (f.getName().equals(filename)) {
		resultsFile = f;
		break;
	    }
	}

	if (resultsFile == null) {
	    LogHandler.getLogs().displayMsg("SCANSUN: DRAO results file problem.", Logging.ERROR);
	}

	int beginIndex = SCANSUN_DRAO_SOLARFLUXFILE_BASENAME.length() + 1;
	int endIndex = beginIndex + 8;
	String date = resultsFile.getName().substring(beginIndex, endIndex);
	LogHandler.getLogs().displayMsg("SCANSUN: DRAO results file date is " + date, Logging.NORMAL);

	return resultsFile;
    }

    private ArrayList<ScansunSolarFluxObservation> getSolarFluxes() {
	ArrayList<ScansunSolarFluxObservation> solarFluxes = new ArrayList<>();

	try {
	    Scanner scanner = new Scanner(resultsFile);

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

    public double getSolarFlux(ScansunDay day) {

	double solarFlux = 0.0;

	for (ScansunSolarFluxObservation sfo : solarFluxes) {
	    if (sfo.getDay().equals(day)) {
		solarFlux += sfo.getAdjustedFlux();
	    }
	}

	return solarFlux /= 3.0;
    }

    public double getAdjustedSolarFlux(ScansunDay day) {
	return (0.71 * (getSolarFlux(day) - 64.0) + 126.0);
    }

}