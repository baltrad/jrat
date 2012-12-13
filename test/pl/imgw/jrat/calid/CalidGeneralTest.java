/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

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
public class CalidGeneralTest {

    @Test
    public void resultsDetailsTest() {
        String[] args = "--calid-result src=WMO:12568 -v".split(" ");
        MainProcessController proc = new MainProcessController(args);
        proc.start();
    }
    
}
