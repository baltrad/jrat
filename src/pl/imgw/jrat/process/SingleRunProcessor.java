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

    private List<File> files;
    private FilesProcessor proc;
    private boolean valid = false;

    /**
     * 
     */
    public SingleRunProcessor(FilesProcessor proc, List<File> folders, List<File> files) {

	for (File folder : folders) {
	    files.addAll(Arrays.asList(folder.listFiles()));
	}

	if (files.isEmpty()) {
	    LogHandler.getLogs().displayMsg("No input files specified", WARNING);
	    return;
	}
	this.files = files;
	this.proc = proc;
	valid = true;
    }

    public boolean isValid() {
	return valid;
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

	else
	    LogHandler.getLogs().displayMsg("Single run process started with: " + proc.getProcessName(), NORMAL);

	proc.processFile(files);
    }

}
