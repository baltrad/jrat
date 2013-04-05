/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.tools.in.Options;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 *
 *  Singleton
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidOptionsHanlder extends Options {

    private File optionFile = null;
    
    private static final String CALID = "calid";
    private static final String RADAR = "radar";
    private static final String INPUT = "input";
    
    private static final String PAIR = "pair";
    private static final String SRC1 = "src1";
    private static final String SRC2 = "src2";
    private static final String ELE = "elevation";
    private static final String DIS = "distance";
    private static final String REF = "reflectivity";
    private static final String RAN = "range";
    
    private static final String SEQ = "sequnce";
    
    private Map<String, CalidParsedParameters> params;
    
    private static CalidOptionsHanlder options = new CalidOptionsHanlder();
    
    private Document doc;
    
    /*private constructor*/
    private CalidOptionsHanlder() {
    }
    
    /**
     * 
     * @param filename
     */
    public void setOptionFile(String filename) {
        File file = new File(filename);
        if(file.isFile()) {
            optionFile = file;
            doc = loadOptions();
        }
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
     * @param pair
     * @return null if options file contains no parameters for the pair
     */
    public CalidParsedParameters getParam(Pair pair) {
        return getParam(pair, null);
    }
    
    public boolean hasPair(Pair pair) {
        if (params == null)
            loadCalidParameter();
        return params.containsKey(getKey(pair));
    }
    
    /**
     * @param pair
     * @param defaultParams
     *            uses default option for fields that are empty in option file
     * @return null if options file contains no parameters for the pair
     */
    public CalidParsedParameters getParam(Pair pair, CalidParsedParameters defaultParams) {
        if(!isSet())
            return null;
            
        if (params == null)
            loadCalidParameter();

        CalidParsedParameters param = params.get(getKey(pair));
        
        if (defaultParams != null) {
            if (param.getDistance() == null)
                param.setDistance(defaultParams.getDistance());
            if (param.getElevation() == null)
                param.setElevation(defaultParams.getElevation());
            if (param.getMaxRange() == null)
                param.setMaxRange(defaultParams.getMaxRange());
            if (param.getReflectivity() == null)
                param.setReflectivity(defaultParams.getReflectivity());
        }
        
        return param;
    }
    
    public static CalidOptionsHanlder getOptions() {
        return options;
    }
    
    private String getKey(Pair pair) {
        return pair.getSource1() + pair.getSource2();
    }
    
    private void loadCalidParameter() {
        params = new HashMap<String, CalidParsedParameters>();
        NodeList pairList = doc.getElementsByTagName(PAIR);
        int l = pairList.getLength();
        for (int i = 0; i < l; i++) {
            
            String src1 = getValueByName(pairList.item(i), null, SRC1);
            String src2 = getValueByName(pairList.item(i), null, SRC2);
            
            if (src1 == null || src2 == null) {
                LogHandler.getLogs().displayMsg(
                        "Source(s) name in calid.opt is missing (" + (i + 1)
                                + ". pair)", LogHandler.ERROR);
                continue;
            }
            
            Pair pair = new Pair(src1, src2);
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

            CalidParsedParameters param = new CalidParsedParameters(ele, dis,
                    ran, ref);

            putParam(pair, param);
            
        }
    }
    

    private void displayErrMsg(String tag, Exception e, int i) {
        LogHandler.getLogs().displayMsg(
                "Parsing " + optionFile.getName() + " file failed " + tag
                        + ": " + e.getMessage() + " in " + (i + 1) + ". pair",
                LogHandler.ERROR);
    }
    
    private void putParam(Pair pair, CalidParsedParameters param) {
        params.put(getKey(pair), param);
    }
    
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.tools.in.Options#printHelp()
     */
    @Override
    public void printHelp() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.tools.in.Options#getOptionPath()
     */
    @Override
    protected File getOptionFile() {
        return optionFile;
        
    }

}
