/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class FileResultPrinter implements ResultPrinter {

    
    PrintWriter pw;
    
    /**
     * @throws IOException 
     * 
     */
    public FileResultPrinter(File f) throws IOException {

        setFile(f);
        
    }
    
    public void setFile(File f) throws IOException {
        closeFile();
        
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        pw = new PrintWriter(new FileOutputStream(f, true), true);
    }
    
    public void closeFile() {
        if(pw != null) {
            pw.close();
        }
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.tools.out.ResultPrinter#println(java.lang.String)
     */
    @Override
    public void println(String str) {
        print(str + "\n");
        
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.tools.out.ResultPrinter#print(java.lang.String)
     */
    @Override
    public void print(String str) {
        pw.printf(str);
    }
    
    

}
