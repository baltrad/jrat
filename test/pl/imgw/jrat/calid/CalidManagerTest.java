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

    CalidParsedParameters manager = new CalidParsedParameters(); 
    String[] args;


    @Test
    public void parserTest() {
        
        args = "ele=91 dis=500 ref=5.0 date=2011-01-01/00:00,2011-12-31/23:59".split(" ");
        assertTrue("Parsing elevation should failed", !manager.initialize(args));
        
        args = "ele=0.5 dis=-1 ref=5.0 date=2011-01-01/00:00,2011-12-31/23:59".split(" ");
        assertTrue("Parsing distance should failed", !manager.initialize(args));
        
        args = "ele=0.5 dis=500 ref=a date=2011-01-01/00:00,2011-12-31/23:59".split(" ");
        assertTrue("Parsing reflectivity should failed", !manager.initialize(args));
        
        args = "ele=0.5 dis=500 ref=a date=2011-13-01/00:00,2011-12-31/23:59".split(" ");
        assertTrue("Parsing date should failed", !manager.initialize(args));
        
        args = "ele=0.5 dis=500 ref=5.0 date=2011-12-31/15:18".split(" ");
        assertTrue("Parsing failed", manager.initialize(args));
        
        int dis = manager.getDistance();
        double elevation = manager.getElevation();
        double reflectivity = manager.getReflectivity();
        Date date1 = manager.getDate1();
        Date date2 = manager.getDate2();
        
        assertEquals(500, dis);
        assertEquals(0.5, elevation, 0.01);
        assertEquals(5.0, reflectivity, 0.01);
        
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 11, 31, 15, 18, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertTrue(date1.equals(cal.getTime()));
        cal.set(2011, 11, 31, 23, 59);
        assertTrue(date2.equals(cal.getTime()));
        
    }
    
    @Test
    public void parseDefaultTest() {
        
        int dis = manager.getDistance();
        Double elevation = manager.getElevation();
        double reflectivity = manager.getReflectivity();
        Date date1 = manager.getDate1();
        Date date2 = manager.getDate2();
        
        assertEquals(0, elevation, 0.01);
        assertNull(date1);
        assertNull(date2);
        assertEquals(1000, dis);
        assertEquals(0.0, reflectivity, 0.01);
        
    }
    
}
