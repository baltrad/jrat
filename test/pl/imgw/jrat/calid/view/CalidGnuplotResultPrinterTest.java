/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.view;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import pl.imgw.util.ConsolePrinter;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidGnuplotResultPrinterTest {

    private File output = new File("test-data/calid", "output.png");
    
    {
        LogManager.getInstance().setLogger(new ConsolePrinter(Log.MODE_VERBOSE));

    }
    
    @After
    public void tearDown() {
        output.delete();
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.view.CalidGnuplotResultPrinter#generateMeanDifferencePlots()}.
     * @throws IOException 
     */
    @Test
    public void shouldGenerateMeanDifferencePlots() throws IOException {
        String[] args = "Swidwin,Gdansk date=2013-04-01,2013-04-30".split(" ");
        output.delete();
        CalidGnuplotResultPrinter printer = new CalidGnuplotResultPrinter(args, output);
        printer.generateMeanDifferencePlots();
        assertTrue(output.exists());
    }

}
