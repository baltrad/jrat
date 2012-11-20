/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.junit.Test;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.process.ProcessController;
import pl.imgw.jrat.projection.ProjectionUtility;
import pl.imgw.jrat.tools.out.ColorScales;
import pl.imgw.jrat.tools.out.ImageBuilder;
import pl.imgw.jrat.tools.out.ImageTools;

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
    public void calculateTest() {
        
        System.out.println("Calculating Test");
        
        String[] args = new String[] { "-i",
                "test-data/calid/2011082113400400dBZ.vol",
                "test-data/calid/2011082113402900dBZ.vol",
                "test-data/calid/T_PAGZ41_C_SOWR_20110922004019.h5",
                "test-data/calid/T_PAGZ44_C_SOWR_20110922004021.h5",
                "--calid a", "-v" };
        
        ProcessController proc = new ProcessController(args);
        proc.start();
        PairsContainer pairs = new PairsContainer(proc.getFiles());
        Iterator<Pair> i = pairs.getPairs().iterator();
        
        args = new String[] { "0.5deg", "500m" };
        System.out.println("Run CalidManager with 0.5deg and 500m");
        manager = new CalidManager(args);
        assertTrue(manager != null);
        Pair pair = i.next();
        System.out.println(pair);
        ArrayList<PairedPoints> results = manager.compare(pair);
        
        assertTrue(results.size() > 0);
        
        args = new String[] { "-0.2deg", "0.5km" };
        System.out.println("Run CalidManager with -0.2deg and 0.5km");
        manager = new CalidManager(args);
        assertTrue(manager != null);
        
        args = new String[] { "0.2deg", "500m" };
        System.out.println("Run CalidManager with 0.2deg and 500m");
        manager = new CalidManager(args);
        pair = i.next();
        System.out.println(pair);
        assertNull(manager.compare(pair));
        
    }
   
    @Test
    public void resultsTest() {
        System.out.println("Results Test");
        String[] args = new String[] { "-i",
                "test-data/calid/T_PAGZ47_C_SOWR_20120109180044.hdf",
                "test-data/calid/T_PAGZ46_C_SOWR_20120109180013.hdf",
                };
        
        ProcessController proc = new ProcessController(args);
        proc.start();
        PairsContainer pairs = new PairsContainer(proc.getFiles());
        
        args = new String[] { "0.5deg", "500m" };
        manager = new CalidManager(args);
        new File("calid/overlapping/WMO:12151WMO:12220/500_0.5/results").delete();
        System.out.println("Asking for results after deleting results file");
        Pair pair = pairs.getPairs().iterator().next();
        ArrayList<PairedPoints> array = manager.compare(pair);
        
        java.lang.Double v11 = array.get(10).getDifference();
        java.lang.Double v12 = array.get(80).getDifference();
        
        System.out.println("Asking for results again");
        
        array = manager.compare(pair);
        
        java.lang.Double v21 = array.get(10).getDifference();
        java.lang.Double v22 = array.get(80).getDifference();
        
        assertEquals(v11, v21);
        assertEquals(v12, v22);
        
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
        
    }
    
}
