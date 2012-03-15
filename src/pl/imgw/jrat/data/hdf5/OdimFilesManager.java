/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import static pl.imgw.jrat.data.hdf5.OdimH5Constans.COMP;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.CONVENTIONS;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.H5_EXT;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.HDF_EXT;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.IMAGE;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.OBJECT;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.ODIM_H5_V2_0;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.ODIM_H5_V2_1;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.PVOL;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.WHAT;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;
import pl.imgw.jrat.util.MessageLogger;
import pl.imgw.jrat.view.Printing;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimFilesManager {

    public static List<OdimH5File> makeList(Collection<File> list, boolean verbose) {
        
        List<OdimH5File> odims = new ArrayList<OdimH5File>();
        Iterator<File> itr = list.iterator();
        while (itr.hasNext()) {
            String newfile = itr.next().getPath();
            if (newfile.endsWith(HDF_EXT)
                    || newfile.endsWith(H5_EXT)) {

                File f = new File(newfile);
                H5File file = H5_Wrapper.openHDF5File(f.getAbsolutePath(),
                        verbose);
                if (file != null && file.canRead()) {

                    Group root = H5_Wrapper.getHDF5RootGroup(file, verbose);
                    // validating conditions
                    String format = H5_Wrapper.getHDF5StringValue(root,
                            WHAT, OBJECT, verbose);
                    String model = H5_Wrapper.getHDF5StringValue(root,
                            CONVENTIONS, verbose);

                    OdimH5File odim = null;
                    if (format.matches(PVOL)) {
                        if (model.matches(ODIM_H5_V2_0)) {
                            odim = new RadarVolumeV2_0();
                        } else if (model.matches(ODIM_H5_V2_1)) {
                            odim = new RadarVolumeV2_1(verbose);
                        } else {
                            System.out.println("Model " + model
                                    + " not supported");
                            break;
                        }

                    } else if (format.matches(IMAGE)) {
                        System.out.println("Reading IMAGE");
                    } else if (format.matches(COMP)) {
                        odim = new OdimCompo(verbose);
                    } else {
                        System.out.println("Format " + format
                                + " not suppoerted");
                    }
                    if (odim.initializeFromRoot(root)) {
                        odims.add(odim);
                        odim.displayGeneralOdimInfo(verbose);
                        odim.displayGeneralObjectInfo(verbose);
                    } else
                        MessageLogger.showMessage("Faild to read the file", true);

                    
                } else {
                    MessageLogger.showMessage("unable to read " + newfile,
                            true);
                }
            } else {
                MessageLogger.showMessage("type of " + newfile
                        + " not supported", true);
            }

        }
        return odims;
    }
    
}
