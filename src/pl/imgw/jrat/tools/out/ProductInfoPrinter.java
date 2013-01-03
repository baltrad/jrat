/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.io.File;
import java.util.List;

import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.ParserManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ProductInfoPrinter {

    public static void print(List<File> files) {
        ParserManager parser = new ParserManager();
        parser.setParser(GlobalParser.getInstance().getParser());
        
        boolean printed = false;
        
        for (File f : files) {
            if (!parser.initialize(f)) {
                continue;
            }
            
            System.out.println("\nInformation about file: " + f.getName());
            
            if (LogHandler.getLogs().getVerbose() > Logging.PROGRESS_BAR_ONLY)
                parser.getProduct().printAllAttributes();
            else
                parser.getProduct().printGeneralIfnormation();
            
            printed = true;
            
        }
        
        if(printed) {
            if (LogHandler.getLogs().getVerbose() == Logging.PROGRESS_BAR_ONLY)
                System.out.println("\nTo print detailed information use -v parameter");
        }
        
    }
    
}
