/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.VolumeParser;

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
    private Map<Date, Set<PolarData>> segregated;
    private Set<PolarVolumesPair> setOfPairs = new HashSet<PolarVolumesPair>();;
    
    private Iterator<PolarVolumesPair> pairItr = setOfPairs.iterator();
    private Iterator<Date> dateItr;

    private VolumeParser parser = GlobalParser.getInstance().getVolumeParser();
    
    private int size = 0;
    
    private String fileNameDatePattern = "yyyyMMddHHmm";
    private SimpleDateFormat fileNameDateFormat = new SimpleDateFormat(fileNameDatePattern);
    private List<PolarData> volumes;


    /**
     * @param name
     * @return
     */
    private Date parseDateFromFileName(String name) {

        Date date = null;

        Pattern pattern = Pattern.compile("\\d{12}");
        Matcher matcher = pattern.matcher(name);
        while (matcher.find()) {
            try {
                date = fileNameDateFormat.parse(matcher.group());
                if (date != null) {
                    return date;
                }
            } catch (ParseException e) {
                continue;
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
        setOfPairs = new HashSet<PolarVolumesPair>();

//        System.out.println(date);
        
        Set<PolarData> files = segregated.get(date);
        Set<Set<PolarData>> pairsOfFiles = combine(files);
        if (pairsOfFiles == null)
            return;
        Iterator<Set<PolarData>> pairedFilesItr = pairsOfFiles.iterator();
        while (pairedFilesItr.hasNext()) {
            Set<PolarData> pairedFile = pairedFilesItr.next();
            Iterator<PolarData> itr = pairedFile.iterator();
            
            PolarData vol1 = itr.next();
            PolarData vol2 = itr.next();
            
            if (vol1 != null && vol2 != null) {
                PolarVolumesPair pair = new PolarVolumesPair(vol1, vol2);
                if (CalidParametersFileHandler.getOptions().isSet()) {
                    /*
                     * add only pairs that are provided in option file, if it
                     * exists
                     */
                    if (CalidParametersFileHandler.getOptions().hasPair(pair))
                        setOfPairs.add(pair);
                } else {
                    setOfPairs.add(pair);
                }
            }

        }
        
        pairItr = setOfPairs.iterator();
    }

    public PolarVolumesPair next() {
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
        Iterator<Set<PolarData>> itr = segregated.values().iterator();
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
    public RadarsPair getPair(Date date, String s1, String s2) {
        return null;
    }

    /*
     * returns 2-element combinations of given set
     * 
     * @param set
     * @return
     */
    private Set<Set<PolarData>> combine(Set<PolarData> set) {
        Set<Set<PolarData>> c = new HashSet<Set<PolarData>>();
        if (set.size() == 2) {
            c.add(set);
            return c;
        } else if(set.size() > 2) {
            for (PolarData o : set) {
                // make a copy of the array
                HashSet<PolarData> rest = new HashSet<PolarData>(set);
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
    private HashSet<HashSet<PolarData>> shuffle(PolarData s, HashSet<PolarData> set) {
        HashSet<HashSet<PolarData>> newList = new HashSet<HashSet<PolarData>>();
        for (PolarData o : set) {
            HashSet<PolarData> parts = new HashSet<PolarData>();
            parts.add(o);
            parts.add(s);
            newList.add(parts);
        }
        return newList;
    }

    /**
     * @param vol
     */
    public void setVolumes(List<PolarData> volumes) {
        this.volumes = volumes;
        initialize();
        
    }


    public void setFiles(List<File> files) {
        List<PolarData> volumes = new LinkedList<PolarData>();
        for (File f : files) {
            PolarData vol = null;
            if (parser.parse(f)) {
                vol = parser.getPolarData();
                volumes.add(vol);
            }
        }
        setVolumes(volumes);
    }

    private void initialize() {
        segregated = new TreeMap<Date, Set<PolarData>>();
        Date date;
        for (PolarData vol : volumes) {

            date = vol.getTime();
//            date = parseDateFromFileName(f.getName());
            if (date != null) {
                Set<PolarData> singles = segregated.get(date);

                if (singles == null) {
                    singles = new HashSet<PolarData>();
                }

                singles.add(vol);
                segregated.put(date, singles);
            }
        }

        dateItr = segregated.keySet().iterator();
        setSize();
    }
    
}
