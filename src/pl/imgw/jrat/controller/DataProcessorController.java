/*
 * OdimH5 :: Converter software for OPERA Data Information Model
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.jrat.controller;

import static pl.imgw.jrat.data.hdf5.OdimH5Constans.*;

import java.io.File;

import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5File;
import pl.imgw.jrat.data.hdf5.H5_Wrapper;
import pl.imgw.jrat.data.hdf5.OdimCompo;
import pl.imgw.jrat.data.hdf5.OdimH5File;
import pl.imgw.jrat.data.hdf5.RadarVolumeV2_0;
import pl.imgw.jrat.data.hdf5.RadarVolumeV2_1;
import pl.imgw.jrat.util.CommandLineArgsParser;
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
            String fileName = cmd.getArgumentValue(cmd.INPUT_OPTION);
            msg.showMessage("processing: " + fileName, verbose);

            if (fileName.endsWith(FILE_NAME_EXTENSION)
                    || fileName.endsWith(FILE_NAME_EXTENSION1)) {

                File f = new File(fileName);
                H5File file = H5_Wrapper.openHDF5File(f.getAbsolutePath(),
                        verbose);
                if (file != null && file.canRead()) {

                    Group root = H5_Wrapper.getHDF5RootGroup(file, verbose);
                    // validating conditions
                    String format = H5_Wrapper.getHDF5StringValue(root, WHAT,
                            OBJECT, verbose);
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
                            return;
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
                        odim.displayGeneralOdimInfo();
                        odim.displayGeneralObjectInfo(verbose);
                    } else
                        msg.showMessage("Faild to read the file", true);

                    if (odim != null && cmd.hasArgument(cmd.DISPLAY_OPTION))
                        odim.displayTree();
                    if (odim != null && cmd.hasArgument(cmd.PRINT_OPTION)) {
                        String dsName = cmd.getArgumentValue(cmd.PRINT_OPTION);
                        Printing.printScan(odim, dsName, verbose);
                    }
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
