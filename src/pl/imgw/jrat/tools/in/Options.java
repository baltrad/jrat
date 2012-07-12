/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

import java.util.HashMap;

/**
 *
 *  Singleton, loading options from file only once on first use.
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Options {
    
    private HashMap<String, String> optionContainer;
    
    private static Options options = new Options();
    
    private Options() {
        
        // loading all options from the file, only once on first use.
        
    }
    
    /**
     * 
     * @param name
     * @return null if value does not exist in the container
     */
    public String getValue(String name) {
        return optionContainer.get(name);
    }
    
    /**
     * 
     * @param name
     * @param value
     */
    public void setValue(String name, String value) {
        optionContainer.put(name, value);
    }

    public static Options getOptons() {
        return options;
    }
}
