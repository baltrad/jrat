/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.text.SimpleDateFormat;

import pl.imgw.jrat.calid.CalidException;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RadarsPair {



    protected String source1 = "";
    protected String source2 = "";
    
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd/HH:mm");

    protected RadarsPair() {
        
    }
    
    /**
     * 
     * @param source1
     * @param source2
     */
    public RadarsPair(String source1, String source2) throws CalidException {
        if (source1 == null || source2 == null)
            throw new CalidException(
                    "Pair constructor failed. Source name cannot be null");
        if (source1.isEmpty() && source2.isEmpty()) {
            throw new CalidException(
                    "Pair constructor failed. Both source names cannot be empty");
        }
        if (!source1.matches(source2)) {
            if (source2.isEmpty() || source1.compareTo(source2) > 0) {
                this.source1 = source1;
                this.source2 = source2;
            } else {
                this.source1 = source2;
                this.source2 = source1;
            }
        } else
            throw new CalidException(
                    "Pair constructor failed. Sources cannot have the same names");
    }
    

    public boolean hasBothSources() {
        if(source1.isEmpty() || source2.isEmpty())
            return false;
        return true;
    }
    
    public boolean hasOnlyOneSource() {
        if(source1.isEmpty() && !source2.isEmpty())
            return true;
        if(source2.isEmpty() && !source1.isEmpty())
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

    public String getBothSources() {
        return source1 + "," + source2;
    }


    public String toString() {
        return "PAIR " + source1 + " and " + source2;
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
        return getBothSources().hashCode();
        
    }
    
}
