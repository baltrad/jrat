/*
 * OdimH5 :: Converter software for OPERA Data Information Model
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.jrat.controller;

import static pl.imgw.jrat.data.hdf5.OdimH5Constans.COMP;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.CONVENTIONS;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.FILE_NAME_EXTENSION;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.FILE_NAME_EXTENSION1;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.IMAGE;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.OBJECT;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.ODIM_H5_V2_0;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.ODIM_H5_V2_1;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.PVOL;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.WHAT;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;
import pl.imgw.jrat.comp.MatchingPoints;
import pl.imgw.jrat.comp.MatchingPointsManager;
import pl.imgw.jrat.data.hdf5.H5_Wrapper;
import pl.imgw.jrat.data.hdf5.OdimCompo;
import pl.imgw.jrat.data.hdf5.OdimH5File;
import pl.imgw.jrat.data.hdf5.RadarVolume;
import pl.imgw.jrat.data.hdf5.RadarVolumeV2_0;
import pl.imgw.jrat.data.hdf5.RadarVolumeV2_1;
import pl.imgw.jrat.util.CommandLineArgsParser;
import pl.imgw.jrat.util.FileListReader;
import pl.imgw.jrat.util.MessageLogger;
import pl.imgw.jrat.view.Printing;

/**
 * Controller class for data processing routines.
 * 
 * @author szewczenko
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class DataProcessorController {

    // Reference to CommandLineArgsParser object
    private CommandLineArgsParser cmd;
    private MessageLogger msg;
    // Variables
    boolean verbose = false;

    /**
     * Data processing control method
     * 
     * @param args
     *            Command line arguments
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    public void startProcessor(String[] args) throws Exception {

        // Parse command line arguments
        cmd.parseCommandLineArgs(args);

        // Check if verbose mode is chosen

        if (cmd.hasArgument(cmd.VERBOSE_OPTION)) {
            verbose = true;
            // System.out.println("verbose mode");
        }

        // =========== input file processing =================
        if (cmd.hasArgument(cmd.INPUT_OPTION)) {
            String[] fileName = cmd.getArgumentValues(cmd.INPUT_OPTION);

            List<File> list = FileListReader.getFileList(fileName);

            List<OdimH5File> odims = new ArrayList<>();
            Iterator<File> itr = list.iterator();
            while (itr.hasNext()) {
                String newfile = itr.next().getPath();
                msg.showMessage("Processing: " + newfile, verbose);
                if (newfile.endsWith(FILE_NAME_EXTENSION)
                        || newfile.endsWith(FILE_NAME_EXTENSION1)) {

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
                            msg.showMessage("Faild to read the file", true);

                        if (odim != null && cmd.hasArgument(cmd.DISPLAY_OPTION))
                            odim.displayTree();
                        if (odim != null && cmd.hasArgument(cmd.PRINT_OPTION)) {
                            String dsName = cmd
                                    .getArgumentValue(cmd.PRINT_OPTION);
                            Printing.printScan(odim, dsName, verbose);
                        }
                    } else {
                        MessageLogger.showMessage("unable to read " + newfile,
                                true);
                    }
                } else {
                    MessageLogger.showMessage("type of " + newfile
                            + " not supported", true);
                }

            }

            if(cmd.hasArgument(cmd.COMPARE_OPTION)) {
                
                MessageLogger.showMessage(
                        "Two radars comparison option choosen", verbose);

                String[] eldist = cmd.getArgumentValues(cmd.COMPARE_OPTION);
                
                /*
                 *<date<source_name,volume_data>> 
                 */
                TreeMap<String, HashMap<String, RadarVolume>> observations = new TreeMap<>();
                HashSet<String> sources = new HashSet<>();
                Iterator<OdimH5File> iterator = odims.iterator();
                while(iterator.hasNext()) {
                    OdimH5File next = iterator.next();
                    if(!next.getType().matches(PVOL))
                        continue;
                    RadarVolume vol = (RadarVolume) next;
                    sources.add(vol.getSource());
                    String date = vol.getFullDate();
                    HashMap<String, RadarVolume> r = null;
                    if(observations.containsKey(date)) {
                        r = observations.get(date);
                    } else {
                        r = new HashMap<String,RadarVolume>();
                    }
                    r.put(vol.getSource(), vol);
                    observations.put(date, r);
                }

                MatchingPointsManager mp = new MatchingPointsManager(
                        observations, sources);
                if (mp.initialize(eldist)) {
                    ;
                } else
                    MessageLogger.showMessage(
                            "Comparison failed! Incorrect parameters", true);

            }
            
        }

    }

    /**
     * Method returns reference to CommandLineArgsParser object.
     * 
     * @return Reference to CommandLineArgsParser object
     */
    public CommandLineArgsParser getCmdParser() {
        return cmd;
    }

    /**
     * Method sets reference to CommandLineArgsParser object.
     * 
     * @param cmd
     *            Reference to CommandLineArgsParser object
     */
    public void setCmdParser(CommandLineArgsParser cmd) {
        this.cmd = cmd;
    }

    /**
     * @return the msg
     */
    public MessageLogger getMsg() {
        return msg;
    }

    /**
     * @param msg
     *            the msg to set
     */
    public void setMsg(MessageLogger msg) {
        this.msg = msg;
    }

}
