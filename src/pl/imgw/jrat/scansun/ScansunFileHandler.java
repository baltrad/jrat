/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.AplicationConstans.ETC;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_DATE_FORMAT;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_FOLDER;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_RESULTSFILE_BASENAME;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_RESULTSFILE_DELIMITER;
import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_RESULTSFILE_EXT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunFileHandler {

    private static Log log = LogManager.getLogger();
    
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

		String filename = SCANSUN_RESULTSFILE_BASENAME + "_"
				+ SCANSUN_DATE_FORMAT.format(event.getDate()) + "."
				+ SCANSUN_RESULTSFILE_EXT;

		return (new File(getScansunPath(), filename)).getPath();
	}

	public static boolean saveResult(ScansunEvent event) {

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
				pw.print("#"
						+ ScansunEvent
								.getStringHeader(SCANSUN_RESULTSFILE_DELIMITER)
						+ "\n");
			}
			pw.println(event
					.toStringWithDelimiter(SCANSUN_RESULTSFILE_DELIMITER));

		} catch (FileNotFoundException e) {
			log.printMsg(
					"SCANSUN: Cannot create result file in path "
							+ file.getAbsolutePath() + "\n" + e.getMessage(),
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return false;
		} catch (IOException e) {
			log.printMsg(
					"SCANSUN: Cannot create result file in path "
							+ file.getAbsolutePath() + "\n" + e.getMessage(),
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
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