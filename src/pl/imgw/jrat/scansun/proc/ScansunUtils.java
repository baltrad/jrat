/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunUtils {

	public static String capitalize(String s) {
		char[] nameArray = s.toLowerCase().toCharArray();
		nameArray[0] = Character.toUpperCase(nameArray[0]);
		return new String(nameArray);
	}

	private static final Map<DateTimeFormatter, String> patternHistory = new HashMap<DateTimeFormatter, String>();

	public static DateTimeFormatter forPattern(String pattern) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
		patternHistory.put(dtf, pattern);
		return dtf;
	}

	public static String getPattern(DateTimeFormatter dateTimeFormatter) {
		return patternHistory.get(dateTimeFormatter);
	}

}
