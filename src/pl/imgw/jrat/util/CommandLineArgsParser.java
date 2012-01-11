package pl.imgw.jrat.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

import pl.imgw.jrat.MainJRat;

/**
 * 
 * Class implementing command line arguments parser functionality.
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 *
 */
@SuppressWarnings("static-access")
public class CommandLineArgsParser {

    // Constants

    public final static String HELP_OPTION = "h";
    public final static String INPUT_OPTION = "i";
    public final static String DISPLAY_OPTION = "d";
    public final static String PRINT_OPTION = "p";
    public final static String VERBOSE_OPTION = "v";
    public static final String COMPARE_OPTION = "c";

    private final static String START_COMMAND = "jrat -i file ... " +
    		"[options ...] [-h] [-v]"; 

    private final static String HELP_DESCRIPTION = "print this message";
    private final static String DISPLAY_DESCRIPTION = "display all attributes of hdf5 file input";

    private final static String COMPARE_DESCRIPTION = "compare two volumes in overlapping points\n"
            + "<args> elevation in (deg)rees and maximum distans threshold in (m)eters, "
            + "e.g. -c 0.5deg 500m";
    
    private final static String PRINT_DESCRIPTION = "print image of data\n"
            + "<arg> dataset number (e.g. 'dataset1')";
    
    private final static String INPUT_DESCRIPTION = "input file option\n"
            + "<file> input file's path";

    private final static String VERBOSE_DESCRIPTION = "verbose mode option";
    
    // Command line options
    private static Options options = null;
    // Command line arguments
    private static CommandLine cmd = null;

    // Create static options object
    static {
        options = new Options();

//        Option output_file = OptionBuilder.withArgName(OUTPUT_FILE_OPTION)
//                .withArgName("arg").hasArg()
//                .withDescription(OUTPUT_FILE_DESCRIPTION)
//                .create(OUTPUT_FILE_OPTION);

        char sep = " ".charAt(0);
        
        Option help = OptionBuilder
                .withDescription(HELP_DESCRIPTION).create(HELP_OPTION);
        Option input = OptionBuilder.withArgName("file").hasArgs()
                .withValueSeparator(sep).withDescription(INPUT_DESCRIPTION)
                .create(INPUT_OPTION);

        Option print = OptionBuilder.withArgName("arg").hasArg()
                .withDescription(PRINT_DESCRIPTION).create(PRINT_OPTION);

        Option diplay = OptionBuilder.withDescription(DISPLAY_DESCRIPTION)
                .create(DISPLAY_OPTION);

        Option compare = OptionBuilder.withArgName("args").hasArgs()
                .withValueSeparator(sep).withDescription(COMPARE_DESCRIPTION)
                .create(COMPARE_OPTION);
        
        Option verbose = OptionBuilder
                .withDescription(VERBOSE_DESCRIPTION).create(VERBOSE_OPTION);

        options.addOption(help);
        options.addOption(input);
        options.addOption(diplay);
        options.addOption(print);
        options.addOption(compare);
        options.addOption(verbose);
    }

    /**
     * Method parses command line parameters.
     * 
     * @param args
     *            Command line parameters
     */
    public void parseCommandLineArgs(String[] args) {

        Parser parser = new PosixParser();
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelpAndExit(1, START_COMMAND, options);
        }

        // Help mode is chosen
        if (cmd.hasOption(HELP_OPTION)) {
            printHelpAndExit(1, HELP_OPTION, options);
        } else if ((cmd.getOptions().length == 0)) {
            printHelpAndExit(1, START_COMMAND, options);
        } else if (!cmd.hasOption(INPUT_OPTION)) {
            printHelpAndExit(1, START_COMMAND, options);
        }
        
    }

    /**
     * Method checks if a given command line argument is provided
     * 
     * @param option
     *            Option name
     * @return True if argument is provided, false otherwise
     */
    public boolean hasArgument(String option) {
        if (cmd.hasOption(option))
            return true;
        else
            return false;
    }

    /**
     * Method returns command line argument value
     * 
     * @param option
     *            Option name
     * @return Command line argument value
     */
    public String getArgumentValue(String option) {
        return cmd.getOptionValue(option);
    }
    
    /**
     * Method returns command line argument value
     * 
     * @param option
     *            Option name
     * @return Command line argument value
     */
    public String[] getArgumentValues(String option) {
        return cmd.getOptionValues(option);
    }

    /**
     * Method prints help information screen and terminates program.
     * 
     * @param exitCode
     *            Program exit code
     * @param startCommand
     *            Program launch command
     * @param options
     *            Command line parameters
     */
    public void printHelpAndExit(int exitCode, String startCommand,
            Options options) {
        // Help formatter
        System.out
                .println("Version: Java Radar data Analizing Tool " 
                        + MainJRat.JRAT + " "+ MainJRat.VERSION
                        + "\n2011-2012 Ground Based Remote Sensing Department, " +
                        "Institute of Meteorology and Water Management\n");
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(startCommand, options);
        System.exit(exitCode);
    }

}
