/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.controller;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CommandLineArgsParser {

    public final static String I = "i";

    public final static String O = "o";

    public final static String A = "a";
    public final static String AUTO = "auto";

    public final static String P = "p";
    public final static String PRINT = "print";
    public final static String IMAGE = "print-image";
    
    public final static String H = "h";
    public final static String HELP = "help";

    public final static String V = "v";
    public final static String VERBOSE = "verbose";

    public final static String Q = "q";
    public final static String QUIET = "quiet";

    public final static String D = "d";
    public final static String DEBUG = "debug";

    public final static String FILE_ARG = "file";
    public final static String VALUE_ARG = "value";
    public final static String FORMAT_ARG = "FORMAT";
    
    public final static String VERSION = "version";

    // private final static String START_COMMAND =
    // "java -jar jrat.jar [-i <data_file>] [-o <output_file>] [-f <path>] "
    // + "[-v] [-h] [-d]";

    private final static String INPUT_DESCR = "input file";

    private final static String OUTPUT_DESCR = "output file";

    private final static String AUTO_DESCR = "start process";

    private final static String DEBUG_DESCR = "print debugging information";

    private final static String VERBOSE_DESCR = "be extra verbose";
    
    private final static String PRINT_DESCR = "print information of the file";
    
    private final static String IMAGE_DESCR = "print dataset as an image";

    public final static String QUIET_DESCR = "be extra quiet";

    private final static String HELP_DESCR = "print this message";
    
    private final static String VERSION_DESCR = "print product version and exit";

    private String helpMsg = "jrat";
    
    private static Options options = null;
    private static CommandLine cmd = null;

    static {
        options = new Options();

        options.addOption(H, HELP, false, HELP_DESCR);
        
        
        
        @SuppressWarnings("static-access")
        Option input_file = OptionBuilder.withArgName(FILE_ARG)
                .hasArgs().withDescription(INPUT_DESCR)
                .create(I);

//        options.addOption(input_file);
        
        @SuppressWarnings("static-access")
        Option output_file = OptionBuilder.withArgName(FILE_ARG)
                .hasArg().withDescription(OUTPUT_DESCR)
                .create(O);
        
//        options.addOption(output_file);
        
        options.addOption(input_file);
        options.addOption(output_file);

        options.addOption(P, PRINT, true, PRINT_DESCR);
        
        Option print_img = OptionBuilder.withLongOpt(IMAGE).withValueSeparator('=')
                .withDescription(IMAGE_DESCR).hasArg().withArgName(FORMAT_ARG)
                .create();
        
        options.addOption(print_img);
        options.addOption(A, AUTO, true, AUTO_DESCR);
        
        options.addOption(Q, QUIET, false, QUIET_DESCR);
        options.addOption(V, VERBOSE,false, VERBOSE_DESCR);
        options.addOption(D, DEBUG, false, DEBUG_DESCR);
        options.addOption(null, VERSION, false, VERSION_DESCR);
    }

    HelpFormatter formatter = new HelpFormatter();

    public void parseArgs(String[] args) {
        Parser parser = new PosixParser();
        
        try {
            cmd = parser.parse(options, args);
            if(cmd.getOptions().length == 0 || cmd.hasOption(H))
                printHelp();
        } catch (ParseException e) {
            printHelp();
        }
    }

    public void setHelpMsg(String helpMsg) {
        this.helpMsg = helpMsg;
    }
    
    public void printHelp() {
        formatter.printHelp(helpMsg, options);
    }
    
    public CommandLine getCmd() {
        return cmd;
    }

    public static void main(String[] args) {
        CommandLineArgsParser cmd = new CommandLineArgsParser();
        cmd.parseArgs(args);
        cmd.printHelp();
    }
    
}
