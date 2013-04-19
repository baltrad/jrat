/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.util.*;

import pl.imgw.jrat.data.containers.*;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.process.FilesProcessor;
import pl.imgw.jrat.tools.out.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunProcessor implements FilesProcessor {

	private DefaultParser parser;
	private ScansunParsedParameters params;

	private boolean valid = false;

	public ScansunProcessor(String[] args) {
		parser = new DefaultParser();

		params = ScansunParsedParameters.getParams();
		valid = params.initialize(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.imgw.jrat.process.FilesProcessor#processFile(java.util.List)
	 */
	@Override
	public void processFile(List<File> files) {

		ConsoleProgressBar.getProgressBar().initialize(20, files.size(),
				LogHandler.getLogs().getVerbose() == PROGRESS_BAR_ONLY,
				"SCANSUN calculations");

		ScansunContainer sc = new ScansunContainer();
		int fileCount = 0;

		for (File f : files) {
			LogHandler.getLogs().displayMsg(
					"Scanning file (" + (fileCount + 1) + " of " + files.size()
							+ "): " + f.getName(), NORMAL);

			parser.initialize(f);

			if (parser.getProduct() instanceof RainbowDataContainer) {
				sc = ScansunManager.getScansunManager().calculate(
						new RainbowVolume(
								(RainbowDataContainer) parser.getProduct()),
						params);
			} else if (parser.getProduct() instanceof OdimDataContainer) {
				sc = ScansunManager.getScansunManager().calculate(
						new OdimH5Volume(
								(OdimDataContainer) parser.getProduct()),
						params);
			}

			sc.save();
			sc.resetContainer();
			fileCount++;
		}

		ConsoleProgressBar.getProgressBar().printDoneMsg();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.imgw.jrat.process.FilesProcessor#isValid()
	 */
	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return valid;
	}

}