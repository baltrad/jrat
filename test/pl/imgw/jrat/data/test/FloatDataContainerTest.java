/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.FloatDataContainer;





/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class FloatDataContainerTest {

    private FloatDataContainer dc;
    private int x = 100;
    private int y = 100;
    private float[][] data = {{20.3f, 1.1f}, {11.2f, 200.1f}};
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dc = new FloatDataContainer(data);
//        dc.initialize(x, y);
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.FloatDataContainer#initialize(int, int)}.
     */
    @Test
    public void testInitialize() {
        
        dc.initialize(x, y);
        assertEquals("Container is not empty", 0, dc.getRawIntPoint(20, 20));
        assertEquals("Out of bounds should return -1", -1, dc.getRawIntPoint(x+1, 80));
        assertEquals("Out of bounds should return 0", 0, dc.getRawBytePoint(x+1, 80));
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.FloatDataContainer#getRawIntPoint(int, int)}.
     */
    @Test
    public void testGetIntPoint() {
        assertEquals("Converting from float to int error", 20, dc.getRawIntPoint(0, 0));
    }


    /**
     * Test method for {@link pl.imgw.jrat.data.FloatDataContainer#getRawBytePoint(int, int)}.
     */
    @Test
    public void testGetBytePoint() {
        assertEquals("Converting from int to byte error", 1, dc.getRawBytePoint(0, 1));
        assertEquals("Converting from int to byte error", -56, dc.getRawBytePoint(1, 1));
    }

    /**
     * Test method for {@link java.lang.Object#clone()}.
     */
    @Test
    public void testClone() {
        dc = new FloatDataContainer(data);
        FloatDataContainer dcClone = (FloatDataContainer) dc.clone();
        assertEquals("Cloning error", 1, dcClone.getRawIntPoint(0, 1));
        assertEquals("Cloning error", 20, dcClone.getRawIntPoint(0, 0));
        assertEquals("Cloning error", 1, dcClone.getRawBytePoint(0, 1));
        assertEquals("Cloning error", -56, dcClone.getRawBytePoint(1, 1));
    }



}
