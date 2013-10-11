/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.view;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jgnuplot.Axes;
import org.jgnuplot.Graph;
import org.jgnuplot.LineType;
import org.jgnuplot.PointType;
import org.jgnuplot.Style;
import org.joda.time.LocalDate;

import pl.imgw.jrat.scansun.data.ScansunEvent;
import pl.imgw.jrat.scansun.data.ScansunEventType;
import pl.imgw.jrat.scansun.data.ScansunMeanPowerCalibrationMode;
import pl.imgw.jrat.scansun.data.ScansunPulseDuration;
import pl.imgw.jrat.scansun.data.ScansunSite;
import pl.imgw.jrat.scansun.view.ScansunGnuplot.GnuplotColors;
import pl.imgw.jrat.scansun.view.ScansunGnuplot.GnuplotCoordinates;
import pl.imgw.jrat.scansun.view.ScansunGnuplot.GnuplotMonoColor;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.FONT_NAME;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.TITLE_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.KEY_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.AXIS_LABEL_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.AXIS_TIC_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.LABEL_FONT_SIZE;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunScatterplot extends ScansunPlot {

	private static Log log = LogManager.getLogger();

	private int eventsTotalNumber = 0;
	private int solarRaysTotalNumber = 0;
	private double meanSolarRaysPower = 0.0;

	static {
		PLOT_NAME = "scatterplot";
	}

	@Override
	public void generatePlot(File plotfile,
			ScansunGnuplot.GnuplotTerminal terminal, ScansunSite site,
			File datafile) {

		ScansunGnuplot plot = new ScansunGnuplot();

		plot.setOutput(terminal, plotfile.getAbsolutePath(), "1024,768",
				GnuplotMonoColor.TRUECOLOR, "enhanced");

		String title = "Offset scatterplot for " + site.getSiteName();
		String subTitle = "";
		plot.setTitle(title, subTitle, FONT_NAME, TITLE_FONT_SIZE);

		plot.setKey("bottom right", 2.5, FONT_NAME, KEY_FONT_SIZE);
		plot.setSquare();

		double xmin = -2.6;
		double xmax = 2.6;

		plot.setXLabel("(Radar azimuth) - (Sun azimuth) [deg]", FONT_NAME,
				AXIS_LABEL_FONT_SIZE, 0, -1);
		plot.setXRange(xmin, xmax);
		plot.setXTics(0.2, FONT_NAME, AXIS_TIC_FONT_SIZE, 90);
		plot.setFormatX("%.1f");

		double ymin = -2.6;
		double ymax = 2.6;

		plot.setYLabel("(Radar elevation) - (Sun elevation) [deg]", FONT_NAME,
				AXIS_LABEL_FONT_SIZE);
		plot.setYRange(ymin, ymax);
		plot.setYTics(0.2, FONT_NAME, AXIS_TIC_FONT_SIZE);
		plot.setFormatY("%.1f");

		plot.setGrid("", 4, 0.75, GnuplotColors.GRAY);

		plot.setLabel("N = " + eventsTotalNumber, GnuplotCoordinates.GRAPH,
				0.1, GnuplotCoordinates.GRAPH, 0.9, FONT_NAME, LABEL_FONT_SIZE);
		plot.setLabel("n = " + solarRaysTotalNumber, GnuplotCoordinates.GRAPH,
				0.1, GnuplotCoordinates.GRAPH, 0.85, FONT_NAME, LABEL_FONT_SIZE);

		plot.addVariable("meanPower", meanSolarRaysPower);

		plot.setParametric();

		plot.pushGraph(new Graph(datafile.getPath(), "(stringcolumn(6) eq '"
				+ ScansunEventType.NON_SOLAR + "'?($7-$9):1/0):($8-$10)",
				Axes.NOT_SPECIFIED, "no hit", Style.POINTS,
				LineType.SCREEN_BLACK_DOTS, PointType.SCREEN_CIRCLE_DOT));

		plot.pushGraph(new Graph(datafile.getPath(), "(stringcolumn(6) eq '"
				+ ScansunEventType.SOLAR_RAY + "' && $13 < "
				+ meanSolarRaysPower + " && stringcolumn(11) eq '"
				+ ScansunPulseDuration.SHORT + "' ?($7-$9):1/0):($8-$10)",
				Axes.NOT_SPECIFIED, "weak short scan", Style.POINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_CIRCLE_FILLED));

		plot.pushGraph(new Graph(datafile.getPath(), "(stringcolumn(6) eq '"
				+ ScansunEventType.SOLAR_RAY + "' && $13 < "
				+ meanSolarRaysPower + " && stringcolumn(11) eq '"
				+ ScansunPulseDuration.LONG + "' ?($7-$9):1/0):($8-$10)",
				Axes.NOT_SPECIFIED, "weak long scan", Style.POINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		plot.pushGraph(new Graph(datafile.getPath(), "(stringcolumn(6) eq '"
				+ ScansunEventType.SOLAR_RAY + "' && $13 > "
				+ meanSolarRaysPower + " && stringcolumn(11) eq '"
				+ ScansunPulseDuration.SHORT + "' ?($7-$9):1/0):($8-$10)",
				Axes.NOT_SPECIFIED, "strong short scan", Style.POINTS,
				LineType.SCREEN_RED, PointType.SCREEN_CIRCLE_FILLED));

		plot.pushGraph(new Graph(datafile.getPath(), "(stringcolumn(6) eq '"
				+ ScansunEventType.SOLAR_RAY + "' && $13 > "
				+ meanSolarRaysPower + " && stringcolumn(11) eq '"
				+ ScansunPulseDuration.LONG + "' ?($7-$9):1/0):($8-$10)",
				Axes.NOT_SPECIFIED, "strong long scan", Style.POINTS,
				LineType.SCREEN_RED, PointType.SCREEN_SQUARE_FILLED));

		plot.pushGraph(new Graph("t,0", Axes.NOT_SPECIFIED, "", Style.LINES,
				LineType.SCREEN_BLACK_DOTS, PointType.NOT_SPECIFIED));
		plot.pushGraph(new Graph("0,t", Axes.NOT_SPECIFIED, "", Style.LINES,
				LineType.SCREEN_BLACK_DOTS, PointType.NOT_SPECIFIED));

		try {
			plot.plot();
		} catch (IOException e) {
			e.printStackTrace();
			log.printMsg(PLOT_NAME + " for " + site.getSiteName()
					+ "not generated!", Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return;
		} catch (InterruptedException e) {
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

		printer.printRawResults(site);

		Map<LocalDate, Set<ScansunEvent>> eventsMap = printer
				.getEventsContainer()
				.byEventType(ScansunEventType.NON_SOLAR)
				.byMeanPowerCalibrationMode(
						ScansunMeanPowerCalibrationMode.CALIBRATED).asMap()
				.get(site);

		eventsTotalNumber = 0;
		for (LocalDate localDate : eventsMap.keySet()) {
			eventsTotalNumber += eventsMap.get(localDate).size();
		}

		Map<LocalDate, Set<ScansunEvent>> solarRaysMap = printer
				.getEventsContainer()
				.byEventType(ScansunEventType.SOLAR_RAY)
				.byMeanPowerCalibrationMode(
						ScansunMeanPowerCalibrationMode.CALIBRATED).asMap()
				.get(site);

		solarRaysTotalNumber = 0;
		meanSolarRaysPower = 0.0;
		for (LocalDate localDate : eventsMap.keySet()) {
			solarRaysTotalNumber += solarRaysMap != null ? solarRaysMap.get(
					localDate).size() : 0;

			Iterator<ScansunEvent> itr = solarRaysMap != null ? solarRaysMap
					.get(localDate).iterator() : null;

			while (itr != null && itr.hasNext()) {
				meanSolarRaysPower += ((ScansunEvent) itr.next())
						.getMeanPower();
			}
		}

		if (solarRaysTotalNumber != 0) {
			meanSolarRaysPower /= solarRaysTotalNumber;
		}

	}
}
