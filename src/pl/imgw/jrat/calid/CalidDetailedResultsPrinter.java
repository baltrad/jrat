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
import java.util.HashSet;
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
        if (!areParametersSet()) {
            System.out.println("set all params for CALID");
            return;
        }
        
        headers = new HashSet<String>();
        CalidContainer cc = new CalidContainer(params);
        Set<File> files = getResultsFiles();

        List<Double> meanRes = new ArrayList<Double>();
        List<Double> rmsRes = new ArrayList<Double>();
        List<Double> undRes = new ArrayList<Double>();
//        Calendar cal0 = Calendar.getInstance();
        Calendar cal1 = Calendar.getInstance();
//        cal0.setTime(params.getDate1());
        cal1.setTime(params.getDate1());
        cal1.add(Calendar.DATE, period);
        
        for (File f : files) {
            
            printResultsHeader(f);
            
            try {
                
                Date fileDate = fsdf.parse(f.getName());
                String date1 = fsdf.format(params.getDate1());
                if (fileDate.before(fsdf.parse(date1))
                        || fileDate.after(params.getDate2())) {
//                    System.out.println("omijam");
                    continue;
                }
                
            } catch (ParseException e) {

            }
            
            Set<Date> dates = new TreeSet<Date>();
            setDates(f, dates);
            
            // System.out.println(params.getDate1());
//            System.out.println("cal0=" + cal0.getTime());
//            System.out.println("cal1=" + cal1.getTime());

            for (Date d : dates) {
                
//                System.out.println("d=" + d);
                
                while(d.after(cal1.getTime())) {
//                    cal0.add(Calendar.DATE, period);
                    cal1.add(Calendar.DATE, period);
                }

                if (d.after(params.getDate2())) {
                    break;
                }
                
                if(!CalidFileHandler.loadResults(f, cc, d)) {
                    continue;
                }
                int freq = params.getFrequency();
                Double mean = cc.getMean(freq);
                Double rms = cc.getRMS(freq);
                Double und =  ((double)(cc.getR1understate() + cc
                        .getR2understate()) / cc.getPairedPointsList().size());
//                System.out.println("und=" + und + " date=" + d + " " + cal1.getTime());
                
                if(mean != null)
                    meanRes.add(mean);
                if(rms != null)
                    rmsRes.add(rms);
                if(und != null)
                    undRes.add(und);
                
                if (!d.before(cal1.getTime())) {
                    cal1.add(Calendar.DATE, period);
//                    cal0.add(Calendar.DATE, period);
                    
//                    System.out.println("nowy");
//                    System.out.println(cal1.getTime());
                    
                    if(method.matches(MEDIAN)) {
                        printer
                                .println(sdf.format(d)
                                        + "\t"
                                        + getMedianResult(meanRes)
                                        + "\t"
                                        + getMedianResult(rmsRes)
                                        + "\t"
                                        + getMedianResult(undRes));
                    } else if (method.matches(MEAN)) {
                        printer
                                .println(sdf.format(d)
                                        + "\t"
                                        + getMeanResult(meanRes)
                                        + "\t"
                                        + getMeanResult(rmsRes)
                                        + "\t"
                                        + getMeanResult(undRes));
                    }
                    undRes.clear();
                    meanRes.clear();
                    rmsRes.clear();
                    
                }

                // System.out.println(d);
            }
        }
        
        if(method.matches(MEDIAN)) {
            
        }
        
    }
    
    private Double getMedianResult(List<Double> list) {

        if (list.size() == 0) {
            return 0.0;
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
            return 0.0;
        }
        
        double mean = 0;
        for (double d : list) {
            mean += d;
        }
        return round(mean / list.size(), 2);
    }
    
}
