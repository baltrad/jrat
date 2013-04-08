/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pl.imgw.jrat.data.containers.OdimDataContainer;
import pl.imgw.jrat.data.containers.OdimH5Volume;
import pl.imgw.jrat.data.containers.RainbowDataContainer;
import pl.imgw.jrat.data.containers.RainbowVolume;
import pl.imgw.jrat.data.containers.VolumeContainer;
import pl.imgw.jrat.data.parsers.FileParser;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.tools.out.ConsoleProgressBar;

/**
 * 
 * The class prepares list of all available pairs. Pairs are created from all
 * products that are received (parsed) from given files. Products are segregated
 * by dates, and each pair contains two different products with the same date.
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PairsContainer {

//    private Set<Pair> pairs = new TreeSet<Pair>();
    private Map<Date, Set<File>> segregated;
    private Set<Pair> setOfPairs = new HashSet<Pair>();;
    
    private Iterator<Pair> pairItr = setOfPairs.iterator();
    private Iterator<Date> dateItr;
    

    private FileParser parser = GlobalParser.getInstance().getParser();
    
    private int size = 0;
    
    private String fileNameDatePattern = "yyyyMMddHHmm";
    private SimpleDateFormat fileNameDateFormat = new SimpleDateFormat(fileNameDatePattern);
    
    /**
     * 
     * @param files only valid volume files will be parsed and used to create pairs.
     */
    public PairsContainer(List<File> files) {

//        fileNameDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        ParserManager manager = new ParserManager();
        manager.setParser(GlobalParser.getInstance().getParser());

        segregated = new TreeMap<Date, Set<File>>();

        Date date;
        for (File f : files) {
            
            ConsoleProgressBar.getProgressBar().evaluate();

            date = parseDateFromFileName(f.getName());
            if (date != null) {
                Set<File> singles = segregated.get(date);

                if (singles == null)
                    singles = new HashSet<File>();
                singles.add(f);
                segregated.put(date, singles);
            }
        }

        dateItr = segregated.keySet().iterator();
        
        setSize();
        
    }

    /**
     * @param name
     * @return
     */
    private Date parseDateFromFileName(String name) {

        int l = fileNameDatePattern.length();

        Date date = null;
        if (name.length() < l)
            return null;

        int iterates = name.length() - l + 1;

        for (int i = 0; i < iterates; i++) {
            try {
                date = fileNameDateFormat.parse(name.substring(i, i + l));
            } catch (ParseException e) {
                
                continue;
            }

            if (date != null) {
                return date;
            }
        }

        return null;
    }

    public boolean hasNext() {
        if (pairItr.hasNext()) {
            return true;
        } else if (dateItr.hasNext()) {
            setPairItr(dateItr.next());
            //set new pairItr
            return hasNext();
        } else
            return false;
    }
    
    /**
     * @param next
     */
    private void setPairItr(Date date) {
        setOfPairs = new HashSet<Pair>();

//        System.out.println(date);
        
        Set<File> files = segregated.get(date);
        Set<Set<File>> pairsOfFiles = combine(files);
        if (pairsOfFiles == null)
            return;
        Iterator<Set<File>> pairedFilesItr = pairsOfFiles.iterator();
        while (pairedFilesItr.hasNext()) {
            Set<File> pairedFile = pairedFilesItr.next();
            Iterator<File> itr = pairedFile.iterator();
            
            File f1 = itr.next();
            File f2 = itr.next();
            
            VolumeContainer vol1 = null;
            VolumeContainer vol2 = null;
            if (parser.initialize(f1)) {
                if (parser.getProduct() instanceof RainbowDataContainer) {
                    vol1 = new RainbowVolume(
                            (RainbowDataContainer) parser.getProduct());
                } else if (parser.getProduct() instanceof OdimDataContainer) {
                    vol1 = new OdimH5Volume(
                            (OdimDataContainer) parser.getProduct());
                }
            }

            if (parser.initialize(f2)) {
                if (parser.getProduct() instanceof RainbowDataContainer) {
                    vol2 = new RainbowVolume(
                            (RainbowDataContainer) parser.getProduct());
                } else if (parser.getProduct() instanceof OdimDataContainer) {
                    vol2 = new OdimH5Volume(
                            (OdimDataContainer) parser.getProduct());
                }
            }

            if (vol1 != null && vol2 != null) {
                Pair pair = new Pair(vol1, vol2);
                if (CalidOptionsHandler.getOptions().isSet()) {
                    /*
                     * add only pairs that are provided in option file, if it
                     * exists
                     */
                    if (CalidOptionsHandler.getOptions().hasPair(pair))
                        setOfPairs.add(pair);
                } else {
                    setOfPairs.add(pair);
                }
            }

        }
        
        pairItr = setOfPairs.iterator();
    }

    public Pair next() {
        if (hasNext())
            return pairItr.next();
        else
            return null;
    }
    
    /**
     * It is just an approximation, because files are being initialized each
     * time new pair has been return, and if given file is corrupted it will not
     * be put to pair. The real results will always be smaller or equal the
     * returned value
     * 
     * @return
     */
    public int getSize() {
        return size;
    }
    
    /**
     * 
     */
    private void setSize() {
        int size = 0;
        Iterator<Set<File>> itr = segregated.values().iterator();
        while (itr.hasNext()) {
            size += combination(itr.next().size());
        }
        this.size = size;
    }
    
    private static int combination(int n) {
        if (n < 2)
            return 0;
        return factorial(n) / (2 * factorial(n - 2));

    }
    
    private static int factorial(int n) {
        int fact = 1; // this  will be the result
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
    
    /**
     * 
     * @return all valid pairs in the container
     *
    public Set<Pair> getPairs() {
        return pairs;
    }
    */

    /**
     * 
     * @param date
     *            with minute precision, make sure seconds and miliseconds are
     *            set to 0
     * @return all valid pairs matching given date</br> return empty set if no
     *         pair matching the date
     *
    public Set<Pair> getPairs(Date date) {
        Iterator<Pair> itr = pairs.iterator();
        Set<Pair> set = new HashSet<Pair>();
        while (itr.hasNext()) {
            Pair pair = itr.next();
            if (pair.getDate().equals(date))
                set.add(pair);
        }
        return set;
    }
     */

    /**
     * Not implemented yet.
     * 
     * @param date
     * @param s1
     * @param s2
     * @return null
     */
    public Pair getPair(Date date, String s1, String s2) {
        return null;
    }

    /*
     * returns 2-element combinations of given set
     * 
     * @param set
     * @return
     */
    private Set<Set<File>> combine(Set<File> set) {
        Set<Set<File>> c = new HashSet<Set<File>>();
        if (set.size() == 2) {
            c.add(set);
            return c;
        } else if(set.size() > 2) {
            for (File o : set) {
                // make a copy of the array
                HashSet<File> rest = new HashSet<File>(set);
                // remove the object
                rest.remove(o);
                c.addAll(shuffle(o, rest));

            }
            return c;
        }
        return null;
    }

    /**
     * Helping method for combination algorithm
     * 
     * @param s
     * @param set
     * @return
     */
    private HashSet<HashSet<File>> shuffle(File s, HashSet<File> set) {
        HashSet<HashSet<File>> newList = new HashSet<HashSet<File>>();
        for (File o : set) {
            HashSet<File> parts = new HashSet<File>();
            parts.add(o);
            parts.add(s);
            newList.add(parts);
        }
        return newList;
    }

}
