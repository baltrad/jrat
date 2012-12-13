/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.*;
import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.calid.CalidManager;
import pl.imgw.jrat.calid.CalidProcessor;
import pl.imgw.jrat.calid.CalidResultManager;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.scansun.ScansunProcessor;
import pl.imgw.jrat.tools.in.FilePatternFilter;
import pl.imgw.jrat.tools.in.RegexFileFilter;
import pl.imgw.jrat.tools.out.ConsoleProgressBar;
import pl.imgw.jrat.tools.out.LogHandler;

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
    private File root = new File(System.getProperty("user.dir"));

    public List<File> getFiles(){
        return files;
    }
    
    
    public MainProcessController(String[] args) {

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
            LogHandler.getLogs().setLoggingVerbose(ALL_MSG);
        } else
            LogHandler.getLogs().setLoggingVerbose(PROGRESS_BAR_ONLY);

    }

    public boolean start() {

        if (cmd == null) {
            return false;
        }

        if (cmd.hasOption(VERSION)) {
            LogHandler.getLogs().printVersion();
            return true;
        }
        
        if (cmd.hasOption(H)) {
            /* nothing to do when help displayed */
            return true;
        }
        
        if (cmd.hasOption(FORMAT)) {
            GlobalParserSetter.getInstance().setParser(cmd.getOptionValue(F));
        }
        
        if(cmd.hasOption(CALID_HELP)) {
            CalidResultManager.printHelp();
            return true;
        }
        
        if(cmd.hasOption(CALID_RESULT)) {
            CalidManager calid = new CalidManager(cmd.getOptionValues(CALID_RESULT));
            if (calid.isValid()) {
                new CalidResultManager(calid).printResults();
                return true;
            } else {
                CalidResultManager.printHelp();
                return false;
            }
        }
        
        if (cmd.hasOption(CALID_LIST)) {

            CalidManager calid = new CalidManager(
                    cmd.getOptionValues(CALID_LIST));
            if (calid.isValid()) {
                new CalidResultManager(calid).printPairsList();
                return true;
            } else {
                CalidResultManager.printHelp();
                return false;
            }
        }
        
        FilesProcessor proc = null;
        
        if (cmd.hasOption(TEST)) {
            proc = new FilesProcessor() {
                @Override
                public void processFile(List<File> files) {
                    for (File file : files)
                        System.out.println(file);
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
        
        
        /* Loading list of files to process or setting input folder path*/
        if (cmd.hasOption(I)) {
            
            FilePatternFilter filter = new RegexFileFilter();
            for (String name : cmd.getOptionValues(I)) {
                if (!name.startsWith("/")) {
                    name = root.getPath() + "/" + name;
                }
                files.addAll(filter.getFileList(name));
                if (new File(name).isDirectory()) {
                    folders.add(new File(name));
                }
            }
            
            
            if (files.isEmpty() && folders.isEmpty()) {
                LogHandler.getLogs().displayMsg("No such file or directory",
                        WARNING);
            } else {
                ConsoleProgressBar.getProgressBar().initialize(20,
                        files.size(),
                        LogHandler.getLogs().getVerbose() == PROGRESS_BAR_ONLY,
                        "Setting files\t");
                for (File f : files) {
                    LogHandler.getLogs().displayMsg(
                            "Input files: " + f.getPath(), NORMAL);
                    ConsoleProgressBar.getProgressBar().evaluate();
                }
                for (File f : folders) {
                    LogHandler.getLogs().displayMsg(
                            "Input folders: " + f.getPath(), NORMAL);
                    ConsoleProgressBar.getProgressBar().evaluate();
                }
                
            }

            ConsoleProgressBar.getProgressBar().printDoneMsg();
            
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
        File output = root;
        if (cmd.hasOption(O)) {
            if (!cmd.getOptionValue(O).startsWith("/")) {
                output = new File(root, cmd.getOptionValue(O));
            } else {
                output = new File(cmd.getOptionValue(O));
            }
            output.mkdirs();
            LogHandler.getLogs().displayMsg("Output: " + output.getPath(), WARNING);
        }
        
        
        if (cmd.hasOption(PRINT)) {
            System.out.println("Printing information about the file is not supported yet");
            return true;
        }
        
        if (cmd.hasOption(PRINTIMAGE)) {
//            LogHandler.getLogs().displayMsg("Printing image from file", WARNING);
            ImagesController ic = null;
            ParserManager pm = new ParserManager();

            pm.setParser(new DefaultParser());

            for (File file : files) {
                if (pm.initialize(file)) {
                    ic = new ImagesController(
                            cmd.getOptionValues(PRINTIMAGE));
                    ic.getBuilder().setData(
                            pm.getProduct().getArray(ic.getDatasetValue()));

                    File imgout = new File(output, file.getName() + "."
                            + ic.getFormat());
                    ic.getBuilder().saveToFile(imgout);
                }
            }
        }
        
        /* CALID */
        if (cmd.hasOption(CALID)) {
            proc = new CalidProcessor(cmd.getOptionValues(CALID));
            if (proc.isValid()) {
                String par = "";
                for (String s : cmd.getOptionValues(CALID)) {
                    par += s + " ";
                }
                LogHandler.getLogs().displayMsg("Starting CALID with: " + par,
                        WARNING);
            } else {
                return false;
            }
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
                        WARNING);
            }
        }
        
        if(cmd.hasOption(WATCH)) {
            /* Starting continues mode */
            FileWatchingProcess watcher = new FileWatchingProcess(proc, folders);
            
            if(!watcher.isValid())
                return false;
            
            Thread t = new Thread(watcher);
            t.start();
            if(t.isAlive()) {
                LogHandler.getLogs().displayMsg("Watching process started",
                        WARNING);
                return true;
            }
        } else if (cmd.hasOption(SEQ)) {
            /* Starting sequence mode */
            SequentialProcess seq = new SequentialProcess(proc, folders,
                    cmd.getOptionValue(SEQ));
            Thread t = new Thread(seq);
            t.start();
            if(t.isAlive()) {
                return true;
            }
        } else {
            SingleRunProcessor single = new SingleRunProcessor(proc, folders, files);
            Thread t = new Thread(single);
            //shouldn't be run as a separate thread, this is why I'm using run() not start()
            t.run();
            if(t.isAlive()) {
                return true;
            }
        }
        
        return false;
    }

    public static void main(String[] args) {
        MainProcessController pc = new MainProcessController(args);
        pc.start();
    }

}
