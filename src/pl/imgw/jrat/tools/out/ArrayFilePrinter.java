/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import pl.imgw.jrat.data.arrays.ArrayData;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ArrayFilePrinter {

    public static void printTXT(ArrayData array, File outputFile) {

        PrintWriter pw = null;
        
        int xSize = array.getSizeX();
        int ySize = array.getSizeY();

//        System.out.println("start printing " + outputFile);
        
        try {
            
            if (!outputFile.exists())
                outputFile.createNewFile();
            pw = new PrintWriter(new FileOutputStream(outputFile, false), true);
            
            for (int y = 0; y < ySize; y++) {
                for (int x = 0; x < xSize; x++) {
                    pw.print(array.getPoint(x, y) + " ");
                }
                pw.print("\n");
            }

            LogHandler.getLogs()
                    .displayMsg("Array data save to file: " + outputFile,
                            LogHandler.NORMAL);
            
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            if (pw != null)
                pw.close();
        }

    }
    
}
