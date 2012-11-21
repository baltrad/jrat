/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.File;
import java.util.List;

import pl.imgw.jrat.calid.CalidManager;
import pl.imgw.jrat.calid.Pair;
import pl.imgw.jrat.calid.PairsContainer;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidProcessor implements FilesProcessor {

    private CalidManager manager;
    
    public CalidProcessor(String args[]) {
        manager = new CalidManager(args);
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FilesProcessor#processFile(java.util.List)
     */
    @Override
    public void processFile(List<File> files) {
        
        PairsContainer pairs = new PairsContainer(files);
        for(Pair pair : pairs.getPairs()) {
            manager.compare(pair);
        }
    }

}
