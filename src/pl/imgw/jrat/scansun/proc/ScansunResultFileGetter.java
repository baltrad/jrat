/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import pl.imgw.jrat.scansun.proc.ScansunDataHandler;
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
public class ScansunResultFileGetter {

	private static Log log = LogManager.getLogger();

	public static Set<File> getResultFiles() {
		File folder = new File(ScansunDataHandler.getScansunPath());
		return getResultFiles(folder);
	}

	public static Set<File> getResultFiles(File folder) {

		Set<File> results = new TreeSet<File>();

		if (!folder.isDirectory())
			return results;

		for (File file : folder.listFiles()) {
			if (file.isFile()
					&& file.getName().endsWith(
							ScansunDataHandler.getResultsFileExtension())) {
				results.add(file);
			}
		}

		return results;
	}

}
