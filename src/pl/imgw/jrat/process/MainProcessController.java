/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.*;
import static pl.imgw.jrat.process.CommandLineArgsParser.printHelp;
import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.calid.CalidPeriodsResultsPrinter;
import pl.imgw.jrat.calid.CalidGnuplotResultPrinter;
import pl.imgw.jrat.calid.CalidOptionsHandler;
import pl.imgw.jrat.calid.CalidParsedParameters;
import pl.imgw.jrat.calid.CalidProcessor;
import pl.imgw.jrat.calid.CalidResultsPrinter;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.scansun.ScansunProcessor;
import pl.imgw.jrat.tools.in.FilePatternFilter;
import pl.imgw.jrat.tools.in.RegexFileFilter;
import pl.imgw.jrat.tools.out.ConsoleProgressBar;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.ProductInfoPrinter;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MainProcessController {

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
            LogHandler.getLogs().setLoggingVerbose(SILENT);
        } else if (cmd.hasOption(VERBOSE)) {
//            System.out.println("ustawia verbose");
            LogHandler.getLogs().setLoggingVerbose(ALL_MSG);
        } else
            LogHandler.getLogs().setLoggingVerbose(PROGRESS_BAR_ONLY);

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
        
        /* display help message */
        if(cmd.getOptions().length == 0 || cmd.hasOption(H)) {
            printHelp();
            return true;
        }

        /* display version message */
        if (cmd.hasOption(VERSION)) {
            LogHandler.getLogs().printVersion();
            return true;
        }
        
        /* display CALID help message */
        if(cmd.hasOption(CALID_HELP)) {
            CalidParsedParameters.printHelp();
            return true;
        }
        
        /* set input file global format */
        if (cmd.hasOption(FORMAT)) {
            GlobalParser.getInstance().setParser(cmd.getOptionValue(F));
        }
        
        /* print CALID results */
        if (cmd.hasOption(CALID_RESULT)) {
            return CalidProcessController.processCalidResult(cmd);
        }
        
        /* print list of available results of CALID */
        if (cmd.hasOption(CALID_LIST)) {
            return CalidProcessController.processCalidList(cmd);
        }
        
        /* set CALID option file */
        if (cmd.hasOption(CALID_OPT)) {
            CalidOptionsHandler.getOptions().setOptionFile(cmd.getOptionValue(CALID_OPT));
        }
        
        
        /* Loading list of files to process or setting input folder path */
        FileProcessController.setInputFilesAndFolders(files, folders,
                    cmd.getOptionValues(I));
        
        
        /* Setting output path */
        File output = root;
        if (cmd.hasOption(O)) {
            FileProcessController.setOutputFile(cmd.getOptionValue(O), output);
        }
        
        /* Print information about all input files */
        if (cmd.hasOption(PRINT)) {
            ProductInfoPrinter.print(files);
            return true;
        }
        
        /* Print image from all input files */
        if (cmd.hasOption(PRINTIMAGE)) {
            PrintingImageProcessController.printImage(files, output,
                    cmd.getOptionValues(PRINTIMAGE));
        }

        /* =========== setting processes =============== */
        
        FilesProcessor proc = null;
        
        /* CALID */
        if (cmd.hasOption(CALID)) {
            proc = CalidProcessController.setCalidProcessor(cmd);
        }

        /* SCANSUN */
        if (cmd.hasOption(SCANSUN)) {
            proc = new ScansunProcessor(cmd.getOptionValues(SCANSUN));
            if (proc.isValid()) {
                String par = "";
                if (cmd.getOptionValue(SCANSUN) == null) {
                    par = "no parameters";
                } else
                    for (String s : cmd.getOptionValues(SCANSUN)) {
                        par += s + " ";
                    }
                LogHandler.getLogs().displayMsg("Starting SCANSUN with: " + par,
                        NORMAL);
            }
        }
        
        // test process, prints files name
        if (cmd.hasOption(TEST)) {
            proc = new FilesProcessor() {
                @Override
                public void processFile(List<File> files) {
                    for (File file : files)
                        LogHandler.getLogs().displayMsg("" + file, SILENT);
                    if(files.isEmpty())
                        LogHandler.getLogs().displayMsg("No files to process", SILENT);
                }

                @Override
                public String getProcessName() {
                    // TODO Auto-generated method stub
                    return "TEST process";
                }

                @Override
                public boolean isValid() {
                    // TODO Auto-generated method stub
                    return true;
                }
            };
        }

        
        if (proc == null || !proc.isValid()) {
            LogHandler.getLogs().displayMsg("No valid process has been set",
                    WARNING);
            return false;
        }
        
        /* =========== setting working mode =============== */
        if (cmd.hasOption(WATCH)) {
            /* Starting continues mode */
            
            FileWatchingProcess watcher = new FileWatchingProcess(proc, folders);

            if (!watcher.isValid())
                return false;
            
            if(!FolderManager.continueWithDeletingFiles(folders)){
                // decline to delete files, exiting
                return true;
            }

            Thread t = new Thread(watcher);
            t.start();
            if (t.isAlive()) {
                LogHandler.getLogs().displayMsg("Watching process started",
                        NORMAL);
                return true;
            }
        } else if (cmd.hasOption(SEQ)) {
            /* Starting sequence mode */
            
            SequentialProcess seq = new SequentialProcess(proc, folders,
                    cmd.getOptionValue(SEQ));

            if (!seq.isValid())
                return false;

            if(!FolderManager.continueWithDeletingFiles(folders)) {
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

    public static void main(String[] args) {
        MainProcessController pc = new MainProcessController(args);
        pc.start();
    }

}
