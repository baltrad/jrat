/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.tools.out.Logging.NORMAL;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;

import org.jgnuplot.Axes;
import org.jgnuplot.Graph;
import org.jgnuplot.LineType;
import org.jgnuplot.PointType;
import org.jgnuplot.Style;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.scansun.ScansunEvent.EventType;
import pl.imgw.jrat.scansun.ScansunPlot.GnuplotArrow;
import pl.imgw.jrat.scansun.ScansunPlot.GnuplotColors;
import pl.imgw.jrat.scansun.ScansunPlot.GnuplotCoordinates;
import pl.imgw.jrat.scansun.ScansunPlot.GnuplotFgBg;
import pl.imgw.jrat.scansun.ScansunPlot.GnuplotMonoColor;
import pl.imgw.jrat.scansun.ScansunPlot.GnuplotTerminal;
import pl.imgw.jrat.tools.out.FileResultPrinter;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;
import static pl.imgw.jrat.scansun.ScansunGnuplotStyle.*;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunGnuplotSolarFluxPlotPrinter extends ScansunGnuplotResultsPrinter {

    private String siteName;

    public ScansunGnuplotSolarFluxPlotPrinter(String siteName, ScansunResultsParsedParameters params) {
	super(params);
	this.siteName = siteName;
    }

    public void generatePlot() {

	try {
	    generateSolarFluxPlot();
	} catch (IOException e) {
	    LogHandler.getLogs().displayMsg("SCANSUN: generateSolarFluxPlot() error", Logging.ERROR);
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    LogHandler.getLogs().displayMsg("SCANSUN: generateSolarFluxPlot() error", Logging.ERROR);
	    e.printStackTrace();
	}

    }

    private void generateSolarFluxPlot() throws IOException, InterruptedException {

	if (!(resultsEventsContainer.filterEvents().bySiteName(siteName).byEventType(EventType.SOLARRAY).byMeanPowerCalibrationMode(true).getEvents().size() > 0)) {
	    LogHandler.getLogs().displayMsg("SCANSUN: no solar rays in results files - skipping creation of solar flux plot for " + siteName, NORMAL);
	    return;
	}

	String baseFilename = "scansun_solar_flux_histogram_" + siteName + "_" + getDays(siteName).first() + "-" + getDays(siteName).last();

	File data = new File(AplicationConstans.TMP, baseFilename + ".data");

	ResultPrinter pr = new FileResultPrinter(data);
	ResultPrinterManager.getManager().setPrinter(pr);
	printResults(siteName);

	ScansunPlot plot = new ScansunPlot();

	String filename = baseFilename + ".eps";
	plot.setOutput(GnuplotTerminal.POSTSCRIPT, (new File(ScansunFileHandler.getScansunPath(), filename)).getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
		"eps enhanced");

	String title = "Solar flux for " + siteName;
	String subTitle = "";
	plot.setTitle(title, subTitle, FONT_NAME, TITLE_FONT_SIZE);

	plot.setKey("bottom right", 2.5, FONT_NAME, KEY_FONT_SIZE);
	plot.setTics(GnuplotFgBg.FRONT);

	SortedSet<ScansunDay> days = getDays(siteName);

	int xmin = days.first().getDayOfYear();
	int xmax = days.last().getDayOfYear();

	plot.setXLabel("Day of year", FONT_NAME, AXIS_LABEL_FONT_SIZE);
	plot.setXRange(xmin - 0.5, xmax + 0.5);
	plot.setXTics(xmin, 1, FONT_NAME, AXIS_TIC_FONT_SIZE);

	double ymin = 10.0;
	double ymax = 40.0;
	double yDate = 0.8;

	plot.setYLabel("Power [dBm]", FONT_NAME, AXIS_LABEL_FONT_SIZE);
	plot.setYRange(ymin, ymax);
	plot.setYTics(ymin, 5, FONT_NAME, AXIS_TIC_FONT_SIZE);
	plot.setMYTics(2);

	plot.setGrid("ytics", 4, 1, GnuplotColors.GRAY);

	plot.setStyleFillSolid(0.75);

	double fillDensity1 = 0.3;
	double fillDensity2 = 0.15;

	for (int i = 0; i < days.size(); i++) {
	    double x1 = 0.0 + (1.0 / days.size()) * i;
	    double x2 = x1 + (1.0 / days.size());

	    String cmd = new String("set object ");
	    cmd += (i + 1) + " rectangle behind ";
	    cmd += "from " + GnuplotCoordinates.GRAPH + " " + x1 + "," + GnuplotCoordinates.GRAPH + " " + 0.0 + " ";
	    cmd += "to " + GnuplotCoordinates.GRAPH + " " + x2 + "," + GnuplotCoordinates.GRAPH + " " + 1.0 + " ";
	    plot.addExtra(cmd);

	    double fillDensity = (i % 2 == 0) ? fillDensity1 : fillDensity2;
	    cmd = new String("set obj ");
	    cmd += (i + 1) + " fillstyle solid noborder " + fillDensity + " fillcolor rgb 'grey'";

	    plot.addExtra(cmd);

	    plot.setLabel(((ScansunDay) days.toArray()[i]).toString(), GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0), GnuplotCoordinates.GRAPH, yDate,
		    FONT_NAME, LABEL_FONT_SIZE, 90, GnuplotColors.MAROON);
	}

	for (int i = 1; i < days.size(); i++) {
	    double x = 0.0 + (1.0 / days.size()) * i;
	    double y1 = 0.0;
	    double y2 = 1.0;

	    plot.setArrow(GnuplotCoordinates.GRAPH, x, GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x, GnuplotCoordinates.GRAPH, y2,
		    GnuplotFgBg.FRONT, GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS, GnuplotColors.GRAY);
	}

	plot.pushGraph(new Graph(data.getAbsolutePath(), "2:20", Axes.NOT_SPECIFIED, siteName, Style.LINESPOINTS, LineType.SCREEN_BLACK_SOLID_BOLD,
		PointType.SCREEN_SQUARE_FILLED));

	plot.pushGraph(new Graph(data.getAbsolutePath(), "2:23", Axes.NOT_SPECIFIED, "DRAO", Style.LINESPOINTS, LineType.SCREEN_BLACK_SOLID_BOLD,
		PointType.SCREEN_SQUARE_DOT));

	plot.plot();

	data.delete();

	LogHandler.getLogs().displayMsg("New plot generated: solar flux plot for " + siteName, NORMAL);
    }

}
