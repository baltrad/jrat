/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.trec;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MVCorrelation {
    
    private double correlation = 0;
    private MV mv = null;
    
    public MVCorrelation(double correlation, MV mv) {
        this.correlation = correlation;
        this.mv = mv;
    }

    /**
     * @return the correlation
     */
    public double getCorrelation() {
        return correlation;
    }

    /**
     * @param correlation the correlation to set
     */
    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    /**
     * @return the mv
     */
    public MV getMv() {
        return mv;
    }

    /**
     * @param mv the mv to set
     */
    public void setMv(MV mv) {
        this.mv = mv;
    }


    public String toString() {
        return (mv + " " + correlation);
    }
    
}
