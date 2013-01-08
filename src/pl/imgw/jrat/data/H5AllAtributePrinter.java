/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data;

import java.util.List;

import ch.systemsx.cisd.hdf5.IHDF5Reader;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class H5AllAtributePrinter {

    private IHDF5Reader reader;
    
    public void printAll(IHDF5Reader reader) {
        
        this.reader = reader;
        
        List<String> groups = reader.getAllGroupMembers("/");
        
        for(String s : groups) {
            System.out.println(s);
        }
    }
    
    
    
}
