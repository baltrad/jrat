/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.process.CommandLineArgsParser.SCANSUN_OPT;
import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.NORMAL;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.w3c.dom.*;

import pl.imgw.jrat.scansun.ScansunConstants.Sites;
import pl.imgw.jrat.scansun.ScansunEvent.PulseDuration;
import pl.imgw.jrat.tools.in.Options;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * Singleton
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunOptionsHandler extends Options {

    private static final String SCANSUN = "scansun";
    private static final String RADAR = "radar";
    private static final String NAME = "name";

    private static final String WAVELENGTH = "wavelength";
    private static final String BEAMWIDTH = "beamwidth";
    private static final String DIELECTRICFACTOR = "dielectricfactor";
    private static final String ANTGAIN = "antgain";
    private static final String POWER = "power";
    private static final String PULSELENGTH = "pulselength";
    private static final String RADOMELOSS = "radomeloss";
    private static final String TXLOSS = "txloss";
    private static final String RXLOSS = "rxloss";
    private static final String BANDWIDTH = "bandwidth";

    /* private constructor */
    private ScansunOptionsHandler() {
    }

    private File optionFile = null;
    private Document doc;
    private Map<String, RadarParsedParameters> radarParams = new HashMap<String, RadarParsedParameters>();

    private static ScansunOptionsHandler options = new ScansunOptionsHandler();

    @Override
    protected File getOptionFile() {
	return optionFile;
    }

    public static ScansunOptionsHandler getOptions() {
	return options;
    }

    public RadarParsedParameters getRadarParsedParameters(String siteName) {
	return options.radarParams.get(siteName);
    }

    private static HashMap<PulseDuration, Double> parseMap(Node node, String nodeName) {
	HashMap<PulseDuration, Double> map = new HashMap<PulseDuration, Double>();
	map.put(PulseDuration.SHORT, Double.parseDouble(options.getValueByName(node, nodeName, null).split(" ")[0]));
	map.put(PulseDuration.LONG, Double.parseDouble(options.getValueByName(node, nodeName, null).split(" ")[1]));

	return map;
    }

    public boolean loadRadarParameters() {
	if (doc == null)
	    return false;

	if (!doc.getDocumentElement().getNodeName().equals(SCANSUN)) {
	    LogHandler.getLogs().displayMsg("SCANSUN: loading radar parameters error", ERROR);
	}

	NodeList radarNodeList = doc.getElementsByTagName(RADAR);
	for (int i = 0; i < radarNodeList.getLength(); i++) {
	    RadarParsedParameters radarParameters = new RadarParsedParameters();
	    radarParameters.setWavelength(Double.parseDouble(options.getValueByName(radarNodeList.item(i), WAVELENGTH, null)));
	    radarParameters.setBeamwidth(Double.parseDouble(options.getValueByName(radarNodeList.item(i), BEAMWIDTH, null)));
	    radarParameters.setDielectricFactor(Double.parseDouble(options.getValueByName(radarNodeList.item(i), DIELECTRICFACTOR, null)));
	    radarParameters.setAntennaGain(Double.parseDouble(options.getValueByName(radarNodeList.item(i), ANTGAIN, null)));
	    radarParameters.setPower(parseMap(radarNodeList.item(i), POWER));
	    radarParameters.setPulselength(parseMap(radarNodeList.item(i), PULSELENGTH));
	    radarParameters.setRadomeloss(Double.parseDouble(options.getValueByName(radarNodeList.item(i), RADOMELOSS, null)));
	    radarParameters.setTxloss(Double.parseDouble(options.getValueByName(radarNodeList.item(i), TXLOSS, null)));
	    radarParameters.setRxloss(Double.parseDouble(options.getValueByName(radarNodeList.item(i), RXLOSS, null)));
	    radarParameters.setBandwidth(parseMap(radarNodeList.item(i), BANDWIDTH));
	    // radarParameters.setParametersProperMode(true);

	    radarParams.put(options.getValueByName(radarNodeList.item(i), RADAR, NAME), radarParameters);
	}

	return true;
    }

    public boolean loadDefaultRadarParameters() {
	for (String s : Sites.getSiteNames()) {
	    radarParams.put(s, RadarParsedParameters.DEFAULT_PARAMETERS);
	}
	return true;
    }

    /**
     * 
     * @param filename
     */
    public boolean setOptionFile(String filename) {
	File file = new File(filename);
	if (file.isFile()) {
	    optionFile = file;
	    doc = options.loadOptions();
	    return true;
	} else {
	    LogHandler.getLogs().displayMsg("SCANSUN: scansun.opt file error - no such file", ERROR);
	    return false;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.tools.in.Options#printHelp()
     */
    @Override
    public void printHelp() {

    }

    public static boolean withOptFileHandling(CommandLine cmd) {

	if (!options.setOptionFile(cmd.getOptionValue(SCANSUN_OPT))) {
	    return false;
	}
	LogHandler.getLogs().displayMsg("SCANSUN: scansun.opt file found - calculations with proper solar flux calibration.", NORMAL);

	if (!options.loadRadarParameters()) {
	    LogHandler.getLogs().displayMsg("SCANSUN: loading radar parameters error", ERROR);
	    return false;
	}
	LogHandler.getLogs().displayMsg("SCANSUN: loading radar parameters from scansun.opt file successful", NORMAL);

	return true;
    }

    public static boolean withoutOptFileHandling() {
	LogHandler.getLogs().displayMsg("SCANSUN: no scansun.opt file in input - calculations without proper solar flux calibration", WARNING);

	if (!options.loadDefaultRadarParameters()) {
	    LogHandler.getLogs().displayMsg("SCANSUN: loading default radar parameters error", ERROR);
	    return false;
	}

	return true;
    }

}