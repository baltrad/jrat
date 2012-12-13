/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.data.RainbowDataContainer;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.Rainbow53VolumeParser;
import pl.imgw.jrat.process.MainProcessController;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OverlappingCoordsTest {

    @Test
    public void calculateCoordsTest() {
        
        LogHandler.getLogs().setLoggingVerbose(Logging.WARNING);
        
        new File(AplicationConstans.ETC + "/calid/RzeszowBrzuchania/500_0.5_5.0/coords.xml").delete();
        
        File a = new File("test-data/calid/2011082113400400dBZ.vol");
        File b = new File("test-data/calid/2011082113402900dBZ.vol");
        
        ParserManager pm = new ParserManager();
        pm.setParser(new Rainbow53VolumeParser());
        pm.initialize(a);
        RainbowDataContainer data = (RainbowDataContainer) pm.getProduct();
        RainbowVolume vol1 = new RainbowVolume(data);
        pm.initialize(b);
        RainbowDataContainer data2 = (RainbowDataContainer) pm.getProduct();
        RainbowVolume vol2 = new RainbowVolume(data2);
        
        Pair pair = new Pair(vol1, vol2);
        
        int dist = 500;
        double ele = 0.5;
        double refl = 5.0;
        CalidContainer coords = new CalidContainer(pair, ele, dist, refl);
        
        assertTrue(coords.valid);
        
        //calculating new coords.xml
        assertNotNull(coords.getCoords());
        
        //loading coords from coords.xml
        assertNotNull(coords.getCoords());


    }

    @Test
    public void loadCoordsTest() {
        
    }
    
}
