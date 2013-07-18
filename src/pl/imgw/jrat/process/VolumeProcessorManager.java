/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.VolumeParser;

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
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FilesProcessor#processFile(java.util.List)
     */
    @Override
    public void processFile(List<File> files) {
        volumes.clear();
        for (File f : files) {
            PolarData vol = null;
            if (parser.parse(f)) {
                vol = parser.getPolarData();
                volumes.add(vol);
            }
        }
        
        for(VolumesProcessor proc : processes) {
            proc.processVolumes(volumes);
        }
        
        volumes.clear();
        System.gc();
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
            name += (s + ", ");
        return name;
    }

}
