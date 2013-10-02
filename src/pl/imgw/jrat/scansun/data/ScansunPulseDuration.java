/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

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
public enum ScansunPulseDuration {

	SHORT, LONG;

	private static Log log = LogManager.getLogger();

	public String toString() {
		return name().toUpperCase();
	}

	public static String toStringHeader() {
		return "PulseDuration";
	}

	public static ScansunPulseDuration parsePulseDuration(String word) {
		if (word.equalsIgnoreCase(SHORT.name())) {
			return SHORT;
		} else if (word.equalsIgnoreCase(LONG.name())) {
			return LONG;
		} else {
			log.printMsg(
					"SCANSUN: ScansunPulseDuration.parsePulseDuration() error: word is not a ScansunPulseDuration",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return null;
		}
	}
}