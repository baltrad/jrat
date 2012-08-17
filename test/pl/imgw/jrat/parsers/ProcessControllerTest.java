/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pl.imgw.jrat.process.ProcessController;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ProcessControllerTest {

    ProcessController proc;
    
    @Test
    public void productListingTest() {
        String[] args = {"-i", "test-data/comp/*"};
        proc = new ProcessController(args);
        proc.start();
        assertEquals("parsing list of files error", 2, proc.getFiles().size());
    }
    
}
