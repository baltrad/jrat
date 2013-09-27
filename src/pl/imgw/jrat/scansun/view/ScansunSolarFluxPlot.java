/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.view;

import static pl.imgw.jrat.scansun.view.ScansunGnuplot.FONT_NAME;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.LABEL_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.TITLE_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.KEY_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.AXIS_LABEL_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.AXIS_TIC_FONT_SIZE;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;

import org.jgnuplot.Axes;
import org.jgnuplot.Graph;
import org.jgnuplot.LineType;
import org.jgnuplot.PointType;
import org.jgnuplot.Style;
import org.joda.time.LocalDate;

import pl.imgw.jrat.scansun.data.ScansunSite;
import pl.imgw.jrat.scansun.view.ScansunGnuplot.GnuplotArrow;
import pl.imgw.jrat.scansun.view.ScansunGnuplot.GnuplotColors;
import pl.imgw.jrat.scansun.view.ScansunGnuplot.GnuplotCoordinates;
import pl.imgw.jrat.scansun.view.ScansunGnuplot.GnuplotFgBg;
import pl.imgw.jrat.scansun.view.ScansunGnuplot.GnuplotMonoColor;
import pl.imgw.jrat.scansun.view.ScansunGnuplot.GnuplotTerminal;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunSolarFluxPlot extends ScansunPlot {

	private static Log log = LogManager.getLogger();

	private SortedSet<LocalDate> days;

	static {
		PLOT_NAME = "solarfluxplot";
	}

	@Override
	public void generatePlot(File plotfile, GnuplotTerminal terminal,
			ScansunSite site, File datafile) {

		int i;

		ScansunGnuplot plot = new ScansunGnuplot();

		plot.setOutput(terminal, plotfile.getAbsolutePath(), "1024,768",
				GnuplotMonoColor.TRUECOLOR, "enhanced");

		String title = "Solar flux for " + site.getSiteName();
		String subTitle = "";
		plot.setTitle(title, subTitle, FONT_NAME, TITLE_FONT_SIZE);

		plot.setKey("bottom right", 2.5, FONT_NAME, KEY_FONT_SIZE);
		plot.setTics(GnuplotFgBg.FRONT);

		int xmin = days.first().getDayOfYear();
		int xmax = days.last().getDayOfYear();

		plot.setXLabel("Day of year", FONT_NAME, AXIS_LABEL_FONT_SIZE);
		plot.setXRange(xmin - 0.5, xmax + 0.5);
		plot.setXTics(xmin, 1, FONT_NAME, AXIS_TIC_FONT_SIZE);

		double ymin = 0.0;
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

		i = 0;
		for (i = 0; i < days.size(); i++) {
			double x1 = 0.0 + (1.0 / days.size()) * i;
			double x2 = x1 + (1.0 / days.size());

			String cmd = new String("set object ");
			cmd += (i + 1) + " rectangle behind ";
			cmd += "from " + GnuplotCoordinates.GRAPH + " " + x1 + ","
					+ GnuplotCoordinates.GRAPH + " " + 0.0 + " ";
			cmd += "to " + GnuplotCoordinates.GRAPH + " " + x2 + ","
					+ GnuplotCoordinates.GRAPH + " " + 1.0 + " ";
			plot.addExtra(cmd);

			double fillDensity = (i % 2 == 0) ? fillDensity1 : fillDensity2;
			cmd = new String("set obj ");
			cmd += (i + 1) + " fillstyle solid noborder " + fillDensity
					+ " fillcolor rgb 'grey'";

			plot.addExtra(cmd);

			plot.setLabelCenter(days.toArray()[i].toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, FONT_NAME,
					LABEL_FONT_SIZE, 90, GnuplotColors.MAROON);
		}

		i = 0;
		for (i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			// double x2 = x1 + (1.0 / days.size());
			double y2 = 1.0;

			plot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		plot.pushGraph(new Graph(datafile.getPath(), "2:21",
				Axes.NOT_SPECIFIED, "observed", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		plot.pushGraph(new Graph(datafile.getAbsolutePath(), "2:24",
				Axes.NOT_SPECIFIED, "DRAO", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD, PointType.SCREEN_SQUARE_DOT));

		try {
			plot.plot();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			log.printMsg(PLOT_NAME + " for " + site.getSiteName()
					+ "not generated!", Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return;
		}

		datafile.delete();

		log.printMsg(
				"New plot generated: " + PLOT_NAME + " for "
						+ site.getSiteName(), Log.TYPE_WARNING,
				Log.MODE_VERBOSE);

	}

	@Override
	public void printResults(ScansunGnuplotResultPrinter printer,
			ScansunSite site) {

		printer.printResults(site);

		days = printer.sitedays.get(site);
	}

}
