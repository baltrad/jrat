/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import static pl.imgw.jrat.AplicationConstans.ETC;
import static pl.imgw.jrat.scansun.data.ScansunConstants.SCANSUN_DATE_PATTERN;

import java.io.File;
import org.joda.time.LocalDate;
import pl.imgw.jrat.scansun.proc.ScansunUtils;
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
public class ScansunDataHandler {

	protected static Log log = LogManager.getLogger();

	private static final String SCANSUN_FOLDER = "scansun";
	private static final String SCANSUN_RESULTSFILE_BASENAME = "scansun";
	private static final String SCANSUN_RESULTSFILE_EXT = "results";

	protected static File getResultsFile(LocalDate day) {

		return new File(getScansunPath(), getScansunFilename(day));
	}

	public static String getScansunPath() {

		if (ETC.isEmpty()) {
			new File(ETC, SCANSUN_FOLDER).mkdirs();
			return new File(ETC, SCANSUN_FOLDER).getPath();
		}
		new File(ETC, SCANSUN_FOLDER).mkdirs();

		return new File(ETC, SCANSUN_FOLDER).getPath();
	}

	public static String getScansunFilename(LocalDate day) {
		StringBuilder filename = new StringBuilder();

		filename.append(SCANSUN_RESULTSFILE_BASENAME);
		filename.append("-");
		filename.append(ScansunUtils.forPattern(SCANSUN_DATE_PATTERN)
				.print(day));
		filename.append(".");
		filename.append(SCANSUN_RESULTSFILE_EXT);

		return (new File(filename.toString())).getPath();
	}

	public static String getResultsFileExtension() {
		return SCANSUN_RESULTSFILE_EXT;
	}

}
