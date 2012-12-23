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
        for (File f : files) {
            parser.initialize(f);
            if (LogHandler.getLogs().getVerbose() > Logging.PROGRESS_BAR_ONLY)
                parser.getProduct().printAllAttributes();
            else
                parser.getProduct().printGeneralIfnormation();
        }
    }
    
}
