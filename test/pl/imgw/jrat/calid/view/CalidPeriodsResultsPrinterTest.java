/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.view;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.calid.view.CalidPeriodsResultsPrinter;
import pl.imgw.jrat.tools.out.FileResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;
import pl.imgw.util.ConsolePrinter;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidPeriodsResultsPrinterTest {

    {
        LogManager.getInstance().setLogger(new ConsolePrinter(Log.MODE_VERBOSE));

    }
    private File f = new File("test-data/calid/out.txt");
    ResultPrinter pr = null;
    CalidPeriodsResultsPrinter printer;
    Set<File> files = new HashSet<File>();

    @Before
    public void setUp() throws IOException {
        String[] calidargs = ("date=2013-05-10/3:00,date=2013-05-12/13:20 period=1")
                .split(" ");
        
        printer = new CalidPeriodsResultsPrinter(calidargs, 1);
        files.add(new File("test-data/calid", "20130511.results"));
        files.add(new File("test-data/calid", "20130512.results"));
        f.delete();
        pr = new FileResultPrinter(f);
        ResultPrinterManager.getManager().setPrinter(pr);

    }

    @After
    public void tearDown() {
        ((FileResultPrinter) pr).closeFile();
        f.delete();
    }

    /**
     * Test method for
     * {@link pl.imgw.jrat.calid.view.CalidPeriodsResultsPrinter#printResults(java.util.Set)}
     * .
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void shouldPrintResultsSetOfFile() throws FileNotFoundException {
        printer.printResults(files);
        Scanner s = new Scanner(f);
        s.nextLine();
        String mean1 = s.nextLine();
        assertEquals("2013-05-11;03:10", mean1.split("\t")[0]);
        assertEquals("0.77", mean1.split("\t")[1]);
        assertEquals("2.42", mean1.split("\t")[2]);
        mean1 = s.nextLine();
        assertEquals("2013-05-12;03:10", mean1.split("\t")[0]);
        assertEquals("3.56", mean1.split("\t")[1]);
        assertEquals("5.12", mean1.split("\t")[2]);
        assertTrue(!s.hasNextLine());
        s.close();

    }

}
