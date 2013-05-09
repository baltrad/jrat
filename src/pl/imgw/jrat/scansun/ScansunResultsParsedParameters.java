/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.scansun.ScansunConstants.SCANSUN_OPTFILE_NAME;
import static pl.imgw.jrat.tools.out.Logging.WARNING;
import pl.imgw.jrat.scansun.ScansunConstants.Sites;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 * 
 */
public class ScansunResultsParsedParameters {

    private static final String SITENAME = "sitename=";

    private String siteName;
    private boolean allSites = false;

    public boolean initialize(String[] par) {

	if (par == null) {
	    allSites = true;
	    return true;
	}

	if (par.length == 0) {
	    allSites = true;
	    return true;
	}

	String errorMsg = "SCANSUN: Arguments for SCANSUN results are incorrect";

	try {
	    for (int i = 0; i < par.length; i++) {
		if (par[i].startsWith(SITENAME)) {
		    siteName = par[i].substring(SITENAME.length());
		    allSites = false;
		} else {
		    LogHandler.getLogs().displayMsg(errorMsg + " (" + par[i] + ")", WARNING);
		    return false;
		}
	    }
	} catch (NumberFormatException e) {
	    LogHandler.getLogs().displayMsg(errorMsg + " (" + e.getLocalizedMessage() + ")", WARNING);
	    return false;
	} catch (ArrayIndexOutOfBoundsException e) {
	    LogHandler.getLogs().displayMsg(errorMsg, WARNING);
	    return false;
	}

	/*
	 * Checks if provided parameters are valid
	 */
	if (!allSites) {
	    if (!Sites.getSiteNames().contains(getSiteName())) {
		LogHandler.getLogs().displayMsg(errorMsg + ": " + getSiteName() + " is not a valid sitename", WARNING);
		return false;
	    }
	}

	return true;
    }

    public static void printHelp() {

	String msg = "SCANSUN algorithm usage: jrat [options]\n";
	msg += "--scansun-help print this message\n\n";
	msg += "--scansun-result [<args>]." + "\n<args> " + "no-args: print all available results\n"
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
