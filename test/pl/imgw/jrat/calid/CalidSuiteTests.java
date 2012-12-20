/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pl.imgw.jrat.calid.CalidContainerTest;
import pl.imgw.jrat.calid.CalidFileHandlerTest;
import pl.imgw.jrat.calid.CalidManagerTest;
import pl.imgw.jrat.calid.CalidProcessTest;
import pl.imgw.jrat.calid.CalidResultsTest;
import pl.imgw.jrat.calid.PairTest;
import pl.imgw.jrat.calid.PairsContainerTest;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ PairsContainerTest.class, PairTest.class,
        CalidManagerTest.class, CalidProcessTest.class, CalidResultsTest.class,
        CalidContainerTest.class, CalidFileHandlerTest.class })


public class CalidSuiteTests {

}
