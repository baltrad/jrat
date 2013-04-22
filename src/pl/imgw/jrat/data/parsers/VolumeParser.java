/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import pl.imgw.jrat.data.containers.VolumeContainer;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface VolumeParser extends FileParser {

    /**
     * 
     * @return <b>null</b> if parsed data is not a valid polar volume
     */
    public VolumeContainer getVolume();
    
    
    public boolean isPolarVolume();
    
}
