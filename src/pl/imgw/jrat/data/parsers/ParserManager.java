/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import java.io.File;

import pl.imgw.jrat.data.ProductDataContainer;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ParserManager {
    
    FileParser parser;
    
    public void setParser(FileParser parser) {
        this.parser = parser;
    }
    
    public void initialize(File file) {
        parser.initialize(file);
    }

    public ProductDataContainer getProduct() {
        return parser.getProduct();
    }
}
