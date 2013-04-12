/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.O;
import static pl.imgw.jrat.tools.out.Logging.NORMAL;
import static pl.imgw.jrat.tools.out.Logging.PROGRESS_BAR_ONLY;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.calid.CalidOptionsHandler;
import pl.imgw.jrat.tools.in.FilePatternFilter;
import pl.imgw.jrat.tools.in.RegexFileFilter;
import pl.imgw.jrat.tools.out.ConsoleProgressBar;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class FileProcessController {

    public static void setInputFilesAndFolders(List<File> files, List<File> folders, String[] options) {
        
        FilePatternFilter filter = new RegexFileFilter();
        if (options != null) {
            for (String name : options) {
                if (!name.startsWith("/")) {
                    name = MainProcessController.root.getPath() + "/" + name;
                }
                if (name.contains("*")) {
                    files.addAll(filter.getFileList(name));
                } else {
                    File f = new File(name);
                    if (f.isFile()) {
                        files.add(f);
                    } else if (f.isDirectory()) {
                        folders.add(f);
                    }
                }
            }
        }
        
        /*
         * Other input files/folders providers:
         */
        folders.addAll(CalidOptionsHandler.getOptions().getInputFolderList());
        
        if (files.isEmpty() && folders.isEmpty()) {
            LogHandler.getLogs().displayMsg("No input",
                    WARNING);
        } else {
            ConsoleProgressBar.getProgressBar().initialize(20, files.size(),
                    LogHandler.getLogs().getVerbose() == PROGRESS_BAR_ONLY,
                    "Setting up files");
            StringBuilder msg = new StringBuilder();
            msg.append("Input files:");
            for (File f : files) {
                msg.append(" ").append(f.getPath());
            }
            LogHandler.getLogs().displayMsg(msg.toString(), NORMAL);
            ConsoleProgressBar.getProgressBar().evaluate();
            msg = new StringBuilder("Input folders:");
            for (File f : folders) {
                msg.append(" ").append(f.getPath());
            }
            LogHandler.getLogs().displayMsg(msg.toString(), NORMAL);
            ConsoleProgressBar.getProgressBar().evaluate();

            msg = new StringBuilder();
            if (!files.isEmpty()) {
                msg.append(files.size()).append(" files");
            }
            ConsoleProgressBar.getProgressBar().printDoneMsg(msg.toString());

        }
        
    }
    
    public static void setOutputFile(String option, File output) {
        if (!option.startsWith("/")) {
            output = new File(MainProcessController.root, option);
        } else {
            output = new File(option);
        }
        output.mkdirs();
        LogHandler.getLogs().displayMsg("Output: " + output.getPath(), NORMAL);
    }
    
}
