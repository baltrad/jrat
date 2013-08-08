/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Set;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.calid.CalidException;
import pl.imgw.jrat.tools.out.FileResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidGnuplotResultPrinter extends CalidPeriodsResultsPrinter {

    private static Log log = LogManager.getLogger();

    private File output;
    private SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * @param params
     * @param detParams
     * @throws IOException
     */
    public CalidGnuplotResultPrinter(String[] args, File output) throws CalidException{
        super(args, 1);
        if(pair.hasOnlyOneSource()) {
            throw new CalidException("Must provide both sources");
        }
        if (output == null) {
            this.output = new File(pair.getSource1() + pair.getSource2()
                    + shortDateFormat.format(params.getStartRangeDate()) + ".png");
        }else if(output.isDirectory()) {
            this.output = new File(output, pair.getSource1() + pair.getSource2()
                    + shortDateFormat.format(params.getStartRangeDate()) + ".png");
        } else
            this.output = output;

    }

    public void generateMeanDifferencePlots() throws IOException {

        Set<File> files = CalidResultFileGetter.getResultFiles(pair, params);

        if (files.isEmpty()) {
            log.printMsg("No data to generate this plot", Log.TYPE_WARNING,
                    Log.MODE_NORMAL);
            return;
        }

        log.printMsg("# Results between "
                + sdf.format(params.getStartRangeDate()) + " and "
                + sdf.format(params.getEndRangeDate()) + " for freq >="
                + params.getFrequency(), Log.TYPE_NORMAL,
                Log.MODE_NORMAL);
        
        // ConsoleProgressBar.getProgressBar().initialize(20, 5,
        // log.getVerbose() == SPARE, "Plot generating");

        File data1day = new File(AplicationConstans.TMP, "tmp1");
        File data5day = new File(AplicationConstans.TMP, "tmp2");
        File data10day = new File(AplicationConstans.TMP, "tmp3");

        ResultPrinter pr = new FileResultPrinter(data1day);
        ResultPrinterManager.getManager().setPrinter(pr);

        setPeriod(1);
        if (!printResults(files)) {
            log.printMsg("No data to generate this plot", Log.TYPE_WARNING,
                    Log.MODE_NORMAL);
            return;
        }

        Double median = getMedianResult();
        
        // ConsoleProgressBar.getProgressBar().evaluate();

        ((FileResultPrinter) pr).setFile(data5day);
        setPeriod(5);
        if (!printResults(files)) {
            log.printMsg("No data to generate this plot", Log.TYPE_WARNING,
                    Log.MODE_NORMAL);
            return;
        }

        // ConsoleProgressBar.getProgressBar().evaluate();

        ((FileResultPrinter) pr).setFile(data10day);
        setPeriod(10);
        if (!printResults(files)) {
            log.printMsg("No data to generate this plot", Log.TYPE_WARNING,
                    Log.MODE_NORMAL);
            return;
        }

        // ConsoleProgressBar.getProgressBar().evaluate();

        CalidMeanDifferencePlot plot = new CalidMeanDifferencePlot(data1day,
                data5day, data10day);

        plot.setPairsName(pair.getSource1(), pair.getSource2());
        plot.setTimePeriod(params.getStartRangeDate(), params.getEndRangeDate());
        plot.setYmin(-20);
        plot.setYmax(20);
        plot.setOutput(output);
        plot.setMedian(median);


        if (plot.plot())
            ;

        data1day.delete();
        data5day.delete();
        data10day.delete();

        // ConsoleProgressBar.getProgressBar().printDoneMsg();

        if(output.exists())
            log.printMsg("New plot generated: " + output, Log.TYPE_NORMAL,
                Log.MODE_NORMAL);
        else
            log.printMsg("Fail to generate plot: " + output, Log.TYPE_WARNING,
                    Log.MODE_NORMAL);

    }

}
