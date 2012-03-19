/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.comp;

import static pl.imgw.jrat.data.hdf5.OdimH5Constans.PVOL;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import pl.imgw.jrat.data.hdf5.OdimH5File;
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

    private HashMap<String, ResultsManager> results = new HashMap<String, ResultsManager>();
    private TreeMap<Date, HashMap<String, RadarVolume>> obs;
    private HashSet<HashSet<String>> pairs;
    private HashMap<String, MatchingPoints> mps = new HashMap<String, MatchingPoints>();
    

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
    public void setData (List<OdimH5File> odims) {

        /*
         * <date<source_name,volume_data>>
         */
        obs = new TreeMap<Date, HashMap<String, RadarVolume>>();
        HashSet<String> sources = new HashSet<String>();
        Iterator<OdimH5File> iterator = odims.iterator();
        while(iterator.hasNext()) {
            OdimH5File next = iterator.next();
            if(!next.getType().matches(PVOL))
                continue;
            RadarVolume vol = (RadarVolume) next;
            sources.add(vol.getSource());
            Date date = vol.getRoundedDate();
            
            HashMap<String, RadarVolume> r = null;
            if(obs.containsKey(date)) {
                r = obs.get(date);
            } else {
                r = new HashMap<String,RadarVolume>();
            }
            r.put(vol.getSource(), vol);
            obs.put(date, r);
        }
        pairs = combine(sources);
    }
    
    public void calculateAll() {
        
        long time = System.currentTimeMillis();
        ResultsManager rm = new ResultsManager();
        /*
         * iterate through dates
         */
        Collection<Date> dates = obs.keySet();
        Iterator<Date> datesItr = dates.iterator();
        while(datesItr.hasNext()) {
            Date date = datesItr.next();
            
            /*
             * iterate through pairs
             */
            Iterator<HashSet<String>> pairsItr = pairs.iterator();
            while (pairsItr.hasNext()) {
                HashSet<String> pair = pairsItr.next();
                if (pair.size() != 2)
                    continue;
                Iterator<String> i = pair.iterator();
                String[] sources = new String[2];
                sources[0] = i.next();
                sources[1] = i.next(); 
                
                RadarVolume vol1 = obs.get(date).get(getFirstFromPair(sources));
                RadarVolume vol2 = obs.get(date).get(getSecondFromPair(sources));
                
                if(vol1 == null || vol2 == null)
                    continue;
                
                String key = getPairKey(sources);
                System.out.println(key + " " + date);
                MatchingPoints mp = null;
                if(!mps.containsKey(key)) {
                    mp = new MatchingPoints(vol1, vol2, elevation, distance);
                    mps.put(key, mp);
                } else
                    mp = mps.get(key);
                
                if (mp.valid) {
                    if (!rm.hasResult(mp.getId(), key, date)) {
                        int[] result = mp.getMatchingPointsData(vol1, vol2);
                        rm.saveResults(mp.getId(), key, date, result);
                    } else {
                        System.out.println("juz bylo policzone");
                    }
                }
                
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
    public String getPairKey(String[] sources) {
        String source1 = sources[0].replaceAll("[^\\p{L}\\p{N}]", "");
        String source2 = sources[1].replaceAll("[^\\p{L}\\p{N}]", "");
        
        if (source1.compareTo(source2) > 0)
            return source1 + "_" + source2;
        else if (source1.compareTo(source2) < 0)
            return source2 + "_" +source1;
        else
            return null;
    }
    
    private String getFirstFromPair(String[] sources) {
        String source1 = sources[0];
        String source2 = sources[1];
        if (source1.compareTo(source2) > 0)
            return source1;
        else if (source1.compareTo(source2) < 0)
            return source2;
        else
            return null;
    }
    
    private String getSecondFromPair(String[] sources) {
        String source1 = sources[0];
        String source2 = sources[1];
        if (source1.compareTo(source2) > 0)
            return source2;
        else if (source1.compareTo(source2) < 0)
            return source1;
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
    public HashSet<HashSet<String>> combine(HashSet<String> set) {
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
    private HashSet<HashSet<String>> shuffle(String s,
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
