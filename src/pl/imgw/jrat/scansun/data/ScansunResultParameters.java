/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import java.io.File;

import pl.imgw.jrat.scansun.proc.ScansunDataHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunResultParameters {

	// private static Log log = LogManager.getLogger();

	private ScansunSite site;
	private boolean allAvailableSites = true;
	private File resultFolder = new File(ScansunDataHandler.getScansunPath());

	public ScansunResultParameters() {

	}

	public ScansunResultParameters(ScansunSite site) {
		this.site = site;
	}

	public ScansunSite getSite() {
		return site;
	}

	public void setSite(ScansunSite site) {
		this.site = site;
	}

	public void setSite(String siteName) {
		this.allAvailableSites = false;
		this.site = ScansunSite.forName(siteName);
	}

	public boolean allAvailableSites() {
		return allAvailableSites;
	}

	public void setResultFolder(File resultFolder) {
		this.resultFolder = resultFolder;
	}

	public File getResultFolder() {
		return resultFolder;
	}

}
