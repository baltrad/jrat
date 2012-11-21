/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.data.H5DataContainer;
import pl.imgw.jrat.data.OdimH5Volume;
import pl.imgw.jrat.data.RainbowDataContainer;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.process.ProcessController;
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
public class FileSegregatorByDateAndSource {

    private Map<Date, HashMap<String, DataContainer>> seg = new HashMap<Date, HashMap<String, DataContainer>>();

    public void setInputFiles(List<File> files) {

        ParserManager manager = new ParserManager();
        manager.setParser(new DefaultParser());

        for (File f : files) {
//            System.out.println(f);
            if (manager.initialize(f)) {
                DataContainer data = manager.getProduct();
                Date date = RegexFileFilter.getDate(f);
                // System.out.println("1 " + date);
                String source = "";

                VolumeContainer vol = null;
                if (data instanceof H5DataContainer) {
                    vol = new OdimH5Volume((H5DataContainer) data);
                } else if (data instanceof RainbowDataContainer) {
                    vol = new RainbowVolume((RainbowDataContainer) data);
                }
                if (vol != null && vol.isValid()) {
                    source = vol.getSiteName();
                    if (!source.isEmpty() && data != null) {
                        HashMap<String, DataContainer> map = seg.get(date);
                        if (map == null) {
                            map = new HashMap<String, DataContainer>();
                        }
//                        System.out.println("1 " + source);
//                        System.out.println("2 " + date);
                        map.put(source, data);
                        seg.put(date, map);
                    }
                }

            }
        }
        /*
        Iterator<Date> i = seg.keySet().iterator();
        while (i.hasNext()) {
            Date d = i.next();
//            System.out.println(d);
            Iterator<String> it = seg.get(d).keySet().iterator();
            System.out.println("time=" + d);
            while (it.hasNext()) {
                String s = it.next();
                System.out.println("source=" + s);
            }
        }
        */

    }

    public Map<Date, HashMap<String, DataContainer>> getList(){
        return seg;
    }
    
    public static void main(String[] args) {
        LogHandler.getLogs().setLoggingVerbose(Logging.SILENT);
        args = new String[] { "-i", "test-data/comp/*", "test-data/*.vol", "-v" };
        ProcessController proc = new ProcessController(args);
        proc.start();
        FileSegregatorByDateAndSource seg = new FileSegregatorByDateAndSource();
        seg.setInputFiles(proc.getFiles());
    }

}
