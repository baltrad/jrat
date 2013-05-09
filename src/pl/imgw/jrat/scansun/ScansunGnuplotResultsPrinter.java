/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.HashSet;
import java.util.Set;

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
	    }
	}
    }

}