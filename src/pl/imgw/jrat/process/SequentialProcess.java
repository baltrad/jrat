/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class SequentialProcess implements Runnable {

    private File path;
    private Calendar cal;
    private FilesProcessor proc;
    // private int length;
    private int interval = 60;

    public SequentialProcess(FilesProcessor proc, File watchedPath, int interval) {
        this.proc = proc;
        this.interval = interval;
        this.path = watchedPath;
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
        if(proc == null)
            return;
        while (true) {
            if (cal.getTime().before(new Date(System.currentTimeMillis()))) {
                File[] files = path.listFiles();
                proc.processFile(files);
                cal.add(Calendar.MINUTE, interval);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

}
