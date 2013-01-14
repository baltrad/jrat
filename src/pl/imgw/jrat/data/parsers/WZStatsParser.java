/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.arrays.IntDataArray;
import pl.imgw.jrat.data.containers.DataContainer;
import pl.imgw.jrat.data.containers.SimpleDataContainer;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class WZStatsParser implements FileParser {

    private SimpleDataContainer data = null;
    
    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.parsers.FileParser#isValid(java.io.File)
     */
    @Override
    public boolean isValid(File file) {
        return initialize(file);
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.parsers.FileParser#initialize(java.io.File)
     */
    @Override
    public boolean initialize(File file) {
        int[][] rawarray = loadArray(file);
        if (rawarray == null)
            return false;
        IntDataArray array = new IntDataArray(rawarray);
        data = new SimpleDataContainer();
        HashMap<String, ArrayData> map = new HashMap<String, ArrayData>();
        map.put(file.getName(), array);
        data.setArrayList(map);

        return true;
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.parsers.FileParser#getProduct()
     */
    @Override
    public DataContainer getProduct() {
        return data;
    }

    
    private void saveArray(int[][] output, String filename) {
        try {
           
           FileOutputStream fos = new FileOutputStream(new File(filename));
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

    private int[][] loadArray(File file) {
         try {
           FileInputStream fis = new FileInputStream(file);
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
    
}
