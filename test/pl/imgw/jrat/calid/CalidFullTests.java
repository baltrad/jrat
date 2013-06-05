/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pl.imgw.jrat.calid.data.CalidCoordsLoaderTest;
import pl.imgw.jrat.calid.data.CalidDataSaverTest;
import pl.imgw.jrat.calid.data.CalidParametersParserTest;
import pl.imgw.jrat.calid.data.CalidParametersTest;
import pl.imgw.jrat.calid.data.CalidResultLoaderTest;
import pl.imgw.jrat.calid.data.PairsContainerTest;
import pl.imgw.jrat.calid.data.PolarVolumesPairTest;
import pl.imgw.jrat.calid.data.RadarsPairTest;
import pl.imgw.jrat.calid.proc.CalidComparatorManagerTest;
import pl.imgw.jrat.calid.proc.CalidComparatorTest;
import pl.imgw.jrat.calid.proc.CalidControllerTest;
import pl.imgw.jrat.calid.proc.CalidCoordsCalcTest;
import pl.imgw.jrat.calid.proc.CalidCoordsHandlerTest;
import pl.imgw.jrat.calid.proc.CalidProcessorTest;
import pl.imgw.jrat.calid.proc.ElevationValidatorTest;
import pl.imgw.jrat.calid.view.CalidResultFileGetterTest;
import pl.imgw.jrat.calid.view.CalidResultsPrinterTest;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ CalidParametersTest.class, CalidParametersParserTest.class,
        CalidProcessorTest.class, PairsContainerTest.class,
        RadarsPairTest.class, PolarVolumesPairTest.class,
        CalidComparatorTest.class, CalidComparatorManagerTest.class,
        ElevationValidatorTest.class, CalidCoordsCalcTest.class,
        CalidCoordsHandlerTest.class, CalidCoordsLoaderTest.class,
        CalidResultLoaderTest.class, CalidDataSaverTest.class,
        CalidResultsPrinterTest.class, CalidResultFileGetterTest.class,
        CalidControllerTest.class })
public class CalidFullTests {

}
