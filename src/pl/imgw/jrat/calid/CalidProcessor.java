/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static pl.imgw.jrat.tools.out.Logging.PROGRESS_BAR_ONLY;

import java.io.File;
import java.util.List;

import pl.imgw.jrat.process.FilesProcessor;
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
public class CalidProcessor implements FilesProcessor {

    private CalidParsedParameters params = new CalidParsedParameters();
    private CalidContainer cc;
    private boolean valid = false;
    
    public CalidProcessor(String args[]) {
        if (params.initialize(args)) {
            cc = new CalidContainer(params);
            valid = true;
        }
    }
    
    public boolean isValid() {
        return valid;
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FilesProcessor#processFile(java.util.List)
     */
    @Override
    public void processFile(List<File> files) {

        PairsContainer pairs = new PairsContainer(files);
        ConsoleProgressBar.getProgressBar().initialize(20, pairs.getSize(),
                LogHandler.getLogs().getVerbose() == PROGRESS_BAR_ONLY,
                "CALID calculations");

        Pair pair = null;
        
        while (pairs.hasNext()) {
            pair = pairs.next();
            ConsoleProgressBar.getProgressBar().evaluate();
            if (isValid()) {
                LogHandler.getLogs().displayMsg("", LogHandler.NORMAL);
                cc.setPair(pair);
                cc.initialize(pair.getDate());
                cc.resetContainer();
//                cc.resetDifferences();
            }
        }
        ConsoleProgressBar.getProgressBar().printDoneMsg();
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FilesProcessor#getProcessName()
     */
    @Override
    public String getProcessName() {
        // TODO Auto-generated method stub
        return "CALID Process";
    }

}
