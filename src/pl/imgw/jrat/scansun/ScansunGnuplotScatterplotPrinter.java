/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.io.IOException;

import org.jgnuplot.Axes;
import org.jgnuplot.Graph;
import org.jgnuplot.LineType;
import org.jgnuplot.PointType;
import org.jgnuplot.Style;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.scansun.ScansunEvent.EventType;
import pl.imgw.jrat.scansun.ScansunPlot.GnuplotColors;
import pl.imgw.jrat.scansun.ScansunPlot.GnuplotCoordinates;
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
public class ScansunGnuplotScatterplotPrinter extends ScansunGnuplotResultsPrinter {

    private String siteName;

    public ScansunGnuplotScatterplotPrinter(String siteName, ScansunResultsParsedParameters params) {
	super(params);
	this.siteName = siteName;
    }

    public void generatePlot() {

	try {
	    generateScatterplot();
	} catch (IOException e) {
	    LogHandler.getLogs().displayMsg("SCANSUN: generateScatterplot() error", Logging.ERROR);
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    LogHandler.getLogs().displayMsg("SCANSUN: generateScatterplot() error", Logging.ERROR);
	    e.printStackTrace();
	}

    }

    private void generateScatterplot() throws IOException, InterruptedException {

	if (!(resultsEventsContainer.filterEvents().bySiteName(siteName).byEventType(EventType.SOLARRAY).getEvents().size() > 0)) {
	    LogHandler.getLogs().displayMsg("SCANSUN: no solar rays in results files - skipping creation of scatterplot for " + siteName, NORMAL);
	    return;
	}

	String baseFilename = "scansun_scatterplot_" + siteName + "_" + getDays(siteName).first() + "-" + getDays(siteName).last();

	File data = new File(AplicationConstans.TMP, baseFilename + ".data");

	ResultPrinter pr = new FileResultPrinter(data);
	ResultPrinterManager.getManager().setPrinter(pr);
	printRawResults(siteName);

	ScansunPlot plot = new ScansunPlot();

	String filename = baseFilename + ".eps";
	plot.setOutput(GnuplotTerminal.POSTSCRIPT, (new File(ScansunFileHandler.getScansunPath(), filename)).getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
		"eps enhanced");

	String title = "Offset scatterplot for " + siteName;
	// String subTitle = "h_{min} = XX" + " rmin = XX" + " tf = XX"
	// + " ad = XX";
	String subTitle = "";
	plot.setTitle(title, subTitle, FONT_NAME, TITLE_FONT_SIZE);

	plot.setKey("bottom right", 2.5, FONT_NAME, KEY_FONT_SIZE);
	plot.setSquare();

	double xmin = -2.6;
	double xmax = 2.6;

	plot.setXLabel("(Radar azimuth) - (Sun azimuth) [deg]", FONT_NAME, AXIS_LABEL_FONT_SIZE, 0, -1);
	plot.setXRange(xmin, xmax);
	plot.setXTics(0.2, FONT_NAME, AXIS_TIC_FONT_SIZE, 90);
	plot.setFormatX("%.1f");

	double ymin = -2.6;
	double ymax = 2.6;

	plot.setYLabel("(Radar elevation) - (Sun elevation) [deg]", FONT_NAME, AXIS_LABEL_FONT_SIZE);
	plot.setYRange(ymin, ymax);
	plot.setYTics(0.2, FONT_NAME, AXIS_TIC_FONT_SIZE);
	plot.setFormatY("%.1f");

	plot.setGrid("", 4, 0.75, GnuplotColors.GRAY);

	int N = getTotalNumber(siteName, EventType.EVENT);
	plot.setLabel("N = " + N, GnuplotCoordinates.GRAPH, 0.1, GnuplotCoordinates.GRAPH, 0.9, FONT_NAME, LABEL_FONT_SIZE);
	int n = getTotalNumber(siteName, EventType.SOLARRAY);
	plot.setLabel("n = " + n, GnuplotCoordinates.GRAPH, 0.1, GnuplotCoordinates.GRAPH, 0.85, FONT_NAME, LABEL_FONT_SIZE);

	double meanPower = getMeanPower(siteName);
	// String meanPowerName = "meanPower";
	// plot.addVariable(meanPowerName, meanPower);

	plot.setParametric();

	plot.pushGraph(new Graph(data.getAbsolutePath(), "(stringcolumn(10) eq 'false'?($2-$4):1/0):($1-$3)", Axes.NOT_SPECIFIED, "no hit", Style.POINTS,
		LineType.SCREEN_BLACK_DOTS, PointType.POSTSCRIPT_CIRCLE_DOT));

	plot.pushGraph(new Graph(data.getAbsolutePath(), "(stringcolumn(10) eq 'true' && $13 < " + meanPower
		+ " && stringcolumn(9) eq 'short' ?($2-$4):1/0):($1-$3)", Axes.NOT_SPECIFIED, "weak short scan", Style.POINTS,
		LineType.SCREEN_BLACK_SOLID_BOLD, PointType.SCREEN_CIRCLE_FILLED));

	plot.pushGraph(new Graph(data.getAbsolutePath(), "(stringcolumn(10) eq 'true' && $13 < " + meanPower
		+ " && stringcolumn(9) eq 'long' ?($2-$4):1/0):($1-$3)", Axes.NOT_SPECIFIED, "weak long scan", Style.POINTS, LineType.SCREEN_BLACK_SOLID_BOLD,
		PointType.SCREEN_SQUARE_FILLED));

	plot.pushGraph(new Graph(data.getAbsolutePath(), "(stringcolumn(10) eq 'true' && $13 > " + meanPower
		+ " && stringcolumn(9) eq 'short' ?($2-$4):1/0):($1-$3)", Axes.NOT_SPECIFIED, "strong short scan", Style.POINTS, LineType.SCREEN_RED,
		PointType.SCREEN_CIRCLE_FILLED));

	plot.pushGraph(new Graph(data.getAbsolutePath(), "(stringcolumn(10) eq 'true' && $13 > " + meanPower
		+ " && stringcolumn(9) eq 'long' ?($2-$4):1/0):($1-$3)", Axes.NOT_SPECIFIED, "strong long scan", Style.POINTS, LineType.SCREEN_RED,
		PointType.SCREEN_SQUARE_FILLED));

	plot.pushGraph(new Graph("t,0", Axes.NOT_SPECIFIED, "", Style.LINES, LineType.SCREEN_BLACK_DOTS, PointType.NOT_SPECIFIED));
	plot.pushGraph(new Graph("0,t", Axes.NOT_SPECIFIED, "", Style.LINES, LineType.SCREEN_BLACK_DOTS, PointType.NOT_SPECIFIED));

	plot.plot();

	data.delete();

	LogHandler.getLogs().displayMsg("New plot generated: scatterplot for " + siteName, NORMAL);
    }

}
