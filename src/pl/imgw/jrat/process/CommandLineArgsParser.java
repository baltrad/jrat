/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.AplicationConstans.APS_NAME;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

import pl.imgw.jrat.data.parsers.GlobalParser;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
@SuppressWarnings("static-access")
public class CommandLineArgsParser {

	public final static String CALID = "calid";
	public final static String CALID_RESULT = "calid-result";
	public final static String CALID_LIST = "calid-list";
	public final static String CALID_HELP = "calid-help";
	public final static String CALID_OPT = "calid-opt";
	// public final static String CALID_RESULT_DETAIL = "calid-detail";
	public final static String CALID_PLOT = "calid-plot";

	// public final static String D = "d";
	// public final static String DEBUG = "debug";

	public final static String F = "f";
	public final static String FORMAT = "format";

	public final static String H = "h";
	public final static String HELP = "help";

	public final static String I = "i";

	public final static String O = "o";

	public final static String P = "p";
	public final static String PRINT = "print";
	public final static String PRINTIMAGE = "print-image";

	public final static String Q = "q";
	public final static String QUIET = "quiet";

	// public final static String


    public final static String SCANSUN = "scansun";
    public final static String SCANSUN_RESULT = "scansun-result";
    public final static String SCANSUN_PLOT = "scansun-plot";
    public final static String SCANSUN_OPT = "scansun-opt";
    public final static String SCANSUN_DRAO = "scansun-drao";
    public final static String SCANSUN_HELP = "scansun-help";


	public final static String SEQ = "seq";

	public final static String TEST = "test";

	public final static String V = "v";
	public final static String VERBOSE = "verbose";

	public final static String W = "w";
	public final static String WATCH = "watch";

	public final static String FILE_ARG = "file";
	public final static String FILES_ARG = "file(s)";
	public final static String VALUE_ARG = "value";
	// public final static String FORMAT_ARG = "FORMAT";

	public final static String VERSION = "version";

	// private final static String START_COMMAND =
	// "java -jar jrat.jar [-i <data_file>] [-o <output_file>] [-f <path>] "
	// + "[-v] [-h] [-d]";

	private final static String INPUT_DESCR = "input file(s) or folder(s)";

	private final static String TEST_DESCR = "testing processor";

	private final static String OUTPUT_DESCR = "output file";

	private final static String WATCH_DESCR = "watching processer";

	// private final static String DEBUG_DESCR = "print debugging information";

	private final static String VERBOSE_DESCR = "be extra verbose";

	private final static String PRINT_DESCR = "print information of the file";

	private final static String FORMAT_DESCR = "choose input file(s) format\n"
			+ "<arg> one of the product format availabe: ";

	private final static String PRINT_IMAGE_DESCR = "print dataset as an image\n"
			+ "<arg> must specify dataset number, nodata value, color scale name and"
			+ " additionally but not necessarily format\n"
			+ "supported scales at the moment are rb and gray\n"
			+ "example use: --print-image dataset=dataset1 nodata=0 scale=rb format=png";

	public final static String QUIET_DESCR = "be extra quiet";

	private final static String HELP_DESCR = "print this message";

	private final static String VERSION_DESCR = "print product version and exit";

	private final static String SEQ_DESCR = "sequence mode to process files\n<arg> interval lenght in minutes";

	private final static String CALID_DESCR = "calibration difference of two radars\n"
			+ "<args> [ele=X] [dis=Y] [ref=Z]\nfor more help use --calid-help option";

	private final static String CALID_HELP_DESCR = "print an information of CALID usage";

	private final static String CALID_RESULT_DESCR = "handle CALID results\n<arg>";
	private final static String CALID_LIST_DESCR = "print list of available CALID results\n<arg>";
	// private static final String CALID_RESULT_DETAIL_DESCR =
	// "CALID detailed result, "
	// + "for more help use --calid-help option";

	private static final String CALID_PLOT_DESCR = "CALID plot result, "
			+ "for more help use --calid-help option";


	private static final String CALID_OPT_DESCR = "CALID option file sets each pair parameter, "
			+ "must be use with --calid\n<file> XML file path";

	private final static String SCANSUN_DESCR = "finds solar rays in radar obseravtions\n"
			+ "<args> [hmin=] [rmin=] [tf=] [ad=] [tw=] [mpw=]";
	private final static String SCANSUN_RESULT_DESCR = "print list of available SCANSUN results\n<arg>";
	private final static String SCANSUN_PLOT_DESCR = "creates plots with results";
	private static final String SCANSUN_OPT_DESCR = "option file with radar parameters (used for power calibraton)\n<arg> XML file path";
	private static final String SCANSUN_DRAO_DESCR = "plain text file with solar flux observations form DRAO";
	private final static String SCANSUN_HELP_DESCR = "print an information of SCANSUN usage";


	// private final static String FORMAT_DESCR =
	// "format of the file(s) to precess\n<arg> h5, hdf, rainbow, rb";

	private static Options options = null;
	private static CommandLine cmd = null;

	static {
		options = new Options();

		options.addOption(H, HELP, false, HELP_DESCR);

		Option input_file = OptionBuilder.withArgName(FILES_ARG).hasArgs()
				.withDescription(INPUT_DESCR).create(I);

		// options.addOption(input_file);

		Option output_file = OptionBuilder.withArgName(FILE_ARG).hasArg()
				.withDescription(OUTPUT_DESCR).create(O);

		// options.addOption(output_file);

		options.addOption(null, TEST, false, TEST_DESCR);

		options.addOption(input_file);
		options.addOption(output_file);

		options.addOption(P, PRINT, false, PRINT_DESCR);

		String formatDescr = "";
		for (GlobalParser.Format format : GlobalParser.Format.values()) {
			formatDescr += format.getName() + ", ";
		}

		options.addOption(F, FORMAT, true, FORMAT_DESCR + formatDescr);

		Option print_img = OptionBuilder.withLongOpt(PRINTIMAGE)
				.withDescription(PRINT_IMAGE_DESCR).hasArgs().create();

		Option seq = OptionBuilder.withLongOpt(SEQ).hasArgs()
				.withDescription(SEQ_DESCR).create();
		options.addOption(seq);

		Option calid = OptionBuilder.withLongOpt(CALID).hasArgs()
				.withDescription(CALID_DESCR).create();
		options.addOption(calid);

		Option calidResults = OptionBuilder.withLongOpt(CALID_RESULT)
				.hasOptionalArgs().withDescription(CALID_RESULT_DESCR).create();
		options.addOption(calidResults);

		Option calidList = OptionBuilder.withLongOpt(CALID_LIST)
				.hasOptionalArgs().withDescription(CALID_LIST_DESCR).create();
		options.addOption(calidList);

		options.addOption(null, CALID_HELP, false, CALID_HELP_DESCR);

		// calidResults =
		// OptionBuilder.withDescription(CALID_RESULT_DETAIL_DESCR)
		// .hasArgs().withLongOpt(CALID_RESULT_DETAIL).create();
		// options.addOption(calidResults);

		calidResults = OptionBuilder.withDescription(CALID_PLOT_DESCR)
				.hasArgs().withLongOpt(CALID_PLOT).create();
		options.addOption(calidResults);

		Option calidOpt = OptionBuilder.withArgName(FILE_ARG).hasArg()
				.withDescription(CALID_OPT_DESCR).withLongOpt(CALID_OPT)
				.create();
		options.addOption(calidOpt);

		
        Option scansun = OptionBuilder.withLongOpt(SCANSUN).hasArgs()
                .withDescription(SCANSUN_DESCR).create();
        options.addOption(scansun);


		Option scansunResults = OptionBuilder.withLongOpt(SCANSUN_RESULT)
				.hasOptionalArg().withDescription(SCANSUN_RESULT_DESCR)
				.create();
		options.addOption(scansunResults);


        Option scansunOpt = OptionBuilder.withArgName(FILE_ARG).hasArg()
                .withDescription(SCANSUN_OPT_DESCR).withLongOpt(SCANSUN_OPT)
                .create();
        options.addOption(scansunOpt);

        Option scansunDRAO = OptionBuilder.withArgName(FILE_ARG).hasArg()
                .withDescription(SCANSUN_DRAO_DESCR).withLongOpt(SCANSUN_DRAO)
                .create();
        options.addOption(scansunDRAO);


		options.addOption(null, SCANSUN_HELP, false, SCANSUN_HELP_DESCR);

		scansunResults = OptionBuilder.withDescription(SCANSUN_PLOT_DESCR)
				.hasOptionalArgs().withLongOpt(SCANSUN_PLOT).create();
		options.addOption(scansunResults);


		options.addOption(print_img);
		options.addOption(W, WATCH, false, WATCH_DESCR);

		options.addOption(Q, QUIET, false, QUIET_DESCR);
		options.addOption(V, VERBOSE, false, VERBOSE_DESCR);
		// options.addOption(D, DEBUG, false, DEBUG_DESCR);
		// options.addOption(F, FORMAT, true, FORMAT_DESCR);
		options.addOption(null, VERSION, false, VERSION_DESCR);
	}

	public boolean parseArgs(String[] args) {
		Parser parser = new PosixParser();

		try {
			cmd = parser.parse(options, args);

		} catch (ParseException e) {
			printHelp();
			// System.out.println("ZLE");
			return false;
		}
		return true;
	}

	public static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(100);
		formatter.printHelp(APS_NAME + " [options]", options);

	}

	public CommandLine getCmd() {
		return cmd;
	}

	public static void main(String[] args) {
		CommandLineArgsParser cmd = new CommandLineArgsParser();
		cmd.parseArgs(args);
		printHelp();
	}

}
