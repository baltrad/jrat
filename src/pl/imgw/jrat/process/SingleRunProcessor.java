/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.File;
import java.util.Arrays;
import java.util.List;

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
public class SingleRunProcessor implements Runnable {

	private static Log log = LogManager.getLogger();

	private List<File> files;
	private FilesProcessor proc;
	private boolean valid = false;

	/**
     * 
     */
	public SingleRunProcessor(FilesProcessor proc, List<File> folders,
			List<File> files) {

		for (File folder : folders) {
			files.addAll(Arrays.asList(folder.listFiles()));
		}

		if (files.isEmpty()) {
			log.printMsg("No input files specified", Log.TYPE_WARNING,
					Log.MODE_VERBOSE);
			return;
		}
		this.files = files;
		this.proc = proc;
		valid = true;
	}

	public boolean isValid() {
		return valid;
	}

	@Override
	public void run() {
		if (proc == null)
			return;

		else
			log.printMsg(
					"Single run process started with: " + proc.getProcessName(),
					Log.TYPE_NORMAL, Log.MODE_VERBOSE);

		proc.processFile(files);

	}

}
