/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat;

import static pl.imgw.jrat.AplicationConstans.LOG;

import java.io.File;

import pl.imgw.jrat.process.MainProcessController;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// File genFile = new File(LOG, "log_gen");
		// File recFile = new File(LOG, "log_rec");
		// File errFile = new File(LOG, "log_err");

		LogManager.getInstance().setLogFile(new File(LOG));

		// LogHandler.getLogs().setGeneralLogs(genFile, 0);
		// LogHandler.getLogs().setErrorLogs(errFile, 0);
		// LogHandler.getLogs().setRecentFileLogs(recFile, 0);
		//
		MainProcessController pc = new MainProcessController(args);
		try {
			if (!pc.start()) {
				System.out.println("JRAT: failed");
			}
		} catch (Exception e) {
			LogManager.getFileLogger().saveErrorLogs(e.getCause().toString(),
					e.getMessage());
		}
		LogManager.getInstance();
	}

}
