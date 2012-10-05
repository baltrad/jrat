/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.awt.geom.Point2D;


/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidCoords {

    private int ray1;
    private int bin1;
    private Point2D.Double coord1;
    private int ray2;
    private int bin2;
    private Point2D.Double coord2;
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
    public void setDifference(double difference) {
        this.difference = difference;
    }

    /**
     * @return the coord1
     */
    public Point2D.Double getCoord1() {
        return coord1;
    }

    /**
     * @param coord1 the coord1 to set
     */
    public void setCoord1(Point2D.Double coord1) {
        this.coord1 = coord1;
    }

    /**
     * @return the coord2
     */
    public Point2D.Double getCoord2() {
        return coord2;
    }

    /**
     * @param coord2 the coord2 to set
     */
    public void setCoord2(Point2D.Double coord2) {
        this.coord2 = coord2;
    }

    /**
     * 
     */
    public CalidCoords(int ray1, int bin1, int ray2, int bin2) {
        this.ray1 = ray1;
        this.bin1 = bin1;
        this.ray2 = ray2;
        this.bin2 = bin2;
    }

    public String toString() {

        String str = "";
        
        str += "(" + ray1 + ", " + bin1 + ")\t(" + ray2 + ", " + bin2
                + ")";
        
        if (coord1 != null && coord2 != null) {
            str += "\t(" + coord1.x + "," + coord1.y + ")\t(" + coord2.x + ","
                    + coord2.y + ")";
        }
        return str;
    }
    
    /**
     * @return the ray1
     */
    public int getRay1() {
        return ray1;
    }

    /**
     * @return the bin1
     */
    public int getBin1() {
        return bin1;
    }

    /**
     * @return the ray2
     */
    public int getRay2() {
        return ray2;
    }

    /**
     * @return the bin2
     */
    public int getBin2() {
        return bin2;
    }
 
    
}
