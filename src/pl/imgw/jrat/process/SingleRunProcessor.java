/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class SingleRunProcessor implements Runnable {

    List<File> files;
    FilesProcessor proc;

    /**
     * 
     */
    public SingleRunProcessor(FilesProcessor proc, List<File> folders,
            List<File> files) {
        for (File folder : folders) {
            files.addAll(Arrays.asList(folder.listFiles()));
        }
        this.files = files;
        this.proc = proc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (proc == null)
            return;
        if (files.isEmpty())
            LogHandler.getLogs()
                    .displayMsg("No input files specified", WARNING);
        else
            LogHandler.getLogs()
                    .displayMsg(
                            "Single run process started with: "
                                    + proc.getProcessName(), NORMAL);

        proc.processFile(files);

    }

}
