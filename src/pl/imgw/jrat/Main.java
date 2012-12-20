/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat;

import java.io.File;

import static pl.imgw.jrat.AplicationConstans.*;

import pl.imgw.jrat.process.MainProcessController;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        File genFile = new File(LOG, "log_gen");
        File recFile = new File(LOG, "log_rec");
        File errFile = new File(LOG, "log_err");
        
        LogHandler.getLogs().setGeneralLogsPath(genFile, 0);
        LogHandler.getLogs().setErrorLogsPath(errFile, 0);
        LogHandler.getLogs().setRecantFileLogsPath(recFile, 0);
        
        MainProcessController pc = new MainProcessController(args);
        if(!pc.start()) {
            if(LogHandler.getLogs().getVerbose() != Logging.SILENT)
                System.out.println("JRAT: failed");
        }
    }

}
