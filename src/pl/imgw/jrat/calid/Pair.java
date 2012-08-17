/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.util.Date;

import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.VolumeContainer;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Pair {

    private Date date;
    private VolumeContainer vol1;
    private VolumeContainer vol2;

    public Pair(VolumeContainer vol1, VolumeContainer vol2) {
        String source1 = vol1.getSiteName();
        String source2 = vol2.getSiteName();
        if (!source1.isEmpty() && !source1.matches(source2)) {
            this.vol1 = vol1;
            this.vol2 = vol2;
        }
        
        Date d1 = vol1.getTime();
        Date d2 = vol2.getTime();
        if (d1.equals(d2))
            this.date = d1;
    }

    public boolean isValid() {
        if (date != null && vol1 != null && vol2 != null)
            return true;
        return false;
    }

    /**
     * Returns 2-element array with site names
     * 
     * @return null if pair is not valid
     */
    public String[] getSiteNames() {
        if (isValid())
            return new String[] { vol1.getSiteName(), vol2.getSiteName() };
        else
            return null;
    }
    
    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the vol1
     */
    public VolumeContainer getVol1() {
        return vol1;
    }

    /**
     * @return the vol2
     */
    public VolumeContainer getVol2() {
        return vol2;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Pair pair = (Pair) obj;
        try {
            String name11 = pair.getSiteNames()[0];
            String name12 = pair.getSiteNames()[1];
            String name21 = getSiteNames()[0];
            String name22 = getSiteNames()[1];
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
    
}
