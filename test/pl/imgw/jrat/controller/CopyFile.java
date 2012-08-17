/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CopyFile implements Runnable {

    private File f1;
    private File f2;

    private File dest;

    private String dateFormat = "yyyyMMddHHmm";
    private SimpleDateFormat datefilename = new SimpleDateFormat(dateFormat);
    
    // Copies src file to dst file.
    // If the dst file does not exist, it is created
    private void copy(File src, File dst) throws IOException {

        if (!dst.getParentFile().exists())
            dst.getParentFile().mkdirs();
        else if (dst.exists())
            dst.delete();

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE) / 10;
        cal.set(Calendar.MINUTE, minute * 10);

        String date2 = datefilename.format(cal.getTime()) + ".cmax";
        cal.add(Calendar.MINUTE, -10);
        String date1 = datefilename.format(cal.getTime()) + ".cmax";

        try {
            copy(f1, new File(dest, date1));
            Thread.sleep(1000);
            copy(f2, new File(dest, date2));
            Thread.sleep(8000);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    
    
    

    /**
     * @param f2
     *            the f2 to set
     */
    public void setFile2(File f2) {
        this.f2 = f2;
    }

    /**
     * @param f1
     *            the f1 to set
     */
    public void setFile1(File f1) {
        this.f1 = f1;
    }

    /**
     * @param dest
     *            the dest to set
     */
    public void setDest(File dest) {
        this.dest = dest;
    }

    public void remove() {
        File[] list = dest.listFiles();
        for (int i = 0; i < list.length; i++) {
            list[i].delete();
        }
    }

}
