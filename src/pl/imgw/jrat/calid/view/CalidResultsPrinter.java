/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.calid.data.CalidDataHandler;
import pl.imgw.jrat.calid.data.CalidPairAndParameters;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.CalidParametersParser;
import pl.imgw.jrat.calid.data.RadarsPair;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;
import pl.imgw.util.Log;
import pl.imgw.util.LogFile;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultsPrinter {

	protected static Log log = LogManager.getLogger();

	protected static LogFile logFile = LogManager.getFileLogger();

	protected CalidParameters params = new CalidParameters();
	protected RadarsPair pair;
	protected Scanner scanner;

	protected Set<String> headers = new HashSet<String>();;

	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm");
	protected SimpleDateFormat fsdf = new SimpleDateFormat("yyyyMMdd");

	protected ResultPrinter printer = ResultPrinterManager.getManager()
			.getPrinter();

	/**
	 * 
	 * @param args
	 * @throws CalidException
	 */
	public CalidResultsPrinter(String[] args) throws CalidException {
		CalidPairAndParameters pp = null;
		if (args != null) {

			try {
				pp = CalidParametersParser.getParser().parsePairAndParameters(
						args);
			} catch (CalidException e) {
				throw e;
			}
			this.params = pp.getParameters();
			// log.printMsg(params.toString(), Log.TYPE_NORMAL,
			// Log.MODE_VERBOSE);
			this.pair = pp.getPair();
			// log.printMsg(pair.toString(), Log.TYPE_NORMAL, Log.MODE_VERBOSE);
		}

	}

	/**
     * 
     */
	public CalidResultsPrinter(CalidPairAndParameters pairParams) {
		params = pairParams.getParameters();
		pair = pairParams.getPair();
	}

	public void printList() {

		headers.clear();

		Set<File> files = CalidResultFileGetter.getResultFiles(pair, params);

		if (files.isEmpty()) {
			printer.println("No results between "
					+ sdf.format(params.getStartRangeDate()) + " and "
					+ sdf.format(params.getEndRangeDate()));
			return;
		}

		if (params.isStartDateDefault() && params.isEndDateDefault()) {
			printer.println("Printing list of pairs with results between "
					+ sdf.format(params.getStartRangeDate()) + " and "
					+ sdf.format(params.getEndRangeDate()));

			for (File f : files) {
				printResultsHeader(f);
			}

			printer.println("\nNumber of pairs matching selected parameters: "
					+ headers.size());
			printer.println("To print list of available dates for any particular pair"
					+ " add date= parameter with correct date value");
		} else {

			printer.println("Printing list of available results between "
					+ sdf.format(params.getStartRangeDate()) + " and "
					+ sdf.format(params.getEndRangeDate()));
			int n = 0;
			for (File f : files) {

				printResultsHeader(f);

				int a = printNumberOfResultsByDate(f);
				if (a < 1) {
					continue;
				}
				n += a;
			}
			if (n > 1)
				printer.println("\t" + n + " results all together in database.");

		}
	}

	protected void setDates(File f, Set<Date> set) {

		// Set<Date> list = new TreeSet<Date>();
		Date date;
		Scanner scanner = null;
		try {
			scanner = new Scanner(f);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith("#")) {
					if (!scanner.hasNextLine()) {
						printer.println("No results found.");
						return;
					}
					continue;
				}
				try {
					date = CalidDataHandler.CALID_DATE_TIME_FORMAT.parse(line
							.split(" ")[0]);
				} catch (ParseException e) {
					continue;
				}
				set.add(date);

			}
		} catch (FileNotFoundException e) {
			log.printMsg(e.getMessage(), Log.TYPE_ERROR, Log.MODE_VERBOSE);
		} catch (Exception e) {
			log.printMsg(e.getMessage(), Log.TYPE_ERROR, Log.MODE_VERBOSE);
		} finally {
			scanner.close();
		}

	}

	protected int printNumberOfResultsByDate(File f) {
		ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();
		// printer.println(f);
		String date = "";
		int i = 0;
		Scanner scanner = null;
		try {
			scanner = new Scanner(f);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith("#")) {
					if (!scanner.hasNextLine()) {
						printer.println("No results found.");
						return 0;
					}
					continue;
				}
				// printer.println("\t\t" + line.split(" ")[0]);

				if (date.isEmpty()) {
					date = line.split(" ")[0].split("/")[0];
				}

				i++;
				// if (log.getVerbose() < Logging.NORMAL && i == 5) {
				// printer.println("\t\t\t.\n\t\t\t.\n\t\t\t.");
				// printer
				// .println("\t(use -v parameter to print all)");
				// return -1;
				// }
			}
		} catch (FileNotFoundException e) {
			log.printMsg(e.getMessage(), Log.TYPE_ERROR, Log.MODE_VERBOSE);
		} catch (Exception e) {
			log.printMsg(e.getMessage(), Log.TYPE_ERROR, Log.MODE_VERBOSE);
		} finally {
			scanner.close();
		}

		printer.println("\t\t" + date + " number of results: " + i);

		return i;
	}

	/**
	 * 
	 * @param f
	 */
	protected boolean printResultsHeader(File f) {

		try {
			scanner = new Scanner(f);
			if (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (headers.contains(line))
					return false;
				headers.add(line);
				if (line.startsWith("#")) {
					printHorizontalLine();
					printer.println(line);
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			log.printMsg(e.getMessage(), Log.TYPE_ERROR, Log.MODE_VERBOSE);
		} catch (Exception e) {
			log.printMsg(e.getMessage(), Log.TYPE_ERROR, Log.MODE_VERBOSE);
		}
		return false;
	}

	protected boolean areParametersSet() {
		if (pair.getSource1().isEmpty() || pair.getSource2().isEmpty()
				|| params.isDistanceDefault() || params.isElevationDefault()
				|| params.isReflectivityDefault()
				|| params.getStartRangeDate() == null)
			return false;

		return true;
	}

	protected void printHorizontalLine() {
		ResultPrinter printer = ResultPrinterManager.getManager().getPrinter();
		printer.print("#");
		for (int i = 0; i < 70; i++) {
			printer.print("=");
		}
		printer.print("\n");
	}

	protected double round(double value, int decimal) {
		double pow = Math.pow(10, decimal);

		value *= pow;

		value = Math.round(value);

		return value / pow;
	}

}
