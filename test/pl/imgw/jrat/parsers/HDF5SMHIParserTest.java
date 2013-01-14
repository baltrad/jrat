/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.containers.DataContainer;
import pl.imgw.jrat.data.containers.OdimDataContainer;
import pl.imgw.jrat.data.containers.OdimH5CompoImage;
import pl.imgw.jrat.data.containers.OdimH5Volume;
import pl.imgw.jrat.data.containers.ScanContainer;
import pl.imgw.jrat.data.containers.VolumeContainer;
import pl.imgw.jrat.data.parsers.FileParser;
import pl.imgw.jrat.data.parsers.OdimH5Parser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;
import static org.junit.Assert.*;
/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class HDF5SMHIParserTest {
    
    File file1;
    OdimH5CompoImage pdc1;
    OdimH5Volume pdc2;
    ParserManager pm;
    int x,y;
    double value;
    @Before
    public void setUp() {
        
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
        
        file1 = new File("test-data", "scanSMHI.h5");
        
        pm = new ParserManager();
        pm.setParser(new OdimH5Parser());
        
    }
    
    //------------ volumes tests ---------------------
        
    @Test
    public void getScanTest() {
        assertTrue(pm.initialize(file1));
        OdimDataContainer data = (OdimDataContainer) pm.getProduct();
        VolumeContainer vol = new OdimH5Volume(data);
        ScanContainer scan = vol.getScan(0.5);
        assertEquals("nbins is wrong", 120, scan.getNBins());
        assertEquals("nrays is wrong", 420, scan.getNRays());
        assertEquals("scale is wrong", 2000.0, scan.getRScale(), 0.01);
        ArrayData array = scan.getArray();
        assertEquals("array value", 132, array.getRawIntPoint(13, 239));
        assertEquals(1, vol.getAllScans().size());
    }
   
    
}
