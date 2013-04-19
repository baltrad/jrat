/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.NORMAL;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.naming.directory.InvalidAttributesException;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.scansun.ScansunPlot.*;
import pl.imgw.jrat.scansun.ScansunConstants.*;
import pl.imgw.jrat.tools.out.*;

import org.jgnuplot.*;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunGnuplotResultsPrinter extends ScansunResultsPrinter {

	private static final String font = "Helvetica Bold";
	private static final int titleFontSize = 32;
	private static final int keyFontSize = 14;
	private static final int axisLabelFontSize = 20;
	private static final int axisTicFontSize = 14;
	private static final int labelFontSize = 18;

	// static {
	// File f = new File(AplicationConstans.TMP);
	// if (!f.exists()) {
	// f.mkdirs();
	// }
	// }

	public ScansunGnuplotResultsPrinter(ScansunResultParsedParameters params) {
		super(params);
	}

	public void generatePlots() {

		try {

			for (String siteName : getResultsSiteNames()) {
				generateScatterplot(siteName, getResultsFiles());
				generateEventsScatterplot(siteName, getResultsFiles());
				generateSolarRaysScatterplot(siteName, getResultsFiles());

				generateTimeHistogram(siteName, getResultsFiles());
				generateElevationHistogram(siteName, getResultsFiles());
				generateAzimuthHistogram(siteName, getResultsFiles());

				generateDetectionHistogram(siteName, getResultsFiles());
				generateDetectionTimePlot(siteName, getResultsFiles());
				generateDetectionElevationPlot(siteName, getResultsFiles());
				generateDetectionAzimuthPlot(siteName, getResultsFiles());
				generateDetectionPowerPlot(siteName, getResultsFiles());
				generateCoefficientsPlots(siteName, getResultsFiles());
				generateSolarFluxPlot(siteName, getResultsFiles());
			}
		} catch (InterruptedException e) {
			LogHandler.getLogs().displayMsg("generatePlots error", ERROR);
			LogHandler.getLogs().saveErrorLogs(this, e);
			return;
		} catch (InvalidAttributesException e) {
			LogHandler.getLogs().displayMsg("generatePlots error", ERROR);
			LogHandler.getLogs().saveErrorLogs(this, e);
			return;
		} catch (IOException e) {
			LogHandler.getLogs().displayMsg("generatePlots error", ERROR);
			LogHandler.getLogs().saveErrorLogs(this, e);
			return;
		}

	}

	private void generateScatterplot(String siteName, Set<File> files)
			throws IOException, InterruptedException,
			InvalidAttributesException {

		File data = new File(AplicationConstans.TMP, "scatterplot_" + siteName
				+ ".data");

		ResultPrinter pr;

		pr = new FileResultPrinter(data);
		ResultPrinterManager.getManager().setPrinter(pr);
		printRawResults(siteName, files);

		ScansunPlot plot = new ScansunPlot();

		String filename = "scatterplot_" + siteName + ".eps";
		plot.setOutput(GnuplotTerminal.POSTSCRIPT,
				(new File(ScansunFileHandler.getScansunPath(), filename))
						.getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
				"eps enhanced");

		String title = "Offset scatterplot for " + siteName;
		// String subTitle = "h_{min} = XX" + " rmin = XX" + " tf = XX"
		// + " ad = XX";
		String subTitle = "";

		plot.setTitle(title, subTitle, font, titleFontSize);

		plot.setKey("bottom right", 2.5, font, keyFontSize);
		plot.setSquare();

		double xmin = -2.6;
		double xmax = 2.6;

		plot.setXLabel("(Radar azimuth) - (Sun azimuth) [deg]", font,
				axisLabelFontSize, 0, -1);
		plot.setXRange(xmin, xmax);
		plot.setXTics(0.2, font, axisTicFontSize, 90);
		plot.setFormatX("%.1f");

		double ymin = -2.6;
		double ymax = 2.6;

		plot.setYLabel("(Radar elevation) - (Sun elevation) [deg]", font,
				axisLabelFontSize);
		plot.setYRange(ymin, ymax);
		plot.setYTics(0.2, font, axisTicFontSize);
		plot.setFormatY("%.1f");

		plot.setGrid("", 4, 0.75, GnuplotColors.GRAY);

		int N = getTotalNumber(siteName, files, EventType.EVENT);
		plot.setLabel("N = " + N, GnuplotCoordinates.GRAPH, 0.1,
				GnuplotCoordinates.GRAPH, 0.9, font, labelFontSize);
		int n = getTotalNumber(siteName, files, EventType.SOLARRAY);
		plot.setLabel("n = " + n, GnuplotCoordinates.GRAPH, 0.1,
				GnuplotCoordinates.GRAPH, 0.85, font, labelFontSize);

		double meanPower = getMeanSolarPower(siteName, files);
		// String meanPowerName = "meanPower";
		// plot.addVariable(meanPowerName, meanPower);

		plot.setParametric();

		plot.pushGraph(new Graph(data.getAbsolutePath(),
				"(stringcolumn(7) eq 'false'?($2-$4):1/0):($1-$3)",
				Axes.NOT_SPECIFIED, "no hit", Style.POINTS,
				LineType.SCREEN_BLACK_DOTS, PointType.POSTSCRIPT_CIRCLE_DOT));

		plot.pushGraph(new Graph(
				data.getAbsolutePath(),
				"(stringcolumn(7) eq 'true' && $5 < "
						+ meanPower
						+ " && stringcolumn(12) eq 'short' ?($2-$4):1/0):($1-$3)",
				Axes.NOT_SPECIFIED, "weak 125 solar rays", Style.POINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_CIRCLE_FILLED));

		plot.pushGraph(new Graph(
				data.getAbsolutePath(),
				"(stringcolumn(7) eq 'true' && $5 < "
						+ meanPower
						+ " && stringcolumn(12) eq 'long' ?($2-$4):1/0):($1-$3)",
				Axes.NOT_SPECIFIED, "weak 250 solar rays", Style.POINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		plot.pushGraph(new Graph(
				data.getAbsolutePath(),
				"(stringcolumn(7) eq 'true' && $5 > "
						+ meanPower
						+ " && stringcolumn(12) eq 'short' ?($2-$4):1/0):($1-$3)",
				Axes.NOT_SPECIFIED, "strong 125 solar rays", Style.POINTS,
				LineType.SCREEN_RED, PointType.SCREEN_CIRCLE_FILLED));

		plot.pushGraph(new Graph(
				data.getAbsolutePath(),
				"(stringcolumn(7) eq 'true' && $5 > "
						+ meanPower
						+ " && stringcolumn(12) eq 'long' ?($2-$4):1/0):($1-$3)",
				Axes.NOT_SPECIFIED, "strong 250 solar rays", Style.POINTS,
				LineType.SCREEN_RED, PointType.SCREEN_SQUARE_FILLED));

		plot.pushGraph(new Graph("t,0", Axes.NOT_SPECIFIED, "", Style.LINES,
				LineType.SCREEN_BLACK_DOTS, PointType.NOT_SPECIFIED));
		plot.pushGraph(new Graph("0,t", Axes.NOT_SPECIFIED, "", Style.LINES,
				LineType.SCREEN_BLACK_DOTS, PointType.NOT_SPECIFIED));

		plot.plot();

		data.delete();

		LogHandler.getLogs().displayMsg(
				"New plot generated: scatterplot for " + siteName, NORMAL);
	}

	public void generateEventsScatterplot(String siteName, Set<File> files)
			throws IOException {

	}

	public void generateSolarRaysScatterplot(String siteName, Set<File> files)
			throws IOException {

	}

	public void generateDetectionHistogram(String siteName, Set<File> files)
			throws IOException, InterruptedException {

		File data = new File(AplicationConstans.TMP, "solar_rays_histogram_"
				+ siteName + ".data");

		ResultPrinter pr;

		pr = new FileResultPrinter(data);
		ResultPrinterManager.getManager().setPrinter(pr);
		printResults(siteName, files);

		ScansunPlot plot = new ScansunPlot();

		String filename = "solar_rays_histogram_" + siteName + ".eps";
		plot.setOutput(GnuplotTerminal.POSTSCRIPT,
				(new File(ScansunFileHandler.getScansunPath(), filename))
						.getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
				"eps enhanced");

		String title = "Detection histogram for " + siteName;
		String subTitle = "";
		plot.setTitle(title, subTitle, font, titleFontSize);

		plot.setKey("", 2.5, font, keyFontSize);
		plot.setTics(GnuplotFgBg.FRONT);

		Set<ScansunDay> days = getDays(siteName, files);

		int xmin = ((ScansunDay) days.toArray()[0]).getDayOfYear();
		int xmax = ((ScansunDay) days.toArray()[days.size() - 1])
				.getDayOfYear();

		plot.setXLabel("Day of year", font, axisLabelFontSize);
		plot.setXRange(xmin - 0.5, xmax + 0.5);
		plot.setXTics(xmin, 1, font, axisTicFontSize);

		int ymin = 0;
		int ymax = ((int) Math.ceil(getDetectionHistogramMax(siteName, files,
				EventType.EVENT) / 100) + 4) * 100;
		double yDate = 0.8;

		plot.setYLabel("Number of detections", font, axisLabelFontSize);
		plot.setYRange(ymin, ymax);
		plot.setYTics(ymin, 20, font, axisTicFontSize);
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

			plot.setLabelCenter(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		DecimalFormat df = new DecimalFormat("#.##");

		for (int i = 0; i < days.size(); i++) {
			double x1 = 0.0 + (1.0 / days.size()) * i;
			double x2 = x1 + (1.0 / days.size());

			Integer eventsNumber125 = getDetectionHistogram(siteName, files,
					EventType.EVENT, PulseDuration.SHORT).get(
					((ScansunDay) days.toArray()[i]));
			Integer eventsNumber250 = getDetectionHistogram(siteName, files,
					EventType.EVENT, PulseDuration.LONG).get(
					((ScansunDay) days.toArray()[i]));
			Integer eventsNumber = eventsNumber125 + eventsNumber250;

			Integer solarRaysNumber125 = getDetectionHistogram(siteName, files,
					EventType.SOLARRAY, PulseDuration.SHORT).get(
					((ScansunDay) days.toArray()[i]));
			Integer solarRaysNumber250 = getDetectionHistogram(siteName, files,
					EventType.SOLARRAY, PulseDuration.LONG).get(
					((ScansunDay) days.toArray()[i]));
			Integer solarRaysNumber = solarRaysNumber125 + solarRaysNumber250;

			Double efficiency = ((double) solarRaysNumber / eventsNumber) * 100.0;

			GnuplotColors color;
			if (efficiency < 5.0) {
				color = GnuplotColors.RED;
			} else {
				color = GnuplotColors.GREEN;
			}

			plot.setLabelCenter(df.format(efficiency),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate - 0.2, font, labelFontSize,
					0, color);
			/*
			 * plot.setLabelCenter(eventsNumber.toString(),
			 * GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
			 * GnuplotCoordinates.GRAPH, 0.85, font, labelFontSize, 0,
			 * GnuplotColors.BLACK);
			 */

			plot.setLabelCenter(eventsNumber250.toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, 0.45, font, labelFontSize, 0,
					GnuplotColors.BLACK);
			plot.setLabelCenter(eventsNumber125.toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, 0.4, font, labelFontSize, 0,
					GnuplotColors.BLACK);

			/*
			 * plot.setLabelCenter(solarRaysNumber.toString(),
			 * GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
			 * GnuplotCoordinates.GRAPH, 0.6, font, labelFontSize, 0,
			 * GnuplotColors.RED);
			 */
			plot.setLabelCenter(solarRaysNumber250.toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, 0.2, font, labelFontSize, 0,
					GnuplotColors.RED);
			plot.setLabelCenter(solarRaysNumber125.toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, 0.15, font, labelFontSize, 0,
					GnuplotColors.RED);
		}

		for (int i = 1; i < days.size(); i++) {
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

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:3",
				Axes.NOT_SPECIFIED, "Expected 250", Style.BOXES,
				LineType.SCREEN_BLACK_SOLID_BOLD, PointType.NOT_SPECIFIED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:4",
				Axes.NOT_SPECIFIED, "Expected 125", Style.BOXES,
				LineType.SCREEN_BROWN, PointType.NOT_SPECIFIED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:6",
				Axes.NOT_SPECIFIED, "Detected 250", Style.BOXES,
				LineType.SCREEN_RED, PointType.NOT_SPECIFIED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:7",
				Axes.NOT_SPECIFIED, "Detected 125", Style.BOXES,
				LineType.SCREEN_SALMON, PointType.NOT_SPECIFIED));

		plot.plot();

		data.delete();

		LogHandler.getLogs().displayMsg(
				"New plot generated: solar rays histogram for " + siteName,
				NORMAL);
	}

	public void generateDetectionTimePlot(String siteName, Set<File> files)
			throws IOException, InterruptedException {
		File data = new File(AplicationConstans.TMP, "time_plot_" + siteName
				+ ".data");

		ResultPrinter pr;

		pr = new FileResultPrinter(data);
		ResultPrinterManager.getManager().setPrinter(pr);
		printResults(siteName, files);

		ScansunPlot plot = new ScansunPlot();

		String filename = "time_plot_" + siteName + ".eps";
		plot.setOutput(GnuplotTerminal.POSTSCRIPT,
				(new File(ScansunFileHandler.getScansunPath(), filename))
						.getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
				"eps enhanced");

		String title = "Detection time for " + siteName;
		String subTitle = "";
		plot.setTitle(title, subTitle, font, titleFontSize);

		plot.setKey("bottom right", 2.5, font, keyFontSize);
		plot.setTics(GnuplotFgBg.FRONT);

		Set<ScansunDay> days = getDays(siteName, files);

		int xmin = ((ScansunDay) days.toArray()[0]).getDayOfYear();
		int xmax = ((ScansunDay) days.toArray()[days.size() - 1])
				.getDayOfYear();

		plot.setXLabel("Day of year", font, axisLabelFontSize);
		plot.setXRange(xmin - 0.5, xmax + 0.5);
		plot.setXTics(xmin, 1, font, axisTicFontSize);

		double ymin = 0.0;
		double ymax = 24.0;
		double yDate = 0.8;

		plot.setYLabel("Time [h]", font, axisLabelFontSize);
		plot.setYRange(ymin, ymax);
		plot.setYTics(ymin, 1, font, axisTicFontSize);
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

			plot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			plot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:13",
				Axes.NOT_SPECIFIED, "sunset", Style.LINES,
				LineType.SCREEN_SALMON, PointType.NOT_SPECIFIED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:12",
				Axes.NOT_SPECIFIED, "last detection", Style.LINESPOINTS,
				LineType.SCREEN_RED, PointType.SCREEN_TRIANGLE_DOWN_FILLED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:10",
				Axes.NOT_SPECIFIED, "first detection", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_TRIANGLE_UP_FILLED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:11",
				Axes.NOT_SPECIFIED, "sunrise", Style.LINES,
				LineType.SCREEN_BLUE, PointType.NOT_SPECIFIED));

		plot.plot();

		data.delete();

		LogHandler.getLogs().displayMsg(
				"New plot generated: time plot for " + siteName, NORMAL);
	}

	public void generateDetectionElevationPlot(String siteName, Set<File> files)
			throws IOException, InterruptedException {

		File data = new File(AplicationConstans.TMP, "elevation_plot_"
				+ siteName + ".data");

		ResultPrinter pr;

		pr = new FileResultPrinter(data);
		ResultPrinterManager.getManager().setPrinter(pr);
		printResults(siteName, files);

		ScansunPlot plot = new ScansunPlot();

		String filename = "elevation_plot_" + siteName + ".eps";
		plot.setOutput(GnuplotTerminal.POSTSCRIPT,
				(new File(ScansunFileHandler.getScansunPath(), filename))
						.getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
				"eps enhanced");

		String title = "Detection elevation for " + siteName;
		String subTitle = "";
		plot.setTitle(title, subTitle, font, titleFontSize);

		plot.setKey("top right", 2.5, font, keyFontSize);
		plot.setTics(GnuplotFgBg.FRONT);

		Set<ScansunDay> days = getDays(siteName, files);

		int xmin = ((ScansunDay) days.toArray()[0]).getDayOfYear();
		int xmax = ((ScansunDay) days.toArray()[days.size() - 1])
				.getDayOfYear();

		plot.setXLabel("Day of year", font, axisLabelFontSize);
		plot.setXRange(xmin - 0.5, xmax + 0.5);
		plot.setXTics(xmin, 1, font, axisTicFontSize);

		double ymin = 0;
		double ymax = 45.0;
		double yDate = 0.7;

		plot.setYLabel("Elevation [deg]", font, axisLabelFontSize);
		plot.setYRange(ymin, ymax);
		plot.setYTics(ymin, 5, font, axisTicFontSize);
		plot.setMYTics(5);

		plot.setGrid("ytics", 4, 1, GnuplotColors.GRAY);

		plot.setStyleFillSolid(0.75);

		double fillDensity1 = 0.3;
		double fillDensity2 = 0.15;

		for (int i = 0; i < days.size(); i++) {
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

			plot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			plot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:14",
				Axes.NOT_SPECIFIED, "min", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_TRIANGLE_UP_FILLED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:15",
				Axes.NOT_SPECIFIED, "max", Style.LINESPOINTS,
				LineType.SCREEN_RED, PointType.SCREEN_TRIANGLE_DOWN_FILLED));

		plot.plot();

		data.delete();

		LogHandler.getLogs().displayMsg(
				"New plot generated: elevation plot for " + siteName, NORMAL);

	}

	public void generateDetectionAzimuthPlot(String siteName, Set<File> files)
			throws IOException, InterruptedException {

		File data = new File(AplicationConstans.TMP, "azimuth_plot_" + siteName
				+ ".data");

		ResultPrinter pr;

		pr = new FileResultPrinter(data);
		ResultPrinterManager.getManager().setPrinter(pr);
		printResults(siteName, files);

		ScansunPlot plot = new ScansunPlot();

		String filename = "azimuth_plot_" + siteName + ".eps";
		plot.setOutput(GnuplotTerminal.POSTSCRIPT,
				(new File(ScansunFileHandler.getScansunPath(), filename))
						.getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
				"eps enhanced");

		String title = "Detection azimuth for " + siteName;
		String subTitle = "";
		plot.setTitle(title, subTitle, font, titleFontSize);

		plot.setKey("bottom right", 2.5, font, keyFontSize);
		plot.setTics(GnuplotFgBg.FRONT);

		Set<ScansunDay> days = getDays(siteName, files);

		int xmin = ((ScansunDay) days.toArray()[0]).getDayOfYear();
		int xmax = ((ScansunDay) days.toArray()[days.size() - 1])
				.getDayOfYear();

		plot.setXLabel("Day of year", font, axisLabelFontSize);
		plot.setXRange(xmin - 0.5, xmax + 0.5);
		plot.setXTics(xmin, 1, font, axisTicFontSize);

		double ymin = 0.0;
		double ymax = 360.0;
		double yDate = 0.8;

		plot.setYLabel("Azimuth [deg]", font, axisLabelFontSize);
		plot.setYRange(ymin, ymax);
		plot.setYTics(ymin, 20, font, axisTicFontSize);
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

			plot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			plot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:19",
				Axes.NOT_SPECIFIED, "sunset", Style.LINES,
				LineType.SCREEN_SALMON, PointType.NOT_SPECIFIED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:18",
				Axes.NOT_SPECIFIED, "max", Style.LINESPOINTS,
				LineType.SCREEN_RED, PointType.SCREEN_TRIANGLE_DOWN_FILLED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:16",
				Axes.NOT_SPECIFIED, "min", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_TRIANGLE_UP_FILLED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:17",
				Axes.NOT_SPECIFIED, "sunrise", Style.LINES,
				LineType.SCREEN_BLUE, PointType.NOT_SPECIFIED));

		plot.plot();

		data.delete();

		LogHandler.getLogs().displayMsg(
				"New plot generated: azimuth plot for " + siteName, NORMAL);
	}

	public void generateDetectionPowerPlot(String siteName, Set<File> files)
			throws IOException, InterruptedException {

		File data = new File(AplicationConstans.TMP, "power_plot_" + siteName
				+ ".data");

		ResultPrinter pr;

		pr = new FileResultPrinter(data);
		ResultPrinterManager.getManager().setPrinter(pr);
		printResults(siteName, files);

		ScansunPlot plot = new ScansunPlot();

		String filename = "power_plot_" + siteName + ".eps";
		plot.setOutput(GnuplotTerminal.POSTSCRIPT,
				(new File(ScansunFileHandler.getScansunPath(), filename))
						.getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
				"eps enhanced");

		String title = "Power for " + siteName;
		String subTitle = "";
		plot.setTitle(title, subTitle, font, titleFontSize);

		plot.setKey("bottom right", 2.5, font, keyFontSize);
		plot.setTics(GnuplotFgBg.FRONT);

		Set<ScansunDay> days = getDays(siteName, files);

		int xmin = ((ScansunDay) days.toArray()[0]).getDayOfYear();
		int xmax = ((ScansunDay) days.toArray()[days.size() - 1])
				.getDayOfYear();

		plot.setXLabel("Day of year", font, axisLabelFontSize);
		plot.setXRange(xmin - 0.5, xmax + 0.5);
		plot.setXTics(xmin, 1, font, axisTicFontSize);

		double ymin = (Math
				.floor((getMinSolarPower(siteName, files) - 20.0) / 5.0) + 1.0) * 5.0;
		double ymax = (Math
				.floor((getMaxSolarPower(siteName, files) + 20.0) / 5.0)) * 5.0;
		double yDate = 0.8;

		plot.setYLabel("Power [dBm]", font, axisLabelFontSize);
		plot.setYRange(ymin, ymax);
		plot.setYTics(ymin, 5, font, axisTicFontSize);
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

			plot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			plot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:20:21",
				Axes.NOT_SPECIFIED, "", Style.FILLEDCURVES,
				LineType.SCREEN_BROWN, PointType.NOT_SPECIFIED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:21",
				Axes.NOT_SPECIFIED, "max", Style.LINESPOINTS,
				LineType.SCREEN_RED, PointType.SCREEN_TRIANGLE_DOWN_FILLED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:22",
				Axes.NOT_SPECIFIED, "mean", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:20",
				Axes.NOT_SPECIFIED, "min", Style.LINESPOINTS,
				LineType.SCREEN_GREEN, PointType.SCREEN_TRIANGLE_UP_FILLED));

		plot.plot();

		data.delete();

		LogHandler.getLogs().displayMsg(
				"New plot generated: power plot for " + siteName, NORMAL);
	}

	public void generateCoefficientsPlots(String siteName, Set<File> files)
			throws IOException, InterruptedException {

		File data = new File(AplicationConstans.TMP, "coefficients_plot_"
				+ siteName + ".data");

		ResultPrinter pr;

		pr = new FileResultPrinter(data);
		ResultPrinterManager.getManager().setPrinter(pr);
		printResults(siteName, files);

		Set<ScansunDay> days = getDays(siteName, files);

		int xmin = ((ScansunDay) days.toArray()[0]).getDayOfYear();
		int xmax = ((ScansunDay) days.toArray()[days.size() - 1])
				.getDayOfYear();

		double yDate = 0.8;
		double fillDensity1 = 0.3;
		double fillDensity2 = 0.15;

		// 1
		ScansunPlot axPlot = new ScansunPlot();

		String axPlotFilename = "ax_plot_" + siteName + ".eps";
		axPlot.setOutput(GnuplotTerminal.POSTSCRIPT, (new File(
				ScansunFileHandler.getScansunPath(), axPlotFilename))
				.getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
				"eps enhanced");

		axPlot.setTitle("a_{x} for " + siteName, "", font, titleFontSize);

		axPlot.setKey("bottom right", 2.5, font, keyFontSize);
		axPlot.setTics(GnuplotFgBg.FRONT);

		axPlot.setXLabel("Day of year", font, axisLabelFontSize);
		axPlot.setXRange(xmin - 0.5, xmax + 0.5);
		axPlot.setXTics(xmin, 1, font, axisTicFontSize);

		// double ymin = 0.0;
		// double ymax = 150.0;

		axPlot.setYLabel("a_{x}", font, axisLabelFontSize);
		// plot.setYRange(ymin, ymax);
		// plot.setYTics(ymin, 5, font, axisTicFontSize);
		axPlot.setMYTics(2);

		axPlot.setGrid("ytics", 4, 1, GnuplotColors.GRAY);

		axPlot.setStyleFillSolid(0.75);

		for (int i = 0; i < days.size(); i++) {
			double x1 = 0.0 + (1.0 / days.size()) * i;
			double x2 = x1 + (1.0 / days.size());

			String cmd = new String("set object ");
			cmd += (i + 1) + " rectangle behind ";
			cmd += "from " + GnuplotCoordinates.GRAPH + " " + x1 + ","
					+ GnuplotCoordinates.GRAPH + " " + 0.0 + " ";
			cmd += "to " + GnuplotCoordinates.GRAPH + " " + x2 + ","
					+ GnuplotCoordinates.GRAPH + " " + 1.0 + " ";
			axPlot.addExtra(cmd);

			double fillDensity = (i % 2 == 0) ? fillDensity1 : fillDensity2;
			cmd = new String("set obj ");
			cmd += (i + 1) + " fillstyle solid noborder " + fillDensity
					+ " fillcolor rgb 'grey'";

			axPlot.addExtra(cmd);

			axPlot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			axPlot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		axPlot.pushGraph(new Graph(data.getAbsolutePath(), "2:23",
				Axes.NOT_SPECIFIED, "", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		axPlot.plot();

		LogHandler.getLogs().displayMsg(
				"New plot generated: ax plot for " + siteName, NORMAL);

		// 2
		ScansunPlot ayPlot = new ScansunPlot();

		ayPlot.setOutput(GnuplotTerminal.POSTSCRIPT, (new File(
				ScansunFileHandler.getScansunPath(), "ay_plot_" + siteName
						+ ".eps")).getAbsolutePath(), "5,5",
				GnuplotMonoColor.COLOR, "eps enhanced");

		ayPlot.setTitle("a_{y} for " + siteName, "", font, titleFontSize);

		ayPlot.setKey("bottom right", 2.5, font, keyFontSize);
		ayPlot.setTics(GnuplotFgBg.FRONT);

		ayPlot.setXLabel("Day of year", font, axisLabelFontSize);
		ayPlot.setXRange(xmin - 0.5, xmax + 0.5);
		ayPlot.setXTics(xmin, 1, font, axisTicFontSize);

		// double ymin = 0.0;
		// double ymax = 150.0;

		ayPlot.setYLabel("a_{y}", font, axisLabelFontSize);
		// plot.setYRange(ymin, ymax);
		// plot.setYTics(ymin, 5, font, axisTicFontSize);
		ayPlot.setMYTics(2);

		ayPlot.setGrid("ytics", 4, 1, GnuplotColors.GRAY);

		ayPlot.setStyleFillSolid(0.75);

		for (int i = 0; i < days.size(); i++) {
			double x1 = 0.0 + (1.0 / days.size()) * i;
			double x2 = x1 + (1.0 / days.size());

			String cmd = new String("set object ");
			cmd += (i + 1) + " rectangle behind ";
			cmd += "from " + GnuplotCoordinates.GRAPH + " " + x1 + ","
					+ GnuplotCoordinates.GRAPH + " " + 0.0 + " ";
			cmd += "to " + GnuplotCoordinates.GRAPH + " " + x2 + ","
					+ GnuplotCoordinates.GRAPH + " " + 1.0 + " ";
			ayPlot.addExtra(cmd);

			double fillDensity = (i % 2 == 0) ? fillDensity1 : fillDensity2;
			cmd = new String("set obj ");
			cmd += (i + 1) + " fillstyle solid noborder " + fillDensity
					+ " fillcolor rgb 'grey'";

			ayPlot.addExtra(cmd);

			ayPlot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			ayPlot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		ayPlot.pushGraph(new Graph(data.getAbsolutePath(), "2:24",
				Axes.NOT_SPECIFIED, "", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		ayPlot.plot();

		LogHandler.getLogs().displayMsg(
				"New plot generated: ay plot for " + siteName, NORMAL);

		// 3
		ScansunPlot bxPlot = new ScansunPlot();

		bxPlot.setOutput(GnuplotTerminal.POSTSCRIPT, (new File(
				ScansunFileHandler.getScansunPath(), "bx_plot_" + siteName
						+ ".eps")).getAbsolutePath(), "5,5",
				GnuplotMonoColor.COLOR, "eps enhanced");

		bxPlot.setTitle("b_{x} for " + siteName, "", font, titleFontSize);

		bxPlot.setKey("bottom right", 2.5, font, keyFontSize);
		bxPlot.setTics(GnuplotFgBg.FRONT);

		bxPlot.setXLabel("Day of year", font, axisLabelFontSize);
		bxPlot.setXRange(xmin - 0.5, xmax + 0.5);
		bxPlot.setXTics(xmin, 1, font, axisTicFontSize);

		// double ymin = 0.0;
		// double ymax = 150.0;

		bxPlot.setYLabel("b_{x}", font, axisLabelFontSize);
		// plot.setYRange(ymin, ymax);
		// plot.setYTics(ymin, 5, font, axisTicFontSize);
		bxPlot.setMYTics(2);

		bxPlot.setGrid("ytics", 4, 1, GnuplotColors.GRAY);

		bxPlot.setStyleFillSolid(0.75);

		for (int i = 0; i < days.size(); i++) {
			double x1 = 0.0 + (1.0 / days.size()) * i;
			double x2 = x1 + (1.0 / days.size());

			String cmd = new String("set object ");
			cmd += (i + 1) + " rectangle behind ";
			cmd += "from " + GnuplotCoordinates.GRAPH + " " + x1 + ","
					+ GnuplotCoordinates.GRAPH + " " + 0.0 + " ";
			cmd += "to " + GnuplotCoordinates.GRAPH + " " + x2 + ","
					+ GnuplotCoordinates.GRAPH + " " + 1.0 + " ";
			bxPlot.addExtra(cmd);

			double fillDensity = (i % 2 == 0) ? fillDensity1 : fillDensity2;
			cmd = new String("set obj ");
			cmd += (i + 1) + " fillstyle solid noborder " + fillDensity
					+ " fillcolor rgb 'grey'";

			bxPlot.addExtra(cmd);

			bxPlot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			bxPlot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		bxPlot.pushGraph(new Graph(data.getAbsolutePath(), "2:25",
				Axes.NOT_SPECIFIED, "", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		bxPlot.plot();

		LogHandler.getLogs().displayMsg(
				"New plot generated: bx plot for " + siteName, NORMAL);

		// 4
		ScansunPlot byPlot = new ScansunPlot();

		byPlot.setOutput(GnuplotTerminal.POSTSCRIPT, (new File(
				ScansunFileHandler.getScansunPath(), "by_plot_" + siteName
						+ ".eps")).getAbsolutePath(), "5,5",
				GnuplotMonoColor.COLOR, "eps enhanced");

		byPlot.setTitle("b_{y} for " + siteName, "", font, titleFontSize);

		byPlot.setKey("bottom right", 2.5, font, keyFontSize);
		byPlot.setTics(GnuplotFgBg.FRONT);

		byPlot.setXLabel("Day of year", font, axisLabelFontSize);
		byPlot.setXRange(xmin - 0.5, xmax + 0.5);
		byPlot.setXTics(xmin, 1, font, axisTicFontSize);

		// double ymin = 0.0;
		// double ymax = 150.0;

		byPlot.setYLabel("b_{y}", font, axisLabelFontSize);
		// plot.setYRange(ymin, ymax);
		// plot.setYTics(ymin, 5, font, axisTicFontSize);
		byPlot.setMYTics(2);

		byPlot.setGrid("ytics", 4, 1, GnuplotColors.GRAY);

		byPlot.setStyleFillSolid(0.75);

		for (int i = 0; i < days.size(); i++) {
			double x1 = 0.0 + (1.0 / days.size()) * i;
			double x2 = x1 + (1.0 / days.size());

			String cmd = new String("set object ");
			cmd += (i + 1) + " rectangle behind ";
			cmd += "from " + GnuplotCoordinates.GRAPH + " " + x1 + ","
					+ GnuplotCoordinates.GRAPH + " " + 0.0 + " ";
			cmd += "to " + GnuplotCoordinates.GRAPH + " " + x2 + ","
					+ GnuplotCoordinates.GRAPH + " " + 1.0 + " ";
			byPlot.addExtra(cmd);

			double fillDensity = (i % 2 == 0) ? fillDensity1 : fillDensity2;
			cmd = new String("set obj ");
			cmd += (i + 1) + " fillstyle solid noborder " + fillDensity
					+ " fillcolor rgb 'grey'";

			byPlot.addExtra(cmd);

			byPlot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			byPlot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		byPlot.pushGraph(new Graph(data.getAbsolutePath(), "2:26",
				Axes.NOT_SPECIFIED, "", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		byPlot.plot();

		LogHandler.getLogs().displayMsg(
				"New plot generated: by plot for " + siteName, NORMAL);

		// 5
		ScansunPlot cPlot = new ScansunPlot();

		cPlot.setOutput(GnuplotTerminal.POSTSCRIPT, (new File(
				ScansunFileHandler.getScansunPath(), "c_plot_" + siteName
						+ ".eps")).getAbsolutePath(), "5,5",
				GnuplotMonoColor.COLOR, "eps enhanced");

		cPlot.setTitle("c for " + siteName, "", font, titleFontSize);

		cPlot.setKey("bottom right", 2.5, font, keyFontSize);
		cPlot.setTics(GnuplotFgBg.FRONT);

		cPlot.setXLabel("Day of year", font, axisLabelFontSize);
		cPlot.setXRange(xmin - 0.5, xmax + 0.5);
		cPlot.setXTics(xmin, 1, font, axisTicFontSize);

		// double ymin = 0.0;
		// double ymax = 150.0;

		cPlot.setYLabel("c", font, axisLabelFontSize);
		// plot.setYRange(ymin, ymax);
		// plot.setYTics(ymin, 5, font, axisTicFontSize);
		cPlot.setMYTics(2);

		cPlot.setGrid("ytics", 4, 1, GnuplotColors.GRAY);

		cPlot.setStyleFillSolid(0.75);

		for (int i = 0; i < days.size(); i++) {
			double x1 = 0.0 + (1.0 / days.size()) * i;
			double x2 = x1 + (1.0 / days.size());

			String cmd = new String("set object ");
			cmd += (i + 1) + " rectangle behind ";
			cmd += "from " + GnuplotCoordinates.GRAPH + " " + x1 + ","
					+ GnuplotCoordinates.GRAPH + " " + 0.0 + " ";
			cmd += "to " + GnuplotCoordinates.GRAPH + " " + x2 + ","
					+ GnuplotCoordinates.GRAPH + " " + 1.0 + " ";
			cPlot.addExtra(cmd);

			double fillDensity = (i % 2 == 0) ? fillDensity1 : fillDensity2;
			cmd = new String("set obj ");
			cmd += (i + 1) + " fillstyle solid noborder " + fillDensity
					+ " fillcolor rgb 'grey'";

			cPlot.addExtra(cmd);

			cPlot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			cPlot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		cPlot.pushGraph(new Graph(data.getAbsolutePath(), "2:27",
				Axes.NOT_SPECIFIED, "", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		cPlot.plot();

		LogHandler.getLogs().displayMsg(
				"New plot generated: c plot for " + siteName, NORMAL);

		// 6
		ScansunPlot P0Plot = new ScansunPlot();

		P0Plot.setOutput(GnuplotTerminal.POSTSCRIPT, (new File(
				ScansunFileHandler.getScansunPath(), "P0_plot_" + siteName
						+ ".eps")).getAbsolutePath(), "5,5",
				GnuplotMonoColor.COLOR, "eps enhanced");

		P0Plot.setTitle("P0 for " + siteName, "", font, titleFontSize);

		P0Plot.setKey("bottom right", 2.5, font, keyFontSize);
		P0Plot.setTics(GnuplotFgBg.FRONT);

		P0Plot.setXLabel("Day of year", font, axisLabelFontSize);
		P0Plot.setXRange(xmin - 0.5, xmax + 0.5);
		P0Plot.setXTics(xmin, 1, font, axisTicFontSize);

		// double ymin = 0.0;
		// double ymax = 150.0;

		P0Plot.setYLabel("P0", font, axisLabelFontSize);
		// plot.setYRange(ymin, ymax);
		// plot.setYTics(ymin, 5, font, axisTicFontSize);
		P0Plot.setMYTics(2);

		P0Plot.setGrid("ytics", 4, 1, GnuplotColors.GRAY);

		P0Plot.setStyleFillSolid(0.75);

		for (int i = 0; i < days.size(); i++) {
			double x1 = 0.0 + (1.0 / days.size()) * i;
			double x2 = x1 + (1.0 / days.size());

			String cmd = new String("set object ");
			cmd += (i + 1) + " rectangle behind ";
			cmd += "from " + GnuplotCoordinates.GRAPH + " " + x1 + ","
					+ GnuplotCoordinates.GRAPH + " " + 0.0 + " ";
			cmd += "to " + GnuplotCoordinates.GRAPH + " " + x2 + ","
					+ GnuplotCoordinates.GRAPH + " " + 1.0 + " ";
			P0Plot.addExtra(cmd);

			double fillDensity = (i % 2 == 0) ? fillDensity1 : fillDensity2;
			cmd = new String("set obj ");
			cmd += (i + 1) + " fillstyle solid noborder " + fillDensity
					+ " fillcolor rgb 'grey'";

			P0Plot.addExtra(cmd);

			P0Plot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			P0Plot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		P0Plot.pushGraph(new Graph(data.getAbsolutePath(), "2:28",
				Axes.NOT_SPECIFIED, "", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		P0Plot.plot();

		LogHandler.getLogs().displayMsg(
				"New plot generated: P0 plot for " + siteName, NORMAL);

		data.delete();
	}

	public void generateSolarFluxPlot(String siteName, Set<File> files)
			throws IOException, InterruptedException {

		File data = new File(AplicationConstans.TMP, "solar_flux_plot_"
				+ siteName + ".data");

		ResultPrinter pr;

		pr = new FileResultPrinter(data);
		ResultPrinterManager.getManager().setPrinter(pr);
		printResults(siteName, files);

		ScansunPlot plot = new ScansunPlot();

		String filename = "solar_flux_plot_" + siteName + ".eps";
		plot.setOutput(GnuplotTerminal.POSTSCRIPT,
				(new File(ScansunFileHandler.getScansunPath(), filename))
						.getAbsolutePath(), "5,5", GnuplotMonoColor.COLOR,
				"eps enhanced");

		String title = "Solar flux for " + siteName;
		String subTitle = "";
		plot.setTitle(title, subTitle, font, titleFontSize);

		plot.setKey("bottom right", 2.5, font, keyFontSize);
		plot.setTics(GnuplotFgBg.FRONT);

		Set<ScansunDay> days = getDays(siteName, files);

		int xmin = ((ScansunDay) days.toArray()[0]).getDayOfYear();
		int xmax = ((ScansunDay) days.toArray()[days.size() - 1])
				.getDayOfYear();

		plot.setXLabel("Day of year", font, axisLabelFontSize);
		plot.setXRange(xmin - 0.5, xmax + 0.5);
		plot.setXTics(xmin, 1, font, axisTicFontSize);

		double ymin = 10.0;
		double ymax = 40.0;
		double yDate = 0.8;

		plot.setYLabel("Power [dBm]", font, axisLabelFontSize);
		plot.setYRange(ymin, ymax);
		plot.setYTics(ymin, 5, font, axisTicFontSize);
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

			plot.setLabel(((ScansunDay) days.toArray()[i]).toString(),
					GnuplotCoordinates.GRAPH, (x1 + (x2 - x1) / 2.0),
					GnuplotCoordinates.GRAPH, yDate, font, labelFontSize, 90,
					GnuplotColors.MAROON);
		}

		for (int i = 1; i < days.size(); i++) {
			double x = 0.0 + (1.0 / days.size()) * i;
			double y1 = 0.0;
			double y2 = 1.0;

			plot.setArrow(GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y1, GnuplotCoordinates.GRAPH, x,
					GnuplotCoordinates.GRAPH, y2, GnuplotFgBg.FRONT,
					GnuplotArrow.NOHEAD, LineType.SCREEN_BLACK_DOTS,
					GnuplotColors.GRAY);
		}

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:29",
				Axes.NOT_SPECIFIED, siteName, Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD,
				PointType.SCREEN_SQUARE_FILLED));

		plot.pushGraph(new Graph(data.getAbsolutePath(), "2:32",
				Axes.NOT_SPECIFIED, "DRAO", Style.LINESPOINTS,
				LineType.SCREEN_BLACK_SOLID_BOLD, PointType.SCREEN_SQUARE_DOT));

		plot.plot();

		data.delete();

		LogHandler.getLogs().displayMsg(
				"New plot generated: solar flux plot for " + siteName, NORMAL);

	}

	public void generateTimeHistogram(String siteName, Set<File> files)
			throws IOException {

	}

	public void generateElevationHistogram(String siteName, Set<File> files)
			throws IOException {

	}

	public void generateAzimuthHistogram(String siteName, Set<File> files)
			throws IOException {

	}

}