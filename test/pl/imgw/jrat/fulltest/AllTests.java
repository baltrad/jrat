/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.fulltest;

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
import pl.imgw.jrat.controller.ProcessControllerTest;
import pl.imgw.jrat.data.ByteDataContainerTest;
import pl.imgw.jrat.data.DoubleDataContainerTest;
import pl.imgw.jrat.data.FloatDataContainerTest;
import pl.imgw.jrat.output.LogHandlerTest;
import pl.imgw.jrat.parsers.HDF5ParserTest;
import pl.imgw.jrat.parsers.IntArrayParserTest;
import pl.imgw.jrat.parsers.RainbowCMAXParserTest;
import pl.imgw.jrat.parsers.RainbowVolumeParserTest;
import pl.imgw.jrat.parsers.WZParserTest;
import pl.imgw.jrat.tools.in.FilePatternFilterTest;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ ProcessControllerTest.class, ByteDataContainerTest.class,
        DoubleDataContainerTest.class, FloatDataContainerTest.class,
        LogHandlerTest.class, HDF5ParserTest.class, IntArrayParserTest.class,
        RainbowCMAXParserTest.class, RainbowVolumeParserTest.class,
        WZParserTest.class, FilePatternFilterTest.class,
        PairsContainerTest.class, PairTest.class, CalidManagerTest.class,
        CalidProcessTest.class, CalidResultsTest.class,
        CalidContainerTest.class, CalidFileHandlerTest.class })

public class AllTests {

}
