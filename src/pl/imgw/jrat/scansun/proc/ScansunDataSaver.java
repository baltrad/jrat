/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import pl.imgw.jrat.scansun.data.ScansunEvent;
import pl.imgw.jrat.scansun.data.ScansunScanResult;
import pl.imgw.util.Log;
import static pl.imgw.jrat.scansun.data.ScansunConstants.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunDataSaver extends ScansunDataHandler {

	public static void saveResults(ScansunScanResult results) {

		Iterator<ScansunEvent> itr = results.iterator();

		while (itr.hasNext()) {
			ScansunEvent event = itr.next();

			File file = getResultsFile(event.getLocalDate());
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
					pw.println(COMMENT + event.toStringHeader());
				}
				pw.println(event);

				log.printMsg("SCANSUN: Saving results complete",
						Log.TYPE_NORMAL, Log.MODE_VERBOSE);
			} catch (FileNotFoundException e) {
				log.printMsg("SCANSUN: Cannot create result file in path "
						+ file.getAbsolutePath() + "\n" + e.getMessage(),
						Log.TYPE_ERROR, Log.MODE_VERBOSE);

			} catch (IOException e) {
				log.printMsg("SCANSUN: Cannot create result file in path "
						+ file.getAbsolutePath() + "\n" + e.getMessage(),
						Log.TYPE_ERROR, Log.MODE_VERBOSE);

			} finally {
				if (pw != null) {
					pw.close();
				}
			}
		}
	}
}