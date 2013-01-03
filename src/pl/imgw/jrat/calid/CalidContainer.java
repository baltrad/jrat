/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidContainer implements Comparable<CalidContainer> {

    private ArrayList<PairedPoints> pairedPointsList = new ArrayList<PairedPoints>();

    private Pair pair;
    private CalidParsedParameters params;
    private boolean hasResults = false;

    /**
     * @param pair
     * @param params
     */
    public CalidContainer(Pair pair, CalidParsedParameters params) {
        this.pair = pair;
        this.params = params;
    }

    public CalidContainer(CalidParsedParameters params) {
        this(new Pair(params.getSource1(), params.getSource2()), params);
    }

    /**
     * @param pairedPointsList
     *            the pairedPointsList to set
     */
    public void setPairedPointsList(ArrayList<PairedPoints> pairedPointsList) {
        this.pairedPointsList = pairedPointsList;
    }

    /**
     * @return the hasResults
     */
    public boolean hasResults() {
        return hasResults;
    }

    /**
     * @param hasResults
     *            the hasResults to set
     */
    public void setHasResults(boolean hasResults) {
        this.hasResults = hasResults;
    }

    /**
     * @param pair
     *            the pair to set
     */
    public void setPair(Pair pair) {
        this.pair = pair;
    }

    /**
     * Tries to load coordinates from file, if failed then calculates them. When
     * done, set the results to pairedPointsList variable.
     * 
     */
    public boolean initialize() {
        if (pair != null && pair.hasRealVolumes())
            return initialize(pair.getDate());
        return false;
    }

    /**
     * 
     * Initialize container with data from comparison. Loading data if results
     * are available in file or comparing both volumes if provided (after
     * comparison results are saved to file and loaded with next use). If 
     * 
     * @param date
     */
    public boolean initialize(Date date) {
        if (pair == null) {
            LogHandler.getLogs().displayMsg("CALID: pairs must be set",
                    Logging.WARNING);
            return false;
        }
        
        CalidComparator.receiveResults(this, date);

        return hasResults;

    }

    /**
     * 
     * @return elevation set as --calid parameter.
     *         <p>
     *         If elevation was not set as parameter, but volumes data was
     *         loaded then the lowest elevation available in both volumes is
     *         return
     *         <p>
     *         null if elevation was not set as parameter and no volume data was
     *         loaded,
     *         <p>
     *         also null if elevation was set as parameter but volumes do not
     *         contain this elevation,
     *         <p>
     *         also null if either elevation was no set as parameter or no
     *         volumes data was loaded.
     * 
     */
    public Double getVerifiedElevation() {
        if (pair == null)
            return null;
        if (params.getElevation() != null) {
            if (pair.hasRealVolumes()) {
                if (pair.getVol1().getScan(params.getElevation()) == null)
                    return null;
                if (pair.getVol2().getScan(params.getElevation()) == null)
                    return null;
            }
            return params.getElevation();
        }
        if (pair.hasRealVolumes()) {
            double ele1 = getLowestElevation(pair.getVol1());
            double ele2 = getLowestElevation(pair.getVol2());
            if (ele1 == ele2)
                return ele1;
        }
        return null;
    }

    private double getLowestElevation(VolumeContainer vol) {
        double ele = 90;
        for (ScanContainer scan : vol.getAllScans()) {
            if (scan.getElevation() < ele)
                ele = scan.getElevation();
        }
        return ele;
    }

    /**
     * Mean
     * 
     * @return
     */
    public Double getMean(int perc) {
        
        if(perc > getFreq())
            return null;
        
        double mean = 0;
        int size = 0;
        for (PairedPoints pp : pairedPointsList) {
            if (pp.getDifference() != null) {
                mean += pp.getDifference();
                size++;
            }
        }
        if (size == 0)
            return null;
        
        return new Double(round(mean / size, 2));
    }

    public Double getMean() {
        return getMean(0);
    }
    
    public Double getRMS() {
        return getRMS(0);
    }
    
    /**
     * Root mean square
     * 
     * @return
     */
    public Double getRMS(int perc) {
        
        if(perc > getFreq())
            return null;
        double rms = 0;
        int size = 0;
        for (PairedPoints pp : pairedPointsList) {
            if (pp.getDifference() != null) {
                rms += (pp.getDifference() * pp.getDifference());
                size++;
            }
        }
        if (size == 0)
            return null;
        
        return round(Math.sqrt(rms / size), 2);
    }

    
    public Double getMedian() {
        return getMedian(0);
    }
    
    /**
     * Median
     * 
     * @return
     */
    public Double getMedian(int perc) {

        if(perc > getFreq())
            return null;
        
//        perc = scalePerc(perc);
        ArrayList<Double> array = new ArrayList<Double>();

//        int size = 0;
        for (PairedPoints pp : pairedPointsList) {
            if (pp.getDifference() != null) {
                array.add(pp.getDifference());
//                size++;
            }
        }

//        System.out.println("aray size=" + array.size());
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
    
    public Integer getFreq() {
        int size = 0;
        for (PairedPoints pp : pairedPointsList) {
            if (pp.getDifference() != null) {
                size++;
            }
        }
        
        double freq = (double) size / pairedPointsList.size() * 100;
        
        return (int) Math.round(freq);
    }

    public boolean hasVolumeData() {
        if (pair == null)
            return false;
        return pair.hasRealVolumes();
    }

    public boolean hasCoordsLoad() {
        return !pairedPointsList.isEmpty();
    }

    /**
     * @return the pairedPointsList
     */
    public ArrayList<PairedPoints> getPairedPointsList() {
        return pairedPointsList;
    }

    /**
     * @return the pair
     */
    public Pair getPair() {
        return pair;
    }

    /**
     * @return the manager
     */
    public CalidParsedParameters getParsedParameters() {
        return params;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(CalidContainer o) {
        return pair.getDate().compareTo(o.getPair().getDate());
    }
    
    private int scalePerc(int perc) {
        return (int) ((double)(pairedPointsList.size() * perc) / 100);
    }
    
    
    private double round(double value, int decimal) {
        double pow = Math.pow(10, decimal);
        
        value *= pow;
        
        value = Math.round(value);
        
        return value / pow;
    }

}
