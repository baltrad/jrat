/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import static pl.imgw.jrat.data.hdf5.OdimH5Constans.*;
import pl.imgw.jrat.util.HdfTreeUtil;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimH5DatasetCompo extends OdimH5Dataset {

    protected String quantity;
    protected Double gain;
    protected Double offset;
    protected Double nodata;
    protected Double undetect;
    /**
     * @return the quantity
     */
    public String getQuantity() {
        return quantity;
    }
    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    /**
     * @return the gain
     */
    public Double getGain() {
        return gain;
    }
    /**
     * @param gain the gain to set
     */
    public void setGain(Double gain) {
        this.gain = gain;
    }
    /**
     * @return the offset
     */
    public Double getOffset() {
        return offset;
    }
    /**
     * @param offset the offset to set
     */
    public void setOffset(Double offset) {
        this.offset = offset;
    }
    /**
     * @return the nodata
     */
    public Double getNodata() {
        return nodata;
    }
    /**
     * @param nodata the nodata to set
     */
    public void setNodata(Double nodata) {
        this.nodata = nodata;
    }
    /**
     * @return the undetect
     */
    public Double getUndetect() {
        return undetect;
    }
    /**
     * @param undetect the undetect to set
     */
    public void setUndetect(Double undetect) {
        this.undetect = undetect;
    }
 
    public void displayTree(int level) {
        
        
        String p1 = HdfTreeUtil.makeParent(level, WHAT);
        System.out.println(p1);
        int space = p1.length() - 1;
        HdfTreeUtil.makeAttribe(space, PRODUCT, this.product);
        HdfTreeUtil.makeAttribe(space, STARTDATE, getStartdate());
        HdfTreeUtil.makeAttribe(space, STARTTIME, getStarttime());
        HdfTreeUtil.makeAttribe(space, ENDDATE, getEnddate());
        HdfTreeUtil.makeAttribe(space, ENDTIME, getEndtime());
        HdfTreeUtil.makeAttribe(space, QUANTITY, getQuantity());
        HdfTreeUtil.makeAttribe(space, GAIN, getGain());
        HdfTreeUtil.makeAttribe(space, OFFSET, getOffset());
        HdfTreeUtil.makeAttribe(space, NODATA, getNodata());
        HdfTreeUtil.makeAttribe(space, UNDETECT, getUndetect());
        
        for(int i = 0; i < data.length; i++) {
            String pn = HdfTreeUtil.makeParent(level, data[i].getDataName());
            System.out.println(pn);
            space = pn.length() - 1;

            data[i].displayTree(space);
        }
    }
    
}
