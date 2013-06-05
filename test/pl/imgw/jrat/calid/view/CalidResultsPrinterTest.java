/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.view;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.view.CalidResultsPrinter;
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
public class CalidResultsPrinterTest {

    String[] args = ("date=2011-10-10/3:00,date=2011-10-10/3:20 Rzeszow,Brzuchania "
            + "ele=0.5 dis=500 range=200 ref=3.0").split(" ");

    {
        LogManager.getInstance().setLogger(new ConsolePrinter(Log.MODE_SILENT));

    }

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

    @Test
    public void shouldPrintResultsList() throws FileNotFoundException {

        args = "ele=0.5 dis=500 ref=3.0 date=2013-04-02 Poznan,Swidwin".split(" ");
        CalidResultsPrinter printer = new CalidResultsPrinter(args);

        printer.printList();

        Scanner s = new Scanner(f);
        int i = 0;
        while (s.hasNextLine()) {
            String a = s.nextLine();
            System.out.println(a);
            if (!a.startsWith("#"))
                i++;
        }
        assertEquals(4, i);
        s.close();

    }

    @Test
    public void shouldPrintResults() throws FileNotFoundException {
        args = "ele=0.5 dis=500 ref=3.0 date=2013-04-01/06:10 Poznan,Swidwin"
                .split(" ");

        CalidSingleResultPrinter printer = new CalidSingleResultPrinter(args);
//        printer.printList();
        printer.printResults();

        Scanner s = new Scanner(f);
        int i = 0;
        while (s.hasNextLine()) {
            String line = s.nextLine();
            System.out.println(line);
            if (!line.startsWith("#") && !line.isEmpty()) {
                i++;
                String[] res = line.split("\t");
                assertEquals(15, Integer.parseInt(res[1].replaceAll("\\s", "")));
                assertEquals(-3.27, Double.parseDouble(res[2]), 0.01);
                assertEquals(3.64, Double.parseDouble(res[3]), 0.01);
                assertEquals(-3.5, Double.parseDouble(res[4]), 0.01);
                assertEquals(0, Integer.parseInt(res[5]));
                assertEquals(0, Integer.parseInt(res[6]));
            }
        }
        s.close();
        assertEquals(1, i);

    }

}
