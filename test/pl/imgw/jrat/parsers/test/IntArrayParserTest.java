/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers.test;

import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.RawByteDataArray;
import pl.imgw.jrat.data.parsers.IntArrayParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.tools.out.ColorScales;
import pl.imgw.jrat.tools.out.ImageBuilder;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.LogsType;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class IntArrayParserTest {

    private ParserManager pm;
    File f;
    
    @Before
    public void setUp() {
        LogHandler.getLogs().setLoggingVerbose(LogsType.ERROR);
        f = new File("test-data", "data35alt_2011.txt");
        pm = new ParserManager();
        pm.setParser(new IntArrayParser());
    }
    
//    @Test
//    public void isValidTest() {
//        assertTrue(pm.isValid(f));
//    }
    
    @Test
    public void initializeTest() {
        assertTrue(pm.initialize(f));
        BufferedImage img;
        ArrayData data;

        data = pm.getProduct().getArrayList()
                .get(pm.getProduct().getArrayList().keySet().iterator().next());

        img = new ImageBuilder()
                // .setDarker(true)
                .setData(data)
                .setTransparency(250)
                .setScale(ColorScales.getGrayScale(50))
                .create();
        try {
            ImageIO.write(img, "PNG", new File("test-data", "data35alt_2011.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
}
