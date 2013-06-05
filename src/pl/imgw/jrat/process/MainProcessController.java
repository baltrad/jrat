/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.AplicationConstans.APS_DESC;
import static pl.imgw.jrat.AplicationConstans.DATE;
import static pl.imgw.jrat.AplicationConstans.REL_DATE;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_HELP;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_LIST;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_OPT;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_RESULT;
import static pl.imgw.jrat.process.CommandLineArgsParser.CALID_PLOT;
import static pl.imgw.jrat.process.CommandLineArgsParser.F;
import static pl.imgw.jrat.process.CommandLineArgsParser.FORMAT;
import static pl.imgw.jrat.process.CommandLineArgsParser.H;
import static pl.imgw.jrat.process.CommandLineArgsParser.I;
import static pl.imgw.jrat.process.CommandLineArgsParser.O;
import static pl.imgw.jrat.process.CommandLineArgsParser.PRINT;
import static pl.imgw.jrat.process.CommandLineArgsParser.PRINTIMAGE;
import static pl.imgw.jrat.process.CommandLineArgsParser.QUIET;
import static pl.imgw.jrat.process.CommandLineArgsParser.SCANSUN;
import static pl.imgw.jrat.process.CommandLineArgsParser.SEQ;
import static pl.imgw.jrat.process.CommandLineArgsParser.TEST;
import static pl.imgw.jrat.process.CommandLineArgsParser.VERBOSE;
import static pl.imgw.jrat.process.CommandLineArgsParser.WATCH;
import static pl.imgw.jrat.process.CommandLineArgsParser.printHelp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.calid.data.CalidParametersFileHandler;
import pl.imgw.jrat.calid.data.CalidParametersParser;
import pl.imgw.jrat.calid.proc.CalidController;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.scansun.ScansunProcessor;
import pl.imgw.jrat.tools.out.ProductInfoPrinter;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MainProcessController {

    private static Log log = LogManager.getLogger();
    private CommandLine cmd;
    private List<File> files = new LinkedList<File>();
    private List<File> folders = new LinkedList<File>();
    public static File root = new File(System.getProperty("user.dir"));

    public List<File> getFiles(){
        return files;
    }
    
    
    public MainProcessController(String[] args) {

        CommandLineArgsParser parser = new CommandLineArgsParser();
        if(parser.parseArgs(args))
            cmd = parser.getCmd();

        if (cmd == null)
            return;
        
        if (cmd.hasOption(QUIET)) {
//            System.out.println("ustawia quiet");
            LogManager.getInstance().setLogMode(Log.MODE_SILENT);
        } else if (cmd.hasOption(VERBOSE)) {
//            System.out.println("ustawia verbose");
            LogManager.getInstance().setLogMode(Log.MODE_VERBOSE);
        } else
            LogManager.getInstance().setLogMode(Log.MODE_NORMAL);

    }

    /**
     * Starting main process, parsing Command line arguments and run proper
     * processes
     * 
     * @return returns true if all goes well, without errors, otherwise return false
     */
    public boolean start() {
        
        if (cmd == null) {
            return false;
        }
        
        /* Setting output path */
        File output = root;
        if (cmd.hasOption(O)) {
            FileProcessController.setOutputFile(cmd.getOptionValue(O), output);
        }
        
        /* display help message */
        if(cmd.getOptions().length == 0 || cmd.hasOption(H)) {
            printHelp();
            return true;
        }

        /* display version message */
        if (cmd.hasOption(AplicationConstans.VERSION)) {
            printVersion();
            return true;
        }
        
        /* display CALID help message */
        if(cmd.hasOption(CALID_HELP)) {
            CalidParametersParser.printHelp();
            return true;
        }
        
        /* set input file global format */
        if (cmd.hasOption(FORMAT)) {
            GlobalParser.getInstance().setParser(cmd.getOptionValue(F));
        }
        
        /* print CALID results */
        if (cmd.hasOption(CALID_RESULT)) {
            CalidController.processResult(cmd);
            return true;
        }

        /* print list of available results of CALID */
        if (cmd.hasOption(CALID_LIST)) {
            CalidController.processList(cmd);
            return true;
        }
        
        /* print list of available results of CALID */
        if (cmd.hasOption(CALID_PLOT)) {
            
            CalidController.processPlot(cmd, output);
            return true;
        }

        /* set CALID option file */
        if (cmd.hasOption(CALID_OPT)) {
            CalidParametersFileHandler.getOptions().setOptionFile(
                    cmd.getOptionValue(CALID_OPT));
        }
        
        
        /* Loading list of files to process or setting input folder path */
        FileProcessController.setInputFilesAndFolders(files, folders,
                    cmd.getOptionValues(I));
        
        
        /* Print information about all input files */
        if (cmd.hasOption(PRINT)) {
            ProductInfoPrinter.print(files, cmd.hasOption(VERBOSE));
            return true;
        }
        
        /* Print image from all input files */
        if (cmd.hasOption(PRINTIMAGE)) {
            PrintingImageProcessController.printImage(files, output,
                    cmd.getOptionValues(PRINTIMAGE));
            return true;
        }

        /* =========== setting processes =============== */
        FilesProcessor proc = null;
        
        /* CALID */
        if (cmd.hasOption(CALID)) {
            proc = CalidController.setCalidProcessor(cmd);
        }

        /* SCANSUN */
        if (cmd.hasOption(SCANSUN)) {
            proc = new ScansunProcessor(cmd.getOptionValues(SCANSUN));
            
                String par = "";
                if (cmd.getOptionValue(SCANSUN) == null) {
                    par = "no parameters";
                } else
                    for (String s : cmd.getOptionValues(SCANSUN)) {
                        par += s + " ";
                    }
                log.printMsg("Starting SCANSUN with: " + par,
                        Log.TYPE_NORMAL, Log.MODE_VERBOSE);
            
        }
        
        // test process, prints files name
        if (cmd.hasOption(TEST)) {
            proc = new FilesProcessor() {
                @Override
                public void processFile(List<File> files) {
                    for (File file : files)
                        log.printMsg("" + file, Log.MODE_SILENT);
                    if (files.isEmpty())
                        log.printMsg("No files to process", Log.MODE_SILENT);
                }

                @Override
                public String getProcessName() {
                    // TODO Auto-generated method stub
                    return "TEST process";
                }

                
            };
        }

        
        if (proc == null) {
            log.printMsg("No valid process has been set",
                    Log.TYPE_WARNING, Log.MODE_VERBOSE);
            return false;
        }
        
        /* =========== setting working mode =============== */
        if (cmd.hasOption(WATCH)) {
            /* Starting continues mode */
            
            FileWatchingProcess watcher = new FileWatchingProcess(proc, folders);

            if (!watcher.isValid())
                return false;
            
            if (!cmd.hasOption(QUIET))
                if (!FolderManager.continueWithDeletingFiles(folders)) {
                    // decline to delete files, exiting
                    return true;
                }

            Thread t = new Thread(watcher);
            t.start();
            if (t.isAlive()) {
                log.printMsg("Watching process started",
                        Log.TYPE_NORMAL, Log.MODE_VERBOSE);
                return true;
            }
        } else if (cmd.hasOption(SEQ)) {
            /* Starting sequence mode */
            
            SequentialProcess seq = new SequentialProcess(proc, folders,
                    cmd.getOptionValue(SEQ));

            if (!seq.isValid())
                return false;
            if (!cmd.hasOption(QUIET))
                if (!FolderManager.continueWithDeletingFiles(folders)) {
                    // decline to delete files, exiting
                    return true;
                }
            
            Thread t = new Thread(seq);
            t.start();
            if (t.isAlive()) {
                return true;
            }
        } else {
            SingleRunProcessor single = new SingleRunProcessor(proc, folders,
                    files);
            if (!single.isValid())
                return false;
            // shouldn't be run as a separate thread, this is why I'm using
            // run() not start()
            single.run();
            return true;
        }
        
        return false;
    }

    public void printVersion() {
        log.printMsg(APS_DESC + "\nversion:\t" + AplicationConstans.VERSION
                + "\nreleased date:\t" + REL_DATE + "\ncompiled on:\t" + DATE,
                Log.MODE_SILENT);
    }
    
}
