/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RegexFileFilter implements FilePatternFilter {

    private List<FileDate> list = new ArrayList<FileDate>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

    /*
     * (non-Javadoc)
     * 
     * @see
     * pl.imgw.jrat.tools.in.FilePatternFilter#getFileList(java.lang.String)
     */
    @Override
    public List<FileDate> getFileList(String expression) {
        String pattern = expression.split("/")[expression.split("/").length - 1];
        String parent = expression.substring(0, expression.indexOf(pattern));
        if (parent.isEmpty())
            parent = ".";
        File dir = new File(parent);
        FileFilter fileFilter = new WildcardFileFilter(pattern);
        File[] files = dir.listFiles(fileFilter);
        if (files == null) {
            return null;
        }
        for (int i = 0; i < files.length; i++) {

            FileDate fd = new FileDate(files[i], getDate(files[i]));
            list.add(fd);
        }
        Collections.sort(list);
        return list;
    }

    /**
     * 
     * Helping method. Receives date from file name, or if not available, returns
     * date of last file modification.
     * 
     * @param file
     * @return
     */
    private Date getDate(File file) {
        String word = file.getName();
        int count = 0;
        String s = "";
        for (int i = 0; i < word.length(); i++) {
            if (Character.isDigit(word.charAt(i))) {
                count++;
            } else
                count = 0;
            if (count == 12) {
                s = word.substring(i - 11, i + 1);
                break;
            }
        }

        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (Exception e) {
            date = new Date(file.lastModified());
        }
        return date;
    }

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        FilePatternFilter filter = new RegexFileFilter();
        String expression = "/home/lwojtas/poligon/vol/brz/vol/BRZ_250_Z.vol/*";
        List<FileDate> list = filter.getFileList(expression);
        System.out.println("time=" + (System.currentTimeMillis() - time));
        
    }

}
