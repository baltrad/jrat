/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

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
public class LineParseTool {

	private static Log log = LogManager.getLogger();

	public static <T extends LineParseable, FACTORY extends LineParseableFactory<T>> T parseLine(
			String line, FACTORY factory, String delimiter) {

		T t = factory.create();

		String[] words = line.split(delimiter);
		String[] header = t.lineHeader(";").split(";");

		if (words.length != header.length) {
			log.printMsg(
					"SCANSUN: parseEvent() error: words array wrong length",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return null;
		}

		t.parseLine(words);

		return t;
	}
}