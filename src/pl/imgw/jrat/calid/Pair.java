/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.text.SimpleDateFormat;
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
    private String source1;
    private String source2;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd/HH:mm");

    public Pair(String source1, String source2) {
        if (!source1.isEmpty() && !source1.matches(source2)) {
            if (source1.compareTo(source2) > 1) {
                this.source1 = source2;
                this.source2 = source1;
            } else {
                this.source1 = source1;
                this.source2 = source2;
            }
        }
    }
    
    /**
     * Volumes are sorted by site name in alphabetic order. Method
     * <code>getVol1()</code> will return the first volume and
     * <code>getVol2()</code> will return the other volume, and it will be
     * always the same order for the same pair.
     * 
     * @param vol1
     * @param vol2
     */
    public Pair(VolumeContainer vol1, VolumeContainer vol2) {
        String source1 = vol1.getSiteName();
        String source2 = vol2.getSiteName();
        if (!source1.isEmpty() && !source1.matches(source2)) {
            if (source1.compareTo(source2) > 1) {
                this.vol1 = vol1;
                this.vol2 = vol2;
                this.source1 = source1;
                this.source2 = source2;
            } else {
                this.vol1 = vol2;
                this.vol2 = vol1;
                this.source1 = source2;
                this.source2 = source1;
            }
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
     * @return the source1
     */
    public String getSource1() {
        return source1;
    }

    /**
     * @return the source2
     */
    public String getSource2() {
        return source2;
    }

    

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * 
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

    public String toString() {
        return "[pair] " + sdf.format(date) + ": " + source1 + " " + source2;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Pair pair = (Pair) obj;
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

}
