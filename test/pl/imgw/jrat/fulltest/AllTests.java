/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.fulltest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pl.imgw.jrat.controller.ProcessControllerTest;
import pl.imgw.jrat.data.test.ByteDataContainerTest;
import pl.imgw.jrat.data.test.DoubleDataContainerTest;
import pl.imgw.jrat.data.test.FloatDataContainerTest;
import pl.imgw.jrat.output.test.LogHandlerTest;
import pl.imgw.jrat.parsers.test.HDF5ParserTest;
import pl.imgw.jrat.parsers.test.RainbowCMAXParserTest;
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
@SuiteClasses({ ByteDataContainerTest.class, LogHandlerTest.class,
        HDF5ParserTest.class, RainbowCMAXParserTest.class,
        DoubleDataContainerTest.class, FloatDataContainerTest.class,
        FilePatternFilterTest.class, ProcessControllerTest.class })
public class AllTests {

}
