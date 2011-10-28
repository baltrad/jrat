/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat;

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
    
    public static void main(String[] args) {
        
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
