/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import java.util.Map;

import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.scansun.ScansunException;
import pl.imgw.jrat.scansun.data.ScansunParameters;
import pl.imgw.jrat.scansun.data.ScansunRadarParameters;
import pl.imgw.jrat.scansun.data.ScansunScanResult;
import pl.imgw.jrat.scansun.data.ScansunSite;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunScannerManager {

	private ScansunParameters cmdLineParams;
	private Map<ScansunSite, ScansunRadarParameters> optionFileRadarParams;

	public void setParsedParameters(ScansunParameters params) {
		this.cmdLineParams = params;
	}

	public void setOptionFileRadarParameters(
			Map<ScansunSite, ScansunRadarParameters> optionFileRadarParams) {
		this.optionFileRadarParams = optionFileRadarParams;
	}

	public ScansunScanResult scan(PolarData volume) throws ScansunException {
		return ScansunScanner
				.scan(volume, cmdLineParams, optionFileRadarParams);
	}

}
