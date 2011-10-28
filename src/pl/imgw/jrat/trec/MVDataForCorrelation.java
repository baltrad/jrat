/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.trec;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MVDataForCorrelation {

    private double[] data1 = null;
    private double[] data2 = null;
    private int position = 0;
    private int size = 0;

    /**
     * 
     * @param size
     */
    public MVDataForCorrelation(int size) {

        this.size = size;
        this.data1 = new double[size];
        this.data2 = new double[size];
        position = 0;

    }

    /**
     * 
     * @param value1
     * @param value2
     * @return
     */
    public boolean addPoint(int value1, int value2) {
        if (position < size) {
            data1[position] = value1;
            data2[position] = value2;
            position++;
            return true;
        }
        return false;
    }

    public double[] getData1() {
        return data1;
    }

    public double[] getData2() {
        return data2;
    }

}
