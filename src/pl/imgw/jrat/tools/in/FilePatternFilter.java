/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

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
     * @param expression
     *            should point the path and pattern of expected file's name
     * @return set of files, key is a date, it can be either date included in
     *         the file name or if not exists date of last file modification.
     *         List should be sorted by date.
     */
    public List<FileDate> getFileList(String expression);

}
