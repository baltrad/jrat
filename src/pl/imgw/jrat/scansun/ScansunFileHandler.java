/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.AplicationConstans.*;
import static pl.imgw.jrat.scansun.ScansunConstants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunFileHandler {

    private ScansunFileHandler() {
    }

    public static String getScansunPath() {

	if (ETC.isEmpty()) {
	    new File(SCANSUN_FOLDER).mkdirs();
	    return (new File(SCANSUN_FOLDER)).getPath();
	}
	(new File(ETC, SCANSUN_FOLDER)).mkdirs();

	return (new File(ETC, SCANSUN_FOLDER)).getPath();
    }

    private static String getResultsPath(ScansunEvent event) {

	String filename = SCANSUN_RESULTSFILE_BASENAME + "_" + SCANSUN_DATE_FORMAT.format(event.getDate()) + "."
		+ SCANSUN_RESULTSFILE_EXT;

	return (new File(getScansunPath(), filename)).getPath();
    }

    public static boolean saveEvent(ScansunEvent event) {

	File file = new File(getResultsPath(event));
	PrintWriter pw = null;
	boolean newfile = false;

	try {

	    if (!file.exists()) {
		file.createNewFile();
		newfile = true;
	    }
	    pw = new PrintWriter(new FileOutputStream(file, true), true);

	    /*
	     * Creating header
	     */
	    if (newfile) {
		pw.print("#" + event.getStringHeaderWithDelimiter(SCANSUN_RESULTSFILE_DELIMITER) + "\n");
	    }
	    pw.println(event.toStringWithDelimiter(SCANSUN_RESULTSFILE_DELIMITER));

	} catch (FileNotFoundException e) {
	    LogHandler.getLogs().displayMsg(
		    "SCANSUN: Cannot create result file in path " + file.getAbsolutePath() + "\n" + e.getMessage(),
		    LogHandler.ERROR);
	    return false;
	} catch (IOException e) {
	    LogHandler.getLogs().displayMsg(
		    "SCANSUN: Cannot create result file in path " + file.getAbsolutePath() + "\n" + e.getMessage(),
		    LogHandler.ERROR);
	    return false;
	} finally {
	    if (pw != null) {
		pw.close();
	    }
	}

	return true;
    }

    public static String getResultsfile() {
	return SCANSUN_RESULTSFILE_EXT;
    }

}