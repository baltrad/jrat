/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.imgw.jrat.scansun.ScansunException;
import pl.imgw.jrat.scansun.data.ScansunRadarParameters;
import pl.imgw.jrat.scansun.data.ScansunSite;
import pl.imgw.jrat.scansun.data.ScansunPulseDuration;
import pl.imgw.jrat.tools.in.Options;
import pl.imgw.util.Log;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunRadarParametersFileHandler extends Options {

	private File optionFile = null;

	private static final String SCANSUN = "scansun";
	private static final String RADAR = "radar";
	private static final String NAME = "name";

	private static final String WAVELENGTH = "wavelength";
	private static final String BEAMWIDTH = "beamwidth";
	private static final String DIELECTRIC_FACTOR = "dielectricfactor";
	private static final String ANTGAIN = "antgain";
	private static final String RADOMELOSS = "radomeloss";
	private static final String TXLOSS = "txloss";
	private static final String RXLOSS = "rxloss";
	private static final String POWER = "power";
	private static final String PULSELENGTH = "pulselength";
	private static final String BANDWIDTH = "bandwidth";

	private static ScansunRadarParametersFileHandler handler = new ScansunRadarParametersFileHandler();
	private Document doc;
	private Map<ScansunSite, ScansunRadarParameters> radarParams;

	private ScansunRadarParametersFileHandler() {
	}

	public static ScansunRadarParametersFileHandler getHandler() {
		return handler;
	}

	public void setOptionFile(String filename) throws ScansunException {
		File file = new File(filename);
		if (file.isFile()) {
			optionFile = file;
			doc = loadOptions();
		} else {
			throw new ScansunException("Scansun Option file: " + filename
					+ " cannot be found");
		}

		if (doc == null)
			throw new ScansunException("Scansun Option file: " + filename
					+ " is not valid");
	}

	public Map<ScansunSite, ScansunRadarParameters> getRadarParameters() {

		if (handler.radarParams == null) {
			radarParams = loadScansunRadarParameters();
		}

		return handler.radarParams;
	}

	private Map<ScansunSite, ScansunRadarParameters> loadScansunRadarParameters() {
		Map<ScansunSite, ScansunRadarParameters> result = new HashMap<>();

		if (handler.doc == null) {
			return null;
		}

		if (!handler.doc.getDocumentElement().getNodeName().equals(SCANSUN)) {
			log.printMsg("SCANSUN: loading radar parameters error",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
		}

		NodeList radarNodeList = handler.doc.getElementsByTagName(RADAR);

		for (int i = 0; i < radarNodeList.getLength(); i++) {
			ScansunRadarParameters params = new ScansunRadarParameters();
			params.setWavelength(Double.parseDouble(handler.getValueByName(
					radarNodeList.item(i), WAVELENGTH, null)));
			params.setBeamwidth(Double.parseDouble(handler.getValueByName(
					radarNodeList.item(i), BEAMWIDTH, null)));
			params.setDielectricFactor(Double.parseDouble(handler
					.getValueByName(radarNodeList.item(i), DIELECTRIC_FACTOR,
							null)));
			params.setAntennaGain(Double.parseDouble(handler.getValueByName(
					radarNodeList.item(i), ANTGAIN, null)));
			params.setRadomeloss(Double.parseDouble(handler.getValueByName(
					radarNodeList.item(i), RADOMELOSS, null)));
			params.setTxloss(Double.parseDouble(handler.getValueByName(
					radarNodeList.item(i), TXLOSS, null)));
			params.setRxloss(Double.parseDouble(handler.getValueByName(
					radarNodeList.item(i), RXLOSS, null)));
			params.setPower(handler.parseMap(radarNodeList.item(i), POWER));
			params.setPulselength(handler.parseMap(radarNodeList.item(i),
					PULSELENGTH));
			params.setBandwidth(handler.parseMap(radarNodeList.item(i),
					BANDWIDTH));

			result.put(
					ScansunSite.forName(handler.getValueByName(
							radarNodeList.item(i), RADAR, NAME)), params);
		}

		return result;
	}

	private HashMap<ScansunPulseDuration, Double> parseMap(Node node,
			String nodeName) {
		HashMap<ScansunPulseDuration, Double> map = new HashMap<ScansunPulseDuration, Double>();
		map.put(ScansunPulseDuration.SHORT, Double.parseDouble(handler
				.getValueByName(node, nodeName, null).split(" ")[0]));
		map.put(ScansunPulseDuration.LONG, Double.parseDouble(handler
				.getValueByName(node, nodeName, null).split(" ")[1]));

		return map;
	}

	@Override
	public void printHelp() {
		StringBuilder s = new StringBuilder();

		s.append("Print info about scansun.opt file structure\n");
		System.out.print(s.toString());
	}

	@Override
	protected File getOptionFile() {
		return optionFile;
	}

}