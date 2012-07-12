/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

import java.io.File;
import java.util.Date;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class FileDate implements Comparable<FileDate>{

    private Date date;
    private File file;
    
    public FileDate(File file, Date date) {
        this.date = date;
        this.file = file;
    }
    
    public Date getDate() {
        return date;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(FileDate o) {
        if (date.before(o.date))
            return -1;
        else if (date.equals(o.date))
            return 0;
        else
            return 1;
    }

    public String toString() {
        return date + ": " + file; 
    }

    
}
