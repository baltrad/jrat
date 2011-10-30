/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import pl.imgw.jrat.util.HdfTreeUtil;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimH5Dataset {

    
    protected String datasetname;
    // what
    protected String product;
    protected Date startdate;
    protected Date enddate;
    protected SimpleDateFormat sdfDate = new SimpleDateFormat("YYYYMMdd");
    protected SimpleDateFormat sdfTime = new SimpleDateFormat("HHmmss");
    protected SimpleDateFormat sdfDateTime = new SimpleDateFormat("YYYYMMddHHmmss");
    protected DateFormat sdfGMT = new SimpleDateFormat("YYYY-MM-dd HH:mm z");

    // data
    protected OdimH5Data[] data;
    protected Integer dataSize;

    
    /**
     * @return the datasetname
     */
    public String getDatasetname() {
        return datasetname;
    }

    /**
     * @param datasetname the datasetname to set
     */
    public void setDatasetname(String datasetname) {
        this.datasetname = datasetname;
    }

    /**
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @param product
     *            the product to set
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * @return the startdate
     */
    public String getStartdate() {
        return sdfDate.format(startdate);
         
    }
    /**
     * @return the startdate
     */
    public String getStarttime() {
        return sdfTime.format(startdate);
        
    }
    
    /**
     * 
     * @return
     */
    public String getFullStartDate() {
        sdfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdfGMT.format(startdate);
    }

    /**
     * 
     * @return
     */
    public String getFullEndDate() {
        sdfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdfGMT.format(enddate);
    }

    /**
     * 
     * @param date
     *            YYYYMMdd
     * @param time
     *            HHmmss
     * @throws ParseException
     */
    public void setStartdate(String date, String time) throws ParseException {

        this.startdate = sdfDateTime.parse(date + time);

    }

    /**
     * 
     * @param date
     *            YYYYMMdd
     * @param time
     *            HHmmss
     * @throws ParseException
     */
    public void setEnddate(String date, String time) throws ParseException {

        this.enddate = sdfDateTime.parse(date + time);

    }

    /**
     * @return the enddate
     */
    public String getEnddate() {
        return sdfDate.format(enddate);
    }
    /**
     * @return the enddate
     */
    public String getEndtime() {
        return sdfTime.format(enddate);
    }

    /**
     * @param enddate
     *            the enddate to set
     */
    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }
    /**
     * @param enddate
     *            the enddate to set
     */
    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }





    /**
     * @return the data
     */
    public OdimH5Data[] getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(OdimH5Data[] data) {
        this.data = data;
        if (data != null)
            this.dataSize = data.length;
        else
            this.dataSize = 0;
    }

    /**
     * @return the dataSize
     */
    public int getDataSize() {
        return dataSize;
    }
    /**
     * @param dataSize
     *            the dataSize to set
     */

    /*
     * Helping method
     *
    public void displayAll(int number) {
        
        System.out.println("dataset"+number + " attributes:");
        
        System.out.println("a1gate " + this.a1gate);
        System.out.println("azmethod " + this.azmethod);
        System.out.println("binmethod " + this.binmethod);
        System.out.println("csr " + this.csr);
        System.out.println("dcclutter " + this.dclutter);
        System.out.println("elangle " + this.elangle);
        System.out.println("highprf " + this.highprf);
        System.out.println("log " + this.log);
        System.out.println("lowprf " + this.lowprf);
        System.out.println("malfunc " + this.malfunc);
        System.out.println("nbins " + this.nbins);
        System.out.println("nez " + this.nez);
        System.out.println("ni " + this.ni);
        System.out.println("nomTXpower " + this.nomTXpower);
        System.out.println("nrays " + this.nrays);
        System.out.println("pac " + this.pac);
        System.out.println("polarization " + this.polarization);
        System.out.println("product " + this.product);
        System.out.println("pulsewidth " + this.pulsewidth);
        System.out.println("rac " + this.rac);
        System.out.println("radar_msg " + this.radar_msg);
        System.out.println("radconstH " + this.radconstH);
        System.out.println("radconstV " + this.radconstV);
        System.out.println("rpm " + this.rpm);
        System.out.println("rscale " + this.rscale);
        System.out.println("rstart " + this.rstart);
        System.out.println("rxbandwidth " + this.rxbandwidth);
        System.out.println("rxloss " + this.rxloss);
        System.out.println("s2n " + this.s2n);
        System.out.println("simulated " + this.simulated);
        System.out.println("sqi " + this.sqi);
        System.out.println("task " + this.task);
        System.out.println("txloss " + this.txloss);
        System.out.println("vsamples " + this.vsamples);
        System.out.println("datasize " + this.dataSize);
        System.out.println("--------------------------");
        for(int i = 0; i < data.length; i++) {
            data[i].displayAll(i);
        }
    }
*/


    /**
     * @param i
     * @param j
     */
    public void displayTree(int level) {
        
        
        String p1 = HdfTreeUtil.makeParent(level, WHAT);
        System.out.println(p1);
        int space = p1.length() - 1;
        HdfTreeUtil.makeAttribe(space, PRODUCT, this.product);
        HdfTreeUtil.makeAttribe(space, STARTDATE, getStartdate());
        HdfTreeUtil.makeAttribe(space, STARTTIME, getStarttime());
        HdfTreeUtil.makeAttribe(space, ENDDATE, getEnddate());
        HdfTreeUtil.makeAttribe(space, ENDTIME, getEndtime());
        
        for(int i = 0; i < data.length; i++) {
            String pn = HdfTreeUtil.makeParent(level, data[i].getDataName());
            System.out.println(pn);
            space = pn.length() - 1;

            data[i].displayTree(space);
        }
        
//        for(int i = 0; i < data.length; i++) {
//            data[i].displayAll(i);
        
    }
    
    private String makeChild (int level, String name) {
        String line = "";
        for(int i = 0; i < level; i++)
            line += " ";
        return line+"|-"+name;
    }

    
}
