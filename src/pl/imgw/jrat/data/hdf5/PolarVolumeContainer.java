/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

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
public class PolarVolumeContainer {

    // what
    private String object;
    private String version;
    private Date date;
    private String source;
    private SimpleDateFormat sdfDate = new SimpleDateFormat("YYYYMMdd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HHmmss");
    private SimpleDateFormat sdfDateTime = new SimpleDateFormat(
            "YYYYMMddHHmmss");
    private DateFormat sdfGMT = new SimpleDateFormat("YYYY-MM-dd HH:mm z");

    // where
    private Double lon;
    private Double lat;
    private Double height;

    // how
    private String system;
    private String software;
    private String sw_version;
    private Double beamwidth;
    private Double wavelength;
    private Double radomeloss;
    private Double antgain;
    private Double beamwH;
    private Double beamwV;
    private Double gasattn;

    // dataset
    private PolarScanDataset[] dataset;
    int datasetSize;

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
     * @return the date
     */
    public String getDate() {
        return sdfDate.format(date);
    }

    /**
     * @return the date
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
     *            the date to set
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
     * @return the lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     * @param lon
     *            the lon to set
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * @return the lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     * @param lat
     *            the lat to set
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * @return the height
     */
    public Double getHeight() {
        return height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system
     *            the system to set
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the software
     */
    public String getSoftware() {
        return software;
    }

    /**
     * @param software
     *            the software to set
     */
    public void setSoftware(String software) {
        this.software = software;
    }

    /**
     * @return the sw_version
     */
    public String getSw_version() {
        return sw_version;
    }

    /**
     * @param sw_version
     *            the sw_version to set
     */
    public void setSw_version(String sw_version) {
        this.sw_version = sw_version;
    }

    /**
     * @return the beamwidth
     */
    public Double getBeamwidth() {
        return beamwidth;
    }

    /**
     * @param beamwidth
     *            the beamwidth to set
     */
    public void setBeamwidth(Double beamwidth) {
        this.beamwidth = beamwidth;
    }

    /**
     * @return the wavelength
     */
    public Double getWavelength() {
        return wavelength;
    }

    /**
     * @param wavelength
     *            the wavelength to set
     */
    public void setWavelength(Double wavelength) {
        this.wavelength = wavelength;
    }

    /**
     * @return the radomeloss
     */
    public Double getRadomeloss() {
        return radomeloss;
    }

    /**
     * @param radomeloss
     *            the radomeloss to set
     */
    public void setRadomeloss(Double radomeloss) {
        this.radomeloss = radomeloss;
    }

    /**
     * @return the antgain
     */
    public Double getAntgain() {
        return antgain;
    }

    /**
     * @param antgain
     *            the antgain to set
     */
    public void setAntgain(Double antgain) {
        this.antgain = antgain;
    }

    /**
     * @return the beamwH
     */
    public Double getBeamwH() {
        return beamwH;
    }

    /**
     * @param beamwH
     *            the beamwH to set
     */
    public void setBeamwH(Double beamwH) {
        this.beamwH = beamwH;
    }

    /**
     * @return the beamwV
     */
    public Double getBeamwV() {
        return beamwV;
    }

    /**
     * @param beamwV
     *            the beamwV to set
     */
    public void setBeamwV(Double beamwV) {
        this.beamwV = beamwV;
    }

    /**
     * @return the gasattn
     */
    public Double getGasattn() {
        return gasattn;
    }

    /**
     * @param gasattn
     *            the gasattn to set
     */
    public void setGasattn(Double gasattn) {
        this.gasattn = gasattn;
    }

    /**
     * @return the dataset
     */
    public PolarScanDataset[] getDataset() {
        return dataset;
    }

    /**
     * Setting dataset and dataset size fields.
     * 
     * @param dataset
     *            the dataset to set
     */
    public void setDataset(PolarScanDataset[] dataset) {
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

    /*
     * Helping method
     */
    public void displayAll() {
        System.out.println("root att:");

        System.out.println("antgain " + this.antgain);
        System.out.println("beamwH " + this.beamwH);
        System.out.println("beamwidth " + this.beamwidth);
        System.out.println("beamwV " + this.beamwV);
        System.out.println("gasattn " + this.gasattn);
        System.out.println("height " + this.height);
        System.out.println("lat " + this.lat);
        System.out.println("lon " + this.lon);
        System.out.println("object " + this.object);
        System.out.println("radomeloss " + this.radomeloss);
        System.out.println("sofware " + this.software);
        System.out.println("source " + this.source);
        System.out.println("sw_version " + this.sw_version);
        System.out.println("system " + this.system);
        System.out.println("version " + this.version);
        System.out.println("wavelenght " + this.wavelength);
        System.out.println("date " + this.date);
        System.out.println("datasetSize " + this.datasetSize);
        System.out.println("________________________________");
        for (int i = 0; i < datasetSize; i++) {
            dataset[i].displayAll(i);
        }
    }

    /*
     * Helping method
     */
    public void displayTree() {
        String gp = HdfTreeUtil.makeGrantparent("\\");
        System.out.println(gp);
        String p1 = HdfTreeUtil.makeParent(gp.length() - 1, "what");
        System.out.println(p1);
        int space = p1.length() - 1;
        HdfTreeUtil.makeAttribe(space, "object", this.object);
        HdfTreeUtil.makeAttribe(space, "version", this.version);
        HdfTreeUtil.makeAttribe(space, "date", getDate());
        HdfTreeUtil.makeAttribe(space, "time", getTime());
        HdfTreeUtil.makeAttribe(space, "source", this.source);

        String p2 = HdfTreeUtil.makeParent(gp.length() - 1, "where");
        System.out.println(p2);
        space = p2.length() - 1;
        HdfTreeUtil.makeAttribe(space, "lon", this.lon);
        HdfTreeUtil.makeAttribe(space, "lat", this.lat);
        HdfTreeUtil.makeAttribe(space, "height", this.height);

        String p3 = HdfTreeUtil.makeParent(gp.length() - 1, "how");
        System.out.println(p3);
        space = p3.length() - 1;
        HdfTreeUtil.makeAttribe(space, "system", this.system);
        HdfTreeUtil.makeAttribe(space, "sofware", this.software);
        HdfTreeUtil.makeAttribe(space, "sw_version", this.sw_version);
        HdfTreeUtil.makeAttribe(space, "beamwidth", this.beamwidth);
        HdfTreeUtil.makeAttribe(space, "wavelenght", this.wavelength);
        HdfTreeUtil.makeAttribe(space, "radomeloss", this.radomeloss);
        HdfTreeUtil.makeAttribe(space, "antgain", this.antgain);
        HdfTreeUtil.makeAttribe(space, "beamwH", this.beamwH);
        HdfTreeUtil.makeAttribe(space, "beamwV", this.beamwV);
        HdfTreeUtil.makeAttribe(space, "gasattn", this.gasattn);

        // System.out.println(makeAttribe(space, "datasetSize",
        // String.valueOf(this.datasetSize)));
        for (int i = 0; i < datasetSize; i++) {
            String pn = HdfTreeUtil.makeParent(gp.length() - 1, dataset[i].getDatasetname());
            System.out.println(pn);
            space = pn.length() - 1;

            dataset[i].displayTree(space);
        }
    }

}
