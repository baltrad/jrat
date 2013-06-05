/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.tools.out.FileResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;
import pl.imgw.util.ConsolePrinter;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidControllerTest {

    {
        LogManager.getInstance().setLogger(new ConsolePrinter(Log.MODE_SILENT));

    }
    
    String[] args;
    
    private File f = new File("test-data/calid/out.txt");
    ResultPrinter pr = null;

    @Before
    public void setUp() throws IOException {
        f.delete();

        pr = new FileResultPrinter(f);
        ResultPrinterManager.getManager().setPrinter(pr);

    }

    @After
    public void closing() {
        ((FileResultPrinter) pr).closeFile();
        f.delete();
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidController#processResult(java.lang.String[])}.
     */
    @Test(expected=CalidException.class)
    public void shouldntProcessResult() {
        args = "".split(" ");
        CalidController.processResult(args);
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidController#processResult(java.lang.String[])}.
     * @throws FileNotFoundException 
     */
    @Test
    public void shouldProcessResultByPair() throws FileNotFoundException {
        args = new String[]{ "Swidwin,Gdansk" };
        
        CalidController.processResult(args);
        Scanner s = new Scanner(f);
        int i = 0;
        while (s.hasNextLine()) {
            String a = s.nextLine();
//            System.out.println(a);
            if (!a.isEmpty() && !a.startsWith("#"))
                i++;
        }
        assertEquals(1147, i);
        s.close();
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidController#processResult(java.lang.String[])}.
     * @throws FileNotFoundException 
     */
    @Test
    public void shouldProcessResultWithPeriod() throws FileNotFoundException {
        args = "date=2013-04-05,2013-04-10 period=1 Swidwin".split(" ");
        CalidController.processResult(args);
        Scanner s = new Scanner(f);
        int i = 0;
        while (s.hasNextLine()) {
            String a = s.nextLine();
//            System.out.println(a);
            if (!a.isEmpty() && !a.startsWith("#"))
                i++;
        }
        assertEquals(10, i);
        s.close();
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidController#processResult(java.lang.String[])}.
     * @throws FileNotFoundException 
     */
    @Test
    public void shouldProcessResultByPairAndDate() throws FileNotFoundException {
        args = "date=2013-04-01 Swidwin,Gdansk".split(" ");
        CalidController.processResult(args);
        Scanner s = new Scanner(f);
        int i = 0;
        while (s.hasNextLine()) {
            String a = s.nextLine();
//            System.out.println(a);
            if (!a.isEmpty() && !a.startsWith("#"))
                i++;
        }
        assertEquals(68, i);
        s.close();
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidController#processResult(java.lang.String[])}.
     * @throws FileNotFoundException 
     */
    @Test
    public void shouldProcessResultByDate() throws FileNotFoundException {
        args = "date=2013-04-01,2013-04-05".split(" ");
        CalidController.processResult(args);
        Scanner s = new Scanner(f);
        int i = 0;
        while (s.hasNextLine()) {
            String a = s.nextLine();
//            System.out.println(a);
            if (!a.isEmpty() && !a.startsWith("#"))
                i++;
        }
        assertEquals(329, i);
        s.close();
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidController#processList(java.lang.String[])}.
     * @throws FileNotFoundException 
     */
    @Test
    public void shouldProcessListByPair() throws FileNotFoundException {
        args = "Swidwin,Gdansk".split(" ");
        CalidController.processList(args);
        Scanner s = new Scanner(f);
        int i = 0;
        while (s.hasNextLine()) {
            String a = s.nextLine();
//            System.out.println(a);
            if (!a.isEmpty() && !a.startsWith("#"))
                i++;
        }
        assertEquals(30, i);
        s.close();
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidController#processList(java.lang.String[])}.
     * @throws FileNotFoundException 
     */
    @Test
    public void shouldProcessListBySrc() throws FileNotFoundException {
        args = "Swidwin".split(" ");
        CalidController.processList(args);
        Scanner s = new Scanner(f);
        int i = 0;
        while (s.hasNextLine()) {
            String a = s.nextLine();
//            System.out.println(a);
            if (!a.isEmpty() && !a.startsWith("#"))
                i++;
        }
        assertEquals(58, i);
        s.close();
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidController#processPlot(java.lang.String[], java.io.File)}.
     */
    @Test
    public void shouldProcessPlot() {
        args = "date=2013-04 Swidwin,Gdansk".split(" ");
        File output = new File("test-data/calid/plot.png");
        output.delete();
        CalidController.processPlot(args, output);
        assertTrue(output.exists());
        output.delete();
    }
    
}
