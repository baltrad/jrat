/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out.plot;

import java.io.File;
import java.io.IOException;

import org.jgnuplot.Axes;
import org.jgnuplot.Graph;
import org.jgnuplot.Plot;
import org.jgnuplot.Style;
import org.jgnuplot.Terminal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.calid.CalidMeanDifferencePlot;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
import static org.junit.Assert.*;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidPlotTest {

    static {
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
    }
    File data1day = new File("test-data/calid/pozleg_f10a1");
    File data5day = new File("test-data/calid/pozleg_f10a5");
    File data10day = new File("test-data/calid/pozleg_f10a10");
    File output = new File("test-data/calid", "plot.png");
    
    @Test
    public void meanDifferencePlotTest() {
        
        CalidMeanDifferencePlot plot = new CalidMeanDifferencePlot(data1day, data5day, data10day);
        plot.setPairsName("Poznan", "Legionowo");
        plot.setTimePeriod("2012-01-01", "2012-12-31");
        plot.setYmin(-15);
        plot.setYmax(15);
        plot.setOutput(output);
        
        assertTrue(plot.plot());
        assertTrue(output.exists());
        assertTrue(output.delete());
        
    }
    
}
