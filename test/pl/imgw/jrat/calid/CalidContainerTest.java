/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.data.containers.OdimDataContainer;
import pl.imgw.jrat.data.containers.OdimH5Volume;
import pl.imgw.jrat.data.containers.RainbowDataContainer;
import pl.imgw.jrat.data.containers.RainbowVolume;
import pl.imgw.jrat.data.containers.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.Rainbow53VolumeParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidContainerTest {

    CalidContainer cont;
    CalidParsedParameters manager = new CalidParsedParameters();
    String[] par;
    
    @Test
    public void firstInitializationTest() {
        
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
        
        par = "ele=0.5 dis=500 ref=5.0 range=250".split(" ");
        manager.initialize(par);
        cont = new CalidContainer(manager);
        assertTrue(!cont.initialize());
        cont.setPair(getPair());
        assertTrue(cont.initialize());
        
    }
    
    @Test
    public void fullInitializationTest() {
        
        LogHandler.getLogs().setLoggingVerbose(Logging.ALL_MSG);
        
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
        
        String[] par = "ele=0.5 dis=500 ref=5.0 range=250".split(" ");
        CalidParsedParameters calid = new CalidParsedParameters();
        calid.initialize(par);
        
        CalidContainer coords = new CalidContainer(pair, calid);
        assertTrue(coords.hasVolumeData());
        
        assertTrue(coords.initialize());
        //calculating new coords.xml
        assertTrue(!coords.getPairedPointsList().isEmpty());
        assertTrue(coords.hasResults());
        
        assertTrue(coords.initialize());

    }
    
    private Pair getPair() {
        ParserManager pm = new ParserManager();
        pm.setParser(new DefaultParser());
        pm.initialize(new File("test-data/calid", "T_PAGZ41_C_SOWR_20110922004019.h5"));
        VolumeContainer vol1 = new OdimH5Volume((OdimDataContainer) pm.getProduct());
        pm.initialize(new File("test-data/calid", "T_PAGZ44_C_SOWR_20110922004021.h5"));
        VolumeContainer vol2 = new OdimH5Volume((OdimDataContainer) pm.getProduct());
        return new Pair(vol1, vol2);
    }
    
}
