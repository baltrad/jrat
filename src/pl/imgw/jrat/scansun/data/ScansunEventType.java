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
public enum ScansunEventType {
	NON_SOLAR(false), SOLAR_RAY(true);

	private static Log log = LogManager.getLogger();

	private boolean isSolarRay;

	private ScansunEventType(boolean isSolarRay) {
		this.isSolarRay = isSolarRay;
	}

	public boolean isSolarRay() {
		return isSolarRay;
	}

	public String toString() {
		return name().toUpperCase();
	}

	public static String toStringHeader() {
		return "EventType";
	}

	public static ScansunEventType parseEventType(String word) {
		if (word.equalsIgnoreCase(SOLAR_RAY.name())) {
			return SOLAR_RAY;
		} else if (word.equalsIgnoreCase(NON_SOLAR.name())) {
			return NON_SOLAR;
		} else {
			log.printMsg(
					"SCANSUN: ScansunEventType.parseEventType() error: word is not a ScansunEventType",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return null;
		}
	}

}