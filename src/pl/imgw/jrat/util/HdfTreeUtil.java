/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.util;

/**
 * 
 * Class contains methods which help creating attribute tree of hdf file
 * structure
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class HdfTreeUtil {

    /**
     * 
     * putting name between '|-' and '-+'
     * 
     * @param level
     *            size of indentation (number of characters)
     * @param name
     *            parent name
     * @return
     */
    public static String makeParent(int level, String name) {
        String line = "";
        for (int i = 0; i < level; i++)
            line += " ";
        return line + "|-" + name + "-+";
    }

    /**
     * 
     * adding '|-' before name
     * 
     * @param level
     *            size of indentation (number of characters)
     * @param name
     * @return
     */
    public static String makeChild(int level, String name) {
        String line = "";
        for (int i = 0; i < level; i++)
            line += " ";
        return line + "|-" + name;
    }

    /**
     * adding '|-' before attribute name and '=value' after attribute
     * 
     * @param level
     *            size of indentation (number of characters)
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     */
    public static void makeAttribe(int level, String name, Object value) {
        if (value == null)
            return;
        String line = "";
        for (int i = 0; i < level; i++)
            line += " ";
        System.out.println(line + "|-" + name + "=" + String.valueOf(value));

    }

    /**
     * 
     * @param name
     * @return
     */
    public static String makeGrantparent(String name) {
        return name + "-+";
    }
    
}
