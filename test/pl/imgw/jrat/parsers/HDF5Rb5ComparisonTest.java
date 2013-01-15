/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;

import java.io.File;

import org.junit.Test;

import pl.imgw.jrat.data.containers.OdimDataContainer;
import pl.imgw.jrat.data.containers.OdimH5Volume;
import pl.imgw.jrat.data.containers.RainbowDataContainer;
import pl.imgw.jrat.data.containers.RainbowVolume;
import pl.imgw.jrat.data.containers.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class HDF5Rb5ComparisonTest {

    ParserManager parser = new ParserManager();
    
    @Test
    public void comparingVolumesTest() {
        parser.setParser(new DefaultParser());   
        
        File fH5 = new File("test-data/pair/T_PAGZ44_C_SOWR_20111010143020.h5");
        File fRb5 = new File("test-data/pair/2011101014302000dBZ.vol");
        
        parser.initialize(fH5);
        
        VolumeContainer volH5 = new OdimH5Volume((OdimDataContainer)parser.getProduct());
        
        parser.initialize(fRb5);
        
        VolumeContainer volRb5 = new RainbowVolume((RainbowDataContainer)parser.getProduct());
        /*
        System.out.println(volH5.getScan(0.5).getArray().getRawIntPoint(101, 21));
        
        for (int y = 0; y < 250; y++) {
            for (int x = 0; x < 360; x++)
                if (volRb5.getScan(0.5).getArray().getRawIntPoint(x, y) == 116)
                    System.out.println(x + "," + y);
        }
        System.out.println("\n");
        for (int y = 0; y < 250; y++) {
            for (int x = 0; x < 360; x++)
                if (volH5.getScan(0.5).getArray().getRawIntPoint(x, y) == 116)
                    System.out.println(x + "," + y);
        }
        */
        assertEquals(volH5.getScan(0.5).getArray().getSizeX(),
                volRb5.getScan(0.5).getArray().getSizeX());

        assertEquals(volH5.getScan(0.5).getArray().getSizeY(),
                volRb5.getScan(0.5).getArray().getSizeY());

        assertEquals(volH5.getScan(0.5).getArray().getPoint(101, 21), volRb5
                .getScan(0.5).getArray().getPoint(101, 21), 0.01);

        assertEquals(volH5.getScan(0.5).getArray().getPoint(301, 18), volRb5
                .getScan(0.5).getArray().getPoint(301, 18), 0.01);
        
        assertEquals(volH5.getScan(0.5).getArray().getRawIntPoint(101, 21),
                volRb5.getScan(0.5).getArray().getRawIntPoint(101, 21));
        
        assertEquals(volH5.getScan(0.5).getArray().getRawIntPoint(11, 100),
                volRb5.getScan(0.5).getArray().getRawIntPoint(11, 100));
        

//        for (int x = 0; x < volH5.getScan(0.5).getArray().getSizeX(); x++)
//            for (int y = 0; y < volRb5.getScan(0.5).getArray().getSizeY(); y++) {
//                System.out.println("x=" + x + " y=" + y);
//                assertEquals(volH5.getScan(0.5).getArray()
//                        .getRawBytePoint(x, y), volRb5.getScan(0.5).getArray()
//                        .getRawBytePoint(x, y));
//            }
        
        
        
        
    }
    
}
