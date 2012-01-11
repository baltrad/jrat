/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.List;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Filter implements FilenameFilter {

    protected String pattern = null;
    protected String suffix = null;

    public Filter(String patthern) {
        this.pattern = patthern;
    }
    public Filter(String pattern, String suffix) {
        this.pattern = pattern;
        this.suffix = suffix;
    }
    
    
    /* (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    @Override
    public boolean accept(File dir, String name) {
        if (suffix.isEmpty()) {
            if (name.startsWith(pattern))
                return true;
        } else {
            if (name.startsWith(pattern) && name.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

}
