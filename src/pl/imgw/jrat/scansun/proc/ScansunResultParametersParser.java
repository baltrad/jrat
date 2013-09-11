/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import pl.imgw.jrat.scansun.ScansunException;
import pl.imgw.jrat.scansun.data.ScansunResultParameters;
import pl.imgw.jrat.scansun.data.ScansunSite;
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
public class ScansunResultParametersParser {

	private static Log log = LogManager.getLogger();

	private static final String SITENAME = "sitename=";
	private static final String errorMsg = "SCANSUN results: Arguments are incorrect";

	private static ScansunResultParametersParser parser = new ScansunResultParametersParser();

	private ScansunResultParametersParser() {

	}

	public static ScansunResultParametersParser getParser() {
		return parser;
	}

	public ScansunResultParameters parseParameters(String[] par)
			throws ScansunException {
		ScansunResultParameters params = new ScansunResultParameters();

		if (par == null) {
			return params;
		}

		for (int i = 0; i < par.length; i++) {

			if (par[i].isEmpty()) {
				throw new ScansunException(errorMsg + ": string " + i
						+ " is empty");
			}

			if (par[i].startsWith(SITENAME)) {
				setSiteName(params, par[i].substring(SITENAME.length()));
			} else if (par[i].contains("=")) {
				log.printMsg(errorMsg + " (" + par[i] + ")", Log.TYPE_WARNING,
						Log.MODE_VERBOSE);

				throw new ScansunException("Cannot parse parameter: " + par[i]);
			}
		}

		return params;

	}

	private void setSiteName(ScansunResultParameters params, String word)
			throws ScansunException {

		String siteName = word;

		if (siteName == null || !ScansunSite.getSiteNames().contains(siteName)) {
			throw new ScansunException(siteName + " is ont a valid siteName");
		}

		params.setSite(siteName);
	}

}
