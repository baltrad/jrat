/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
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
public class SequentialProcess implements Runnable {

    private static Log log = LogManager.getLogger();

    private List<File> folders;
    private List<File> files = new LinkedList<File>();
    private Calendar cal;
    private FilesProcessor proc;
    // private int length;
    private int interval = 60;
    private boolean valid = false;

    /**
     * 
     * @param proc
     *            process to run
     * @param folders
     *            folders to watch
     * @param seqValue
     *            in minutes
     */
    public SequentialProcess(FilesProcessor proc, List<File> folders,
            String seqValue) {

        if (folders.isEmpty()) {
            log.printMsg("No valid folders specified.", Log.TYPE_WARNING,
                    Log.MODE_VERBOSE);
            return;
        }
        this.proc = proc;
        this.folders = folders;
        try {
            interval = Integer.parseInt(seqValue);
        } catch (NumberFormatException e) {
            log.printMsg("Incorrect value for seqence time interval",
                    Log.TYPE_ERROR, Log.MODE_VERBOSE);
            proc = null;
        }
        cal = Calendar.getInstance();
        // cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.SECOND, 0);
        int minute = cal.get(Calendar.MINUTE);
        minute = (minute / interval) * interval;
        cal.set(Calendar.MINUTE, minute);
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
        if (proc == null) {
            return;
        }
        if (folders.isEmpty()) {
            log.printMsg("No input folders for sequential process specified",
                    Log.TYPE_WARNING, Log.MODE_SILENT);
            return;
        }
        log.printMsg(
                "Sequential process started with: " + proc.getProcessName(),
                Log.TYPE_NORMAL, Log.MODE_VERBOSE);
        while (true) {
            if (cal.getTime().before(new Date(System.currentTimeMillis()))) {
                files.clear();
                for (File folder : folders) {
                    files.addAll(Arrays.asList(folder.listFiles()));
                }

                proc.processFile(files);

                for (File f : files) {
                    if (f.delete())
                        log.printMsg(f.getName() + " deleted.",
                                Log.TYPE_WARNING, Log.MODE_VERBOSE);
                }

                cal.add(Calendar.MINUTE, interval);
                
                System.gc();
                
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.printMsg("Process '" + proc.getProcessName()
                            + "' interupted", Log.TYPE_ERROR, Log.MODE_VERBOSE);
                }
            }
        }

    }

}
