/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.comp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import pl.imgw.jrat.data.hdf5.RadarVolume;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MatchingPointsManager {

    private static final String DEG = "deg";
    private static final String M = "m";

    private HashMap<String, ResultPairs> results = new HashMap<>();
    private TreeMap<String, HashMap<String, RadarVolume>> obs;
    private HashSet<HashSet<String>> pairs;
    

    private double elevation = 0;
    private int distance = 0;

    /**
     * 
     * 
     * @param obs
     *            Dictionary contains date in string as a key and list of
     *            volumes of matching date with source name as a key
     * 
     * @param sources
     */
    public MatchingPointsManager(
            TreeMap<String, HashMap<String, RadarVolume>> obs,
            HashSet<String> sources) {

        this.obs = obs;
        this.pairs = combine(sources);

    }
    
    public void calculateAll() {
        
        /*
         * iterate through dates
         */
        Collection<String> dates = obs.keySet();
        Iterator<String> datesItr = dates.iterator();
        while(datesItr.hasNext()) {
            String date = datesItr.next();
            
            /*
             * iterate through pairs
             */
            Iterator<HashSet<String>> pairsItr = pairs.iterator();
            while (pairsItr.hasNext()) {
                HashSet<String> pair = pairsItr.next();
                if (pair.size() != 2)
                    continue;
                Iterator<String> i = pair.iterator();
                String s1 = i.next();
                String s2 = i.next(); 
                
                RadarVolume vol1 = obs.get(date).get(s1);
                RadarVolume vol2 = obs.get(date).get(s1);
                
                if(vol1 == null || vol2 == null)
                    continue;
                
                String key = getPairKey(s1, s2);
                System.out.println(key + " " + date);
                MatchingPoints mp = new MatchingPoints();
                mp.initialize(vol1, vol2, elevation, distance);
                
            }
        }
        
    }

    /**
     * 
     * Returns unique key name for a pair of two sources
     * 
     * @param source1
     *            name of the first source
     * @param source2
     *            name of the second source
     * @return null if two names are equal
     */
    public static String getPairKey(String source1, String source2) {
        if (source1.compareTo(source2) > 0)
            return source1 + source2;
        else if (source1.compareTo(source2) < 0)
            return source2 + source1;
        else
            return null;
    }

    /**
     * 
     * @param para
     * @return
     */
    public boolean initialize(String[] para) {

        if (para == null)
            return false;

        if (para.length != 2) {
            return false;
        }

        try {
            for (int i = 0; i < 2; i++) {
                if (para[i].endsWith(DEG)) {
                    elevation = Double.parseDouble(para[i].substring(0,
                            para[i].length() - 3));
                } else if (para[i].endsWith(M)) {
                    distance = Integer.parseInt(para[i].substring(0,
                            para[i].length() - 1));
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }

        if (elevation < 0 || distance < 0) {
            return false;
        }

        System.out.println("distance = " + distance + ", elevation = "
                + elevation);
        return true;
    }

    /**
     * returns 2-element combinations of given set
     * 
     * @param set
     * @return
     */
    public static HashSet<HashSet<String>> combine(HashSet<String> set) {
        if (set.size() < 2) {
            {
                HashSet<HashSet<String>> c = new HashSet<HashSet<String>>();
                c.add(set);
                return c;
            }
        } else {
            HashSet<HashSet<String>> newList = new HashSet<HashSet<String>>();
            for (String o : set) {
                // make a copy of the array
                HashSet<String> rest = new HashSet<String>(set);
                // remove the object
                rest.remove(o);
                newList.addAll(shuffle(o, rest));

            }
            return newList;

        }

    }

    /**
     * Helping method for combination algorithm
     * @param s
     * @param set
     * @return
     */
    private static HashSet<HashSet<String>> shuffle(String s,
            HashSet<String> set) {
        HashSet<HashSet<String>> newList = new HashSet<HashSet<String>>();
        for (String o : set) {
            HashSet<String> parts = new HashSet<String>();
            parts.add(o);
            parts.add(s);
            newList.add(parts);
        }
        return newList;
    }

}
