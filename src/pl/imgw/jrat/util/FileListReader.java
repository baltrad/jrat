/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class FileListReader {
    
    private SimpleDateFormat sdf;
    TreeMap<Date, Map<String, File>> map;

    public FileListReader() {
        sdf  = new SimpleDateFormat("yyyyMMddHHmm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    /**
     * Returns all files pointed by path. If path ended with '*' all files that
     * matches the pattern before '*' are added.
     * 
     * @param path
     * @return
     */
    public TreeMap<Date, Map<String,File>> getFileList(String[] path) {

        map = new TreeMap<Date, Map<String,File>>();
        
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
//                System.out.println(parent + " fi: " + filter + " suf: " + suffix);
                File folder = new File(parent);

                if (folder.isDirectory()) {
                    File[] files = folder.listFiles(new Filter(filter, suffix));
                    if (files != null && files.length > 0) {
                        for (int j = 0; j < files.length; j++) {
                            if (files[j].isFile()) {
                                putSourceFile(files[j]);
                                list.add(files[j]);
                            }
                        }
                    }
                }

            } else {
                File file = new File(path[i]);
                if (file.isFile()) {
                    putSourceFile(file);
                    list.add(file);
                }
            }
        }

        return map;
    }

    private void putSourceFile(File file){
        String name = file.getName();
        Map<String, File> sources;
        String[] nameParts = name.split("_");
        Date date = getDateFromFile(nameParts[nameParts.length - 1]);
        if(date == null)
            return;
        String key = name
                .substring(
                        0,
                        name.indexOf(nameParts[nameParts.length - 1]) -1);
        if(map.containsKey(date)) {
            sources = map.get(date);
            map.remove(date);
        } else {
            sources = new HashMap<String, File>();
        }
        sources.put(key, file);
        map.put(date, sources);
    }
    
    private Date getDateFromFile(String fileName) {
        
        try {
            return sdf.parse(fileName.substring(0, 12));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static void main(String[] args) {
        
        String[] a = new String[1];
        
        a[0] = "/home/vrolok/poligon/T*";
        
        
        FileListReader flr = new FileListReader();

        TreeMap<Date, Map<String, File>> map = flr.getFileList(a);
        Iterator<Date> itr = map.keySet().iterator();
        while (itr.hasNext()) {
            Date date = itr.next();
            System.out.println("date: " + flr.sdf.format(date));
            Iterator<String> sources = map.get(date).keySet().iterator();
            while(sources.hasNext()) {
                String key = sources.next();
                System.out.println("sources: " + key + " plik: " + map.get(date).get(key).getName());
            }
                
        }
    }

}
