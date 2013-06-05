/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidParametersTest {

    CalidParameters params;
    private static final Integer DEFAULT_DIS = new Integer(1000);
    private static final Double DEFAULT_REF = new Double(0.0);
    private static final Double DEFAULT_ELE = new Double(0.0);
    private static final Integer DEFAULT_FREQ = new Integer(1);
    private static final Integer DEFAULT_RANGE = new Integer(200);
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        params = new CalidParameters();
    }


    /**
     * Test method for {@link pl.imgw.jrat.calid.data.CalidParameters#isElevationDefault()}.
     */
    @Test
    public void shouldBeElevationDefault() {
        assertTrue(params.isElevationDefault());
        assertNotNull(params.getElevation());
        assertEquals(DEFAULT_ELE, params.getElevation());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.CalidParameters#isDistanceDefault()}.
     */
    @Test
    public void shouldBeDistanceDefault() {
        assertTrue(params.isDistanceDefault());
        assertNotNull(params.getDistance());
        assertEquals(DEFAULT_DIS, params.getDistance());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.CalidParameters#isFrequencyDefault()}.
     */
    @Test
    public void shouldBeFrequencyDefault() {
        assertTrue(params.isFrequencyDefault());
        assertNotNull(params.getFrequency());
        assertEquals(DEFAULT_FREQ, params.getFrequency());
                
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.CalidParameters#isReflectivityDefault()}.
     */
    @Test
    public void shouldBeReflectivityDefault() {
        assertTrue(params.isReflectivityDefault());
        assertNotNull(params.getReflectivity());
        assertEquals(DEFAULT_REF, params.getReflectivity());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.CalidParameters#isReflectivityDefault()}.
     */
    @Test
    public void shouldBeRangeDefault() {
        assertTrue(params.isMaxRangeDefault());
        assertNotNull(params.getMaxRange());
        assertEquals(DEFAULT_RANGE, params.getMaxRange());
    }
    
    /**
     * Test method for {@link pl.imgw.jrat.calid.data.CalidParameters#isStartDateDefault()}.
     */
    @Test
    public void shouldBeStartDateDefault() {
        assertTrue(params.isStartDateDefault());
        assertNotNull(params.getStartRangeDate());
    }

    /**
     * Test method for {@link pl.imgw.jrat.calid.data.CalidParameters#isEndDateDefault()}.
     */
    @Test
    public void shouldBeEndDateDefault() {
        assertTrue(params.isEndDateDefault());
        assertNotNull(params.getEndRangeDate());
    }

}
