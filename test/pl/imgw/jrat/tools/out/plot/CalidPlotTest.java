/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out.plot;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import pl.imgw.jrat.calid.view.CalidMeanDifferencePlot;
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
public class CalidPlotTest {

    private SimpleDateFormat gnudate = new SimpleDateFormat("yyyy-MM-dd");
    
    static {
        LogManager.getInstance().setLogMode(Log.MODE_VERBOSE);
    }
    File data1day = new File("test-data/calid/pozleg_f10a1");
    File data5day = new File("test-data/calid/pozleg_f10a5");
    File data10day = new File("test-data/calid/pozleg_f10a10");
    File output = new File("test-data/calid", "plot.png");
    
    @Test
    public void meanDifferencePlotTest() {
        
        CalidMeanDifferencePlot plot = new CalidMeanDifferencePlot(data1day, data5day, data10day);
        plot.setPairsName("Poznan", "Legionowo");
        try {
            plot.setTimePeriod(gnudate.parse("2012-01-01"), gnudate.parse("2012-12-31"));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        plot.setYmin(-15);
        plot.setYmax(15);
        plot.setOutput(output);
        
        assertTrue(plot.plot());
        assertTrue(output.exists());
        assertTrue(output.delete());
        
    }
    
}
