/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.FileParser;
import pl.imgw.jrat.data.parsers.OdimH5Parser;
import pl.imgw.jrat.data.parsers.Rainbow53ImageParser;
import pl.imgw.jrat.data.parsers.Rainbow53VolumeParser;
import pl.imgw.jrat.data.parsers.WZFileParser;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class GlobalParserSetter {

    private FileParser parser = new DefaultParser();
    
    public enum Format {
        RB53VOL("rb5.3vol"), RB53IMG("rb5.3img"), ODIM("odim"), WZ("wz");
        private String name;
        private Format(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    };
    
    private static GlobalParserSetter gps = new GlobalParserSetter();
    
    private GlobalParserSetter() {
        //private constructor
    }
    
    public static GlobalParserSetter getInstance() {
        return gps;
    }
    
    public FileParser getParser() {
        return parser;
    }
    
    public void setParser(String format) {
        if (format.matches(Format.ODIM.name)) {
            parser = new OdimH5Parser();
            LogHandler.getLogs().displayMsg(format + " parser set", Logging.NORMAL);
        } else if (format.matches(Format.RB53IMG.name)) {
            parser = new Rainbow53ImageParser();
            LogHandler.getLogs().displayMsg(format + " parser set", Logging.NORMAL);
        } else if (format.matches(Format.RB53VOL.name)) {
            parser = new Rainbow53VolumeParser();
            LogHandler.getLogs().displayMsg(format + " parser set", Logging.NORMAL);
        } else if (format.matches(Format.WZ.name)) {
            parser = new WZFileParser();
            LogHandler.getLogs().displayMsg(format + " parser set", Logging.NORMAL);
        } else {
            LogHandler.getLogs().displayMsg("Default parser set", Logging.NORMAL);
        }

    }
    
}
