/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

import java.io.File;
import java.util.List;

/**
 * 
 * Receiving list of files matching given pattern.
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface FilePatternFilter {

    /**
     * 
     * Returns list of <code>FileDate</code> objects based on location and
     * pattern given as a string <code>expression</code>. 
     * 
     * <p>
     * If siteName of the file contains date written as "yyyyMMddHHmm", then this
     * date will be saved to <code>FileDate</code> object, otherwise last
     * modification date will be saved.
     * 
     * @param expression
     *            should point the path and pattern of expected file's siteName
     * 
     * @return list of files with date, it can be either date parsed from the
     *         file siteName or if not exists date of last file modification. List
     *         should be sorted by date.
     */
    public List<File> getFileList(String expression);

}
