/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

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
        //must provide all necessary options
        args = "--calid ref=3.5 dis=500 range=200 ele=0.5 -i test-data/calid/*.vol -v".split(" ");
        MainProcessController main = new MainProcessController(args);
        assertTrue(main.start());
    }
    
    @Test
    public void runFromMainProcessWithOptFileTest() {
        System.out.println("only opt file");
        args = "--calid -v --calid-opt test-data/calid/calid.opt".split(" ");
        MainProcessController main = new MainProcessController(args);
        assertTrue(main.start());
    }
    
    @Test
    public void runFromMainProcessWithMixedOptTest() {
        args = "--calid ref=5.0 -i test-data/calid/*.vol -v --calid-opt test-data/calid/calid.opt".split(" ");
        MainProcessController main = new MainProcessController(args);
        assertTrue(main.start());
    }
    
}
