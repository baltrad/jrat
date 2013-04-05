/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.*;

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
        CalidOptionsHanlder.getOptions().setOptionFile("test-data/calid/calid.opt");
    }

    @Test
    public void loadOptionsTest() {

        assertTrue(CalidOptionsHanlder.getOptions().isSet());
        
        Pair pair = new Pair("Brzuchania", "Rzeszow");
        assertTrue(CalidOptionsHanlder.getOptions().getParam(pair) != null);
        CalidParsedParameters params = CalidOptionsHanlder.getOptions()
                .getParam(pair);
        
        assertEquals(200, params.getMaxRange().intValue());
        assertEquals(500, params.getDistance().intValue());
        assertEquals(3.0, params.getReflectivity().doubleValue(), 0.1);
        assertEquals(0.5, params.getElevation().doubleValue(), 0.01);
        
//        System.out.println(params.getElevation());
//        System.out.println(params.getReflectivity());
        
        pair = new Pair("Brzuchania", "Ramza");
        assertTrue(CalidOptionsHanlder.getOptions().getParam(pair) != null);

        params = CalidOptionsHanlder.getOptions()                
                .getParam(pair);
        
//        System.out.println(params.getMaxRange());
//        System.out.println(params.getDistance());
//        System.out.println(params.getElevation());
//        System.out.println(params.getReflectivity());

    }

}

