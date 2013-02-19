/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.io.File;

import javax.naming.directory.InvalidAttributesException;

import org.jgnuplot.Axes;
import org.jgnuplot.Graph;
import org.jgnuplot.Style;
import org.jgnuplot.Terminal;

import pl.imgw.jrat.tools.out.plot.TimeSeriesPlot;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidMeanDifferencePlot extends TimeSeriesPlot {

    File data1day;
    File data5day;
    File data10day;
    
    /**
     * 
     */
    public CalidMeanDifferencePlot(File data1day, File data5day, File data10day) {
        this.data1day = data1day;
        this.data5day = data5day;
        this.data10day = data10day;
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.tools.out.plot.TimeSeriesPlot#setPlot()
     */
    @Override
    public void setPlot() throws InvalidAttributesException{
        
        if(data1day == null || data5day == null || data10day == null)
            throw new InvalidAttributesException("input data file is null");
        
        if(!data1day.exists() || !data1day.isFile())
            throw new InvalidAttributesException(data1day + " is not a valid file");
        if(!data5day.exists() || !data5day.isFile())
            throw new InvalidAttributesException(data5day + " is not a valid file");
        if(!data10day.exists() || !data10day.isFile())
            throw new InvalidAttributesException(data10day + " is not a valid file");
        
        plot.setGrid();
        plot.pushGraph(new Graph(data1day.getAbsolutePath(), "1:2", Axes.NOT_SPECIFIED,
                "avg(1)", Style.POINTS));
        plot.pushGraph(new Graph(data5day.getAbsolutePath(), "1:2", Axes.NOT_SPECIFIED,
                "avg(5)", Style.LINESPOINTS));
        plot.pushGraph(new Graph(data10day.getAbsolutePath(), "1:2", Axes.NOT_SPECIFIED,
                "avg(10)", Style.LINESPOINTS));
        
        plot.setOutput(Terminal.PNG, output);

    }

    public void setPairsName(String pair1, String pair2) {
        title.append("Pair wise comparison for ").append(pair1).append(" and ").append(pair2);
        
    }
    
}
