/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.util.Date;
import java.util.List;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.proc.CalidCoordsHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidSingleResultContainer implements
        Comparable<CalidSingleResultContainer> {

    private List<PairedPoint> pairedPointsList;

    private Date resultDate = null;

    private int r1understate = 0;
    private int r2understate = 0;

    private RadarsPair pair;
    private CalidParameters params;

    /**
     * 
     */
    public CalidSingleResultContainer(CalidParameters params, RadarsPair pair) {
        this.params = params;
        this.pair = pair;
    }

    /**
     * @return the params
     */
    public CalidParameters getParams() {
        return params;
    }

    /**
     * 
     */
    public void setCoords() {
        pairedPointsList = CalidCoordsHandler.getCoords(params, pair);
    }

    /**
     * 
     */
    public void setCoords(List<PairedPoint> pairedPointsList) {
        this.pairedPointsList = pairedPointsList;
    }

    /**
     * incrementing number of understated cases of radar 1 by one
     */
    public void r1understated() {
        r1understate++;
    }

    /**
     * incrementing number of understated cases of radar 2 by one
     */
    public void r2understated() {
        r2understate++;
    }

    /**
     * @return the r1understate
     */
    public int getR1understate() {
        return r1understate;
    }

    /**
     * @param r1understate
     *            the r1understate to set
     */
    public void setR1understate(int r1understate) {
        this.r1understate = r1understate;
    }

    /**
     * @return the r2understate
     */
    public int getR2understate() {
        return r2understate;
    }

    /**
     * @param r2understate
     *            the r2understate to set
     */
    public void setR2understate(int r2understate) {
        this.r2understate = r2understate;
    }

    /**
     * @param pairedPointsList
     *            the pairedPointsList to set
     */
    protected void setPairedPointsList(List<PairedPoint> pairedPointsList) {
        this.pairedPointsList = pairedPointsList;
    }

    /**
     * @return the date
     */
    public Date getResultDate() {
        if (hasVolumeData())
            return ((PolarVolumesPair) pair).getDate();
        return resultDate;
    }
    
    private boolean hasVolumeData() {
        return pair instanceof PolarVolumesPair;
    }

    
    public void setResultDate(Date date) {
        this.resultDate = date;
    }

    public void resetDifferences() {
        if (pairedPointsList == null)
            throw new CalidException(
                    "Pair points are not set, initialize or set first");
        for (PairedPoint pp : pairedPointsList)
            pp.setDifference(null);
        r1understate = 0;
        r2understate = 0;
    }


    /**
     * @return the pairedPointsList
     */
    public List<PairedPoint> getPairedPointsList() {
        if (pairedPointsList == null)
            throw new CalidException(
                    "Pair point are not set, initialize or set first");
        return pairedPointsList;
    }

    public boolean hasCoords() {
        return pairedPointsList != null;
    }

    /**
     * @return the pair
     */
    public PolarVolumesPair getPolarVolumePair() throws CalidException {
        if (pair instanceof PolarVolumesPair)
            return (PolarVolumesPair) pair;
        else
            throw new CalidException(pair + " must contains polara data");
    }

    /**
     * @return the pair
     */
    public RadarsPair getPair() {
        return pair;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(CalidSingleResultContainer o) {
         return getResultDate().compareTo(o.getResultDate());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CalidSingleResultContainer))
            return false;
        CalidSingleResultContainer r = (CalidSingleResultContainer) obj;
        if (pair.equals(r.getPair())
                && getResultDate().equals(r.getResultDate()))
            return true;
        return false;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getResultDate().hashCode() + pair.hashCode();
    }

}
