/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.VolumeParser;

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

        VolumeParser parser = GlobalParser.getInstance().getVolumeParser();

        for (File f : files) {
            // System.out.println(f);
            if (parser.parse(f)) {
                Date date = RegexFileFilter.getDate(f);
                // System.out.println("1 " + date);
                String source = "";

                PolarData vol = parser.getPolarData();
                if (vol != null) {
                    source = vol.getSiteName();
                    if (!source.isEmpty()) {
                        HashMap<String, DataContainer> map = seg.get(date);
                        if (map == null) {
                            map = new HashMap<String, DataContainer>();
                        }
                        // System.out.println("1 " + source);
                        // System.out.println("2 " + date);
                        map.put(source, parser.getData());
                        seg.put(date, map);
                    }
                }

            }
        }
        /*
         * Iterator<Date> i = seg.keySet().iterator(); while (i.hasNext()) {
         * Date d = i.next(); // System.out.println(d); Iterator<String> it =
         * seg.get(d).keySet().iterator(); System.out.println("time=" + d);
         * while (it.hasNext()) { String s = it.next();
         * System.out.println("source=" + s); } }
         */

    }

    public Map<Date, HashMap<String, DataContainer>> getList() {
        return seg;
    }

}
