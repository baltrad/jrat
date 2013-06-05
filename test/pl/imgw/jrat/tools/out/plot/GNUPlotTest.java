/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out.plot;

import java.io.File;
import java.io.IOException;

import org.jgnuplot.Graph;
import org.jgnuplot.Plot;
import org.jgnuplot.Terminal;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class GNUPlotTest {

    File f = new File("test-data", "plot.png");
    
    @Test @Ignore
    public void printSimplePlot() {
        
        Plot.setGnuplotExecutable("gnuplot");
        Plot.setPlotDirectory("/tmp");

        Plot aPlot = new Plot();
        aPlot.pushGraph(new Graph("sin(x)"));
        aPlot.setOutput(Terminal.PNG, f.getAbsolutePath());
        
        try {
            aPlot.plot();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertTrue(f.exists());
        assertTrue(f.isFile());
        assertTrue(f.delete());
        
    }
    
}
