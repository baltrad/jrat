/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.controller;

import static pl.imgw.jrat.controller.CommandLineArgsParser.*;

import java.io.File;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.LogsType;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ProcessController {

    private CommandLine cmd;

    public ProcessController(String[] args) {

        CommandLineArgsParser parser = new CommandLineArgsParser();
        parser.setHelpMsg("jrat [options]");
        parser.parseArgs(args);
        cmd = parser.getCmd();

        if (cmd == null)
            return;
        
        if (cmd.hasOption(QUIET)) {
            System.out.println("ustawia quiet");
            LogHandler.getLogs().setLoggingVerbose(LogsType.SILENT);
        } else if (cmd.hasOption(VERBOSE)) {
            System.out.println("ustawia verbose");
            LogHandler.getLogs().setLoggingVerbose(LogsType.ERROR);
        } else
            LogHandler.getLogs().setLoggingVerbose(LogsType.INITIATION);

    }

    public boolean start() {

        if (cmd == null) {
            return false;
        }

        if (cmd.hasOption(VERSION)) {
            LogHandler.getLogs().printVersion();
            return true;
        }

        if (cmd.hasOption(PRINT)) {
            System.out.println(cmd.getOptionValue(PRINT));
            return true;
        }

        if (cmd.hasOption(AUTO)) {
            // TO-DO
            return true;
        }

        File output = null;
        if (cmd.hasOption(O)) {
            output = new File(cmd.getOptionValue(O));
        }

        if (cmd.hasOption(I)) {
            if (output == null)
                return false;
            for (int i = 0; i < cmd.getOptionValues(I).length; i++)
                System.out.println(cmd.getOptionValues(I)[i]);
            // TO-DO
            return true;
        }

        if (cmd.hasOption(IMAGE)) {
            System.out.println(cmd.getOptionValue(IMAGE));
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        ProcessController pc = new ProcessController(args);
        pc.start();
    }

}
