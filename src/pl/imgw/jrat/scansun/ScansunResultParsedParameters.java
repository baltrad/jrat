/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import pl.imgw.jrat.scansun.ScansunConstants.Sites;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 * 
 */
public class ScansunResultParsedParameters {

    private static Log log = LogManager.getLogger();
    private static final String SITENAME = "sitename=";

    private String siteName;
    private boolean allSites = false;

    public boolean initialize(String[] par) {

        if (par == null) {
            allSites = true;
            return true;
        }

        String errorMsg = "SCANSUN: Arguments for SCANSUN are incorrect";

        try {
            for (int i = 0; i < par.length; i++) {
                if (par[i].startsWith(SITENAME)) {
                    siteName = par[i].substring(SITENAME.length());
                    allSites = false;
                } else {
                    log.printMsg(errorMsg + " (" + par[i] + ")",
                            Log.TYPE_WARNING, Log.MODE_VERBOSE);
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            log.printMsg(errorMsg + " (" + e.getLocalizedMessage() + ")",
                    Log.TYPE_WARNING, Log.MODE_VERBOSE);
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            log.printMsg(errorMsg, Log.TYPE_WARNING, Log.MODE_VERBOSE);
            return false;
        }

        /*
         * Checks if provided parameters are valid
         */
        if (!allSites) {
            if (!Sites.getSiteNames().contains(getSiteName())) {
                log.printMsg(errorMsg + ": " + getSiteName()
                        + " is not a valid sitename", Log.TYPE_WARNING,
                        Log.MODE_VERBOSE);
                return false;
            }
        }

        return true;
    }

    public static void printHelp() {

        String msg = "SCANSUN algorithm usage: jrat [options]\n";
        msg += "--scansun-help print this message\n\n";
        msg += "--scansun-result [<args>]."
                + "\n<args> "
                + "no-args: print all available results\n"
                + "sitename=<siteName>: print available results for particular siteName\n";
        msg += "\nuse jrat --help to print more general help message";

        System.out.println(msg);
    }

    public boolean allSites() {
        return allSites;
    }

    public String getSiteName() {
        return siteName;
    }

}
