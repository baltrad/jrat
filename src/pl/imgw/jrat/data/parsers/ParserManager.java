/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.File;

import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ParserManager {
    
    FileParser parser = null;
    
    public void setParser(FileParser parser) {
        this.parser = parser;
    }
    
    public boolean initialize(File file) {
        if(!file.exists()) {
            LogHandler.getLogs().displayMsg("File '" + file.getName()
                    + "' does not exist", WARNING);
            return false;
        }
        if(!isSet())
            return false;
        return parser.initialize(file);
    }

    /**
     * 
     * @return null if parser is not set
     */
    public DataContainer getProduct() {
        if(!isSet()) {
            return null;
        }
        return parser.getProduct();
    }
    
    public boolean isValid(File file) {
        return parser.isValid(file);
    }
    
    /**
     * Method checks if <code>FileParser</code> is set with not null object
     * 
     * @return true if <code>FileParser</code> is not null otherwise returns false
     */
    public boolean isSet() {
        return (parser == null) ? false : true;
    }
}
