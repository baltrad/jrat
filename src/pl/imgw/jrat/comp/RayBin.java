/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.comp;

import java.awt.geom.Point2D;


/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RayBin {

    private int ray1;
    private int bin1;
    private Point2D.Double coord1;
    private Double data1;
    private int ray2;
    private int bin2;
    private Point2D.Double coord2;
    private Double data2;
    
    /**
     * @return the data1
     */
    public double getData1() {
        return data1;
    }

    /**
     * @param data1 the data1 to set
     */
    public void setData1(double data1) {
        this.data1 = data1;
    }

    /**
     * @return the data2
     */
    public double getData2() {
        return data2;
    }

    /**
     * @param data2 the data2 to set
     */
    public void setData2(double data2) {
        this.data2 = data2;
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
    public RayBin(int ray1, int bin1, int ray2, int bin2) {
        this.ray1 = ray1;
        this.bin1 = bin1;
        this.ray2 = ray2;
        this.bin2 = bin2;
    }

    public String toString() {

        String str = "";
        if(data1 != null && data2 != null) {
            str += data1 + "\t" + data2 + "\t";
        }
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
