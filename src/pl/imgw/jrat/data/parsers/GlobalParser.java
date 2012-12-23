/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

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
public class GlobalParser {

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
    
    private static GlobalParser gps = new GlobalParser();
    
    private GlobalParser() {
        //private constructor
    }
    
    public static GlobalParser getInstance() {
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
