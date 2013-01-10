/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PairPointDifference {

    private Double difference = null;
    
    /**
     * @return the difference,
     * null if not set
     */
    public Double getDifference() {
        return difference;
    }

    /**
     * @param difference the difference to set
     */
    public void setDifference(Double difference) {
        this.difference = difference;
    }
    
}
