/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

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
public class ScansunSite {
	private static Log log = LogManager.getLogger();

	String siteName;
	double longitude;
	double latitude;
	double altitude;

	public ScansunSite(String siteName) {
		this.siteName = siteName;
	}

	public ScansunSite(String siteName, double longitude, double latitude,
			double altitude) {
		this.siteName = siteName;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}

	public String getSiteName() {
		return siteName;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getAltitude() {
		return this.altitude;
	}

	public String toString() {
		return toString(" ");
	}

	public String toString(String delimiter) {
		StringBuilder result = new StringBuilder();

		result.append(ScansunUtils.capitalize(siteName) + delimiter);
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

		ScansunSite site = new ScansunSite(siteName, longitude, latitude,
				altitude);

		return site;
	}

	@Override
	public final boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		ScansunSite site = (ScansunSite) obj;

		return ((this.siteName == site.getSiteName()) || (this.siteName != null && this.siteName
				.equals(site.getSiteName())));
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (siteName == null ? 0 : siteName.hashCode());
		return hash;
	}
}