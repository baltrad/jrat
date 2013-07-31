/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.VolumeParser;
import pl.imgw.util.LogFile;
import pl.imgw.util.LogManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class VolumeProcessorManager implements FilesProcessor {

    private Set<VolumesProcessor> processes = new HashSet<VolumesProcessor>();
    private VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
    private List<PolarData> volumes = new LinkedList<PolarData>();
    private static final String DATE_PATTERN = "yyyyMMddHHmm";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
    
    private static LogFile log = LogManager.getFileLogger();
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FilesProcessor#processFile(java.util.List)
     */
    @Override
    public void processFile(List<File> files) {
        volumes.clear();
        Collections.sort(files, fileWithDate);
        
        Date olddate = null;
        try {
            olddate = parseDate(files.get(0));
        } catch (ParseException e1) {
            olddate = new Date();
        }
        
        Date newdate = null;
        boolean next = false;
        for (File f : files) {
            System.out.println("file: " + f);
            try {
                newdate = parseDate(f);
                System.out.println("newdate: " + newdate);
                System.out.println("olddate: " + olddate);
                if(!newdate.equals(olddate)) {
                    next = true;
                }
                else
                    next = false;
                olddate = newdate;
            } catch (ParseException e) {
                next = false;
            }
            
            
            if(next) {
                System.out.println("process: " + volumes.size());
                for(VolumesProcessor proc : processes) {
                    proc.processVolumes(volumes);
                }
                volumes.clear();
//                System.gc();
            }
            
            PolarData vol = null;
            if (parser.parse(f)) {
                vol = parser.getPolarData();
                volumes.add(vol);
            }
            
        }
        
        //needed for processing last bunch of files
        for (VolumesProcessor proc : processes) {
            try {
                proc.processVolumes(volumes);
            } catch (Exception e) {
                log.saveErrorLogs(VolumeProcessorManager.class.getName(),
                        e.getMessage());
            }
        }
        
        
        
    }
    
    public void addProcess(VolumesProcessor proc) {
        processes.add(proc);
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FilesProcessor#getProcessName()
     */
    @Override
    public String getProcessName() {
        String name = "";
        for(VolumesProcessor s : processes)
            name += (s.getProcessName() + ", ");
        return name;
    }

    protected Date parseDate(File file) throws ParseException {
        Pattern pattern = Pattern.compile("\\d{12,}");
        Matcher matcher = pattern.matcher(file.getName());
        if(matcher.find()) {
//            System.out.println(matcher.group());
            return sdf.parse(matcher.group().substring(0, 12));
        }
        throw new ParseException("This is not a date file name", 0);
    }
    
    Comparator<File> fileWithDate = new Comparator<File>() {

        @Override
        public int compare(File o1, File o2) {
            Pattern pattern = Pattern.compile("\\d{12,}");
            Matcher matcher = pattern.matcher(o1.getName());
            String s1 = null, s2 = null;
            if(matcher.find()) {
                s1 = matcher.group();
            }
            matcher = pattern.matcher(o2.getName());
            if(matcher.find()) {
                s2 = matcher.group();
            }
            if(s1 != null && s2 != null)
                return s1.compareTo(s2);
            return o1.compareTo(o2);
        }
    };
    
}
