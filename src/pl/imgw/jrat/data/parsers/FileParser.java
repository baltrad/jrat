/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import java.io.File;

import pl.imgw.jrat.data.DataContainer;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface FileParser {

    /**
     * Checking if it is a valid file for this parser
     * 
     * @param file 
     * @return false if file is invalid
     */
    public boolean isValid(File file);
    
    /**
     * Initializing file, loading attributes and datasets to memory
     * @param file
     * @return
     */
    public boolean initialize(File file);
        
    /**
     * Receiving datase
     * @return
     */
    public DataContainer getProduct();

}
