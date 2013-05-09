package pl.imgw.jrat.tools.in;

import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_RESULTSFILE_DELIMITER;
import static pl.imgw.jrat.tools.out.Logging.ERROR;
import pl.imgw.jrat.tools.out.LogHandler;

public class LineParseTool {

    public static <T extends LineParseable, FACTORY extends LineParseableFactory<T>> T parseLine(String line,
	    FACTORY factory) {
	T t = factory.create();

	String[] words = line.split(SCANSUN_RESULTSFILE_DELIMITER);
	String[] header = t.getStringHeaderWithDelimiter(SCANSUN_RESULTSFILE_DELIMITER).split(
		SCANSUN_RESULTSFILE_DELIMITER);

	if (words.length != header.length) {
	    LogHandler.getLogs().displayMsg("SCANSUN: parseLine error for " + t.getClass().getSimpleName(), ERROR);
	    return null;
	}

	t.parseValues(words);

	return t;
    }

}
