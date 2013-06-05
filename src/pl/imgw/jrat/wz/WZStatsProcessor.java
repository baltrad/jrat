/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.wz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import pl.imgw.jrat.data.UnsignedByteArray;
import pl.imgw.jrat.data.WZDataContainer;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.process.FileWatchingProcess;
import pl.imgw.jrat.process.FilesProcessor;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class WZStatsProcessor implements FilesProcessor {

    private int[][] newArray = null;
    private WZDataContainer data = null;
    private File dest = new File(".");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_");
    private ParserManager pm = new ParserManager();
    
    public WZStatsProcessor() {
        pm.setParser(null);
    }
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FileProcessor#processFile(java.io.File)
     */
    @Override
    public void processFile(List<File> files) {
        for (File file : files) {
            if (pm.initialize(file)) {
                System.out.println("processing WZ file: "
                        + file.getAbsolutePath());
                data = (WZDataContainer) pm.getProduct();
                updateResults();
                data = null;
                updateList(file.getName());
                
            }
        }
    }
    
    /**
     * @param dest the dest to set
     */
    public void setDest(File dest) {
        this.dest = dest;
    }

    private String getFileName(String layerName) {
        String fileName = "";
        Calendar now = Calendar.getInstance();
        fileName += sdf.format(now.getTime());
        fileName +=layerName.replaceAll(":", "_");
        return fileName;
    }
    
    private void updateResults() {
        Iterator<String> i = data.getKeySet().iterator();
        double nodata = data.getNodata();
        double belowth = data.getBelowth();
        while (i.hasNext()) {
            newArray = null;
            String name = i.next();
            UnsignedByteArray array = (UnsignedByteArray) data.getArray(name);
            int xmax = array.getSizeX();
            int ymax = array.getSizeY();
            for (int x = 0; x < xmax; x++) {
                for (int y = 0; y < ymax; y++) {
                    if (array.getRawIntPoint(x, y) != nodata
                            && array.getRawIntPoint(x, y) != belowth) {
                        increaseResults(name, x, y, xmax, ymax);
//                        results.get(name)[x][y] = array.getRawBytePoint(x, y);
                    }
                }
            }
            
            if(newArray != null) {
                saveArray(newArray, getFileName(name));
            }
            
        }
    }
    
    private void increaseResults(String name, int x, int y, int xmax, int ymax) {
        if (newArray == null) {
            newArray = loadArray(getFileName(name));
            if (newArray == null)
                newArray = new int[xmax][ymax];
        }
        try {
            newArray[x][y]++;
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing
        }
    }

    private void saveArray(int[][] output, String filename) {
        try {
           
           FileOutputStream fos = new FileOutputStream(new File(dest, filename));
           GZIPOutputStream gzos = new GZIPOutputStream(fos);
           ObjectOutputStream out = new ObjectOutputStream(gzos);
           out.writeObject(output);
           out.flush();
           out.close();
        }
        catch (IOException e) {
//            System.out.println(e); 
        }
     }

    private int[][] loadArray(String filename) {
         try {
           FileInputStream fis = new FileInputStream(new File(dest, filename));
           GZIPInputStream gzis = new GZIPInputStream(fis);
           ObjectInputStream in = new ObjectInputStream(gzis);
           int[][] gelezen_veld = (int[][])in.readObject();
           in.close();
           return gelezen_veld;
         }
         catch (Exception e) {
//             System.out.println(e);
         }
         return null;
     }
     
    private void updateList(String filename) {
        try {
            FileWriter out = new FileWriter(new File(dest, getFileName("list")), true);
            out.write(filename + "\n");
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FilesProcessor#getProcessName()
     */
    @Override
    public String getProcessName() {
        // TODO Auto-generated method stub
        return "WZ Statistics";
    }

}
