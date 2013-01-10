/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidFileHandlerTest {

    @Test
    public void loadingTest() {

        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);

        String[] par = "src=Rzeszow,Brzuchania ele=0.5 dis=500 ref=5.0 date=2011-08-21/13:40 range=250"
                .split(" ");
        CalidParsedParameters calid = new CalidParsedParameters();
        calid.initialize(par);

        CalidContainer cc = new CalidContainer(calid);
        assertTrue(CalidFileHandler.loadCoords(cc));
        assertTrue(cc.getPairedPointsList().size() == 101);

        assertTrue(CalidFileHandler.loadResults(cc, calid.getDate1()));
        assertTrue(cc.hasResults());

    }

}
