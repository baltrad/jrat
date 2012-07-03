/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.ByteDataContainer;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ByteDataContainerTest {

    ByteDataContainer dc;
    int[][] data = {{200, 1}, {1, 1}};
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dc  = new ByteDataContainer();
        dc = new ByteDataContainer();
        dc.setIntData(data);
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.ByteDataContainer#initialize(int, int)}.
     */
    @Test
    public void testInitialize() {
        dc.initialize(100, 100);
        assertNotNull("Container is null", dc.getData());
        assertEquals("Container is not empty:", 0, dc.getIntPoint(20, 20));
        assertEquals("Out of bounds should return -1:", -1, dc.getIntPoint(101, 80));
        assertEquals("Out of bounds should return 0:", 0, dc.getBytePoint(101, 80));
        
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.ByteDataContainer#setData(short[][])}.
     */
    @Test
    public void testSetData() {
        
        assertEquals("Setting error:", 1, dc.getIntPoint(0, 1));
        assertEquals("Setting error:", 200, dc.getIntPoint(0, 0));
        
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.ByteDataContainer#getByteData()}.
     */
    @Test
    public void testGetByteData() {

        assertEquals("Converting from int to byte error:", 1, dc.getBytePoint(0, 1));
        assertEquals("Converting from int to byte error:", -56, dc.getBytePoint(0, 0));
    }

    /**
     * Test method for {@link pl.imgw.jrat.data.ByteDataContainer#clone()}.
     */
    @Test
    public void testClone() {
        ByteDataContainer dcClone = (ByteDataContainer) dc.clone();
        assertEquals("Cloning error:", 1, dcClone.getIntPoint(0, 1));
        assertEquals("Cloning error:", 200, dcClone.getIntPoint(0, 0));
        assertEquals("Cloning error:", 1, dcClone.getBytePoint(0, 1));
        assertEquals("Cloning error:", -56, dcClone.getBytePoint(0, 0));
    }

}
