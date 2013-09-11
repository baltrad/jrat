/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.util.ArrayList;
import java.util.Collections;


/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidStatistics {

    /**
     * @param result
     * @param perc
     * @return
     */
    public static Double getMean(CalidSingleResultContainer result, int perc) {
        if (perc > getFreq(result))
            return null;

        double sum = 0;
        int size = 0;
        for (PairedPoint pp : result.getPairedPointsList()) {
            if (pp.getDifference() != null) {
                sum += pp.getDifference();
                size++;
            }
        }
        if (size == 0)
            return null;

        return new Double(round(sum / size, 2));
    }

    public static Double getMean(CalidSingleResultContainer result) {
        return getMean(result, 0);
    }
    
    public static Integer getFreq(CalidSingleResultContainer result) {

        int size = 0;
        for (PairedPoint pp : result.getPairedPointsList()) {
            if (pp.getDifference() != null) {
                size++;
            }
        }

        double freq = (double) size / result.getPairedPointsList().size() * 100;

        return (int) Math.round(freq);

    }
    
    public static Double getMedian(CalidSingleResultContainer result) {
        return getMedian(result, 0);
    }

    /**
     * Median
     * 
     * @return
     */
    public static Double getMedian(CalidSingleResultContainer result, int perc) {

        if (perc > getFreq(result))
            return null;

        // perc = scalePerc(perc);
        ArrayList<Double> array = new ArrayList<Double>();

        // int size = 0;
        for (PairedPoint pp : result.getPairedPointsList()) {
            if (pp.getDifference() != null) {
                array.add(pp.getDifference());
                // size++;
            }
        }

        // System.out.println("aray size=" + array.size());
        if (array.size() == 0) {
            return null;
        }

        if (array.size() == 1) {
            return array.get(0);
        }

        Collections.sort(array);
        int middle = array.size() / 2;

        if (array.size() % 2 == 1) {
            return array.get(middle);
        } else {
            return (array.get(middle - 1) + array.get(middle)) / 2.0;
        }
    }
    
    
    public static Double getRMS(CalidSingleResultContainer result) {
        return getRMS(result, 0);
    }
    public static Double getRMS(CalidSingleResultContainer result, int perc) {

        if (perc > getFreq(result))
            return null;
        double rms = 0;
        int size = 0;
        for (PairedPoint pp : result.getPairedPointsList()) {
            if (pp.getDifference() != null) {
                rms += (pp.getDifference() * pp.getDifference());
                size++;
            }
        }
        if (size == 0)
            return null;

        return round(Math.sqrt(rms / size), 2);
    }
    
    
    
    
    private static double round(double value, int decimal) {
        double pow = Math.pow(10, decimal);

        value *= pow;

        value = Math.round(value);

        return value / pow;
    }
    
}
