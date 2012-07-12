/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.controller;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ProcessControllerTest {
    
    private ProcessController pc;
    private String[] args;


    /**
     * Test method for {@link pl.imgw.jrat.controller.ProcessController#start()}.
     */
    @Test
    public void testStart() {

        args = "-i plik s -v -h".split(" ");
        pc = new ProcessController(args);
        
        assertTrue(pc.start());
        
        args = "-a plik sdf".split(" ");
        pc = new ProcessController(args);
        
        assertTrue(pc.start());
        
    }

}
