/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.comp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import pl.imgw.jrat.MainJRat;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ResultsManager {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ResultsManager() {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void saveResults(int id, String pairKey, Date date, int[] result) {

        File file = new File(getResultPath(id, pairKey));

        try {
            file.createNewFile();
            RandomAccessFile rf = new RandomAccessFile(file, "rw");
            FileChannel fc = rf.getChannel();
            fc.position(fc.size());
            fc.write(ByteBuffer.wrap((sdf.format(date) + ";").getBytes()));
            for (int i = 0; i < result.length; i++) {
                fc.write(ByteBuffer.wrap((result[i] + ";").getBytes()));
            }
            fc.write(ByteBuffer.wrap(("\n").getBytes()));
            fc.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int[] loadResults(int id, String pairKey, Date date) {

        int[] data = null;

        try {
            FileInputStream fstream = new FileInputStream(getResultPath(id,
                    pairKey));
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] cut = strLine.split(";");
                Date d = sdf.parse(cut[0]);
                if (d.equals(date)) {
                    data = new int[cut.length - 1];
                    for (int i = 1; i < cut.length; i++) {
                        data[i - 1] = Integer.parseInt(cut[i]);
                    }
                }

            }
            // Close the input stream
            in.close();
        } catch (Exception e) {// Catch exception if any
            return null;
        }

        return data;
    }

    public boolean hasResult(int id, String pairKey, Date date) {

        try {
            FileInputStream fstream = new FileInputStream(getResultPath(id,
                    pairKey));
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                Date d = sdf.parse(strLine.split(";")[0]);
                if (d.equals(date))
                    return true;
            }
            // Close the input stream
            in.close();
        } catch (Exception e) {// Catch exception if any
            // System.err.println("Error: " + e.getMessage());
        }

        return false;
    }

    private String getResultPath(int id, String pair) {
        return new File(MainJRat.getProgPath(), id + "_" + pair + ".res")
                .getPath();
    }

}
