/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.trec;

import pl.imgw.jrat.data.hdf5.ArrayData;


/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MVPrepContainer {

    private ArrayData data = null;
    private ArrayData aggrData = null;
    private int numberOfAggr = 0;
    private MVCorrelation[] corrMV = null;
    private boolean[] aggrValidGrid = null;
    private int gridsize = 0;
    
    public ArrayData getData() {
        return data;
    }
    public void setData(ArrayData data) {
        this.data = data;
    }
    public ArrayData getAggrData() {
        return aggrData;
    }
    public void setAggrData(ArrayData aggregatesData) {
        this.aggrData = aggregatesData;
    }
    public int getNumberOfAggr() {
        return numberOfAggr;
    }
    public void setNumberOfAggr(int numberOfAggr) {
        this.numberOfAggr = numberOfAggr;
    }
    public MVCorrelation[] getCorrMV() {
        return corrMV;
    }
    public void setCorrMV(MVCorrelation[] corrMV) {
        this.corrMV = corrMV;
    }
    public boolean[] getAggrValidGrid() {
        return aggrValidGrid;
    }
    public void setAggrValidGrid(boolean[] aggrValidGrid) {
        this.aggrValidGrid = aggrValidGrid;
    }
    
    /**
     * @return the gridsize
     */
    public int getGridsize() {
        return gridsize;
    }
    /**
     * @param gridsize the gridsize to set
     */
    public void setGridsize(int gridsize) {
        this.gridsize = gridsize;
    }

    public boolean isValid() {

        if (data == null || aggrData == null || corrMV == null)
            return false;

        return true;
    }

    
    
}
