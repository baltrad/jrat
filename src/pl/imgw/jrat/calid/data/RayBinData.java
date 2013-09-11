/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RayBinData extends PairedPoint {
    
    private Double data1;
    private Double data2;

    /**
     * @param ray1
     * @param bin1
     * @param ray2
     * @param bin2
     */
    public RayBinData(int ray1, int bin1, int ray2, int bin2) {
        super(ray1, bin1, ray2, bin2);
        // TODO Auto-generated constructor stub
    }
    
   
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
    
    
}
