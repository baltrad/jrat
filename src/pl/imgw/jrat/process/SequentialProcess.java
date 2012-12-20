/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class SequentialProcess implements Runnable {

    private List<File> folders;
    private List<File> files = new LinkedList<File>();
    private Calendar cal;
    private FilesProcessor proc;
    // private int length;
    private int interval = 60;

    public SequentialProcess(FilesProcessor proc, List<File> folders,
            String seqValue) {

        this.proc = proc;
        this.folders = folders;
        try {
            interval = Integer.parseInt(seqValue);
        } catch (NumberFormatException e) {
            LogHandler.getLogs().displayMsg(
                    "Incorrect value for seqence time interval",
                    LogHandler.ERROR);
            proc = null;
        }
        cal = Calendar.getInstance();
        // cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.SECOND, 0);
        int minute = cal.get(Calendar.MINUTE);
        minute = (minute / interval) * interval;
        cal.set(Calendar.MINUTE, minute);
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
        LogHandler.getLogs().displayMsg(
                "Sequential process started with: " + proc.getProcessName(),
                NORMAL);
        while (true) {
            if (cal.getTime().before(new Date(System.currentTimeMillis()))) {
                files.clear();
                for (File folder : folders) {
                    files.addAll(Arrays.asList(folder.listFiles()));
                }

                proc.processFile(files);

                for (File f : files) {
                    f.delete();
                }
                cal.add(Calendar.MINUTE, interval);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LogHandler.getLogs().displayMsg(
                            "Process '" + proc.getProcessName()
                                    + "' interupted", ERROR);
                    LogHandler.getLogs().saveErrorLogs(this, e);
                }
            }
        }

    }

}
