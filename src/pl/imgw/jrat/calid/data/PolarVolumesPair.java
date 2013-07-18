/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.util.Date;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.data.PolarData;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PolarVolumesPair extends RadarsPair {

    protected Date date;
    protected PolarData vol1;
    protected PolarData vol2;
    
    /**
     * Volumes are sorted by site name in alphabetic order. Method
     * <code>getVol1()</code> will return the first volume and
     * <code>getVol2()</code> will return the other volume, and it will be
     * always the same order for the same pair.
     * <p>
     * If vol1 or vol2 is null <code>NullPointerException</code> is thrown
     * 
     * @param vol1
     * @param vol2
     */
    public PolarVolumesPair(PolarData vol1, PolarData vol2)
            throws CalidException {
        super(vol1.getSiteName(), vol2.getSiteName());

        
        if (vol1.getSiteName().compareTo(vol2.getSiteName()) > 0) {
            this.vol1 = vol1;
            this.vol2 = vol2;
        } else {
            this.vol1 = vol2;
            this.vol2 = vol1;
        }

        Date d1 = vol1.getTime();
        Date d2 = vol2.getTime();

        if (d1.equals(d2)) {
            this.date = d1;
        } else {
            throw new CalidException("Polar volumes dates are not equal: " + d1
                    + " and " + d2);
        }
    }

    
    /**
     * 
     * @return the vol1
     */
    public PolarData getVol1() {
        return vol1;
    }

    /**
     * @return the vol2
     */
    public PolarData getVol2() {
        return vol2;
    }
 
    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }
    
    public double getElevation() throws CalidException{
        return 0;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        PolarVolumesPair pair = (PolarVolumesPair) obj;
        try {
            String name11 = pair.getSource1();
            String name12 = pair.getSource2();
            String name21 = getSource1();
            String name22 = getSource2();
            if (!pair.getDate().equals(getDate()))
                return false;
            if (name11.matches(name21) && name12.matches(name22))
                return true;
            if (name12.matches(name21) && name11.matches(name22))
                return true;
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return date.hashCode();
        
    }
    
    public String toString() {
        return "PAIR [" + sdf.format(date) + "] [" + vol1.getSiteName() + " and " + vol2.getSiteName() + "]";
    }

    
}
