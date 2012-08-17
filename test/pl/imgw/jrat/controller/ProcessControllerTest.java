/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.controller;

import static org.junit.Assert.*;

import org.junit.Before;
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
    
    private ProcessController pc;
    private String[] args;


    /**
     * Test method for {@link pl.imgw.jrat.process.ProcessController#start()}.
     */
    @Test
    public void testStart() {

        args = "-a arg".split(" ");
        pc = new ProcessController(args);
        
        assertTrue(pc.start());
        
        args = "--print".split(" ");
        pc = new ProcessController(args);
        
        assertTrue(pc.start());
        
        args = "--version".split(" ");
        pc = new ProcessController(args);
        
        assertTrue(pc.start());
    }

}
