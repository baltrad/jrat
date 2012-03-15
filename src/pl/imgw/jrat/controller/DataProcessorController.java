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
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.HDF_EXT;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.H5_EXT;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.IMAGE;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.OBJECT;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.ODIM_H5_V2_0;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.ODIM_H5_V2_1;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.PVOL;
import static pl.imgw.jrat.data.hdf5.OdimH5Constans.WHAT;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;
import pl.imgw.jrat.comp.MatchingPoints;
import pl.imgw.jrat.comp.MatchingPointsManager;
import pl.imgw.jrat.data.hdf5.H5_Wrapper;
import pl.imgw.jrat.data.hdf5.OdimCompo;
import pl.imgw.jrat.data.hdf5.OdimFilesManager;
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String[] fileName = cmd.getArgumentValues(cmd.INPUT_OPTION);

            FileListReader flr = new FileListReader();
            
            HashMap<Date, Map<String, File>> map = flr.getFileList(fileName);
            
            Iterator<Date> itr = map.keySet().iterator();
            while (itr.hasNext()) {
                Date date = itr.next();
//                Iterator<String> sources = map.get(date).keySet().iterator();
                Collection<File> list =  map.get(date).values();
                
                msg.showMessage("Number of files " + sdf.format(date) + ": " + list.size(),
                        true);

                List<OdimH5File> odims = OdimFilesManager.makeList(list,
                        verbose);

                if (cmd.hasArgument(cmd.DISPLAY_OPTION)) {
                    Iterator<OdimH5File> i = odims.iterator();
                    if (i.hasNext()) {
                        i.next().displayTree();
                    }
                }

                if (cmd.hasArgument(cmd.PRINT_OPTION)) {
                    String dsName = cmd.getArgumentValue(cmd.PRINT_OPTION);
                    Iterator<OdimH5File> i = odims.iterator();
                    if (i.hasNext()) {
                        Printing.printScan(i.next(), dsName, verbose);
                    }
                }

                if (cmd.hasArgument(cmd.COMPARE_OPTION)) {

                    MessageLogger.showMessage(
                            "Two radars comparison option choosen", verbose);

                    MatchingPointsManager mp = new MatchingPointsManager(odims);

                    String[] eldist = cmd.getArgumentValues(cmd.COMPARE_OPTION);
                    if (mp.initialize(eldist)) {
                        mp.calculateAll();
                    } else
                        MessageLogger
                                .showMessage(
                                        "Comparison failed! Incorrect parameters",
                                        true);
                }

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
