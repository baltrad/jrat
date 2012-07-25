/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.RawByteDataContainer;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ByteDataContainerTest {

    RawByteDataContainer dc;
    int[][] data = {{200, 1}, {1, 1}};
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dc  = new RawByteDataContainer();
        dc = new RawByteDataContainer();
        dc.setIntData(data);
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.RawByteDataContainer#initialize(int, int)}.
     */
    @Test
    public void testInitialize() {
        dc.initialize(100, 100);
        assertNotNull("Container is null", dc.getData());
        assertEquals("Container is not empty:", 0, dc.getRawIntPoint(20, 20));
        assertEquals("Out of bounds should return -1:", -1, dc.getRawIntPoint(101, 80));
        assertEquals("Out of bounds should return 0:", 0, dc.getRawBytePoint(101, 80));
        
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.RawByteDataContainer#setData(short[][])}.
     */
    @Test
    public void testSetData() {
        
        assertEquals("Setting error:", 1, dc.getRawIntPoint(0, 1));
        assertEquals("Setting error:", 200, dc.getRawIntPoint(0, 0));
        
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.RawByteDataContainer#getByteData()}.
     */
    @Test
    public void testGetByteData() {

        assertEquals("Converting from int to byte error:", 1, dc.getRawBytePoint(0, 1));
        assertEquals("Converting from int to byte error:", -56, dc.getRawBytePoint(0, 0));
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.RawByteDataContainer#clone()}.
     */
    @Test
    public void testClone() {
        RawByteDataContainer dcClone = (RawByteDataContainer) dc.clone();
        assertEquals("Cloning error:", 1, dcClone.getRawIntPoint(0, 1));
        assertEquals("Cloning error:", 200, dcClone.getRawIntPoint(0, 0));
        assertEquals("Cloning error:", 1, dcClone.getRawBytePoint(0, 1));
        assertEquals("Cloning error:", -56, dcClone.getRawBytePoint(0, 0));
    }

}
