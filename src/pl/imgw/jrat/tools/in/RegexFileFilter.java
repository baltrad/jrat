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
 * Filtering files from given path using regular expression as a pattern for
 * file names. For example:
 * 
 * <blockquote>
 * 
 * String expression = "/home/user/archive/*.doc" <br>
 * new RegexFileFilter().getFileList(expression);
 * 
 * </blockquote>
 * 
 * Will include all files from "/home/user/archive" directory that end with
 * ".doc"
 * 
 * Only files with date in file name will be included.
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RegexFileFilter implements FilePatternFilter {

    private List<File> list;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * pl.imgw.jrat.tools.in.FilePatternFilter#getFileList(java.lang.String)
     */
    @Override
    public List<File> getFileList(String exp) {
        System.out.println(exp);
        list = new ArrayList<File>();
        String[] parts = exp.split(" ");
        for (int p = 0; p < parts.length; p++) {
            String part = parts[p];
            String pattern = part.split("/")[part.split("/").length - 1];
            String parent = part.substring(0, part.indexOf(pattern));
            File dir = new File(parent);
            FileFilter fileFilter = new WildcardFileFilter(pattern);
            File[] files = dir.listFiles(fileFilter);
            if (files == null) {
                return list;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
//                    System.out.println(files[i]);
//                    FileDate fd = new FileDate(files[i], getDate(files[i]));
//                    if(getDate(files[i]) != null)
                        list.add(files[i]);
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    /**
     * 
     * Helping method. Receives date from file name, or if not available,
     * returns date of last file modification.
     * 
     * @param file
     * @return
     */
    public static Date getDate(File file) {
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

}
