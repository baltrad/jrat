/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import java.io.File;
import java.util.List;
import java.util.Map;

import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.process.FilesProcessor;
import pl.imgw.jrat.process.VolumesProcessor;
import pl.imgw.jrat.scansun.ScansunException;
import pl.imgw.jrat.scansun.data.ScansunRadarParameters;
import pl.imgw.jrat.scansun.data.ScansunScanResult;
import pl.imgw.jrat.scansun.data.ScansunSite;
import pl.imgw.jrat.scansun.data.ScansunVolumesContainer;
import pl.imgw.jrat.scansun.data.ScansunParameters;
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
public class ScansunProcessor implements FilesProcessor, VolumesProcessor {

	private static final String SCANSUN_PROCESS_NAME = "SCANSUN Process";

	private static Log log = LogManager.getLogger();

	private ScansunScannerManager manager;

	private ScansunVolumesContainer volumes;

	public ScansunProcessor(String args[]) throws ScansunException {
		manager = new ScansunScannerManager();

		try {
			ScansunParameters params = ScansunParametersParser.getParser()
					.parseParameters(args);
			manager.setParsedParameters(params);

			Map<ScansunSite, ScansunRadarParameters> optionFileRadarParams = ScansunRadarParametersFileHandler
					.getHandler().getRadarParameters();
			manager.setOptionFileRadarParameters(optionFileRadarParams);
		} catch (ScansunException e) {
			throw e;
		}
	}

	@Override
	public void processVolumes(List<PolarData> vol) {
		volumes = new ScansunVolumesContainer();
		volumes.setVolumes(vol);
		process();
	}

	@Override
	public void processFile(List<File> files) {
		volumes = new ScansunVolumesContainer();
		volumes.setFiles(files);
		process();
	}

	private void process() {
		PolarData volume = null;

		while (volumes.hasNext()) {
			volume = volumes.next();
			// LogManager.getProgBar().evaluate();

			log.printMsg("SCANSUN: process: " + volume.getVolId(),
					Log.TYPE_NORMAL, Log.MODE_VERBOSE);

			ScansunScanResult result = manager.scan(volume);

			if (result.hasResults()) {
				ScansunDataSaver.saveResults(result);
			}
		}
	}

	@Override
	public String getProcessName() {
		return SCANSUN_PROCESS_NAME;
	}

}