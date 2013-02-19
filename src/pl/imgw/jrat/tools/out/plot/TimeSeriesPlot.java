/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out.plot;

import java.io.File;
import java.io.IOException;

import javax.naming.directory.InvalidAttributesException;

import org.jgnuplot.Axes;
import org.jgnuplot.Graph;
import org.jgnuplot.Plot;
import org.jgnuplot.Style;
import org.jgnuplot.Terminal;

import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * This class helps creating time series, which are plots with x axis formated
 * for time, you must implement setPlot() abstract method to provide plot
 * settings for output format, data series files with one column formated as
 * "%Y-%m-%d",
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public abstract class TimeSeriesPlot {

    static {
        Plot.setGnuplotExecutable("gnuplot");
        Plot.setPlotDirectory("/tmp");
    }

    protected int ymin;
    protected int ymax;
    protected String output = "newplot";
    protected StringBuilder title = new StringBuilder();
    protected StringBuilder period = new StringBuilder();

    protected Plot plot;

    public abstract void setPlot() throws InvalidAttributesException;
    
    public boolean plot() {

        plot = new Plot();
        
        plot.setTitle(title.append("\\n").append(period).toString());
        plot.setYRange(ymin, ymax);
        plot.setXData("time");
        plot.setTimeFormat("%Y-%m-%d");
        plot.setFormatX("%b");


        try {
            setPlot();
            plot.plot();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAttributesException e) {
            LogHandler.getLogs().displayMsg(
                    "PLOT: Invalid attribute: " + e.getExplanation(),
                    Logging.WARNING);
        }
        return false;
    }

    /**
     * @param ymin
     *            the ymin to set
     */
    public void setYmin(int ymin) {
        this.ymin = ymin;
    }

    /**
     * @param ymax
     *            the ymax to set
     */
    public void setYmax(int ymax) {
        this.ymax = ymax;
    }

    /**
     * @param output
     *            the output to set
     */
    public void setOutput(File output) {
        if(output != null)
            this.output = output.getAbsolutePath();
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title.append(title);
    }
    
    public void setTimePeriod(String from, String to) {
        period.append("From ").append(from).append(" to ").append(to);
    }

}
