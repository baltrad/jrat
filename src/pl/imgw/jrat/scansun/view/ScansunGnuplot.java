/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.view;

//import java.io.IOException;

import java.io.IOException;

import org.jgnuplot.Graph;
import org.jgnuplot.Plot;
import org.jgnuplot.Terminal;

import pl.imgw.jrat.AplicationConstans;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunGnuplot {
	private Plot plot;

	public static final String FONT_NAME = "Helvetica Bold";
	public static final int TITLE_FONT_SIZE = 32;
	public static final int KEY_FONT_SIZE = 14;
	public static final int AXIS_LABEL_FONT_SIZE = 20;
	public static final int AXIS_TIC_FONT_SIZE = 14;
	public static final int LABEL_FONT_SIZE = 18;

	enum GnuplotTerminal {
		POSTSCRIPT(Terminal.POSTSCRIPT), PNG(Terminal.PNG);

		private String terminal;

		private GnuplotTerminal(String terminal) {
			this.terminal = terminal;
		}

		public String getTerminal() {
			return terminal;
		}

		public String extension() {
			String result = new String();
			if (terminal == org.jgnuplot.Terminal.POSTSCRIPT) {
				result = "eps";
			} else if (terminal == org.jgnuplot.Terminal.PNG) {
				result = "png";
			}

			return result;
		}
	}

	enum GnuplotMonoColor {
		MONO("mono"), COLOR("color"), TRUECOLOR("truecolor");

		private String mc;

		private GnuplotMonoColor(String mc) {
			this.mc = mc;
		}

		public String toString() {
			return mc;
		}
	}

	enum GnuplotColors {
		GRAY("gray"), RED("red"), GREEN("green"), MAROON("#8B0000"), BLACK(
				"black"), BROWN("brown"), SALMON("salmon");

		private String color;

		private GnuplotColors(String color) {
			this.color = color;
		}

		public String toString() {
			return color;
		}
	}

	enum GnuplotCoordinates {
		FIRST("first"), GRAPH("graph"), SCREEN("screen");

		private String coordinates;

		private GnuplotCoordinates(String coordinates) {
			this.coordinates = coordinates;
		}

		public String toString() {
			return coordinates;
		}
	}

	enum GnuplotFgBg {
		FRONT("front"), BEHIND("behind");

		private String fb;

		private GnuplotFgBg(String fb) {
			this.fb = fb;
		}

		public String toString() {
			return fb;
		}
	}

	enum GnuplotArrow {
		NOHEAD("nohead");

		private String type;

		private GnuplotArrow(String type) {
			this.type = type;
		}

		public String toString() {
			return type;
		}
	}

	public ScansunGnuplot() {
		plot = new Plot();

		Plot.setPlotDirectory(AplicationConstans.TMP);
		Plot.setGnuplotExecutable("gnuplot");
	}
	
	public void setOutput(GnuplotTerminal terminal, String outputFileName,
			String size, GnuplotMonoColor color, String extra) {

		plot.setOutput(terminal.getTerminal(), outputFileName, size);

		String cmd = new String("set terminal ");
		cmd += terminal.getTerminal() + " ";
		cmd += color + " ";
		cmd += extra + " ";

		plot.addExtra(cmd);
	}

	public void setTitle(String title, String subTitle, String font,
			int fontSize) {

		String plotTitle = title;
		if (!subTitle.isEmpty()) {
			plotTitle += "\\n {/*0.6" + subTitle + "}";
		}
		plot.setTitle(plotTitle);
		plot.addExtra("set title font '" + font + "," + fontSize + "'");
	}

	public void setKey(String position, double spacing, String font,
			int fontSize) {
		String cmd = new String();
		cmd += position + " ";
		cmd += "spacing " + spacing + " ";
		cmd += "font '" + font + "," + fontSize + "'";
		plot.setKey(cmd);
	}

	public void setSquare() {
		plot.addExtra("set size square");
	}

	public void setTics(GnuplotFgBg fb) {
		String cmd = new String("set tics");
		cmd += " " + fb;

		plot.addExtra(cmd);
	}

	public void setXLabel(String label, String font, int fontSize) {
		plot.setXLabel(label);
		String cmd = new String("set xlabel");
		cmd += " font '" + font + "," + fontSize + "' ";
		plot.addExtra(cmd);
	}

	public void setXLabel(String label, String font, int fontSize,
			double xOffset, double yOffset) {
		plot.setXLabel(label);
		String cmd = new String("set xlabel");
		cmd += " font '" + font + "," + fontSize + "' ";
		cmd += "offset " + xOffset + "," + yOffset;
		plot.addExtra(cmd);
	}

	public void setXRange(double xmin, double xmax) {
		plot.setXRange(xmin, xmax);
	}

	public void setXTics(double delta, String font, int fontSize) {
		String cmd = new String();
		cmd += delta + " ";
		cmd += "font '" + font + "," + fontSize + "' ";

		plot.setXTics(cmd);
	}

	public void setXTics(double delta, String font, int fontSize, int rotateBy) {
		String cmd = new String();
		cmd += delta + " ";
		cmd += "font '" + font + "," + fontSize + "' ";
		if (rotateBy == 90)
			cmd += "rotate";
		else
			cmd += "rotate by " + rotateBy;

		plot.setXTics(cmd);
	}

	public void setXTics(double xmin, double delta, String font, int fontSize) {
		String cmd = new String();
		cmd += xmin + "," + delta + " ";
		cmd += "font '" + font + "," + fontSize + "' ";

		plot.setXTics(cmd);
	}

	public void setXTics(double xmin, double delta, String font, int fontSize,
			int rotateBy) {
		String cmd = new String();
		cmd += xmin + "," + delta + " ";
		cmd += delta + " ";
		cmd += "font '" + font + "," + fontSize + "' ";
		if (rotateBy == 90)
			cmd += "rotate";
		else
			cmd += "rotate by " + rotateBy;

		plot.setXTics(cmd);
	}

	public void setFormatX(String FormatSpecifier) {
		String cmd = new String("set format x");
		cmd += " \"" + FormatSpecifier + "\"";

		plot.addExtra(cmd);
	}

	public void setYLabel(String label, String font, int fontSize) {
		plot.setYLabel(label);
		String cmd = new String("set ylabel");
		cmd += " font '" + font + "," + fontSize + "' ";
		plot.addExtra(cmd);
	}

	public void setYRange(double ymin, double ymax) {
		plot.setYRange(ymin, ymax);
	}

	public void setYTics(double delta, String font, int fontSize) {
		String cmd = new String();
		cmd += delta + " ";
		cmd += "font '" + font + "," + fontSize + "' ";

		plot.setYTics(cmd);
	}

	public void setYTics(double delta, String font, int fontSize, int rotateBy) {
		String cmd = new String();
		cmd += delta + " ";
		cmd += "font '" + font + "," + fontSize + "' ";
		if (rotateBy == 90)
			cmd += "rotate";
		else
			cmd += "rotate by " + rotateBy;

		plot.setYTics(cmd);
	}

	public void setYTics(double ymin, double delta, String font, int fontSize) {
		String cmd = new String();
		cmd += ymin + "," + delta + " ";
		cmd += "font '" + font + "," + fontSize + "' ";

		plot.setYTics(cmd);
	}

	public void setMYTics(int i) {
		plot.setMYTics(((Integer) i).toString());
	}

	public void setFormatY(String FormatSpecifier) {
		String cmd = new String("set format y");
		cmd += " \"" + FormatSpecifier + "\"";

		plot.addExtra(cmd);
	}

	public void setGrid(String tics, int lineType, double lineWidth,
			GnuplotColors color) {
		String cmd = new String();
		cmd += tics + " ";
		cmd += "lt " + lineType + " ";
		cmd += "lw " + lineWidth + " ";
		cmd += "lc rgb '" + color + "'";

		plot.setGrid(cmd);
	}

	public void setStyleFillSolid(double density) {
		String cmd = new String("set style fill solid");
		cmd += " " + density;

		plot.addExtra(cmd);
	}

	public void setLabel(String label, GnuplotCoordinates xCoordinates,
			double xPosition, GnuplotCoordinates yCoordinates,
			double yPosition, String font, int fontSize) {
		String cmd = new String("set label");
		cmd += " \"" + label + "\" front ";
		cmd += "at " + xCoordinates + " " + xPosition + ", " + yCoordinates
				+ " " + yPosition + " ";
		cmd += "font '" + font + "," + fontSize + "' ";

		plot.addExtra(cmd);
	}

	public void setLabelCenter(String label, GnuplotCoordinates xCoordinates,
			double xPosition, GnuplotCoordinates yCoordinates,
			double yPosition, String font, int fontSize) {
		String cmd = new String("set label");
		cmd += " \"" + label + "\" center front ";
		cmd += "at " + xCoordinates + " " + xPosition + ", " + yCoordinates
				+ " " + yPosition + " ";
		cmd += "font '" + font + "," + fontSize + "' ";

		plot.addExtra(cmd);
	}

	public void setLabel(String label, GnuplotCoordinates xCoordinates,
			double xPosition, GnuplotCoordinates yCoordinates,
			double yPosition, String font, int fontSize, int rotateBy,
			GnuplotColors color) {
		String cmd = new String("set label");
		cmd += " \"" + label + "\" center front ";
		cmd += "at " + xCoordinates + " " + xPosition + ", " + yCoordinates
				+ " " + yPosition + " ";
		cmd += "font '" + font + "," + fontSize + "' ";
		cmd += "rotate by " + rotateBy + " ";
		cmd += "textcolor rgb '" + color + "'";

		plot.addExtra(cmd);
	}

	public void setLabelCenter(String label, GnuplotCoordinates xCoordinates,
			double xPosition, GnuplotCoordinates yCoordinates,
			double yPosition, String font, int fontSize, int rotateBy,
			GnuplotColors color) {
		String cmd = new String("set label");
		cmd += " \"" + label + "\" center front ";
		cmd += "at " + xCoordinates + " " + xPosition + ", " + yCoordinates
				+ " " + yPosition + " ";
		cmd += "font '" + font + "," + fontSize + "' ";
		cmd += "rotate by " + rotateBy + " ";
		cmd += "textcolor rgb '" + color + "'";

		plot.addExtra(cmd);
	}

	public void setArrow(GnuplotCoordinates x1Coordinates, double x1,
			GnuplotCoordinates y1Coordinates, double y1,
			GnuplotCoordinates x2Coordinates, double x2,
			GnuplotCoordinates y2Coordinates, double y2, GnuplotFgBg fb,
			GnuplotArrow arrowType, int lineType, GnuplotColors color) {

		String cmd = new String("set arrow");
		cmd += " from ";
		cmd += x1Coordinates + " " + x1 + ", " + y1Coordinates + " " + y1 + " ";
		cmd += " to ";
		cmd += x2Coordinates + " " + x2 + ", " + y2Coordinates + " " + y2 + " ";
		cmd += fb + " ";
		cmd += arrowType + " ";
		cmd += "lt " + lineType + " ";
		cmd += "lc rgb '" + color + "'";

		plot.addExtra(cmd);
	}

	public void setParametric() {
		plot.setParametric();
	}

	public void addVariable(String name, double value) {
		String cmd = new String();
		cmd += name + "=" + value;

		plot.addExtra(cmd);
	}

	public void addExtra(String cmd) {
		plot.addExtra(cmd);
	}

	public void pushGraph(Graph graph) {
		plot.pushGraph(graph);
	}

	public void plot() throws IOException, InterruptedException {
		plot.plot();
	}

}
