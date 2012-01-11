/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class FileListReader {

    /**
     * Returns all files pointed by path. If path ended with '*' all files that
     * matches the pattern before '*' are added.
     * 
     * @param path
     * @return
     */
    public static List<File> getFileList(String[] path) {

        List<File> list = new ArrayList<File>();

        for (int i = 0; i < path.length; i++) {
            if (path[i].contains("*")) {
                int end = path[i].lastIndexOf("/");
                int star = path[i].indexOf("*");
                String parent = null;
                String filter = null;
                String suffix = "";
                if (end > -1) {
                    parent = path[i].substring(0, end);
                    filter = path[i].substring(end + 1, star);
                } else {
                    try {
                        parent = new File(".").getCanonicalPath();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                    }
                    filter = path[i].substring(0, star);
                }
                suffix = path[i].substring(star + 1);
                System.out.println(parent + " fi: " + filter + " suf: " + suffix);
                File folder = new File(parent);

                if (folder.isDirectory()) {
                    File[] files = folder.listFiles(new Filter(filter, suffix));
                    if (files != null && files.length > 0) {
                        for (int j = 0; j < files.length; j++) {
                            if (files[j].isFile()) {
                                list.add(files[j]);
                            }
                        }
                    }
                }

            } else {
                File file = new File(path[i]);
                if (file.isFile()) {
                    list.add(file);
                }
            }
        }

        return list;
    }

    public static void main(String[] args) {
        String[] a = new String[3];
        a[0] = "*.xml";
        a[1] = "/home/vrolok/poligon/hs*";
        a[2] = "/home/vrolok/workspace/*";
        
        
        List<File> list = getFileList(a);
        Iterator<File> itr = list.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next().getPath());
        }
    }

}
