/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.util.List;

import pl.imgw.jrat.data.PolarData;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface VolumesProcessor extends FilesProcessor{

    void processVolumes(List<PolarData> vol);
    
}
