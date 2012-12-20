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
public class CalidProcessTest {

    String[] args;
    CalidProcessor proc;
    
    @Test
    public void processTest() {
        
        proc = new CalidProcessor(null);
        assertTrue(proc.isValid());
        
        args = "ele=0.5".split(" ");
        proc = new CalidProcessor(args);
        assertTrue(proc.isValid());
        
        args = "dis=500 ele=0.5".split(" ");
        proc = new CalidProcessor(args);
        assertTrue(proc.isValid());
        
        
    }
 
    @Test
    public void runFromMainProcessTest() {
        args = "--calid ref=3.5 -i test-data/calid/*.vol -v".split(" ");
        MainProcessController main = new MainProcessController(args);
        assertTrue(main.start());
    }
}
