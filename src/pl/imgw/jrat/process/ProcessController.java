/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.*;
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
import pl.imgw.jrat.data.parsers.Rainbow53ImageParser;
import pl.imgw.jrat.data.parsers.Rainbow53VolumeParser;
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
    private List<File> files = new LinkedList<File>();
    private List<File> folders = new LinkedList<File>();

    public List<File> getFiles(){
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
        FilesProcessor proc = null;
        
        if (cmd.hasOption(TEST)) {
            proc = new FilesProcessor() {
                @Override
                public void processFile(List<File> files) {
                    for (File file : files)
                        System.out.println(file);
                }
            };
        }
        
        /* Loading list of files to process*/
        if (cmd.hasOption(I)) {
            FilePatternFilter filter = new RegexFileFilter();
            for (int i = 0; i < cmd.getOptionValues(I).length; i++) {
                files.addAll(filter.getFileList(cmd.getOptionValues(I)[i]));
                if(new File(cmd.getOptionValues(I)[i]).isDirectory()) {
                    folders.add(new File(cmd.getOptionValues(I)[i]));
                }
            }
        }
        /*----------------------------------
        
        if(cmd.hasOption(F)) {
            String f = cmd.getOptionValue(F);
            if(f.matches("hdf") || f.matches("h5"))
                format = HDF;
            else if(f.matches("rbimg") || f.matches("rainbowimage"))
                format = RBI;
            else if(f.matches("rbvol") || f.matches("rainbowvol"))
                format = RBV;
        }
        */
        
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
            ImagesController ic = new ImagesController(
                    cmd.getOptionValues(PRINTIMAGE));
            ParserManager pm = new ParserManager();

            pm.setParser(new DefaultParser());

            for (File file : files) {
                if (pm.initialize(file)) {
                    ic.getBuilder().setData(
                            pm.getProduct().getArray(ic.getDatasetValue()));

                    File imgout = new File(output, file.getName() + "."
                            + ic.getFormat());
                    ic.getBuilder().saveToFile(imgout);
                }
            }
        }

        /* Starting continues mode */
        if(cmd.hasOption(WATCH)) {
            FileWatcher watcher = new FileWatcher(proc, folders);
            Thread t = new Thread(watcher);
            t.run();
            return true;
        }
        
        /* Starting sequence mode */
        if (cmd.hasOption(SEQ)) {
            SequentialProcess seq = new SequentialProcess(proc, folders,
                    cmd.getOptionValue(SEQ));
            Thread t = new Thread(seq);
            t.run();
            return true;
        }
        
        return false;
    }

    public static void main(String[] args) {
        ProcessController pc = new ProcessController(args);
        pc.start();
    }

}
