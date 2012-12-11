/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.RawByteDataArray;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ByteDataContainerTest {

    RawByteDataArray dc;
    int[][] data = {{200, 1}, {1, 1}};
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dc  = new RawByteDataArray();
        dc = new RawByteDataArray();
        dc.setIntData(data);
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.RawByteDataArray#initialize(int, int)}.
     */
    @Test
    public void testInitialize() {
        dc.initialize(100, 100);
        assertNotNull("Container is null", dc.getShortData());
        assertEquals("Container is not empty:", 0, dc.getRawIntPoint(20, 20));
        assertEquals("Out of bounds should return -1:", -1, dc.getRawIntPoint(101, 80));
        assertEquals("Out of bounds should return 0:", 0, dc.getRawBytePoint(101, 80));
        
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.RawByteDataArray#setData(short[][])}.
     */
    @Test
    public void testSetData() {
        
        assertEquals("Setting error:", 1, dc.getRawIntPoint(0, 1));
        assertEquals("Setting error:", 200, dc.getRawIntPoint(0, 0));
        
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.RawByteDataArray#getByteData()}.
     */
    @Test
    public void testGetByteData() {

        assertEquals("Converting from int to byte error:", 1, dc.getRawBytePoint(0, 1));
        assertEquals("Converting from int to byte error:", -56, dc.getRawBytePoint(0, 0));
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.RawByteDataArray#clone()}.
     */
    @Test
    public void testClone() {
        RawByteDataArray dcClone = (RawByteDataArray) dc.clone();
        assertEquals("Cloning error:", 1, dcClone.getRawIntPoint(0, 1));
        assertEquals("Cloning error:", 200, dcClone.getRawIntPoint(0, 0));
        assertEquals("Cloning error:", 1, dcClone.getRawBytePoint(0, 1));
        assertEquals("Cloning error:", -56, dcClone.getRawBytePoint(0, 0));
    }

}
