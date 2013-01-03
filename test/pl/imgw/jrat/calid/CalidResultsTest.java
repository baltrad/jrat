/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultsTest {

    private CalidParsedParameters params = new CalidParsedParameters();
    private String[] args;

    @Test
    public void printResultsTest() {

        args = "src=WMO:12579,WMO:12568 ele=0.5 dis=500 ref=5.0".split(" ");
        params.initialize(args);

        CalidResultsPrinter printer = new CalidResultsPrinter(params);

        printer.printList();
        assertTrue(true);
    }

}
