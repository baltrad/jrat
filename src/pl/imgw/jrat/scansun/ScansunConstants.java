/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 * 
 */

public class ScansunConstants {

    /* numbers */
    public static final double RADIUS_43 = 8495.0;
    public static final double GASEOUS_ATTENUATION = 0.016;
    public static final double Z_0 = 8.4;
    public static final double DELTA_S = 0.57;
    public static final double LIGHT_SPEED = 3.0e8;
    public static final double ELEVATION_THRESHOLD = 1.0;

    public static final double NO_VALUE = -9999;
    /* strings */
    public static final String SCANSUN_FOLDER = "scansun";
    public static final String SCANSUN_RESULTSFILE_BASENAME = "scansun_results";
    public static final String SCANSUN_RESULTSFILE_EXT = "results";
    public static final String SCANSUN_RESULTSFILE_DELIMITER = ";";
    public static final String SCANSUN_OPTFILE_NAME = "scansun.opt";

    public static final String SCANSUN_DRAO_SOLARFLUXFILE_BASENAME = "DRAO_10.7cm_solar_flux";
    public static final String SCANSUN_DRAO_SOLARFLUXFILE_EXT = "data";
    public static final String SCANSUN_DRAO_SOLARFLUXFILE_DELIMITER = ";";

    public static final String SCANSUN_DATE_SEPARATOR = "T";

    /* formats */
    public static final DecimalFormat SCANSUN_DECIMAL_FORMAT = new DecimalFormat("#.######");

    public static final SimpleDateFormat SCANSUN_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat SCANSUN_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'" + SCANSUN_DATE_SEPARATOR + "'HH:mm");
    public static final SimpleDateFormat SCANSUN_DATE_TIME_FORMAT_LONG = new SimpleDateFormat("yyyy-MM-dd'" + SCANSUN_DATE_SEPARATOR + "'HH:mm:ss");

    /* enums */

    public enum PulsePolarization {
	HORIZONTAL, VERTICAL;
    }

    public enum Sites {
	BRZUCHANIA(20.079720, 50.394170, 453.000000), GDANSK(18.456310, 54.384250, 158.000000), LEGIONOWO(20.960630, 52.405220, 119.000000), PASTEWNIK(
		16.039499, 50.891998, 688.000000), POZNAN(16.797060, 52.413260, 123.000000), RAMZA(18.726670, 50.151670, 358.000000), RZESZOW(22.002501,
		50.114170, 241.000000), SWIDWIN(15.831110, 53.790280, 146.000000);

	double longitude;
	double latitude;
	double altitude;

	private Sites(double longitude, double latitude, double altitude) {
	    this.longitude = longitude;
	    this.latitude = latitude;
	    this.altitude = altitude;
	}

	private static int findIndex(String siteName) {
	    for (Sites s : values()) {
		if (s.name().equalsIgnoreCase(siteName))
		    return s.ordinal();
	    }

	    return -1;
	}

	public static double getLongitude(String siteName) {
	    return values()[findIndex(siteName)].longitude;
	}

	public static double getLatitude(String siteName) {
	    return values()[findIndex(siteName)].latitude;
	}

	public static double getAltitude(String siteName) {
	    return values()[findIndex(siteName)].altitude;
	}

	public static Set<String> getSiteNames() {
	    Set<String> siteNames = new HashSet<String>();

	    for (Sites s : values()) {
		char[] nameArray = s.name().toLowerCase().toCharArray();
		nameArray[0] = Character.toUpperCase(nameArray[0]);
		siteNames.add(new String(nameArray));
	    }

	    return siteNames;
	}

	public static String toStringWithDelimiter(String siteName, String delimiter) {
	    String result = new String();

	    result += siteName + delimiter;
	    result += SCANSUN_DECIMAL_FORMAT.format(getLongitude(siteName)) + delimiter;
	    result += SCANSUN_DECIMAL_FORMAT.format(getLatitude(siteName)) + delimiter;
	    result += SCANSUN_DECIMAL_FORMAT.format(getAltitude(siteName)) + delimiter;

	    return result;
	}
    }

}