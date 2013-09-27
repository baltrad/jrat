/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import java.util.HashSet;
import java.util.Set;

import pl.imgw.jrat.scansun.proc.ScansunUtils;
import static pl.imgw.jrat.scansun.data.ScansunConstants.SCANSUN_DECIMAL_FORMAT;

import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public enum ScansunSite {
	BRZUCHANIA(20.079720, 50.394170, 453.000000), GDANSK(18.456310, 54.384250,
			158.000000), LEGIONOWO(20.960630, 52.405220, 119.000000), PASTEWNIK(
			16.039499, 50.891998, 688.000000), POZNAN(16.797060, 52.413260,
			123.000000), RAMZA(18.726670, 50.151670, 358.000000), RZESZOW(
			22.002501, 50.114170, 241.000000), SWIDWIN(15.831110, 53.790280,
			146.000000);

	double longitude;
	double latitude;
	double altitude;

	private static Log log = LogManager.getLogger();

	private ScansunSite(double longitude, double latitude, double altitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}

	private static int findIndex(String siteName) {
		for (ScansunSite s : values()) {
			if (s.name().equalsIgnoreCase(siteName))
				return s.ordinal();
		}

		return -1;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public static double getLongitude(String siteName) {
		return values()[findIndex(siteName)].longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public static double getLatitude(String siteName) {
		return values()[findIndex(siteName)].latitude;
	}

	public double getAltitude() {
		return this.altitude;
	}

	public static double getAltitude(String siteName) {
		return values()[findIndex(siteName)].altitude;
	}

	public static ScansunSite forName(String siteName) {
		return values()[findIndex(siteName)];
	}

	public String getSiteName() {
		return ScansunUtils.capitalize(this.name());
	}

	public String toString() {
		return toString(" ");
	}

	public String toString(String delimiter) {
		StringBuilder result = new StringBuilder();

		result.append(ScansunUtils.capitalize(this.name()) + delimiter);
		result.append(SCANSUN_DECIMAL_FORMAT.format(getLongitude()) + delimiter);
		result.append(SCANSUN_DECIMAL_FORMAT.format(getLatitude()) + delimiter);
		result.append(SCANSUN_DECIMAL_FORMAT.format(getAltitude()));

		return result.toString();
	}

	public static String toStringHeader(String delimiter) {
		StringBuilder result = new StringBuilder();

		result.append("siteName" + delimiter);
		result.append("longitude" + delimiter);
		result.append("latitude" + delimiter);
		result.append("altitude");

		return result.toString();
	}

	public static Set<String> getSiteNames() {
		Set<String> siteNames = new HashSet<String>();

		for (ScansunSite site : values()) {
			siteNames.add(site.getSiteName());
		}

		return siteNames;
	}

	public static ScansunSite parseSite(String[] words) {

		if (words.length != toStringHeader(" ").split(" ").length) {
			log.printMsg(
					"SCANSUN: ScansunSite.parseValues() error: words array wrong length",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return null;
		}

		String siteName = words[0];
		double longitude = Double.parseDouble(words[1]);
		double latitude = Double.parseDouble(words[2]);
		double altitude = Double.parseDouble(words[3]);

		ScansunSite site = forName(siteName);

		if (longitude != site.getLongitude() || latitude != site.getLatitude()
				|| altitude != site.getAltitude()) {
			log.printMsg(
					"SCANSUN: ScansunSite.parseValues() error: site parameters do not match",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return null;
		}

		return site;
	}

}
