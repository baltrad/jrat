/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultManager {

    private static final String HELP = "help";
    private static final String LIST = "list";
    private static final String PRINT = "print";
    private static final String SOURCE1 = "src1";
    private static final String SOURCE2 = "src2";
    private static final String ELEVATION = "ele";
    private static final String DISTANCE = "dis";
    private static final String REFLECTIVITY = "ref";
    

    public CalidResultManager(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        if (args.length == 1) {
            if (args[0].matches(HELP)) {
                printHelp();
            } else if (args[0].matches(LIST)) {
                printList("");
            }
        } else if (args.length == 6) {
            if (args[0].matches(LIST)) {
                printList(args[1] + " " + args[2]);
            } else if (args[0].matches(PRINT)) {

            }
        } else
            printHelp();
    }

    private List<File> getResultsFiles() {

        List<File> results = new LinkedList<File>();

        File folder = new File(CalidManager.getCalidPath());
        for (File pairname : folder.listFiles()) {
            if (pairname.isDirectory()) {
                // pair name
                for (File parameters : pairname.listFiles()) {
                    if (parameters.isDirectory()) {
                        // parameters
                        for (File file : parameters.listFiles()) {
                            if (file.isFile()
                                    && file.getName().startsWith("result")) {
                                results.add(file);
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    private File getResultFile(String pair) {

        List<File> results = getResultsFiles();
        for (File f : results) {
            try {
                Scanner scanner = new Scanner(f);
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("#")) {
                        String readpair = line.substring(2);

                        if (pair.split(" ")[0].matches(readpair.split(" ")[0])
                                && pair.split(" ")[1].matches(readpair
                                        .split(" ")[1]))
                            return f;
                    }
                }

            } catch (FileNotFoundException e) {
                LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            } catch (Exception e) {
                LogHandler.getLogs().displayMsg(e.getMessage(), Logging.ERROR);
            }
        }

        return null;
    }

    private void printList(String pair) {
        LogHandler.getLogs().displayMsg(
                "CALID Result Manager. List of available pairs:",
                Logging.SILENT);

        System.out.println(pair);

        File folder = new File(CalidManager.getCalidPath());
        for (File pairname : folder.listFiles()) {
            if (pairname.isDirectory()) {
                // pair name
                for (File parameters : pairname.listFiles()) {
                    if (parameters.isDirectory()) {
                        // parameters
                        for (File file : parameters.listFiles()) {
                            if (file.isFile()
                                    && file.getName().startsWith("result")) {
                                try {
                                    Scanner scanner = new Scanner(file);
                                    if (scanner.hasNextLine()) {
                                        String line = scanner.nextLine();
                                        if (line.startsWith("#")) {
                                            String readpair = line.substring(2);

                                            if (pair.isEmpty())
                                                System.out.println(readpair);
                                            else if (pair.split(" ")[0]
                                                    .matches(readpair
                                                            .split(" ")[0])
                                                    && pair.split(" ")[1]
                                                            .matches(readpair
                                                                    .split(" ")[1]))
                                                while (scanner.hasNextLine()) {
                                                    System.out.println(scanner
                                                            .nextLine().split(
                                                                    " ")[0]);
                                                }
                                        }
                                    }
                                } catch (FileNotFoundException e) {
                                    LogHandler.getLogs().displayMsg(
                                            e.getMessage(), Logging.ERROR);
                                } catch (Exception e) {
                                    LogHandler.getLogs().displayMsg(
                                            e.getMessage(), Logging.ERROR);
                                }

                            }
                        }
                    }
                }
            }
        }

    }

    private void printHelp() {
        LogHandler.getLogs().displayMsg("CALID Result Manager usage:\n",
                Logging.SILENT);
        String msg = "";
        msg += "--calid-result help\t\tdisplay this information\n";
        msg += "--calid-result list [<args>]\tlist all available pairs with results [name1 name2]";
        System.out.println(msg);
    }
}
