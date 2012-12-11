/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

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
public class ScansunTest {

    
    @Test
    public void scansunProcessorTest() {
        
        String[] args = "--scansun -v".split(" ");
        
        MainProcessController proc = new MainProcessController(args);
        proc.start();
        
    }
    
    @Test
    public void scansunProcessFileTest() {
        
        String[] args = "--scansun -i test-data/1.vol -v".split(" ");
        
        MainProcessController proc = new MainProcessController(args);
        proc.start();
        
    }
    
}
