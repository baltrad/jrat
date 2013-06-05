/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.io.File;
import java.util.List;

import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.process.FilesProcessor;
import pl.imgw.util.ConsoleProgressBar;
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
public class ScansunProcessor implements FilesProcessor {
    private static Log log = LogManager.getLogger();
	private DefaultParser parser;
	private ScansunParsedParameters params;


	public ScansunProcessor(String[] args) {
		parser = new DefaultParser();

		params = ScansunParsedParameters.getParams();
		params.initialize(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.imgw.jrat.process.FilesProcessor#processFile(java.util.List)
	 */
	@Override
	public void processFile(List<File> files) {

		LogManager.getProgBar().initialize(20, files.size(), "SCANSUN calculations");

		ScansunContainer sc = new ScansunContainer();
		int fileCount = 0;

		for (File f : files) {
			log.printMsg(
					"Scanning file (" + (fileCount + 1) + " of " + files.size()
							+ "): " + f.getName(), Log.TYPE_NORMAL, Log.MODE_VERBOSE);

			parser.parse(f);
			PolarData vol = parser.getPolarData();
			
            sc = ScansunManager.getScansunManager().calculate(vol, params);

			sc.save();
			sc.resetContainer();
			fileCount++;
			LogManager.getProgBar().evaluate();
		}

		LogManager.getProgBar().complete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.imgw.jrat.process.FilesProcessor#getProcessName()
	 */
	@Override
	public String getProcessName() {
		// TODO Auto-generated method stub
		return "SCANSUN Process";
	}



}