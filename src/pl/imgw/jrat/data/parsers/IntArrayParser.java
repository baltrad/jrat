/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.arrays.IntDataArray;
import pl.imgw.jrat.data.containers.DataContainer;
import pl.imgw.jrat.data.containers.SimpleDataContainer;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class IntArrayParser implements FileParser {

    SimpleDataContainer data = new SimpleDataContainer();
    
    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#isValid(java.io.File)
     */
    @Override
    public boolean isValid(File file) {
        return initialize(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#initialize(java.io.File)
     */
    @Override
    public boolean initialize(File file) {

        
        
        FileReader input;
        BufferedReader in;
        try {
            input = new FileReader(file);
            in = new BufferedReader(input);

            String s = in.readLine();

            if (s == null)
                return false;

            int xmax = s.split(" ").length;
            int ymax = count(file);

            if(xmax == 0 || ymax == 0)
                return false;
            
            int y = 0;

            int[][] array = new int[xmax][ymax];

            while (s != null) {
                String[] word = s.split(" ");
                for (int x = 0; x < xmax; x++) {
                    array[x][y] = Integer.parseInt(word[x]);
                }
                y++;
                s = in.readLine();
            }
            in.close();
            
            IntDataArray arraydata = new IntDataArray(array);
            HashMap<String, ArrayData> arrayList = new HashMap<String, ArrayData>();
            arrayList.put(file.getName(), arraydata);
            data.setArrayList(arrayList);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#getProduct()
     */
    @Override
    public DataContainer getProduct() {
        return data;
    }

    public int count(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
            }
            return count;
        } finally {
            is.close();
        }
    }

}
