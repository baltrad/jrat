/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import java.io.File;
import java.util.List;
import java.util.Map;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.CalidParametersFileHandler;
import pl.imgw.jrat.calid.data.CalidParametersParser;
import pl.imgw.jrat.calid.data.PairsContainer;
import pl.imgw.jrat.calid.data.PolarVolumesPair;
import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.process.FilesProcessor;
import pl.imgw.jrat.process.VolumesProcessor;
import pl.imgw.util.Log;
import pl.imgw.util.LogFile;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidProcessor implements FilesProcessor, VolumesProcessor {

	/**
     * 
     */
	private static final String CALID_PROCESS_NAME = "CALID Process";

	private static Log log = LogManager.getLogger();
	private static LogFile logFile = LogManager.getFileLogger();

	private CalidComparatorManager manager;

	private PairsContainer pairs;

	public CalidProcessor(String args[]) throws CalidException {
		manager = new CalidComparatorManager();
		try {
			// cc = new CalidSingleResultContainer();

			CalidParameters params = CalidParametersParser.getParser()
					.parseParameters(args);
			manager.setParsedParameters(params);

			Map<String, CalidParameters> optionFileParams = CalidParametersFileHandler
					.getOptions().getParametersForAllPairs();
			manager.setOptionFileParameters(optionFileParams);

		} catch (CalidException e) {
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.imgw.jrat.process.VolumesProcessor#processVolumes(java.util.List)
	 */
	@Override
	public void processVolumes(List<PolarData> vol) {
		if (vol.size() < 2) {
			log.printMsg("Need at least two volumes for a pair to start CALID",
					Log.TYPE_WARNING, Log.MODE_NORMAL);
			return;
		}
		pairs = new PairsContainer();
		pairs.setVolumes(vol);
		process();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.imgw.jrat.process.FilesProcessor#processFile(java.util.List)
	 */
	@Override
	public void processFile(List<File> files) {
		if (files.size() < 2) {
			log.printMsg("Need at least two files for a pair to start CALID",
					Log.TYPE_WARNING, Log.MODE_NORMAL);
			return;
		}
		pairs = new PairsContainer();
		pairs.setFiles(files);
		process();

	}

	private void process() {
		PolarVolumesPair pair = null;

		while (pairs.hasNext()) {
			pair = pairs.next();

			// LogManager.getProgBar().evaluate();

			log.printMsg("CALID: process: " + pair, Log.TYPE_NORMAL,
					Log.MODE_VERBOSE);

			try {
				manager.compare(pair);
			} catch (CalidException e) {
				logFile.saveErrorLogs(CalidProcessor.class.getName(),
						e.getMessage());
			} catch (Exception e) {
				logFile.saveErrorLogs(CalidProcessor.class.getName(),
						e.getMessage());
			}
		}
		pairs = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.imgw.jrat.process.FilesProcessor#getProcessName()
	 */
	@Override
	public String getProcessName() {
		return CALID_PROCESS_NAME;
	}

}
