/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import java.io.File;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface DataSource {

    /**
     * Initialize an object with values read from given file.
     *
     * 
     * 
     * @param file - object representing file with data that is needed to initialize file
     * @return 
     * 
     * 
     */
    public boolean initializeFromFile(File f);
    
}
