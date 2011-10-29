/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import pl.imgw.jrat.util.HdfTreeUtil;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimH5Data {
    
    private String dataName;
    //what
    private String quantity;
    private Double gain;
    private Double offset;
    private Double nodata;
    private Double undetect;
    
    private String dclass;
    private String image_version;
    private ArrayData array;
    
    
    /**
     * @return the dataName
     */
    public String getDataName() {
        return dataName;
    }
    /**
     * @param dataName the dataName to set
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }
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
    /**
     * @return the dclass
     */
    public String getDclass() {
        return dclass;
    }
    /**
     * @param dclass the dclass to set
     */
    public void setDclass(String dclass) {
        this.dclass = dclass;
    }
    /**
     * @return the image_version
     */
    public String getImage_version() {
        return image_version;
    }
    /**
     * @param image_version the image_version to set
     */
    public void setImage_version(String image_version) {
        this.image_version = image_version;
    }
    /**
     * @return the array
     */
    public ArrayData getArray() {
        return array;
    }
    /**
     * @param array the array to set
     */
    public void setArray(ArrayData array) {
        this.array = array;
    }
    
    public void displayAll(int number) {
        System.out.println("data"+number + " attributes:");
        System.out.println("dclass "+this.dclass);
        System.out.println("gain "+this.gain);
        System.out.println("image_version "+this.image_version);
        System.out.println("nodata "+this.nodata);
        System.out.println("offset "+this.offset);
        System.out.println("undetected "+this.undetect);
        System.out.println("xsize "+this.array.sizeX);
        System.out.println("ysize "+this.array.sizeY);
        System.out.println(".............................");
    }
    /**
     * @param i
     * @param space
     */
    public void displayTree(int level) {
        String p1 = HdfTreeUtil.makeParent(level, "what");
        System.out.println(p1);
        int space = p1.length() - 1;
        HdfTreeUtil.makeAttribe(space, "quantity", this.quantity);
        HdfTreeUtil.makeAttribe(space, "gain", this.gain);
        HdfTreeUtil.makeAttribe(space, "offset", this.offset);
        HdfTreeUtil.makeAttribe(space, "nodata", this.nodata);
        HdfTreeUtil.makeAttribe(space, "undetected", this.undetect);
        
        String p2 = HdfTreeUtil.makeParent(level, "data");
        System.out.println(p2);
        space = p2.length() - 1;
        HdfTreeUtil.makeAttribe(space, "dclass", this.dclass);
        HdfTreeUtil.makeAttribe(space, "image_version", this.image_version);
        HdfTreeUtil.makeAttribe(space, "xsize", this.array.sizeX);
        HdfTreeUtil.makeAttribe(space, "ysize", this.array.sizeY);
    }
    

}
