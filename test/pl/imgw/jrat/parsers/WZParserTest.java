/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.parsers;

import static org.junit.Assert.assertTrue;
import static pl.imgw.jrat.tools.out.Logging.ERROR;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.WZFileParser;
import pl.imgw.jrat.tools.out.LogHandler;


/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class WZParserTest {

    private ParserManager pm;
    File f;
    
    @Before
    public void setUp() {
        LogHandler.getLogs().setLoggingVerbose(ERROR);
        f = new File("test-data", "20120731083600cpx.wz");
        pm = new ParserManager();
        pm.setParser(new WZFileParser());
    }
    
    @Test
    public void isValidTest() {
        assertTrue(pm.isValid(f));
    }
    
    @Test
    public void initializeTest() {
        assertTrue(pm.initialize(f));
        /*
        BufferedImage img;
        RawByteDataArray data;
        
        data = (RawByteDataArray) pm.getProduct().getArray("FL0-FL100:Z");
        
        double minvalue = data.getOffset() + 2* data.getGain();
        double stepvalue = data.getGain();
        
        img = new ImageBuilder()
                // .setDarker(true)
                .setData(data)
                .setNoDataValue(0)
                .setNoDetectedValue(1)
                .setTransparency(250)
                .setScale(ColorScales.getRedScale(minvalue, stepvalue))
                .create();
        try {
            ImageIO.write(img, "PNG", new File("test-data", "imagebuilder2.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
    }
    
}
