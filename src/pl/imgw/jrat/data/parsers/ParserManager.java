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
public class ParserManager {
    
    FileParser parser = null;
    
    public void setParser(FileParser parser) {
        this.parser = parser;
    }
    
    public boolean initialize(File file) {
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
    
    public boolean isSet() {
        return (parser == null) ? false : true;
    }
}
