/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.wz;

import javax.swing.JFrame;

import pl.imgw.jrat.tools.out.LogHandler;
import static pl.imgw.jrat.tools.out.Logging.*;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class WZViewer {

    /**
     * @param args
     */
    public static void main(String[] args) {
        LogHandler.getLogs().setLoggingVerbose(SILENT);
        JFrame frame = new JFrame("Radar data viewer - beta version");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DisplayPanel displayPane = new DisplayPanel();
        displayPane.setFrame(frame);
        
        frame.setContentPane(displayPane);
        frame.setResizable(false);
      
        frame.pack();
        frame.setVisible(true);

    }

}
