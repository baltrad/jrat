/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ncsa.hdf.object.Group;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimH5 implements Comparable<OdimH5> {

    
    protected Group root;
    // what
    protected String object;
    protected String version;
    protected Date date;
    protected String source;
    protected SimpleDateFormat sdfDate = new SimpleDateFormat("YYYYMMdd");
    protected SimpleDateFormat sdfTime = new SimpleDateFormat("HHmmss");
    protected SimpleDateFormat sdfDateTime = new SimpleDateFormat(
            "YYYYMMddHHmmss");
    protected final static DateFormat sdfGMT = new SimpleDateFormat("YYYY-MM-dd HH:mm z");
    

 // dataset
    protected OdimH5Dataset[] dataset;
    protected int datasetSize;
    
    /**
     * 
     */
    public void displayGeneralOdimInfo(boolean verbose) {
        if (verbose) {
            System.out.println("This is OdimH5 file");
            System.out.println("Model version:\t" + version);
            System.out.println("Object:\t\t" + object);
        }
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.hdf5.OdimH5File#getArrayData(java.lang.String)
     */
    public OdimH5Dataset getDataset(String path) {
        String[] groups = path.split("/");
        String dataset = "";
        for(int i = 0; i < groups.length; i++)
            if(groups[i].contains("dataset"))
                dataset = groups[i];
        for (int i = 0; i < getDatasetSize(); i++) {
            if (getDataset()[i].datasetname.matches(dataset))
                return getDataset()[i];
        }
        return null;
    }
    
    /**
     * @return the object
     */
    public String getObject() {
        return object;
    }

    /**
     * @param object
     *            the object to set
     */
    public void setObject(String object) {
        this.object = object;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * YYYY-MM-dd HH:mm z eg. 2011-01-03 22:20 GMT
     * 
     * @return
     */
    public String getFullDate() {
        sdfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdfGMT.format(date);
    }

    /**
     * @return the date YYYYMMdd
     */
    public String getDate() {
        return sdfDate.format(date);
    }

    /**
     * @return the time HHmmss
     */
    public String getTime() {
        return sdfTime.format(date);
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @param date
     *            the date to set YYYYMMdd and HHmmss
     * @throws ParseException
     */
    public void setDate(String date, String time) throws ParseException {
        this.date = sdfDateTime.parse(date + time);
    }
    
    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the dataset
     */
    public OdimH5Dataset[] getDataset() {
        return dataset;
    }

    /**
     * Setting dataset and dataset size fields.
     * 
     * @param dataset
     *            the dataset to set
     */
    public void setDataset(OdimH5Dataset[] dataset) {
        this.dataset = dataset;
        if (dataset != null)
            this.datasetSize = dataset.length;
    }

    /**
     * @return the datasetSize
     */
    public int getDatasetSize() {
        return datasetSize;
    }
    
    protected String getDataTree() {
        
        
        return "";
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(OdimH5 o) {
        if(this.getFullDate().matches(o.getFullDate())) {
            return 0;
        }
        if(this.date.after(o.date))
            return 1;
        return -1;
    }

    /**
     * "YYYY-MM-dd HH:mm z"
     * @return the sdfGMT
     */
    public static DateFormat getFullDateFormat() {
        return sdfGMT;
    }
    
    
    
}
