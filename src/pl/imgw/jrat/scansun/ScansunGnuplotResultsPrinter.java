/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import static pl.imgw.jrat.tools.out.Logging.NORMAL;

import java.util.HashSet;
import java.util.Set;

import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunGnuplotResultsPrinter extends ScansunResultsPrinter {

    public ScansunGnuplotResultsPrinter(ScansunResultsParsedParameters params) {
	super(params);
    }

    public void generatePlots() {

	Set<String> siteNames = new HashSet<>();

	if (params.allSites()) {
	    siteNames.addAll(resultsEventsContainer.getSiteNames());
	} else {
	    siteNames.add(params.getSiteName());
	}

	for (String siteName : siteNames) {
	    new ScansunGnuplotScatterplotPrinter(siteName, params).generatePlot();
	    new ScansunGnuplotDetectionHistogramPrinter(siteName, params).generatePlot();
	    // new ScansunGnuplotDetectionTimePlotPrinter(siteName,
	    // params).generatePlot();
	    // new ScansunGnuplotDetectionAzimuthPlotPrinter(siteName,
	    // params).generatePlot();
	    // new ScansunGnuplotFitCoefficientsPlotPrinter(siteName,
	    // params).generatePlot();
	    if (ScansunOptionsHandler.getOptions().getRadarParsedParameters(siteName).areParametersProper()) {
		new ScansunGnuplotSolarFluxPlotPrinter(siteName, params).generatePlot();
	    } else {
		LogHandler.getLogs().displayMsg("SCANSUN: skipping creation of solar flux plot for " + siteName, NORMAL);
	    }
	}
    }
}