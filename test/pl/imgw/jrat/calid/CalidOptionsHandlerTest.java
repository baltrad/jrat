/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

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
public class CalidOptionsHandlerTest {

    {
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
        CalidOptionsHandler.getOptions().setOptionFile("test-data/calid/calid.opt");
    }

    @Test
    public void loadPairParametersTest() {

        assertTrue(CalidOptionsHandler.getOptions().isSet());
        
        Pair pair = new Pair("Brzuchania", "Rzeszow");
        assertTrue(CalidOptionsHandler.getOptions().getPairParam(pair) != null);
        CalidParsedParameters params = CalidOptionsHandler.getOptions()
                .getPairParam(pair);
        
        assertEquals(200, params.getMaxRange().intValue());
        assertEquals(500, params.getDistance().intValue());
        assertEquals(3.0, params.getReflectivity().doubleValue(), 0.1);
        assertEquals(0.5, params.getElevation().doubleValue(), 0.01);
        
//        System.out.println(params.getElevation());
//        System.out.println(params.getReflectivity());
        
        pair = new Pair("Brzuchania", "Ramza");
        assertTrue(CalidOptionsHandler.getOptions().getPairParam(pair) != null);

        params = CalidOptionsHandler.getOptions()                
                .getPairParam(pair);
        
//        System.out.println(params.getMaxRange());
//        System.out.println(params.getDistance());
//        System.out.println(params.getElevation());
//        System.out.println(params.getReflectivity());

    }

    @Test
    public void loadRadarParametersTest() {
        List<File> list = CalidOptionsHandler.getOptions().getInputFolderList();
        assertTrue(!list.isEmpty());
        for(File f : list) {
            System.out.println("folder added: " + f);
        }
    }
    
}

