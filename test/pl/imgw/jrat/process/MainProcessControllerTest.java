/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 *
 *  This is just for a manual testing so far
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MainProcessControllerTest {

    {
        LogManager.getInstance().setLogMode(Log.MODE_VERBOSE);
    }
    
    MainProcessController main;
    String[] args;
    /**
     * Test method for {@link pl.imgw.jrat.process.MainProcessController#start()}.
     */
    @Test
    public void shouldStartWithCalidList() {
        args = "--calid-plot Swidwin date=2013-04 -v".split(" ");
        main = new MainProcessController(args);
        assertTrue(main.start());
        
    }

    @Test @Ignore
    public void shouldStartWithCalidResult() {
        args = "--calid-result".split(" ");
        main = new MainProcessController(args);
        assertTrue(main.start());
        
    }
    
    @Test @Ignore
    public void shouldAskForParameters() {
        System.out.println("start");
        args = "--calid-result".split(" ");
        MainProcessController proc = new MainProcessController(args);
        proc.start();
        System.out.println("koniec");
        
    }
    @Test @Ignore
    public void shouldQuickPrintResult() {
        System.out.println("start");
        args = "--calid-result Swidwin date=2013-04-01 freq =0 -v".split(" ");
        MainProcessController proc = new MainProcessController(args);
        proc.start();
        System.out.println("koniec");
    }
    @Test @Ignore
    public void shouldGeneratePlot() {
        System.out.println("start");
        args = "--calid-plot Swidwin,Gdansk date=2013-04-01,2013-04-30 -v".split(" ");
        MainProcessController proc = new MainProcessController(args);
        proc.start();
        System.out.println("koniec");
    }
    
    @Test @Ignore
    public void shouldGeneratePlotByDate() {
        System.out.println("start");
        args = "--calid-plot Swidwin,Gdansk -v".split(" ");
        MainProcessController proc = new MainProcessController(args);
        proc.start();
        System.out.println("koniec");
    }
    
    @Test @Ignore
    public void shouldNotHangWithYearsThatAreNotInDB() {
        System.out.println("start");
        args = "--calid ele=0.5 -i test-data/calid/2010100305000900dBZ.vol test-data/calid/2010100305002900dBZ.vol -v".split(" ");
        MainProcessController proc = new MainProcessController(args);
        proc.start();
        System.out.println("koniec");
    }
}
