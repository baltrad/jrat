/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Set;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.tools.out.ConsoleProgressBar;
import pl.imgw.jrat.tools.out.FileResultPrinter;
import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.ResultPrinter;
import pl.imgw.jrat.tools.out.ResultPrinterManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidGnuplotResultPrinter extends CalidDetailedResultsPrinter {

    private File output;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    /**
     * @param params
     * @param detParams
     * @throws IOException
     */
    public CalidGnuplotResultPrinter(CalidParsedParameters params, String output) {
        super(params);
        if(output.isEmpty()) {
            output = params.getSource1() + params.getSource2()
                    + sdf.format(params.getStartDate()) + ".png";
        }
        this.output = new File(output);

    }

    public void generateMeanDifferencePlots() throws IOException {
        
        Set<File> files = getResultsFiles();
        if (files.size() > 1) {
            File f = files.iterator().next();
            files.clear();
            files.add(f);
        }
        
        if(files.isEmpty()) {
            LogHandler.getLogs().displayMsg("No data to generate this plot", WARNING);
            return;
        }
        
        ConsoleProgressBar.getProgressBar().initialize(20, 5,
                LogHandler.getLogs().getVerbose() == PROGRESS_BAR_ONLY,
                "Plot generating");
        
        File data1day = new File(AplicationConstans.TMP, "tmp1");
        File data5day = new File(AplicationConstans.TMP, "tmp2");
        File data10day = new File(AplicationConstans.TMP, "tmp3");

        ResultPrinter pr = new FileResultPrinter(data1day);
        ResultPrinterManager.getManager().setPrinter(pr);
        
        
        setMethod(MEAN);
        setPeriod(1);
        if (!printResults(files)) {
            LogHandler.getLogs().displayMsg("No data to generate this plot",
                    WARNING);
            return;
        }

        ConsoleProgressBar.getProgressBar().evaluate();
        
        ((FileResultPrinter) pr).setFile(data5day);
        setMethod(MEAN);
        setPeriod(5);
        if (!printResults(files)) {
            LogHandler.getLogs().displayMsg("No data to generate this plot",
                    WARNING);
            return;
        }

        ConsoleProgressBar.getProgressBar().evaluate();
        
        ((FileResultPrinter) pr).setFile(data10day);
        setMethod(MEAN);
        setPeriod(10);
        if (!printResults(files)) {
            LogHandler.getLogs().displayMsg("No data to generate this plot",
                    WARNING);
            return;
        }

        ConsoleProgressBar.getProgressBar().evaluate();
        
        CalidMeanDifferencePlot plot = new CalidMeanDifferencePlot(data1day,
                data5day, data10day);

        plot.setPairsName(params.getSource1(), params.getSource2());
        plot.setTimePeriod(params.getStartDate(), params.getEndDate());
        plot.setYmin(-20);
        plot.setYmax(20);
        plot.setOutput(output);
        
        ConsoleProgressBar.getProgressBar().evaluate();
        
        if(plot.plot());
        
        data1day.delete();
        data5day.delete();
        data10day.delete();
        
        ConsoleProgressBar.getProgressBar().printDoneMsg();
        
        LogHandler.getLogs().displayMsg("New plot generated: " + output, NORMAL);
        
    }
    

}
