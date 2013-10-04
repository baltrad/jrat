/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.tools.in.Options;
import pl.imgw.util.Log;

/**
 * 
 * Singleton
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidParametersFileHandler extends Options {

    private File optionFile = null;

//    private static final String CALID = "calid";
    private static final String RADAR = "radar";
    private static final String INPUT = "input";
//    private static final String NAME = "siteName";

    private static final String PAIR = "pair";
    private static final String SRC1 = "src1";
    private static final String SRC2 = "src2";
    private static final String ELE = "elevation";
    private static final String DIS = "distance";
    private static final String REF = "reflectivity";
    private static final String RAN = "range";

//    private static final String SEQ = "sequnce";

    private Map<String, CalidParameters> params;
    private List<File> folders;

    private static CalidParametersFileHandler options = new CalidParametersFileHandler();

    private Document doc;

    /* private constructor */
    private CalidParametersFileHandler() {
    }

    /**
     * 
     * @param filename
     */
    public void setOptionFile(String filename) throws CalidException{
        File file = new File(filename);
        if (file.isFile()) {
            optionFile = file;
            doc = loadOptions();
        } else
            throw new CalidException("Calid Option file: " + filename + " cannot be found");
        if(doc == null)
            throw new CalidException("Calid Option file: " + filename + " is not valid");
    }

    /**
     * Checking if calid option file is set.
     * 
     * @return returns true if provided file is well formatted (xml).
     */
    public boolean isSet() {
        return doc != null;
    }

    /**
     * 
     * @return list of input folders provided in option file, if no folders
     *         provided the list is empty
     */
    public List<File> getInputFolderList() {
        if (folders == null)
            loadCalidRadarParameters();
        return folders;
    }

    /**
     * 
     * @param pair
     * @return returns true if there are parameters for the pair
     */
    public boolean hasPair(RadarsPair pair) {
        if (params == null)
            loadCalidPairsParameters();
        return params.containsKey(pair.getBothSources());
    }

    /**
     * @param pair
     * @return null if options file contains no parameters for the pair
     */
    public CalidParameters getPairParam(RadarsPair pair) {
        return params.get(pair.getBothSources());
    }

    /**
     * @param pair
     * @param defaultParams
     *            uses default option for fields that are empty in option file
     * @return null if options file contains no parameters for the pair
     */
    public Map<String, CalidParameters> getParametersForAllPairs() {
        
        if (params == null) {
            loadCalidPairsParameters();
        }
        
        return params;
    }

    public static CalidParametersFileHandler getOptions() {
        return options;
    }

    
    private void loadCalidRadarParameters() {
        folders = new LinkedList<File>();
        if (doc == null)
            return;
        NodeList radarList = doc.getElementsByTagName(RADAR);
        int l = radarList.getLength();
        String val;
        File file;
        for (int i = 0; i < l; i++) {
            val = getValueByName(radarList.item(i), INPUT, null);

            if (val != null) {
                file = new File(val);
                if (file.isDirectory()) {
                    folders.add(file);
                    log.printMsg("Adding folder: " + file + " for CALID watch",
                            Log.TYPE_NORMAL, Log.MODE_VERBOSE);
                } else {
                    log.printMsg("Given input: " + file + " is not a folder",
                            Log.TYPE_WARNING, Log.MODE_VERBOSE);
                }
            }
        }

    }

    private void loadCalidPairsParameters() {
        if (doc == null)
            return;
        params = new HashMap<String, CalidParameters>();
        NodeList pairList = doc.getElementsByTagName(PAIR);
        int l = pairList.getLength();
        for (int i = 0; i < l; i++) {

            String src1 = getValueByName(pairList.item(i), null, SRC1);
            String src2 = getValueByName(pairList.item(i), null, SRC2);

            if (src1 == null || src2 == null) {
                log.printMsg("Source(s) siteName in calid.opt is missing ("
                        + (i + 1) + ". pair)", Log.TYPE_ERROR, Log.MODE_VERBOSE);
                continue;
            }

            RadarsPair pair = new RadarsPair(src1, src2);
            Double ele = null, ref = null;
            Integer dis = null, ran = null;
            String val;
            try {
                val = getValueByName(pairList.item(i), ELE, null);
                if (val != null)
                    ele = Double.parseDouble(val);
            } catch (NumberFormatException e) {
                displayErrMsg(ELE, e, i);
                continue;
            }
            try {
                val = getValueByName(pairList.item(i), REF, null);
                if (val != null)
                    ref = Double.parseDouble(val);

            } catch (NumberFormatException e) {
                displayErrMsg(REF, e, i);
                continue;
            }
            try {
                val = getValueByName(pairList.item(i), DIS, null);
                if (val != null)
                    dis = Integer.parseInt(val);
            } catch (NumberFormatException e) {
                displayErrMsg(DIS, e, i);
                continue;
            }
            try {
                val = getValueByName(pairList.item(i), RAN, null);
                if (val != null)
                    ran = Integer.parseInt(val);
            } catch (NumberFormatException e) {
                displayErrMsg(RAN, e, i);
                continue;
            }

            CalidParameters param = new CalidParameters(ele, dis,
                    ran, ref);

            params.put(pair.getBothSources(), param);

        }
    }

    private void displayErrMsg(String tag, Exception e, int i) {
        log.printMsg("Parsing " + optionFile.getName() + " file failed " + tag
                + ": " + e.getMessage() + " in " + (i + 1) + ". pair",
                Log.TYPE_ERROR, Log.MODE_VERBOSE);
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

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.tools.in.Options#getOptionPath()
     */
    @Override
    protected File getOptionFile() {
        return optionFile;

    }

}
