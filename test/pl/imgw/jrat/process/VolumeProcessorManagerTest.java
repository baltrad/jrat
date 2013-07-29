/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static org.junit.Assert.*;

import java.io.File;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import pl.imgw.jrat.data.PolarData;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class VolumeProcessorManagerTest {

    VolumeProcessorManager proc = new VolumeProcessorManager();
    VolumesProcessor vproc = new VolumesProcessor() {
        
        @Override
        public void processVolumes(List<PolarData> vol) {
            for(PolarData data : vol)
                System.out.println(data.getSiteName() + ", date: " + data.getTime());
        }

        @Override
        public void processFile(List<File> files) {
            for(File f : files)
                System.out.println(f);
            
        }

        @Override
        public String getProcessName() {
            return "vol test proc";
        }
    };
    
    /**
     * Test method for {@link pl.imgw.jrat.process.VolumeProcessorManager#processFile(java.util.List)}.
     */
    @Test
    public void shouldProcessFile() {
        proc.addProcess(vproc);
        List<File> files = Arrays.asList((new File("test-data/pair").listFiles()));
        proc.processFile(files);
    }

    /**
     * Test method for {@link pl.imgw.jrat.process.VolumeProcessorManager#parseDate(java.io.File)}.
     * @throws ParseException 
     */
    @Test
    public void shouldParseDate() throws ParseException {
        File file = new File("test-data/pair", "2011101003002600dBZ.vol");
        Date exp = new Date(111,9,10,3,0);
        Date date = proc.parseDate(file);
        assertEquals(exp, date);
        file = new File("test-data/pair", "T_PAGZ44_C_SOWR_20111010031022.h5");
        exp = new Date(111,9,10,3,10);
        date = proc.parseDate(file);
        assertEquals(exp, date);
        
    }

}
