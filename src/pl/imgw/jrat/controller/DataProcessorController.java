/*
 * OdimH5 :: Converter software for OPERA Data Information Model
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.jrat.controller;

import static pl.imgw.jrat.data.hdf5.Constants.*;

import java.io.File;

import pl.imgw.jrat.data.hdf5.RadarVolume;
import pl.imgw.jrat.util.CommandLineArgsParser;
import pl.imgw.jrat.util.LogsHandler;
import pl.imgw.jrat.util.MessageLogger;
import pl.imgw.jrat.view.ColorScales;
import pl.imgw.jrat.view.ImageFrame;
import pl.imgw.jrat.view.PictureFromArray;

/**
 * Controller class for data processing routines.
 * 
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DataProcessorController {

    // Reference to CommandLineArgsParser object
    private CommandLineArgsParser cmd;
    private MessageLogger msg;
    // Variables
    boolean verbose;

    /**
     * Data processing control method
     * 
     * @param args
     *            Command line arguments
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    public void startProcessor(String[] args) throws Exception {

        boolean verbose = false;
        // Parse command line arguments
        cmd.parseCommandLineArgs(args);

        // Check if verbose mode is chosen

        if (cmd.hasArgument(cmd.VERBOSE_OPTION)) {
            verbose = true;
//            System.out.println("verbose mode");
        }
        if (cmd.hasArgument(cmd.INPUT_OPTION)) {
            String fileName = cmd.getArgumentValue(cmd.INPUT_OPTION);
            msg.showMessage("processing: " + fileName, verbose);

            if (fileName.endsWith(FILE_NAME_EXTENSION)
                    || fileName.endsWith(FILE_NAME_EXTENSION1)) {

                RadarVolume vol = new RadarVolume(verbose);
                File f = new File(fileName);
                if (vol.initializeFromFile(f)) {
                    if (cmd.hasArgument(cmd.DISPLAY_OPTION))
                        vol.displayTree();
                    if (cmd.hasArgument(cmd.PRINT_OPTION)) {
                        printScan(vol);
                    }

                } else
                    msg.showMessage("Faild to read the file", true);

            }
        }

    }

    /**
     * Print scan selected in command line argument in new frame
     * 
     * @param vol
     */
    private void printScan(RadarVolume vol) {
        
        try {
            int i = Integer.parseInt(cmd
                    .getArgumentValue(CommandLineArgsParser.PRINT_OPTION));
            PictureFromArray pic = new PictureFromArray(
                    vol.getDataset()[i].getData()[0].getArray()
                            .getData(),
                    ColorScales.getGray256Scale());
            ImageFrame frame = new ImageFrame(pic.getImg(), vol.getFullDate(),
                    vol.getDataset()[i].getData()[0].getArray()
                            .getSizeX(),
                    vol.getDataset()[i].getData()[0].getArray()
                            .getSizeY());
            frame.displayImage();
        } catch (Exception e) {
            MessageLogger.showMessage("Couldn't print a map", true);
            LogsHandler.saveProgramLogs("DataProcessorController", e.getMessage());
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
