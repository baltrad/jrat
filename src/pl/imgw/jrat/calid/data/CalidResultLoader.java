/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import pl.imgw.util.Log;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultLoader extends CalidDataHandler {

    public static CalidSingleResultContainer loadSingleResult(
            CalidParameters params, RadarsPair pair, Date date) {
        CalidSingleResultContainer result = new CalidSingleResultContainer(
                params, pair);
        result.setCoords();
        result.setResultDate(date);
        if (loadSingleResult(result))
            return result;
        return null;
    }

    /**
     * Load results from default file.
     * 
     * @param results
     * @return false if couldn't find result for these parameters and date
     */
    public static boolean loadSingleResult(CalidSingleResultContainer results) {
        File file = getResultsPath(results.getParams(),
                results.getResultDate(), results.getPair());
        if (!file.exists()) {
            log.printMsg("CALID results file: " + file + " does not exist",
                    Log.TYPE_WARNING, Log.MODE_VERBOSE);
            return false;
        }
        return loadSingleResult(results, file);
    }

    /**
     * Load results from file to CalidSingleResultContainer
     * 
     * @param file
     * @return false if couldn't find result for these parameters and date
     */
    protected static boolean loadSingleResult(
            CalidSingleResultContainer results, File file) {

        results.resetDifferences();

        Date date = results.getResultDate();
        List<PairedPoint> points = results.getPairedPointsList();
        Scanner scan = null;
        try {
            scan = new Scanner(file);

            log.printMsg("Loading results from file: " + file, Log.TYPE_NORMAL,
                    Log.MODE_VERBOSE);

            while (scan.hasNext()) {
                String line = scan.nextLine();
                if (line.startsWith(COMMENTS)) {
                    continue;
                }
                String[] words = line.split(" ");

                if (words.length != points.size() + 3) {
                    // System.out.println(words.length + " powinno byc: "
                    // + cc.getPairedPointsList().size() + 3);
                    continue;
                }
                Date dateRead = CALID_DATE_TIME_FORMAT.parse(words[0]);

                if (dateRead.equals(date)) {
                    for (int i = 1; i < words.length - 2; i++) {
                        if (!words[i].matches(NULL))
                            points.get(i - 1).setDifference(
                                    Double.parseDouble(words[i]));
                    }
                    int r1understate = Integer
                            .parseInt(words[words.length - 2]);
                    int r2understate = Integer
                            .parseInt(words[words.length - 1]);

                    results.setR1understate(r1understate);
                    results.setR2understate(r2understate);
                    scan.close();
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            log.printMsg("CALID: Results file not found: " + file,
                    Log.TYPE_WARNING, Log.MODE_VERBOSE);
        } catch (ParseException e) {
            log.printMsg("CALID: Parsing file error: " + file,
                    Log.TYPE_WARNING, Log.MODE_VERBOSE);
        } catch (NumberFormatException e) {
            log.printMsg("CALID: Wrong format: " + file, Log.TYPE_WARNING,
                    Log.MODE_VERBOSE);
        }

        return false;
    }

    public static CalidSingleResultContainer loadResultsFromLine(String line,
            CalidParameters params) {
        return loadResultsFromLine(line, params, null);

    }

    /**
     * 
     * @param line
     * @param params
     * @param pair
     * @return <b>null</b> if no results in time range given in <code>CalidParameters</code>
     */
    public static CalidSingleResultContainer loadResultsFromLine(String line,
            CalidParameters params, RadarsPair pair) {

        String[] words = line.split(" ");
        try {
            Date dateRead = CALID_DATE_TIME_FORMAT.parse(words[0]);
            Date from = params.getStartRangeDate();
            Date to = params.getEndRangeDate();
            if (params.isStartDateDefault()
                    || (!dateRead.before(from) && !dateRead.after(to))) {
                List<PairedPoint> points = new ArrayList<PairedPoint>();

                for (int i = 1; i < words.length - 2; i++) {

                    PairedPoint point;
                    if (words[i].matches(NULL)) {
                        point = new PairedPoint(null);
                    } else
                        point = new PairedPoint(Double.parseDouble(words[i]));
                    points.add(point);
                }
                CalidSingleResultContainer result = new CalidSingleResultContainer(
                        params, pair);
                result.setCoords(points);
                result.setR1understate(Integer
                        .parseInt(words[words.length - 2]));
                result.setR2understate(Integer
                        .parseInt(words[words.length - 1]));
                result.setResultDate(dateRead);

                return result;
            }
        } catch (ParseException e) {
            log.printMsg("CALID: Wrong format: " + words[0], Log.TYPE_WARNING,
                    Log.MODE_VERBOSE);
            return null;
        }

        return null;

    }

}
