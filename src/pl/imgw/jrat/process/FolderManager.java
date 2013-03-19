/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
public class FolderManager {

    public static boolean continueWithDeletingFiles(List<File> folders) {
        
        if (LogHandler.getLogs().getVerbose() == LogHandler.SILENT)
            return true;

        String msg = "Folder is not empty, all files will be deleted after processing. Continue? ";

        boolean ask = false;

        for (File f : folders) {
            if (f.listFiles().length != 0) {
                ask = true;
            }
        }

        if (!ask)
            return true;

        System.out.print(msg);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String answer = reader.readLine();
            if (answer.matches("y") || answer.matches("yes")) {
                return true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    public static void main(String[] args) {
        continueWithDeletingFiles(null);
    }
    
}
