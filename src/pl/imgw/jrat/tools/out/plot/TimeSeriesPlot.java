/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out.plot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.directory.InvalidAttributesException;

import org.jgnuplot.Plot;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

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

    private static Log log = LogManager.getLogger();

    static {
        Plot.setGnuplotExecutable("gnuplot");
        File f = new File(AplicationConstans.TMP);
        if (!f.exists()) {
            f.mkdirs();
        }

        Plot.setPlotDirectory(AplicationConstans.TMP);
    }

    private SimpleDateFormat gnudate = new SimpleDateFormat("yyyy-MM-dd");

    protected int ymin;
    protected int ymax;
    protected String output = "newplot";
    protected StringBuilder title = new StringBuilder();
    protected StringBuilder period = new StringBuilder();
    Double median = null;

    protected Plot plot;

    public abstract void setPlot() throws InvalidAttributesException;

    private String xformat = "%d.%m";

    public boolean plot() {

        plot = new Plot();

        plot.setTitle(title.append("\\n").append(period).toString());
        plot.setYRange(ymin, ymax);
        plot.setXData("time");
        plot.setTimeFormat("%Y-%m-%d");
        plot.setFormatX(xformat);
        if(median != null)
            plot.setKey("horizontal title \"Daily median = " + median + "\";");

        try {
            setPlot();
            plot.plot();
            return true;
        } catch (IOException e) {
            log.printMsg(e.getMessage() + " \nInstall gnuplot first",
                    Log.TYPE_WARNING, Log.MODE_VERBOSE);
        } catch (InterruptedException e) {
            log.printMsg("gnuplot failed to run: " + e.getMessage(),
                    Log.TYPE_WARNING, Log.MODE_VERBOSE);
        } catch (InvalidAttributesException e) {
            log.printMsg("PLOT: Invalid attribute: " + e.getExplanation(),
                    Log.TYPE_WARNING, Log.MODE_VERBOSE);
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
        if (output != null)
            this.output = output.getAbsolutePath();
    }
    
    public void setMedian(Double median) {
        this.median = median;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title.append(title);
    }

    public void setTimePeriod(Date from, Date to) {
        period.append("From ").append(gnudate.format(from)).append(" to ")
                .append(gnudate.format(to));
        if (to.getTime() - from.getTime() > 10368000000l)
            xformat = "%b";

    }

}
