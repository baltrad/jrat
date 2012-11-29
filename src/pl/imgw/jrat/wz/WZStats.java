/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.wz;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import pl.imgw.jrat.process.FileWatchingProcess;
import pl.imgw.jrat.tools.out.LogHandler;
import static pl.imgw.jrat.tools.out.Logging.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class WZStats {

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length < 2)
            return;

        File file = new File(args[0]);
        List<File> files = new LinkedList<File>();
        files.add(file);
        File dest = new File(args[1]);
        if (file.exists() && dest.exists()) {
            LogHandler.getLogs().setLoggingVerbose(ERROR);
            WZStatsProcessor proc = new WZStatsProcessor();
            proc.setDest(dest);
            FileWatchingProcess fw = new FileWatchingProcess(proc, files);

            // SequentialProcess sp = new SequentialProcess(proc, new
            // File("test-data/watched"), 1);
            Thread t = new Thread(fw);
            t.start();
        }
    }

}
