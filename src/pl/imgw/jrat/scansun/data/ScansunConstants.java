/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import java.text.DecimalFormat;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunConstants {

	public static final double DELTA_S = 0.57;

	public static final double NO_VALUE = -9999;
	public static final String COMMENT = "#";

	/* formats */
	public static final DecimalFormat SCANSUN_DECIMAL_FORMAT = new DecimalFormat(
			"#.######");

	public static final String SCANSUN_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm'Z'";
	public static final String SCANSUN_DATE_PATTERN = "yyyy-MM-dd";

	/* enums */

	public enum PulsePolarization {
		HORIZONTAL, VERTICAL;
	}

}
