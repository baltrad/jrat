/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.util;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class HdfTreeUtil {

    public static String makeParent(int level, String name) {
        String line = "";
        for(int i = 0; i < level; i++)
            line += " ";
        return line+"|-"+name+"-+";
    }
    public static String makeGrantparent(String name) {
        return name + "-+";
    }
    
    public static String makeChild (int level, String name) {
        String line = "";
        for(int i = 0; i < level; i++)
            line += " ";
        return line+"|-"+name;
    }
    public static void makeAttribe(int level, String name, Object value) {
        if(value == null)
            return;
        String line = "";
        for(int i = 0; i < level; i++)
            line += " ";
        System.out.println(line+"|-"+name+"="+String.valueOf(value));
                
    }
    
}
