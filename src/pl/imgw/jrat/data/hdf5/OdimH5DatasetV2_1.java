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
public class OdimH5DatasetV2_1 extends OdimH5Dataset {

    // how
    private String task;
    private boolean simulated;
    private Double rpm;
    private Double pulsewidth;
    private Double rxbandwidth;
    private Double lowprf;
    private Double highprf;
    private Double txloss;
    private Double rxloss;
    private Double radconstH;
    private Double radconstV;
    private Double nomTXpower;
    private Double[] txpower;
    private Double ni;
    private Integer vsamples;
    private String azmethod;
    private String binmethod;
    private Double[] elangles;
    private Double[] startazA;
    private Double[] stopazA;
    private Double[] startazT;
    private Double[] stopazT;
    private boolean malfunc;
    private String radar_msg;
    private Double nez;
    private String dclutter;
    private Double sqi;
    private Double csr;
    private Double log;
    private Double rac;
    private Double pac;
    private Double s2n;
    private String polarization;

    

    
    /**
     * @param i
     * @param j
     */
    public void displayTree(int level) {
        
        String p1 = HdfTreeUtil.makeParent(level, "what");
        System.out.println(p1);
        int space = p1.length() - 1;
        HdfTreeUtil.makeAttribe(space, "product", this.product);
        HdfTreeUtil.makeAttribe(space, "startdate", getStartdate());
        HdfTreeUtil.makeAttribe(space, "starttime", getStarttime());
        HdfTreeUtil.makeAttribe(space, "enddate", getEnddate());
        HdfTreeUtil.makeAttribe(space, "endtime", getEndtime());
        
        String p2 = HdfTreeUtil.makeParent(level, "where");
        System.out.println(p2);
        space = p2.length() - 1;
        HdfTreeUtil.makeAttribe(space, "elangle", this.elangle);
        HdfTreeUtil.makeAttribe(space, "a1gate", this.a1gate);
        HdfTreeUtil.makeAttribe(space, "nbins", this.nbins);
        HdfTreeUtil.makeAttribe(space, "rstart", this.rstart);
        HdfTreeUtil.makeAttribe(space, "rscale", this.rscale);
        HdfTreeUtil.makeAttribe(space, "nrays", this.nrays);
        
        String p3 = HdfTreeUtil.makeParent(level, "how");
        System.out.println(p3);
        space = p3.length() - 1;
        HdfTreeUtil.makeAttribe(space, "azmethod", this.azmethod);
        HdfTreeUtil.makeAttribe(space, "binmethod", this.binmethod);
        HdfTreeUtil.makeAttribe(space, "csr", this.csr);
        HdfTreeUtil.makeAttribe(space, "dcclutter", this.dclutter);
        HdfTreeUtil.makeAttribe(space, "highprf", this.highprf);
        HdfTreeUtil.makeAttribe(space, "log", this.log);
        HdfTreeUtil.makeAttribe(space, "lowprf", this.lowprf);
        HdfTreeUtil.makeAttribe(space, "malfunc", this.malfunc);
        HdfTreeUtil.makeAttribe(space, "nez", this.nez);
        HdfTreeUtil.makeAttribe(space, "ni", this.ni);
        HdfTreeUtil.makeAttribe(space, "nomTXpower", this.nomTXpower);
        HdfTreeUtil.makeAttribe(space, "pac", this.pac);
        HdfTreeUtil.makeAttribe(space, "polarization", this.polarization);
        HdfTreeUtil.makeAttribe(space, "pulsewidth", this.pulsewidth);
        HdfTreeUtil.makeAttribe(space, "rac", this.rac);
        HdfTreeUtil.makeAttribe(space, "radar_msg", this.radar_msg);
        HdfTreeUtil.makeAttribe(space, "radconstH", this.radconstH);
        HdfTreeUtil.makeAttribe(space, "radconstV", this.radconstV);
        HdfTreeUtil.makeAttribe(space, "rpm", this.rpm);
        HdfTreeUtil.makeAttribe(space, "rxbandwidth", this.rxbandwidth);
        HdfTreeUtil.makeAttribe(space, "rxloss", this.rxloss);
        HdfTreeUtil.makeAttribe(space, "s2n", this.s2n);
        HdfTreeUtil.makeAttribe(space, "simulated", this.simulated);
        HdfTreeUtil.makeAttribe(space, "sqi", this.sqi);
        HdfTreeUtil.makeAttribe(space, "task", this.task);
        HdfTreeUtil.makeAttribe(space, "txloss", this.txloss);
        HdfTreeUtil.makeAttribe(space, "vsamples", this.vsamples);
        
        for(int i = 0; i < data.length; i++) {
            String pn = HdfTreeUtil.makeParent(level, data[i].getDataName());
            System.out.println(pn);
            space = pn.length() - 1;

            data[i].displayTree(space);
        }
        
//        for(int i = 0; i < data.length; i++) {
//            data[i].displayAll(i);
        
    }
    
    /**
     * @return the task
     */
    public String getTask() {
        return task;
    }

    /**
     * @param task
     *            the task to set
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     * @return the simulated
     */
    public boolean isSimulated() {
        return simulated;
    }

    /**
     * @param simulated
     *            the simulated to set
     */
    public void setSimulated(boolean simulated) {
        this.simulated = simulated;
    }

    /**
     * @return the rpm
     */
    public Double getRpm() {
        return rpm;
    }

    /**
     * @param rpm
     *            the rpm to set
     */
    public void setRpm(Double rpm) {
        this.rpm = rpm;
    }

    /**
     * @return the pulsewidth
     */
    public Double getPulsewidth() {
        return pulsewidth;
    }

    /**
     * @param pulsewidth
     *            the pulsewidth to set
     */
    public void setPulsewidth(Double pulsewidth) {
        this.pulsewidth = pulsewidth;
    }

    /**
     * @return the rxbandwidth
     */
    public Double getRxbandwidth() {
        return rxbandwidth;
    }

    /**
     * @param rxbandwidth
     *            the rxbandwidth to set
     */
    public void setRxbandwidth(Double rxbandwidth) {
        this.rxbandwidth = rxbandwidth;
    }

    /**
     * @return the lowprf
     */
    public Double getLowprf() {
        return lowprf;
    }

    /**
     * @param lowprf
     *            the lowprf to set
     */
    public void setLowprf(Double lowprf) {
        this.lowprf = lowprf;
    }

    /**
     * @return the highprf
     */
    public Double getHighprf() {
        return highprf;
    }

    /**
     * @param highprf
     *            the highprf to set
     */
    public void setHighprf(Double highprf) {
        this.highprf = highprf;
    }

    /**
     * @return the txloss
     */
    public Double getTxloss() {
        return txloss;
    }

    /**
     * @param txloss
     *            the txloss to set
     */
    public void setTxloss(Double txloss) {
        this.txloss = txloss;
    }

    /**
     * @return the rxloss
     */
    public Double getRxloss() {
        return rxloss;
    }

    /**
     * @param rxloss
     *            the rxloss to set
     */
    public void setRxloss(Double rxloss) {
        this.rxloss = rxloss;
    }

    /**
     * @return the radconstH
     */
    public Double getRadconstH() {
        return radconstH;
    }

    /**
     * @param radconstH
     *            the radconstH to set
     */
    public void setRadconstH(Double radconstH) {
        this.radconstH = radconstH;
    }

    /**
     * @return the radconstV
     */
    public Double getRadconstV() {
        return radconstV;
    }

    /**
     * @param radconstV
     *            the radconstV to set
     */
    public void setRadconstV(Double radconstV) {
        this.radconstV = radconstV;
    }

    /**
     * @return the nomTXpower
     */
    public Double getNomTXpower() {
        return nomTXpower;
    }

    /**
     * @param nomTXpower
     *            the nomTXpower to set
     */
    public void setNomTXpower(Double nomTXpower) {
        this.nomTXpower = nomTXpower;
    }

    /**
     * @return the txpower
     */
    public Double[] getTxpower() {
        return txpower;
    }

    /**
     * @param txpower
     *            the txpower to set
     */
    public void setTxpower(Double[] txpower) {
        this.txpower = txpower;
    }

    /**
     * @return the ni
     */
    public Double getNi() {
        return ni;
    }

    /**
     * @param ni
     *            the ni to set
     */
    public void setNi(Double ni) {
        this.ni = ni;
    }

    /**
     * @return the vsamples
     */
    public Integer getVsamples() {
        return vsamples;
    }

    /**
     * @param Integereger
     *            the vsamples to set
     */
    public void setVsamples(Integer Integereger) {
        this.vsamples = Integereger;
    }

    /**
     * @return the azmethod
     */
    public String getAzmethod() {
        return azmethod;
    }

    /**
     * @param azmethod
     *            the azmethod to set
     */
    public void setAzmethod(String azmethod) {
        this.azmethod = azmethod;
    }

    /**
     * @return the binmethod
     */
    public String getBinmethod() {
        return binmethod;
    }

    /**
     * @param binmethod
     *            the binmethod to set
     */
    public void setBinmethod(String binmethod) {
        this.binmethod = binmethod;
    }

    /**
     * @return the elangles
     */
    public Double[] getElangles() {
        return elangles;
    }

    /**
     * @param elangles
     *            the elangles to set
     */
    public void setElangles(Double[] elangles) {
        this.elangles = elangles;
    }

    /**
     * @return the startazA
     */
    public Double[] getStartazA() {
        return startazA;
    }

    /**
     * @param startazA
     *            the startazA to set
     */
    public void setStartazA(Double[] startazA) {
        this.startazA = startazA;
    }

    /**
     * @return the stopazA
     */
    public Double[] getStopazA() {
        return stopazA;
    }

    /**
     * @param stopazA
     *            the stopazA to set
     */
    public void setStopazA(Double[] stopazA) {
        this.stopazA = stopazA;
    }

    /**
     * @return the startazT
     */
    public Double[] getStartazT() {
        return startazT;
    }

    /**
     * @param startazT
     *            the startazT to set
     */
    public void setStartazT(Double[] startazT) {
        this.startazT = startazT;
    }

    /**
     * @return the stopazT
     */
    public Double[] getStopazT() {
        return stopazT;
    }

    /**
     * @param stopazT
     *            the stopazT to set
     */
    public void setStopazT(Double[] stopazT) {
        this.stopazT = stopazT;
    }

    /**
     * @return the malfunc
     */
    public boolean isMalfunc() {
        return malfunc;
    }

    /**
     * @param malfunc
     *            the malfunc to set
     */
    public void setMalfunc(boolean malfunc) {
        this.malfunc = malfunc;
    }

    /**
     * @return the radar_msg
     */
    public String getRadar_msg() {
        return radar_msg;
    }

    /**
     * @param radar_msg
     *            the radar_msg to set
     */
    public void setRadar_msg(String radar_msg) {
        this.radar_msg = radar_msg;
    }

    /**
     * @return the nez
     */
    public Double getNez() {
        return nez;
    }

    /**
     * @param nez
     *            the nez to set
     */
    public void setNez(Double nez) {
        this.nez = nez;
    }

    /**
     * @return the dclutter
     */
    public String getDclutter() {
        return dclutter;
    }

    /**
     * @param dclutter
     *            the dclutter to set
     */
    public void setDclutter(String dclutter) {
        this.dclutter = dclutter;
    }

    /**
     * @return the sqi
     */
    public Double getSqi() {
        return sqi;
    }

    /**
     * @param sqi
     *            the sqi to set
     */
    public void setSqi(Double sqi) {
        this.sqi = sqi;
    }

    /**
     * @return the csr
     */
    public Double getCsr() {
        return csr;
    }

    /**
     * @param csr
     *            the csr to set
     */
    public void setCsr(Double csr) {
        this.csr = csr;
    }

    /**
     * @return the log
     */
    public Double getLog() {
        return log;
    }

    /**
     * @param log
     *            the log to set
     */
    public void setLog(Double log) {
        this.log = log;
    }

    /**
     * @return the rac
     */
    public Double getRac() {
        return rac;
    }

    /**
     * @param rac
     *            the rac to set
     */
    public void setRac(Double rac) {
        this.rac = rac;
    }

    /**
     * @return the pac
     */
    public Double getPac() {
        return pac;
    }

    /**
     * @param pac
     *            the pac to set
     */
    public void setPac(Double pac) {
        this.pac = pac;
    }

    /**
     * @return the s2n
     */
    public Double getS2n() {
        return s2n;
    }

    /**
     * @param s2n
     *            the s2n to set
     */
    public void setS2n(Double s2n) {
        this.s2n = s2n;
    }

    /**
     * @return the polarization
     */
    public String getPolarization() {
        return polarization;
    }

    /**
     * @param polarization
     *            the polarization to set
     */
    public void setPolarization(String polarization) {
        this.polarization = polarization;
    }
    
}
