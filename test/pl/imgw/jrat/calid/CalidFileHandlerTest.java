/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        String[] par = "src=Rzeszow,Brzuchania ele=0.5 dis=500 ref=5.0 date=2012-06-03/17:40 range=200"
                .split(" ");
        CalidParsedParameters calid = new CalidParsedParameters();
        calid.initialize(par);

        CalidContainer cc = new CalidContainer(calid);
        assertTrue(CalidFileHandler.loadCoords(cc));
        assertEquals(90, cc.getPairedPointsList().size());

        assertTrue(CalidFileHandler.loadResults(cc, calid.getDate1()));
        assertTrue(cc.hasResults());

    }

}
