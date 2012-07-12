/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.io.File;

/**
 * 
 * Hanling all logging stuff, saving logs to file and displaying to console.
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface Logging {

    /**
     * Setting path for general logs. Name of the file will depend on frequency.
     * 
     * @param path
     * @param frequency
     *            how often a new file should be created, in hours (1-24), for
     *            other values new file will be created daily
     */
    public void setGeneralLogsPath(File path, int frequency);

    /**
     * Setting path for recant file logs. Name of the file will depend on
     * frequency.
     * 
     * @param path
     * @param frequency
     *            how often a new file should be created, in hours (1-24), for
     *            other values new file will be created daily
     */
    public void setRecantFileLogsPath(File path, int frequency);

    /**
     * 
     * Setting path for error logs. Name of the file will depend on frequency.
     * 
     * @param path
     * @param frequency
     *            how often a new file should be created, in hours (1-24), for
     *            other values new file will be created daily
     */
    public void setErrorLogsPath(File path, int frequency);

    /**
     * 
     * Displaying a message to console.
     * 
     * @param msg
     * @param verbose
     *            0 if for silent mode, 1 only creation logs are displayed, 2
     *            initialization and creation logs, and 3 is for all logs
     *            including errors. For convenience use Use
     *            <code>LogsType</code>.
     */
    public void displayMsg(String msg, int type);

    /**
     * 
     * Setting verbose mode, meaning how often logs should be displayed in
     * console. See <code>LogsType</code>.
     * 
     * @param verbose
     *            0 if for silent mode, 1 initialization and creation logs, and
     *            2 is for all logs including debugging information.
     */
    public void setLoggingVerbose(int verbose);

    /**
     * General logs handler. File will be save in the path set by the other
     * method.
     * 
     * @param msg
     */
    public void saveGeneralLogs(String msg);

    /**
     * Recant file logs handler. File will be save in the path set by the other
     * method.
     * 
     * @param msg
     */
    public void saveRecantFileLogs(File file);

    /**
     * Error logs handler. File will be save in the path set by the other
     * method.
     * 
     * @param className
     * @param msg
     */
    public void saveErrorLogs(String className, String msg);

}
