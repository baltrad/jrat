/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.tools.out.FileResultPrinter;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultsTest {

    {
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
    }
    
    private CalidParsedParameters params = new CalidParsedParameters();
    private String[] args;
    private File f = new File("test-data/calid/out.txt");
    ResultPrinter pr = null;

    @Before
    public void setUp() {
        f.delete();
        try {
            pr = new FileResultPrinter(f);
            ResultPrinterManager.getManager().setPrinter(pr);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    @After
    public void closing() {
        ((FileResultPrinter) pr).closeFile();
        f.delete();
    }
    
    @Test
    public void printResultsListTest() {
        
        args = "src=Rzeszow,Brzuchania ele=0.5 dis=500 ref=5.0".split(" ");
        params.initialize(args);

        CalidResultsPrinter printer = new CalidResultsPrinter(params);

        printer.printList();
        
        try {
            Scanner s = new Scanner(f);
            int i = 0;
            while(s.hasNextLine()) {
                
                if(!s.nextLine().startsWith("#"))
                i++;
            }
            assertEquals(1, i);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    @Test
    public void printResultsTest() {
        args = "src=Rzeszow,Brzuchania ele=0.5 dis=500 ref=5.0 date=2012-06-03".split(" ");
        params.initialize(args);

        CalidResultsPrinter printer = new CalidResultsPrinter(params);
        printer.printResults();
        
        try {
            Scanner s = new Scanner(f);
            while(s.hasNextLine()) {
                String line = s.nextLine();
                System.out.println(line);
                if(!line.startsWith("#") && !line.isEmpty()) {
                    String[] res = line.split("\t");
                    assertEquals(40, Integer.parseInt(res[1].replaceAll("\\s", "")));
                    assertEquals(-9.54, Double.parseDouble(res[2]), 0.01);
                    assertEquals(10.11, Double.parseDouble(res[3]), 0.01);
                    assertEquals(-9.5, Double.parseDouble(res[4]), 0.01);
                    assertEquals(3, Integer.parseInt(res[5]));
                    assertEquals(1, Integer.parseInt(res[6]));
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
}
