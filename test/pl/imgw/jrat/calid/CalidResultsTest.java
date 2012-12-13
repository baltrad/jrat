/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import pl.imgw.jrat.process.MainProcessController;


/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultsTest {
    private CalidManager calid;
    private String[] args;
    
    @Test
    public void argsParserTest() {
        
        calid = new CalidManager(null);
        assertTrue(calid.isValid());
        
        args = "ele=-11 dis=500 src=WMO:12568,WMO:12579".split(" ");
        calid = new CalidManager(args);
        assertTrue(!calid.isValid());
        
        args = "ele=0.5 dis=0.6 src=WMO:12568,WMO:12579".split(" ");
        calid = new CalidManager(args);
        assertTrue(!calid.isValid());
        
        args = "ele=0.5 dis=dziesiec src=WMO:12568,WMO:12579".split(" ");
        calid = new CalidManager(args);
        assertTrue(!calid.isValid());
        
        args = "ele=0.5 dis=500 src=WMO:12568,WMO:12579".split(" ");
        calid = new CalidManager(args);
        assertTrue(calid.isValid());
    }
 
    /*
    @Test
    public void resultsListTest() {
        args = "ele=0.5 dis=500 src=WMO:12568,WMO:12579".split(" ");
        calid = new CalidManager(args);
        CalidResultManager manager = new CalidResultManager(calid);
        manager.printPairsList();
    }
    */
    
}
