/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.proc;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.CalidParametersFileHandler;
import pl.imgw.jrat.calid.data.CalidParametersParser;
import pl.imgw.jrat.calid.data.CalidSingleResultContainer;
import pl.imgw.jrat.calid.data.PolarVolumesPair;
import pl.imgw.jrat.calid.data.RadarsPair;
import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.VolumeParser;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidComparatorManagerTest {

    CalidComparatorManager manager;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        manager = new CalidComparatorManager();
        String[] par = ("date=2013-03-18,2013-03-30 Rzeszow,Brzuchania "
                + "ele=0.5 dis=500 range=200 ref=4.5 freq=10").split(" ");
        CalidParameters params = CalidParametersParser.getParser().parseParameters(par);
        manager.setParsedParameters(params);
        CalidParametersFileHandler.getOptions().setOptionFile(
                "test-data/calid/calid.opt");
        manager.setOptionFileParameters(CalidParametersFileHandler.getOptions()
                .getParametersForAllPairs());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidComparatorManager#getParsedParameters()}.
     */
    @Test
    public void shouldGetParsedParameters() {
        assertNotNull(manager.getParsedParameters());
        assertEquals(0.5, manager.getParsedParameters().getElevation(), 0.01);
        assertEquals(4.5, manager.getParsedParameters().getReflectivity().doubleValue(), 0.1);
        
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidComparatorManager#getOptionFilePairParameters(pl.imgw.jrat.calid.data.RadarsPair)}.
     */
    @Test
    public void shouldGetPairParameters() {
        RadarsPair pair = new RadarsPair("Rzeszow", "Brzuchania");
        assertNotNull(manager.getOptionFilePairParameters(pair));
        assertEquals(3.0, manager.getOptionFilePairParameters(pair)
                .getReflectivity().doubleValue(), 0.1);

    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.proc.CalidComparatorManager#compare(pl.imgw.jrat.calid.data.PolarVolumesPair)}.
     */
    @Test
    public void shouldCompare() {
        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
        parser.parse(new File("test-data/pair", "2011101003102200dBZ.vol"));
        PolarData vol1 = parser.getPolarData();
        parser.parse(new File("test-data/pair", "2011101003102600dBZ.vol"));
        PolarData vol2 = parser.getPolarData();
        PolarVolumesPair pair = new PolarVolumesPair(vol1, vol2);
        CalidSingleResultContainer result = manager.compare(pair);
        assertNotNull(result);
        
    }

}
