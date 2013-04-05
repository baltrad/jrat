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
        for (String name : options) {
            if (!name.startsWith("/")) {
                name = MainProcessController.root.getPath() + "/" + name;
            }
            if(name.contains("*")) {
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
        
        
        if (files.isEmpty() && folders.isEmpty()) {
            LogHandler.getLogs().displayMsg("No such file or directory",
                    WARNING);
        } else {
            ConsoleProgressBar.getProgressBar().initialize(20,
                    files.size(),
                    LogHandler.getLogs().getVerbose() == PROGRESS_BAR_ONLY,
                    "Setting up files");
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
            
            String msg = "";
            if (!files.isEmpty()) {
                msg = files.size() + " files";
            }
            ConsoleProgressBar.getProgressBar().printDoneMsg(msg);
            
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
