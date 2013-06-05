/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.calid.CalidException;
import pl.imgw.util.ConsolePrinter;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidParametersParserTest {

    {
        LogManager.getInstance().setLogger(new ConsolePrinter(Log.MODE_VERBOSE));

    }
    
    CalidParametersParser parser = CalidParametersParser.getParser();
    
    String[] args = ("date=2013-03-18,2013-03-30 Rzeszow,Brzuchania "
            + "ele=0.5 dis=500 range=200 ref=3.5 freq=10").split(" ");

    @Test(expected=CalidException.class)
    public void shouldThrowExceptionElevation() {
        args = "ele=91 dis=500 ref=5.0 date=2011-01-01/00:00,2011-12-31/23:59".split(" ");
        parser.parseParameters(args);
        
    }
    
    @Test(expected=CalidException.class)
    public void shouldThrowExceptionDistance() {
        args = "ele=0.5 dis=-1 ref=5.0 date=2011-01-01/00:00,2011-12-31/23:59".split(" ");
        parser.parseParameters(args);
        
    }
    
    @Test(expected=CalidException.class)
    public void shouldThrowExceptionReflectivity() {
        args = "ele=0.5 dis=500 ref=a date=2011-01-01/00:00,2011-12-31/23:59".split(" ");
        parser.parseParameters(args);
        
    }
    
    @Test(expected=CalidException.class)
    public void shouldThrowExceptionDate() {
        args = "ele=0.5 dis=500 ref=3.5 date=2011-13-01/00:00,2011-12-31/23:59".split(" ");
        parser.parseParameters(args);
        
    }
    
    @Test(expected=CalidException.class)
    public void shouldThrowExceptionNoSuchParameter() {
        args = "ele=0.5 dis=500 ref=3.5 data=2013-01-01".split(" ");
        parser.parseParameters(args);
        
    }
    
    @Test
    public void shouldGetParamsFromFolder() {
        String folderName = "500_0.5_3.5_200";
        CalidParameters params = parser.getParamsFromFolderName(folderName);
        double ele = 0.5;
        int dis = 500;
        int range = 200;
        double ref = 3.5;
        
        assertEquals(ele, params.getElevation(), 0.01);
        assertEquals(dis, params.getDistance().intValue());
        assertEquals(range, params.getMaxRange().intValue());
        assertEquals(ref, params.getReflectivity(), 0.01);
    }
    
    /**
     * Test method for
     * {@link pl.imgw.jrat.calid.data.CalidParameters#parseFromArray(java.lang.String[])}
     * .
     */
    @Test
    public void shouldParseFromArray() {
        CalidParameters params = parser.parseParameters(args);
        Date startDate = new Date(113, 2, 18);
        Date endDate =  new Date(113, 2, 30, 23, 59);
        double ele = 0.5;
        int dis = 500;
        int range = 200;
        double ref = 3.5;
        int freq = 10;
        

        assertEquals(ele, params.getElevation(), 0.01);
        assertEquals(dis, params.getDistance().intValue());
        assertEquals(range, params.getMaxRange().intValue());
        assertEquals(ref, params.getReflectivity(), 0.01);
        assertEquals(freq, params.getFrequency().intValue());
        assertEquals(startDate, params.getStartRangeDate());
        assertEquals(endDate, params.getEndRangeDate());
        
    }
    /**
     * Test method for
     * {@link pl.imgw.jrat.calid.data.CalidParameters#parseFromArray(java.lang.String[])}
     * .
     */
    @Test
    public void shouldParseResultParametersFromArray() {
        CalidPairAndParameters params = parser.parsePairAndParameters(args);
        String src1 = "Rzeszow";
        String src2 = "Brzuchania";
        assertEquals(src1, params.getPair().getSource1());
        assertEquals(src2, params.getPair().getSource2());
    }
    /**
     * Test method for
     * {@link pl.imgw.jrat.calid.data.CalidParameters#parseFromArray(java.lang.String[])}
     * .
     */
    @Test
    public void shouldParseFromArrayDate1() {
        
        args = ("date=2013-03-18/10:15,2013-03-30/12:10").split(" ");
        
        CalidParameters params = parser.parseParameters(args);
        Date startDate = new Date(113, 2, 18, 10, 15);
        Date endDate =  new Date(113, 2, 30, 12, 10);
        
        
        
        assertEquals(startDate, params.getStartRangeDate());
        assertEquals(endDate, params.getEndRangeDate());
        
    }
    
    /**
     * Test method for
     * {@link pl.imgw.jrat.calid.data.CalidParameters#parseFromArray(java.lang.String[])}
     * .
     */
    @Test
    public void shouldParseFromArrayDate2() {
        
        args = ("date=2013-03-18/10,2013-03").split(" ");
        
        CalidParameters params = parser.parseParameters(args);
        Date startDate = new Date(113, 2, 18, 10, 00);
        Date endDate =  new Date(113, 2, 31, 23, 59);
        
        
        
        assertEquals(startDate, params.getStartRangeDate());
        assertEquals(endDate, params.getEndRangeDate());
        
    }
    
    /**
     * Test method for
     * {@link pl.imgw.jrat.calid.data.CalidParameters#parseFromArray(java.lang.String[])}
     * .
     */
    @Test
    public void shouldParseFromArrayDate3() {
        
        args = ("date=2013,2013-05").split(" ");
        
        CalidParameters params = parser.parseParameters(args);
        Date startDate = new Date(113, 0, 1, 0, 0);
        Date endDate =  new Date(113, 4, 31, 23, 59);
        
        assertEquals(startDate, params.getStartRangeDate());
        assertEquals(endDate, params.getEndRangeDate());
        
    }
    
    /**
     * Test method for
     * {@link pl.imgw.jrat.calid.data.CalidParameters#parseFromArray(java.lang.String[])}
     * .
     */
    @Test
    public void shouldParseFromArrayDate4() {
        
        args = ("date=2013").split(" ");
        
        CalidParameters params = parser.parseParameters(args);
        Date startDate = new Date(113, 0, 1, 0, 0);
        Date endDate =  new Date(113, 11, 31, 23, 59);
        
        assertEquals(startDate, params.getStartRangeDate());
        assertEquals(endDate, params.getEndRangeDate());
        
    }

}
