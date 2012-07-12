/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.output.test;

import static org.junit.Assert.*;

import java.io.File;
import java.text.SimpleDateFormat;

import org.junit.Test;

import pl.imgw.jrat.tools.out.LogHandler;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class LogHandlerTest {

    private File genFile = new File("test-data/log_gen");
    private File recFile = new File("test-data/log_rec");
    private File errFile = new File("test-data/log_err");
    private String fileFormat = "yyyyMMddHHmm";
    private SimpleDateFormat fileDate = new SimpleDateFormat(fileFormat);

    /**
     * Test method for {@link pl.imgw.jrat.tools.out.LogHandler#getLogs()}.
     */
    @Test
    public void getLogsTest() {
        LogHandler log1 = LogHandler.getLogs();
        LogHandler log2 = LogHandler.getLogs();
        assertSame("LogHandler is not a singleton", log1, log2);
        
    }
    
    @Test
    public void saveGeneralLogsTest() {
        deleteDir(genFile);
        LogHandler.getLogs().setGeneralLogsPath(genFile, 0);
        LogHandler.getLogs().saveGeneralLogs("test");
        assertTrue(genFile.listFiles().length > 0);
        File f = null;
        String s = "";
        try {
            f = genFile.listFiles()[0];
            s = fileDate.parse(f.getName()).toString();
        } catch (Exception e) {

        } finally {
            assertTrue("Log file wasn't saved", f.exists());
            assertTrue("Log file name: " + f.getName()
                    + " has bad format, should be: " + fileFormat, !s.isEmpty());
        }
    }
    
    @Test
    public void saveRecantFileLogsTest() {
        deleteDir(recFile);
        LogHandler.getLogs().setRecantFileLogsPath(recFile, 0);
        LogHandler.getLogs().saveRecantFileLogs(new File("test"));
        assertTrue(recFile.listFiles().length > 0);
        File f = null;
        String s = "";
        try {
            f = recFile.listFiles()[0];
            s = fileDate.parse(f.getName()).toString();
        } catch (Exception e) {

        } finally {
            assertTrue("Log file wasn't saved", f.exists());
            assertTrue("Log file name: " + f.getName()
                    + " has bad format, should be: " + fileFormat, !s.isEmpty());
        }
    }
    
    @Test
    public void saveErrorLogsTest() {
        deleteDir(errFile);
        LogHandler.getLogs().setErrorLogsPath(errFile, 0);
        LogHandler.getLogs().saveErrorLogs(this.getClass().getName(), "test");
        assertTrue(errFile.listFiles().length > 0);
        File f = null;
        String s = "";
        try {
            f = errFile.listFiles()[0];
            s = fileDate.parse(f.getName()).toString();
        } catch (Exception e) {

        } finally {
            assertTrue("Log file wasn't saved", f.exists());
            assertTrue("Log file name: " + f.getName()
                    + " has bad format, should be: " + fileFormat, !s.isEmpty());
        }
    }
    
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }
    
}
