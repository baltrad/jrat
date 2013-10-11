/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.view;

import static pl.imgw.jrat.scansun.view.ScansunGnuplot.FONT_NAME;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.TITLE_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.KEY_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.AXIS_LABEL_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.AXIS_TIC_FONT_SIZE;
import static pl.imgw.jrat.scansun.view.ScansunGnuplot.LABEL_FONT_SIZE;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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
public class ScansunHistogram extends ScansunPlot {

	private static Log log = LogManager.getLogger();

	private SortedSet<LocalDate> days;
	Map<LocalDate, Integer> eventsNumberByDayFromShortScan = new HashMap<LocalDate, Integer>();
	Map<LocalDate, Integer> eventsNumberByDayFromLongScan = new HashMap<LocalDate, Integer>();
	Map<LocalDate, Integer> solarRaysNumberByDayFromShortScan = new HashMap<LocalDate, Integer>();
	Map<LocalDate, Integer> solarRaysNumberByDayFromLongScan = new HashMap<LocalDate, Integer>();

	static {
		PLOT_NAME = "histogram";
	}

	@Override
	public void generatePlot(File plotfile, GnuplotTerminal terminal,
			ScansunSite site, File datafile) {

		int i;

		ScansunGnuplot plot = new ScansunGnuplot();

		plot.setOutput(terminal, plotfile.getAbsolutePath(), "1024,768",
				GnuplotMonoColor.TRUECOLOR, "enhanced");

		String title = "Detection histogram for " + site.getSiteName();
		String subTitle = "";
		plot.setTitle(title, subTitle, FONT_NAME, TITLE_FONT_SIZE);

		plot.setKey("", 2.5, FONT_NAME, KEY_FONT_SIZE);
		plot.setTics(GnuplotFgBg.FRONT);

		int xmin = days.first().getDayOfYear();
		int xmax = days.last().getDayOfYear();

		plot.setXLabel("Day of year", FONT_NAME, AXIS_LABEL_FONT_SIZE);
		plot.setXRange(xmin - 0.5, xmax + 0.5);
		plot.setXTics(xmin, 1, FONT_NAME, AXIS_TIC_FONT_SIZE);

		int ymin = 0;
		// int ymax = ((int) Math.ceil(getDetectionHistogramMax(siteName,
		// EventType.EVENT) / 100) + 3) * 100;
		int ymax = 400;
		double yDate = 0.8;

		plot.setYLabel("Number of detections", FONT_NAME, AXIS_LABEL_FONT_SIZE);
		plot.setYRange(ymin, ymax);
		plot.setYTics(ymin, 50, FONT_NAME, AXIS_TIC_FONT_SIZE);
		plot.setMYTics(2);

		// plot.setGrid("ytics", 4, 0.25, GnuplotColors.GRAY);

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

		DecimalFormat df = new DecimalFormat("#.##");

		i = 0;
		for (LocalDate localDate : days) {
			double x1 = 0.0 + (1.0 / days.size()) * i;
			double x2 = x1 + (1.0 / days.size());

			Integer eventsNumberFromShortScan = eventsNumberByDayFromShortScan
					.get(localDate);
			Integer eventsNumberFromLongScan = eventsNumberByDayFromLongScan
					.get(localDate);
			int eventsNumber = eventsNumberFromShortScan
					+ eventsNumberFromLongScan;

			Integer solarRaysNumberFromShortScan = 0;
			if (solarRaysNumberByDayFromShortScan.get(localDate) != null) {
				solarRaysNumberFromShortScan += solarRaysNumberByDayFromShortScan
						.get(localDate);
			}
			Integer solarRaysNumberFromLongScan = 0;
			if (solarRaysNumberByDayFromLongScan.get(localDate) != null) {
				solarRaysNumberFromLongScan += solarRaysNumberByDayFromLongScan
						.get(localDate);
			}
			int solarRaysNumber = solarRaysNumberFromShortScan
					+ solarRaysNumberFromLongScan;

			double efficiency = ((double) solarRaysNumber / eventsNumber) * 100.0;

			GnuplotColors color;
			if (efficiency < 5.0) {
				color = GnuplotColors.RED;
			} else {
				color = GnuplotColors.GREEN;
			}

			plot.setLabelCenter(df.format(efficiency),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate - 0.2, FONT_NAME,
					LABEL_FONT_SIZE, 0, color);

			/*
			 * plot.setLabelCenter(eventsNumber.toString(),
			 * GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
			 * GnuplotCoordinates.GRAPH, 0.85, font, labelFontSize, 0,
			 * GnuplotColors.BLACK);
			 */

			plot.setLabelCenter(eventsNumberFromLongScan.toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, 0.45, FONT_NAME, LABEL_FONT_SIZE,
					0, GnuplotColors.BLACK);
			plot.setLabelCenter(eventsNumberFromShortScan.toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, 0.4, FONT_NAME, LABEL_FONT_SIZE,
					0, GnuplotColors.BLACK);

			/*
			 * plot.setLabelCenter(solarRaysNumber.toString(),
			 * GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
			 * GnuplotCoordinates.GRAPH, 0.6, font, labelFontSize, 0,
			 * GnuplotColors.RED);
			 */
			plot.setLabelCenter(solarRaysNumberFromLongScan.toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, 0.2, FONT_NAME, LABEL_FONT_SIZE,
					0, GnuplotColors.RED);
			plot.setLabelCenter(solarRaysNumberFromShortScan.toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, 0.15, FONT_NAME, LABEL_FONT_SIZE,
					0, GnuplotColors.RED);

			i++;
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

		plot.pushGraph(new Graph(datafile.getPath(), "2:3", Axes.NOT_SPECIFIED,
				"Expected long scan", Style.BOXES,
				LineType.SCREEN_BLACK_SOLID_BOLD, PointType.NOT_SPECIFIED));

		plot.pushGraph(new Graph(datafile.getPath(), "2:4", Axes.NOT_SPECIFIED,
				"Expected short scan", Style.BOXES, LineType.SCREEN_BROWN,
				PointType.NOT_SPECIFIED));

		plot.pushGraph(new Graph(datafile.getPath(), "2:6", Axes.NOT_SPECIFIED,
				"Detected long scan", Style.BOXES, LineType.SCREEN_RED,
				PointType.NOT_SPECIFIED));

		plot.pushGraph(new Graph(datafile.getPath(), "2:7", Axes.NOT_SPECIFIED,
				"Detected short scan", Style.BOXES, LineType.SCREEN_SALMON,
				PointType.NOT_SPECIFIED));

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

		printer.printResults(site);

		days = printer.sitedays.get(site);

		for (LocalDate localDate : days) {
			eventsNumberByDayFromShortScan.put(
					localDate,
					printer.eventsContainer
							.getEvents()
							.byEventType(ScansunEventType.NON_SOLAR)
							.byPulseDuration(ScansunPulseDuration.SHORT)
							.byMeanPowerCalibrationMode(
									ScansunMeanPowerCalibrationMode.CALIBRATED)
							.asMap().get(site).get(localDate).size());

			eventsNumberByDayFromLongScan.put(
					localDate,
					printer.eventsContainer
							.getEvents()
							.byEventType(ScansunEventType.NON_SOLAR)
							.byPulseDuration(ScansunPulseDuration.LONG)
							.byMeanPowerCalibrationMode(
									ScansunMeanPowerCalibrationMode.CALIBRATED)
							.asMap().get(site).get(localDate).size());

			Map<LocalDate, Set<ScansunEvent>> solarRaysShort = printer.eventsContainer
					.getEvents()
					.byEventType(ScansunEventType.SOLAR_RAY)
					.byPulseDuration(ScansunPulseDuration.SHORT)
					.byMeanPowerCalibrationMode(
							ScansunMeanPowerCalibrationMode.CALIBRATED).asMap()
					.get(site);
			solarRaysNumberByDayFromShortScan.put(localDate,
					solarRaysShort != null ? solarRaysShort.get(localDate)
							.size() : 0);

			Map<LocalDate, Set<ScansunEvent>> solarRaysLong = printer.eventsContainer
					.getEvents()
					.byEventType(ScansunEventType.SOLAR_RAY)
					.byPulseDuration(ScansunPulseDuration.LONG)
					.byMeanPowerCalibrationMode(
							ScansunMeanPowerCalibrationMode.CALIBRATED).asMap()
					.get(site);

			solarRaysNumberByDayFromLongScan.put(localDate,
					solarRaysLong != null ? solarRaysLong.get(localDate).size()
							: 0);
		}

	}

}
