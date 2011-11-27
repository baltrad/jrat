/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat;

import java.io.File;

import pl.imgw.jrat.controller.DataProcessorController;
import pl.imgw.jrat.util.CommandLineArgsParser;
import pl.imgw.jrat.util.MessageLogger;

/**
 *
 *  Main class. 
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MainJRat {
   
    public static final String HOME = System.getProperty("user.home");
    public static final String JRAT = "jrat";
    
    public static String getProgPath() {
        return new File(HOME, JRAT).getPath();
    }
    
    public static void main(String[] args) {
        
        File file = new File(HOME, JRAT);
        if (!file.exists()) {
            file.mkdirs();
        }
        CommandLineArgsParser cmd = new CommandLineArgsParser();
        DataProcessorController cont = new DataProcessorController();
        MessageLogger msg = new MessageLogger();
        cont.setCmdParser(cmd);
        cont.setMsg(msg);
        
        try {
            cont.startProcessor(args);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
