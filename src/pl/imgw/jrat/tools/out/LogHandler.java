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

import org.apache.commons.lang.SystemUtils;

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

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    
    private static LogHandler logs = new LogHandler();
    private int verbose = Logging.PROGRESS_BAR_ONLY;

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

    public int getVerbose() {
        return verbose;
    }
    
    public void printVersion() {
        displayMsg(APS_DESC + 
                "\nversion:\t" + VERSION + 
                "\nreleased date:\t" + REL_DATE +
                "\ncompiled on:\t" + DATE,
                SILENT);
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
            if (type == Logging.ERROR) {
                msg += ".\nFor more details see log file.";
            }
            if (SystemUtils.IS_OS_UNIX) {
                if (type == Logging.ERROR) {
                    System.out.print(ANSI_RED);
                } else if (type == Logging.WARNING) {
                    System.out.print(ANSI_YELLOW);
                } else if (type == Logging.NORMAL) {
                    System.out.print(ANSI_BLUE);
                }
            }
            System.out.println(msgDate.format(new Date()) + msg);
            if (SystemUtils.IS_OS_UNIX) {
                System.out.print(ANSI_RESET);
            }
            
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
     * 
     * Save class name and exception localized message to error log file
     * 
     * @param obj
     * @param e
     */
    public void saveErrorLogs(Object obj, Exception e) {
        saveErrorLogs(obj.getClass().getName(), e.getLocalizedMessage());

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
