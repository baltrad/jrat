/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidDetailedResultsPrinter extends CalidResultsPrinter {

    private static final String METHOD = "method=";
    private static final String MEDIAN = "median";
    private static final String MEAN = "mean";
    
    private static final String PERIOD = "period=";
    
    private String method = "";
    private int period = 0;
    
    /**
     * @param params
     */
    public CalidDetailedResultsPrinter(CalidParsedParameters params, String[] detParams) {
        super(params);
        
        for (String p : detParams) {
            if (p.startsWith(METHOD)) {
                method = p.substring(METHOD.length());
            } else if (p.startsWith(PERIOD)) {
                try {
                    period = Integer.parseInt(p.substring(PERIOD.length()));
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        
    }

    public void printResults() {
        if (!isSet()) {
            System.out.println("set all params for CALID");
            return;
        }
        
        CalidContainer cc = new CalidContainer(params);
        Set<File> files = getResultsFiles();

        List<Double> meanRes = new ArrayList<Double>();
        List<Double> rmsRes = new ArrayList<Double>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(params.getDate1());
        cal.add(Calendar.DATE, period);
        
        for (File f : files) {
            
            try {
                
                Date fileDate = fsdf.parse(f.getName());
                
                if (fileDate.before(params.getDate1())
                        || fileDate.after(params.getDate2())) {
//                    System.out.println("omijam");
                    continue;
                }
                
            } catch (ParseException e) {

            }
            
            Set<Date> dates = new TreeSet<Date>();
            setDates(f, dates);
            
            // System.out.println(params.getDate1());
            // System.out.println(params.getDate2());

            for (Date d : dates) {

                if (d.after(params.getDate2())) {
                    break;
                }
                if(!CalidFileHandler.loadResults(f, cc, d)) {
                    continue;
                }
                int freq = params.getFrequency();
                Double mean = cc.getMean(freq);
                Double rms = cc.getRMS(freq);
                
                if(mean != null)
                    meanRes.add(mean);
                if(rms != null)
                    rmsRes.add(rms);
                
                if (!d.before(cal.getTime())) {
                    cal.add(Calendar.DATE, period);
                    if(meanRes.isEmpty() && rmsRes.isEmpty()) {
                        continue;
                    } else if(method.matches(MEDIAN)) {
                    System.out.println(sdf.format(d) + "\t"
                            + getMedianResult(meanRes) + "\t" + getMedianResult(rmsRes));
                    } else if (method.matches(MEAN)) {
                        System.out.println(sdf.format(d) + "\t"
                                + getMeanResult(meanRes) + "\t" + getMeanResult(rmsRes));
                    }
                    meanRes = new ArrayList<Double>();
                    rmsRes = new ArrayList<Double>();
                }

                // System.out.println(d);
            }
        }
        
        if(method.matches(MEDIAN)) {
            
        }
        
    }
    
    private Double getMedianResult(List<Double> list) {

        if (list.size() == 0) {
            return null;
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        Collections.sort(list);
        int middle = list.size() / 2;

        if (list.size() % 2 == 1) {
            return list.get(middle);
            // return array.get(middle);
        } else {
            return round((list.get(middle - 1) + list.get(middle)) / 2.0, 3);
        }
    }
    
    private Double getMeanResult(List<Double> list) {
        
        if (list.size() == 0) {
            return null;
        }
        
        double mean = 0;
        for (double d : list) {
            mean += d;
        }
        return round(mean / list.size(), 2);
    }
    
}
