/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import static pl.imgw.jrat.data.hdf5.OdimH5_2_1.*;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pl.imgw.jrat.util.LogsHandler;
import pl.imgw.jrat.util.MessageLogger;

import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5Group;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RadarVolume extends PolarVolumeContainer implements DataSource{
    
    private boolean v = false;
    private File f;
    /**
     * 
     */
    public RadarVolume(boolean verbose) {
        this.v = verbose;
        
        // TODO Auto-generated constructor stub
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.hdf5.DataSource#initializeFromFile(java.io.File)
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean initializeFromFile(H5File f) {

        if (!f.canRead())
            return false;
        
        this.f = f;
        
        Group root = H5_Wrapper.getHDF5RootGroup(f, v);
        
        String conventions = H5_Wrapper.getHDF5StringValue(root, CONVENTIONS, v);
        if(!conventions.matches(ODIM_H5_V2_1)) {
            return false;
        }
        Group what = null;
        Group where = null;
        Group how = null;
        
        List<Group> dataset = new ArrayList<Group>();
        List<?> memberList = root.getMemberList();
        Iterator<?> itr = memberList.iterator();
        while (itr.hasNext()) {
            H5Group group = ((H5Group) itr.next());
//            System.out.println(group.getName());
            if (group.getName().matches(WHAT))
                what = group;
            else if (group.getName().matches(WHERE))
                where = group;
            else if (group.getName().matches(HOW))
                how = group;
            else if (group.getName().startsWith(DATASET))
                dataset.add(group);
        }

        if (what.hasAttribute()) {
            setObject(H5_Wrapper.getHDF5StringValue(what, OBJECT, v));
            setVersion(H5_Wrapper.getHDF5StringValue(what, VERSION, v));
            try {
                setDate(H5_Wrapper.getHDF5StringValue(what, DATE, v),
                        H5_Wrapper.getHDF5StringValue(what, TIME, v));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setSource(H5_Wrapper.getHDF5StringValue(what, SOURCE, v));
        }

        if (where.hasAttribute()) {
            setLon(H5_Wrapper.getHDF5DoubleValue(where, LON, v));
            setLat(H5_Wrapper.getHDF5DoubleValue(where, LAT, v));
            setHeight(H5_Wrapper.getHDF5DoubleValue(where, HEIGHT, v));
        }

        if (how.hasAttribute()) {
            setSystem(H5_Wrapper.getHDF5StringValue(how, SYSTEM, v));
            setSoftware(H5_Wrapper.getHDF5StringValue(how, SOFTWARE, v));
            setSw_version(H5_Wrapper.getHDF5StringValue(how, SW_VERSION, v));
            setBeamwidth(H5_Wrapper.getHDF5DoubleValue(how, BEAMWIDTH, v));
            setWavelength(H5_Wrapper.getHDF5DoubleValue(how, WAVELENGTH, v));
            setRadomeloss(H5_Wrapper.getHDF5DoubleValue(how, RADOMELOSS, v));
            setAntgain(H5_Wrapper.getHDF5DoubleValue(how, ANTGAIN, v));
            setBeamwH(H5_Wrapper.getHDF5DoubleValue(how, BEAMWH, v));
            setBeamwV(H5_Wrapper.getHDF5DoubleValue(how, BEAMWV, v));
            setGasattn(H5_Wrapper.getHDF5DoubleValue(how, GASSATTN, v));
        }

        PolarScanDataset[] dsArray = new PolarScanDataset[dataset.size()];
        for (int i = 0; i < dataset.size(); i++) {
            List<Group> data = new ArrayList<Group>();
            PolarScanDataset psd = new PolarScanDataset();
            List<?> datasetlist = dataset.get(i).getMemberList();
            Iterator<?> dtsitr = datasetlist.iterator();
            psd.setDatasetname(dataset.get(i).getName());
            while (dtsitr.hasNext()) {
                H5Group group = ((H5Group) dtsitr.next());
//                System.out.print("i="+i + " " + group.getName()+ " ");
                if (group.getName().contains(WHAT))
                    what = group;
                else if (group.getName().matches(WHERE))
                    where = group;
                else if (group.getName().matches(HOW))
                    how = group;
                else if (group.getName().startsWith(DATA) && group.getName().length() > 4)
                    data.add(group);
            }
//            System.out.print("\n");

            if (what.hasAttribute()) {
                psd.setProduct(H5_Wrapper.getHDF5StringValue(what, PRODUCT, v));
                try {
                    psd.setStartdate(
                            H5_Wrapper.getHDF5StringValue(what, STARTDATE, v),
                            H5_Wrapper.getHDF5StringValue(what, STARTTIME, v));
                    psd.setEnddate(
                            H5_Wrapper.getHDF5StringValue(what, ENDDATE, v),
                            H5_Wrapper.getHDF5StringValue(what, ENDTIME, v));
                    
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (where.hasAttribute()) {
                psd.setElangle(H5_Wrapper.getHDF5DoubleValue(where, ELANGLE, v));
                psd.setA1gate(H5_Wrapper.getHDF5IntegerValue(where, A1GATE, v));
                psd.setNbins(H5_Wrapper.getHDF5IntegerValue(where, NBINS, v));
                psd.setRstart(H5_Wrapper.getHDF5DoubleValue(where, RSTART, v));
                psd.setRscale(H5_Wrapper.getHDF5DoubleValue(where, RSCALE, v));
                psd.setNrays(H5_Wrapper.getHDF5IntegerValue(where, NRAYS, v));
            }
            if(how.hasAttribute()) {
                psd.setTask(H5_Wrapper.getHDF5StringValue(how, TASK, v));
//                psd.setSimulated(H5_Wrapper.getHDF5DoubleValue(how, SIMULATED, v));
                psd.setRpm(H5_Wrapper.getHDF5DoubleValue(how, RPM, v));
                psd.setPulsewidth(H5_Wrapper.getHDF5DoubleValue(how, PULSEWIDTH, v));
                psd.setRxbandwidth(H5_Wrapper.getHDF5DoubleValue(how, RXBANDWIDTH, v));
                psd.setLowprf(H5_Wrapper.getHDF5DoubleValue(how, LOWPRF, v));
                psd.setHighprf(H5_Wrapper.getHDF5DoubleValue(how, HIGHPRF, v));
                psd.setTxloss(H5_Wrapper.getHDF5DoubleValue(how, TXLOSS, v));
                psd.setRxloss(H5_Wrapper.getHDF5DoubleValue(how, RXLOSS, v));
                psd.setRadconstH(H5_Wrapper.getHDF5DoubleValue(how, RADCONSTH, v));
                psd.setRadconstV(H5_Wrapper.getHDF5DoubleValue(how, RADCONSTV, v));
                psd.setNomTXpower(H5_Wrapper.getHDF5DoubleValue(how, NOMTXPOWER, v));
//                psd.setTxpower(??);
                psd.setNi(H5_Wrapper.getHDF5DoubleValue(how, NI, v));
                psd.setVsamples(H5_Wrapper.getHDF5IntegerValue(how, VSAMPLES, v));
                psd.setAzmethod(H5_Wrapper.getHDF5StringValue(how, AZMETHOD, v));
                psd.setBinmethod(H5_Wrapper.getHDF5StringValue(how, BINMETHOD, v));
//                psd.setElangles(??);
//                psd.setStartazA(??);
//                psd.setStopazA(??);
//                psd.setStopazT(??);
//                psd.setMalfunc(??);
                psd.setRadar_msg(H5_Wrapper.getHDF5StringValue(how, RADAR_MSG, v));
                psd.setNez(H5_Wrapper.getHDF5DoubleValue(how, NEZ, v));
                psd.setDclutter(H5_Wrapper.getHDF5StringValue(how, DCLUTTER, v));
                psd.setSqi(H5_Wrapper.getHDF5DoubleValue(how, SQI, v));
                psd.setCsr(H5_Wrapper.getHDF5DoubleValue(how, CSR, v));
                psd.setLog(H5_Wrapper.getHDF5DoubleValue(how, LOG, v));
                psd.setRac(H5_Wrapper.getHDF5DoubleValue(how, RAC, v));
                psd.setPac(H5_Wrapper.getHDF5DoubleValue(how, PAC, v));
                psd.setS2n(H5_Wrapper.getHDF5DoubleValue(how, S2N, v));
                psd.setPolarization(H5_Wrapper.getHDF5StringValue(how, POLARIZATION, v));
                PolarScanData[] psArray = new PolarScanData[data.size()];
                for (int j = 0; j < data.size(); j++) {
                    List<Dataset> dts = new ArrayList<Dataset>();

                    PolarScanData ps = new PolarScanData();
                    List<?> dataList = data.get(j).getMemberList();
                    Iterator<?> dataitr = dataList.iterator();
                    ps.setDataName(data.get(j).getName());
                    while (dataitr.hasNext()) {
                        HObject group = ((HObject) dataitr.next());
//                        System.out.print("j="+j + " " + group.getName() + " ");
                        if (group.getName().matches(WHAT))
                            what = (H5Group)group;
                        else if (group.getName().matches(DATA))
                            dts.add((Dataset)group);
                    }
//                    System.out.println("\n");
                    
                    if(what.hasAttribute()) {
                        ps.setQuantity(H5_Wrapper.getHDF5StringValue(what,
                                QUANTITY, v));
                        ps.setGain(H5_Wrapper.getHDF5DoubleValue(what, GAIN, v));
                        ps.setOffset(H5_Wrapper.getHDF5DoubleValue(what,
                                OFFSET, v));
                        ps.setNodata(H5_Wrapper.getHDF5DoubleValue(what,
                                NODATA, v));
                        ps.setUndetect(H5_Wrapper.getHDF5DoubleValue(what,
                                UNDETECT, v));
                        ArrayData arrayData = new ArrayData(psd.getNbins(), psd.getNrays());
                        try {
//                            System.out.println("type: " + dts.get(j).getDatatype());
                            arrayData.data = H5_Wrapper.getHDF5ByteDataset(
                                    dts.get(j), psd.getNbins(), psd.getNrays(),
                                    v);
                        } catch (OutOfMemoryError e) {
                            LogsHandler.saveProgramLogs("RadarVolume",
                                    "out of memory " + e.getMessage());
                            return false;
                        } catch (Exception e) {
                            LogsHandler.saveProgramLogs("RadarVolume",
                                    e.getMessage());
                            return false;
                        }
                        ps.setArray(arrayData);
                    }
                    psArray[j] = ps;
                }
                psd.setData(psArray);
            }
            dsArray[i] = psd;
        }
        setDataset(dsArray);
        String message = "Volume file loaded:\n";
        message += "System:\t\t" + getSystem() + "\n";
        message += "Software:\t" + getSoftware() + " " + getSw_version() + "\n";
        message += "Data source:\t" + getSource() + "\n";
        message += "Scan time:\t" + getFullDate() + "\n";
        MessageLogger.showMessage(message, v);
        
        return true;
    }
    
    
    
    /**
     * @return volume file
     */
    public File getFile() {
        return f;
    }


}
