/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.imgw.jrat.scansun.ScansunConstants.PulseDuration;
import pl.imgw.jrat.tools.in.Options;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * Singleton
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunOptionsHandler extends Options {

    private static Log log = LogManager.getLogger();

    private static final String SCANSUN = "scansun";
    private static final String RADAR = "radar";
    private static final String NAME = "name";

    private static final String UNIT = "unit";
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

    class RadarParsedParameters {
        private double wavelength;
        private double dielectricFactor;
        private double antennaGain;
        private double radomeloss;
        private double beamwidth;
        private Map<PulseDuration, Double> power;
        private Map<PulseDuration, Double> pulselength;
        private double txloss;
        private double rxloss;
        private Map<PulseDuration, Double> bandwidth;

        public double getWavelength() {
            return wavelength;
        }

        public double getDielectricFactor() {
            return dielectricFactor;
        }

        public double getAntennaGain() {
            return antennaGain;
        }

        public double getRadomeLoss() {
            return radomeloss;
        }

        public double getBeamwidth() {
            return beamwidth;
        }

        public double getPower(PulseDuration pd) {
            return power.get(pd);
        }

        public double getPulseLength(PulseDuration pd) {
            return pulselength.get(pd);
        }

        public double getTxloss() {
            return txloss;
        }

        public double getRxloss() {
            return rxloss;
        }

        public double getBandwidth(PulseDuration pd) {
            return bandwidth.get(pd);
        }
    }

    public RadarParsedParameters getRadarParsedParameters(String siteName) {
        return options.radarParams.get(siteName);
    }

    private static HashMap<PulseDuration, Double> parseMap(Node node,
            String nodeName) {
        HashMap<PulseDuration, Double> map = new HashMap<PulseDuration, Double>();
        map.put(PulseDuration.SHORT, Double.parseDouble(options.getValueByName(
                node, nodeName, null).split(" ")[0]));
        map.put(PulseDuration.LONG, Double.parseDouble(options.getValueByName(
                node, nodeName, null).split(" ")[1]));

        return map;
    }

    public boolean loadRadarParameters() {
        if (doc == null)
            return false;

        if (!doc.getDocumentElement().getNodeName().equals(SCANSUN)) {
            log.printMsg("SCANSUN: loading radar parameters error",
                    Log.TYPE_ERROR, Log.MODE_VERBOSE);
        }

        NodeList radarNodeList = doc.getElementsByTagName(RADAR);
        for (int i = 0; i < radarNodeList.getLength(); i++) {
            RadarParsedParameters p = new RadarParsedParameters();
            p.wavelength = Double.parseDouble(options.getValueByName(
                    radarNodeList.item(i), WAVELENGTH, null));

            p.beamwidth = Double.parseDouble(options.getValueByName(
                    radarNodeList.item(i), BEAMWIDTH, null));

            p.dielectricFactor = Double.parseDouble(options.getValueByName(
                    radarNodeList.item(i), DIELECTRICFACTOR, null));
            p.antennaGain = Double.parseDouble(options.getValueByName(
                    radarNodeList.item(i), ANTGAIN, null));

            p.power = parseMap(radarNodeList.item(i), POWER);
            p.pulselength = parseMap(radarNodeList.item(i), PULSELENGTH);
            p.radomeloss = Double.parseDouble(options.getValueByName(
                    radarNodeList.item(i), RADOMELOSS, null));

            p.txloss = Double.parseDouble(options.getValueByName(
                    radarNodeList.item(i), TXLOSS, null));
            p.rxloss = Double.parseDouble(options.getValueByName(
                    radarNodeList.item(i), RXLOSS, null));
            p.bandwidth = parseMap(radarNodeList.item(i), BANDWIDTH);

            radarParams.put(
                    options.getValueByName(radarNodeList.item(i), RADAR, NAME),
                    p);
        }

        return true;
    }

    /**
     * 
     * @param filename
     */
    public void setOptionFile(String filename) {
        File file = new File(filename);
        if (file.isFile()) {
            optionFile = file;
            doc = options.loadOptions();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.tools.in.Options#printHelp()
     */
    @Override
    public void printHelp() {
        // TODO Auto-generated method stub

    }

}
