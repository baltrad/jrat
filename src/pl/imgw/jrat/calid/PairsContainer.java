/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static pl.imgw.jrat.tools.out.Logging.PROGRESS_BAR_ONLY;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.data.H5DataContainer;
import pl.imgw.jrat.data.OdimH5Volume;
import pl.imgw.jrat.data.RainbowDataContainer;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.tools.out.ConsoleProgressBar;
import pl.imgw.jrat.tools.out.LogHandler;

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

    private Set<Pair> pairs = new TreeSet<Pair>();

    /**
     * 
     * @param files only valid volume files will be parsed and used to create pairs.
     */
    public PairsContainer(List<File> files) {

        ParserManager manager = new ParserManager();
        manager.setParser(new DefaultParser());

        Map<Date, Map<String, VolumeContainer>> segregated = new HashMap<Date, Map<String, VolumeContainer>>();

        ConsoleProgressBar.getProgressBar().initialize(20,
                files.size(),
                LogHandler.getLogs().getVerbose() == PROGRESS_BAR_ONLY,
                "Initializing files");
        
        for (File f : files) {
            ConsoleProgressBar.getProgressBar().evaluate();
            // System.out.println(f);
            if (manager.initialize(f)) {
                DataContainer data = manager.getProduct();
                VolumeContainer vol = null;

                if (data instanceof H5DataContainer) {
                    vol = new OdimH5Volume((H5DataContainer) data);
                } else if (data instanceof RainbowDataContainer) {
                    vol = new RainbowVolume((RainbowDataContainer) data);
                } else
                    continue;

                String source = vol.getSiteName();
                Date date = vol.getTime();

                Map<String, VolumeContainer> single = segregated.get(date);
                if (single == null)
                    single = new HashMap<String, VolumeContainer>();
                single.put(source, vol);
                segregated.put(date, single);
            }
        }

        Iterator<Date> itr = segregated.keySet().iterator();
        while (itr.hasNext()) {
            Date date = itr.next();
            Map<String, VolumeContainer> single = segregated.get(date);
            Set<Set<String>> combined = combine(single.keySet());
            Iterator<Set<String>> itrc = combined.iterator();
            while (itrc.hasNext()) {
                Set<String> pairnames = itrc.next();
                if (pairnames.size() != 2)
                    continue;
                Iterator<String> i = pairnames.iterator();
                Pair pair = new Pair(single.get(i.next()), single.get(i.next()));
                if (pair.hasRealVolums()) {
                    pairs.add(pair);
                }
            }
        }
        
        ConsoleProgressBar.getProgressBar().printDoneMsg();
        
    }

    /**
     * 
     * @return all valid pairs in the container
     */
    public Set<Pair> getPairs() {
        return pairs;
    }

    /**
     * 
     * @param date
     *            with minute precision, make sure seconds and miliseconds are
     *            set to 0
     * @return all valid pairs matching given date</br> return empty set if no
     *         pair matching the date
     */
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
    private Set<Set<String>> combine(Set<String> set) {
        Set<Set<String>> c = new HashSet<Set<String>>();
        if (set.size() < 2) {
            {
                c.add(set);
                return c;
            }
        } else {
            for (String o : set) {
                // make a copy of the array
                HashSet<String> rest = new HashSet<String>(set);
                // remove the object
                rest.remove(o);
                c.addAll(shuffle(o, rest));

            }
            return c;
        }
    }

    /**
     * Helping method for combination algorithm
     * 
     * @param s
     * @param set
     * @return
     */
    private HashSet<HashSet<String>> shuffle(String s, HashSet<String> set) {
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
