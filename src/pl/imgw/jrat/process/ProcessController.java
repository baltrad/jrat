/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.AUTO;
import static pl.imgw.jrat.process.CommandLineArgsParser.F;
import static pl.imgw.jrat.process.CommandLineArgsParser.I;
import static pl.imgw.jrat.process.CommandLineArgsParser.O;
import static pl.imgw.jrat.process.CommandLineArgsParser.PRINT;
import static pl.imgw.jrat.process.CommandLineArgsParser.PRINTIMAGE;
import static pl.imgw.jrat.process.CommandLineArgsParser.QUIET;
import static pl.imgw.jrat.process.CommandLineArgsParser.VERBOSE;
import static pl.imgw.jrat.process.CommandLineArgsParser.VERSION;
import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.SILENT;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.OdimH5Parser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.RainbowImageFieldsName;
import pl.imgw.jrat.data.parsers.RainbowParser;
import pl.imgw.jrat.data.parsers.RainbowVolumeFieldsName;
import pl.imgw.jrat.tools.in.FileDate;
import pl.imgw.jrat.tools.in.FilePatternFilter;
import pl.imgw.jrat.tools.in.RegexFileFilter;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ProcessController {

    private final int HDF = 0;
    private final int RBI = 1;
    private final int RBV = 2;
    private int format = -1;
    
    private CommandLine cmd;
    private List<FileDate> files = new LinkedList<FileDate>();

    public List<FileDate> getFiles(){
        return files;
    }
    
    public ProcessController(String[] args) {

        CommandLineArgsParser parser = new CommandLineArgsParser();
        parser.parseArgs(args);
        cmd = parser.getCmd();

        if (cmd == null)
            return;
        
        if (cmd.hasOption(QUIET)) {
//            System.out.println("ustawia quiet");
            LogHandler.getLogs().setLoggingVerbose(SILENT);
        } else if (cmd.hasOption(VERBOSE)) {
//            System.out.println("ustawia verbose");
            LogHandler.getLogs().setLoggingVerbose(ERROR);
        } else
            LogHandler.getLogs().setLoggingVerbose(WARNING);

    }

    public boolean start() {

        if (cmd == null) {
            return false;
        }

        if (cmd.hasOption(VERSION)) {
            LogHandler.getLogs().printVersion();
            return true;
        }

        /* Loading list of files to precess*/
        if (cmd.hasOption(I)) {
            FilePatternFilter filter = new RegexFileFilter();
            for (int i = 0; i < cmd.getOptionValues(I).length; i++) {
                files.addAll(filter.getFileList(cmd.getOptionValues(I)[i]));
            }
        }
        /*----------------------------------*/
        
        if(cmd.hasOption(F)) {
            String f = cmd.getOptionValue(F);
            if(f.matches("hdf") || f.matches("h5"))
                format = HDF;
            else if(f.matches("rbimg") || f.matches("rainbowimage"))
                format = RBI;
            else if(f.matches("rbvol") || f.matches("rainbowvol"))
                format = RBV;
        }
        
        /* Setting output file */
        File output = new File(".");
        if (cmd.hasOption(O)) {
            output = new File(cmd.getOptionValue(O));
            output.mkdirs();
        }
        /* ................................. */
        
        
        if (cmd.hasOption(PRINT)) {
            System.out.println(cmd.getOptionValue(PRINT));
            return true;
        }
        
        if (cmd.hasOption(PRINTIMAGE)) {
            ImagesController ic = new ImagesController(cmd.getOptionValues(PRINTIMAGE));
            ParserManager pm = new ParserManager();
            
            if(format == HDF) {
                pm.setParser(new OdimH5Parser());
            } else if (format == RBI) {
                pm.setParser(new RainbowParser(new RainbowImageFieldsName()));
            } else if (format == RBV) {
                pm.setParser(new RainbowParser(new RainbowVolumeFieldsName()));
            } else {
                pm.setParser(new DefaultParser());
            }
            
            Iterator<FileDate> itrf = files.iterator();
            while (itrf.hasNext()) {
                File file = itrf.next().getFile();
                if (pm.initialize(file)) {
                    ic.getBuilder().setData(
                            pm.getProduct().getArray(ic.getDatasetValue()));

                    File imgout = new File(output, file.getName() + "."
                            + ic.getFormat());
                    ic.getBuilder().saveToFile(imgout);
                }
            }
        }

        if (cmd.hasOption(AUTO)) {
            // TO-DO
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        ProcessController pc = new ProcessController(args);
        pc.start();
    }

}
