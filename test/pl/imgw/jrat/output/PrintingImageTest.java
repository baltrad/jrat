/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.output;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import pl.imgw.jrat.process.PrintingImageProcessController;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PrintingImageTest {
    {
        LogManager.getInstance().setLogMode(Log.MODE_VERBOSE);
    }

    @Test
    public void printImageTest() {
        File h5 = new File("test-data", "2vol.h5");
        File rb5 = new File("test-data", "1.vol");
        String[] args = {"format=txt"};
        
        List<File> files = new LinkedList<File>();
        files.add(rb5);
        files.add(h5);
        
        File output = new File("test-data", "img");
        
        PrintingImageProcessController.printImage(files, output, args);
        
        int size = output.listFiles()[0].listFiles().length;
        assertEquals(10, size);
        size = output.listFiles()[1].listFiles().length;
        assertEquals(10, size);
        
        for (File f : output.listFiles()[0].listFiles())
            f.delete();

        for (File f : output.listFiles()[1].listFiles())
            f.delete();
        output.listFiles()[0].delete();
        output.listFiles()[0].delete();
    }
    
}
