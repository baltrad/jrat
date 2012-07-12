/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import static pl.imgw.jrat.AplicationConstans.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class LogHandler implements Logging {

    private static LogHandler logs = new LogHandler();
    private int verbose = 1;

    private String msgFormat = "[dd/MM/yy HH:mm:ss] ";
    private SimpleDateFormat msgDate = new SimpleDateFormat(msgFormat);

    private String fileFormat = "yyyyMMddHHmm";
    private SimpleDateFormat fileDate = new SimpleDateFormat(fileFormat);

    private File errorFile = null;
    private File generalFile = null;
    private File recantFile = null;

    private int errorFreq = 0;
    private int generalFreq = 0;
    private int recantFreq = 0;

    private LogHandler() {
        // avoid calling default constructor
    }
    
    /**
     * Receiving instance of this class.
     * 
     * @return
     */
    public static LogHandler getLogs() {
        return logs;
    }

    
    public void printVersion() {
        displayMsg(APS_DESC + " version: " + VERSION,
                LogsType.SILENT);
    }
    
    /*
     * type should be a 3-letter identification code e.g. for general: 'gen'
     * 
     */
    private String getFileName(int frequency, String type) {
        if(frequency < 1 || frequency > 24)
            frequency = 24;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        hour = (hour / frequency) * frequency;
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return fileDate.format(cal.getTime()) + type + ".log";
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.output.Logging#setGeneralLogsPath(java.io.File, int)
     */
    @Override
    public void setGeneralLogsPath(File path, int frequency) {
        path.mkdirs();
        this.generalFile = path;
        this.generalFreq = frequency;

    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.output.Logging#setRecantFileLogsPath(java.io.File, int)
     */
    @Override
    public void setRecantFileLogsPath(File path, int frequency) {
        path.mkdirs();
        this.recantFile = path;
        this.recantFreq = frequency;

    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.output.Logging#setErrorLogsPath(java.io.File, int)
     */
    @Override
    public void setErrorLogsPath(File path, int frequency) {
        path.mkdirs();
        this.errorFile = path;
        this.errorFreq = frequency;

    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.output.Logging#displayMsg(java.lang.String, int)
     */
    @Override
    public void displayMsg(String msg, int type) {
        if (type <= verbose) {
            if (type == LogsType.ERROR) {
                msg += ".\nFor more details see log file.";
            }
            System.out.println(msgDate.format(new Date()) + msg);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.output.Logging#setLoggingVerbose(int)
     */
    @Override
    public void setLoggingVerbose(int verbose) {
        this.verbose = verbose;

    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.output.Logging#saveGeneralLogs(java.lang.String)
     */
    @Override
    public void saveGeneralLogs(String msg) {
        saveLineToFile(new File(generalFile, getFileName(generalFreq, "gen")),
                msgDate.format(new Date()) + msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.output.Logging#saveRecantFileLogs(java.io.File)
     */
    @Override
    public void saveRecantFileLogs(File file) {
        String folder = file.getAbsolutePath();
        String msg = "New file: '" + file.getName() + "' in " + folder;
        saveLineToFile(new File(recantFile, getFileName(recantFreq, "rec")),
                msgDate.format(new Date()) + msg);

    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.output.Logging#saveErrorLogs(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void saveErrorLogs(String className, String msg) {
        String line = msgDate.format(new Date()) + "[" + className + "]: "
                + msg;
        saveLineToFile(new File(errorFile, getFileName(errorFreq, "err")), line);

    }
    
    /**
     * Helping method
     * 
     * @param file
     * @param line
     */
    private void saveLineToFile(File file, String line) {
        PrintWriter pw = null;
        try {
            if (!file.exists())
                file.createNewFile();
            pw = new PrintWriter(new FileOutputStream(file, true), true);
            pw.println(line);
        } catch (FileNotFoundException e) {
            logs.displayMsg("Log file " + file.getName() + " is missing" + "\n"
                    + e.getMessage(), 3);
        } catch (IOException e) {
            logs.displayMsg(
                    "Cannot create log file in path " + file.getAbsolutePath()
                            + "\n" + e.getMessage(), 3);

        } finally {
            if (pw != null)
                pw.close();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("freq=-1\t" + LogHandler.getLogs().getFileName(-1, ""));
        System.out.println("freq=1\t" + LogHandler.getLogs().getFileName(1, ""));
        System.out.println("freq=6\t" + LogHandler.getLogs().getFileName(6, ""));
        System.out.println("freq=24\t" + LogHandler.getLogs().getFileName(24, ""));
        System.out.println("freq=28\t" + LogHandler.getLogs().getFileName(28, ""));
    }

}