/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.File;
import java.util.List;

import pl.imgw.jrat.calid.data.CalidParametersFileHandler;
import pl.imgw.jrat.tools.in.FilePatternFilter;
import pl.imgw.jrat.tools.in.RegexFileFilter;
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
public class FileProcessController {

    private static Log log = LogManager.getLogger();

    public static void setInputFilesAndFolders(List<File> files,
            List<File> folders, String[] options) {

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
        folders.addAll(CalidParametersFileHandler.getOptions().getInputFolderList());

        if (files.isEmpty() && folders.isEmpty()) {
            log.printMsg("No input", Log.TYPE_WARNING, Log.MODE_VERBOSE);
        } else {
            LogManager.getProgBar().initialize(20, files.size(),
                    "Setting up files");
            StringBuilder msg = new StringBuilder();
            msg.append("Input files:");
            for (File f : files) {
                msg.append(" ").append(f.getPath());
            }
            log.printMsg(msg.toString(), Log.TYPE_NORMAL, Log.MODE_VERBOSE);
            LogManager.getProgBar().evaluate();
            msg = new StringBuilder("Input folders:");
            for (File f : folders) {
                msg.append(" ").append(f.getPath());
            }
            log.printMsg(msg.toString(), Log.TYPE_NORMAL, Log.MODE_VERBOSE);
            LogManager.getProgBar().evaluate();

            msg = new StringBuilder();
            if (!files.isEmpty()) {
                msg.append(files.size()).append(" files");
            }
            LogManager.getProgBar().complete(msg.toString());

        }

    }

    public static void setOutputFile(String option, File output) {
        if (!option.startsWith("/")) {
            output = new File(MainProcessController.root, option);
        } else {
            output = new File(option);
        }
        output.mkdirs();
        log.printMsg("Output: " + output.getPath(), Log.TYPE_NORMAL,
                Log.MODE_VERBOSE);
    }

}
