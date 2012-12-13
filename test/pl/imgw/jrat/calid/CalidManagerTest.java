/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.process.MainProcessController;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidManagerTest {

    CalidManager manager;
    
    @Test
    public void emptyTest() {
        
    }
    
    /*
    @Test
    public void calculateTest() {
        
        System.out.println("Calculating Test");
        
        String[] args = new String[] { "-i",
                "test-data/calid/2011082113400400dBZ.vol",
                "test-data/calid/2011082113402900dBZ.vol",
                "test-data/calid/T_PAGZ41_C_SOWR_20110922004019.h5",
                "test-data/calid/T_PAGZ44_C_SOWR_20110922004021.h5",
                "--calid a", "-v" };
        
        MainProcessController proc = new MainProcessController(args);
        proc.start();
        PairsContainer pairs = new PairsContainer(proc.getFiles());
        Iterator<Pair> i = pairs.getPairs().iterator();
        
        args = new String[] { "ele=0.5", "dis=500" };
        System.out.println("Run CalidManager with ele=0.5 and dis=500");
        manager = new CalidManager(args);
        assertTrue(manager != null);
        Pair pair = i.next();
        System.out.println(pair);
        ArrayList<PairedPoints> results = CalidComparator.getResult(manager, pair);
        
        assertTrue(results.size() > 0);
        
        args = new String[] { "ele=-0.2", "dis=0.5" };
        System.out.println("Run CalidManager with ele=-0.2 and dis=0.5");
        manager = new CalidManager(args);
        assertTrue(manager != null);
        
        args = new String[] { "ele=0.2", "dis=500" };
        System.out.println("Run CalidManager with ele=0.2 and dis=500");
        manager = new CalidManager(args);
        pair = i.next();
        System.out.println(pair);
        assertNull(CalidComparator.getResult(manager, pair));
        
        args = new String[] { "ele=0.2", "dis=500", "ref=3.5" };
        System.out.println("Run CalidManager with ele=0.2, dis=500, ref=3.5");
        manager = new CalidManager(args);
//        System.out.println(pair);
        assertTrue(manager != null);
        
    }
   
    @Test
    public void resultsTest() {
        System.out.println("Results Test");
        String[] args = new String[] { "-i",
                "test-data/calid/T_PAGZ47_C_SOWR_20120109180044.hdf",
                "test-data/calid/T_PAGZ46_C_SOWR_20120109180013.hdf", "-q",
                };
        
        MainProcessController proc = new MainProcessController(args);
        proc.start();
        PairsContainer pairs = new PairsContainer(proc.getFiles());
        
        args = new String[] { "ele=0.5", "dis=500" };
        manager = new CalidManager(args);
        
        File f = new File("calid/RzeszowBrzuchania/500_0.5_-31.5/20110821.results");
        
        assertTrue(f.delete());
        
        System.out.println("Asking for results after deleting results file");
        Pair pair = pairs.getPairs().iterator().next();
        ArrayList<PairedPoints> array = CalidComparator.getResult(manager, pair);
        
        java.lang.Double v11 = array.get(10).getDifference();
        java.lang.Double v12 = array.get(80).getDifference();
        
        System.out.println("Asking for results again");
        
        array = CalidComparator.getResult(manager, pair);
        
        java.lang.Double v21 = array.get(10).getDifference();
        java.lang.Double v22 = array.get(80).getDifference();
        
        assertEquals(v11, v21);
        assertEquals(v12, v22);
        
        /*
        System.out.println("Print results to png");
        
        Results2DManager results = new Results2DManager(array, pair);
        ArrayData data = results.getData();
        
        BufferedImage img = new ImageBuilder()
        .setData(data)
        .setNoDataValue(Results2DManager.NO_DATA)
        .setScale(ColorScales.getColdWarmScale(60, 5))
        .addPoint(results.source1.x, results.source1.y, results.name1, Color.PINK)
        .addPoint(results.source2.x, results.source2.y, results.name2, Color.orange)
//        .hasCaption(true)
        .create();
        try {
            ImageIO.write(img, "PNG", new File("calid", "results.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
//    }
    
}
