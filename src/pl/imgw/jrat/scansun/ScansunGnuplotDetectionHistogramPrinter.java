/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.SortedSet;

import org.jgnuplot.Axes;
import org.jgnuplot.Graph;
import org.jgnuplot.LineType;
import org.jgnuplot.PointType;
import org.jgnuplot.Style;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.scansun.ScansunEvent.EventType;
import pl.imgw.jrat.scansun.ScansunEvent.PulseDuration;
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
public class ScansunGnuplotDetectionHistogramPrinter extends ScansunGnuplotResultsPrinter {

    private String siteName;

    public ScansunGnuplotDetectionHistogramPrinter(String siteName, ScansunResultsParsedParameters params) {
	super(params);
	this.siteName = siteName;
    }

    public void generatePlot() {

	try {
	    generateDetectionHistogram();
	} catch (IOException e) {
	    LogHandler.getLogs().displayMsg("SCANSUN: generateDetectionHistogram()  error", Logging.ERROR);
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    LogHandler.getLogs().displayMsg("SCANSUN: generateScatterplot() error", Logging.ERROR);
	    e.printStackTrace();
	}

    }

    private void generateDetectionHistogram() throws IOException, InterruptedException {

	if (!(resultsEventsContainer.filterEvents().bySiteName(siteName).byEventType(EventType.SOLARRAY).getEvents().size() > 0)) {
	    LogHandler.getLogs().displayMsg("SCANSUN: no solar rays in results files - skipping creation of detection histogram for " + siteName, NORMAL);
	    return;
	}

	String baseFilename = "scansun_detection_histogram_" + siteName + "_" + getDays(siteName).first() + "-" + getDays(siteName).last();

	File data = new File(AplicationConstans.TMP, baseFilename + ".data");

	ResultPrinter pr = new FileResultPrinter(data);
	ResultPrinterManager.getManager().setPrinter(pr);
	printResults(siteName);

	ScansunPlot plot = new ScansunPlot();

	String filename = baseFilename + ".eps";
	plot.setOutput(GnuplotTerminal.POSTSCRIPT, (new File(ScansunFileHandler.getScansunPath(), filename)).getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
		"eps enhanced");

	String title = "Detection histogram for " + siteName;
	String subTitle = "";
	plot.setTitle(title, subTitle, FONT_NAME, TITLE_FONT_SIZE);

	plot.setKey("", 2.5, FONT_NAME, KEY_FONT_SIZE);
	plot.setTics(GnuplotFgBg.FRONT);

	SortedSet<ScansunDay> days = getDays(siteName);

	int xmin = days.first().getDayOfYear();
	int xmax = days.last().getDayOfYear();

	plot.setXLabel("Day of year", FONT_NAME, AXIS_LABEL_FONT_SIZE);
	plot.setXRange(xmin - 0.5, xmax + 0.5);
	plot.setXTics(xmin, 1, FONT_NAME, AXIS_TIC_FONT_SIZE);

	int ymin = 0;
	int ymax = ((int) Math.ceil(getDetectionHistogramMax(siteName, EventType.EVENT) / 100) + 3) * 100;
	double yDate = 0.8;

	plot.setYLabel("Number of detections", FONT_NAME, AXIS_LABEL_FONT_SIZE);
	plot.setYRange(ymin, ymax);
	plot.setYTics(ymin, 50, FONT_NAME, AXIS_TIC_FONT_SIZE);
	plot.setMYTics(2);

	// plot.setGrid("ytics", 4, 0.25, GnuplotColors.GRAY);

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

	    plot.setLabelCenter(((ScansunDay) days.toArray()[i]).toString(), GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0), GnuplotCoordinates.GRAPH, yDate,
		    FONT_NAME, LABEL_FONT_SIZE, 90, GnuplotColors.MAROON);
	}

	DecimalFormat df = new DecimalFormat("#.##");

	for (int i = 0; i < days.size(); i++) {
	    double x1 = 0.0 + (1.0 / days.size()) * i;
	    double x2 = x1 + (1.0 / days.size());

	    Integer eventsNumberFromShortScan = getDetectionHistogramByDay(siteName, EventType.EVENT, PulseDuration.SHORT)
		    .get(((ScansunDay) days.toArray()[i]));
	    Integer eventsNumberFromLongScan = getDetectionHistogramByDay(siteName, EventType.EVENT, PulseDuration.LONG).get(((ScansunDay) days.toArray()[i]));
	    Integer eventsNumber = eventsNumberFromShortScan + eventsNumberFromLongScan;

	    Integer solarRaysNumberFromShortScan = getDetectionHistogramByDay(siteName, EventType.SOLARRAY, PulseDuration.SHORT).get(
		    ((ScansunDay) days.toArray()[i]));
	    Integer solarRaysNumberFromLongScan = getDetectionHistogramByDay(siteName, EventType.SOLARRAY, PulseDuration.LONG).get(
		    ((ScansunDay) days.toArray()[i]));
	    Integer solarRaysNumber = solarRaysNumberFromShortScan + solarRaysNumberFromLongScan;

	    Double efficiency = ((double) solarRaysNumber / eventsNumber) * 100.0;

	    GnuplotColors color;
	    if (efficiency < 5.0) {
		color = GnuplotColors.RED;
	    } else {
		color = GnuplotColors.GREEN;
	    }

	    plot.setLabelCenter(df.format(efficiency), GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0), GnuplotCoordinates.GRAPH, yDate - 0.2, FONT_NAME,
		    LABEL_FONT_SIZE, 0, color);
	    /*
	     * plot.setLabelCenter(eventsNumber.toString(),
	     * GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
	     * GnuplotCoordinates.GRAPH, 0.85, font, labelFontSize, 0,
	     * GnuplotColors.BLACK);
	     */

	    plot.setLabelCenter(eventsNumberFromLongScan.toString(), GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0), GnuplotCoordinates.GRAPH, 0.45,
		    FONT_NAME, LABEL_FONT_SIZE, 0, GnuplotColors.BLACK);
	    plot.setLabelCenter(eventsNumberFromShortScan.toString(), GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0), GnuplotCoordinates.GRAPH, 0.4,
		    FONT_NAME, LABEL_FONT_SIZE, 0, GnuplotColors.BLACK);

	    /*
	     * plot.setLabelCenter(solarRaysNumber.toString(),
	     * GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
	     * GnuplotCoordinates.GRAPH, 0.6, font, labelFontSize, 0,
	     * GnuplotColors.RED);
	     */
	    plot.setLabelCenter(solarRaysNumberFromLongScan.toString(), GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0), GnuplotCoordinates.GRAPH, 0.2,
		    FONT_NAME, LABEL_FONT_SIZE, 0, GnuplotColors.RED);
	    plot.setLabelCenter(solarRaysNumberFromShortScan.toString(), GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0), GnuplotCoordinates.GRAPH, 0.15,
		    FONT_NAME, LABEL_FONT_SIZE, 0, GnuplotColors.RED);
	}

	for (int i = 1; i < days.size(); i++) {
	    double x = 0.0 + (1.0 / days.size()) * i;
	    double y1 = 0.0;
	    // double x2 = x1 + (1.0 / days.size());
	    double y2 = 1.0;

	    plot.setArrow(GnuplotCoordinates.GRAPH, x, GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x, GnuplotCoordinates.GRAPH, y2,
		    GnuplotFgBg.FRONT, GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS, GnuplotColors.GRAY);
	}

	plot.pushGraph(new Graph(data.getAbsolutePath(), "2:3", Axes.NOT_SPECIFIED, "Expected long scan", Style.BOXES, LineType.SCREEN_BLACK_SOLID_BOLD,
		PointType.NOT_SPECIFIED));

	plot.pushGraph(new Graph(data.getAbsolutePath(), "2:4", Axes.NOT_SPECIFIED, "Expected short scan", Style.BOXES, LineType.SCREEN_BROWN,
		PointType.NOT_SPECIFIED));

	plot.pushGraph(new Graph(data.getAbsolutePath(), "2:6", Axes.NOT_SPECIFIED, "Detected long scan", Style.BOXES, LineType.SCREEN_RED,
		PointType.NOT_SPECIFIED));

	plot.pushGraph(new Graph(data.getAbsolutePath(), "2:7", Axes.NOT_SPECIFIED, "Detected short scan", Style.BOXES, LineType.SCREEN_SALMON,
		PointType.NOT_SPECIFIED));

	plot.plot();

	data.delete();

	LogHandler.getLogs().displayMsg("New plot generated: detection histogram for " + siteName, NORMAL);
    }

}
