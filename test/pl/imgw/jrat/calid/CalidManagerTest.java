/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 *
 *  Tests CALID arguments parser
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidManagerTest {

    CalidParsedParameters params = new CalidParsedParameters(); 
    String[] args;


    @Test
    public void parserTest() {
        
        args = "ele=91 dis=500 ref=5.0 date=2011-01-01/00:00,2011-12-31/23:59".split(" ");
        assertTrue("Parsing elevation should failed", !params.initialize(args));
        
        args = "ele=0.5 dis=-1 ref=5.0 date=2011-01-01/00:00,2011-12-31/23:59".split(" ");
        assertTrue("Parsing distance should failed", !params.initialize(args));
        
        args = "ele=0.5 dis=500 ref=a date=2011-01-01/00:00,2011-12-31/23:59".split(" ");
        assertTrue("Parsing reflectivity should failed", !params.initialize(args));
        
        args = "ele=0.5 dis=500 ref=a date=2011-13-01/00:00,2011-12-31/23:59".split(" ");
        assertTrue("Parsing date should failed", !params.initialize(args));
        
        args = "ele=0.5 dis=500 ref=5.0 date=2011-12-31/15:18".split(" ");
        assertTrue("Parsing all parameters failed", params.initialize(args));
        
        int dis = params.getDistance();
        double elevation = params.getElevation();
        double reflectivity = params.getReflectivity();
        Date date1 = params.getDate1();
        Date date2 = params.getDate2();
        
        assertEquals(500, dis);
        assertEquals(0.5, elevation, 0.01);
        assertEquals(5.0, reflectivity, 0.01);
        
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 11, 31, 15, 18, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertTrue(date1.equals(cal.getTime()));
//        cal.set(2011, 11, 31, 23, 59);
        assertTrue(date2.equals(cal.getTime()));
        
        args = "ele=0.5 dis=500 ref=5.0 date=2012-09-19".split(" ");
        assertTrue("Parsing all parameters failed", params.initialize(args));
        
        cal.set(2012, 8, 19, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println(date1);
        assertTrue(params.getDate1().equals(cal.getTime()));
        cal.set(2012, 8, 19, 23, 59);
        assertTrue(params.getDate2().equals(cal.getTime()));
        
    }
    
    @Test
    public void parseDefaultTest() {
        
        int dis = params.getDistance();
        Double elevation = params.getElevation();
        double reflectivity = params.getReflectivity();
        Date date1 = params.getDate1();
        Date date2 = params.getDate2();
        
        assertEquals(0, elevation, 0.01);
        assertTrue(params.isDate1Default());
        assertTrue(params.isDate2Default());
        assertEquals(1000, dis);
        assertEquals(0.0, reflectivity, 0.01);
        
    }
    
}
