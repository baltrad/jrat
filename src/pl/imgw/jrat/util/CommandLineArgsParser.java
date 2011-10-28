/*
 * OdimH5 :: Converter software for OPERA Data Information Model
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.jrat.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

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
    private final static String START_COMMAND = "jrat [-h]";

    private final static String HELP_DESCRIPTION = "print this message";
    private final static String DISPLAY_DESCRIPTION = "display all attributes";
    private final static String PRINT_DESCRIPTION = "print image of data\n"
            + "<arg> dataset number (e.g. elevation number in a volume file)";
    
    private final static String INPUT_DESCRIPTION = "input file option\n"
            + "<arg> input file's path";

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

        Option help = OptionBuilder.withArgName(HELP_OPTION)
                .withDescription(HELP_DESCRIPTION).create(HELP_OPTION);
        Option input = OptionBuilder.withArgName(INPUT_OPTION)
                .withArgName("arg").hasArg().withDescription(INPUT_DESCRIPTION)
                .create(INPUT_OPTION);
        
        Option print = OptionBuilder.withArgName(PRINT_OPTION)
                .withArgName("arg").hasArg().withDescription(PRINT_DESCRIPTION)
                .create(PRINT_OPTION);
        
        Option diplay = OptionBuilder.withArgName(DISPLAY_OPTION)
                .withDescription(DISPLAY_DESCRIPTION).create(DISPLAY_OPTION);
        
        Option verbose = OptionBuilder.withArgName(VERBOSE_OPTION)
                .withDescription(VERBOSE_DESCRIPTION).create(VERBOSE_OPTION);

        options.addOption(help);
        options.addOption(input);
        options.addOption(diplay);
        options.addOption(print);
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
        }
        // Descriptor file generation mode (4 arguments) or conversion mode (2
        // arguments )
        // is chosen
        if ((cmd.getOptions().length == 0)) {
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
                .println("Information about the application and how to receive help\n");
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(startCommand, options);
        System.exit(exitCode);
    }

}
