/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import pl.imgw.jrat.tools.in.LineParseTool;
import pl.imgw.jrat.tools.in.LineParseable;
import pl.imgw.jrat.tools.in.LineParseableFactory;
import pl.imgw.jrat.scansun.data.ScansunSite;
import pl.imgw.jrat.scansun.proc.ScansunUtils;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;
import static pl.imgw.jrat.scansun.data.ScansunConstants.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunEvent implements LineParseable {

	private static Log log = LogManager.getLogger();

	private ScansunSite site;
	private DateTime dateTime;
	private ScansunEventType eventType;
	private ScansunEventAngleParameters angleParameters;
	private ScansunPulseDuration pulseDuration;
	private ScansunMeanPowerCalibrationMode meanPowerCalibrationMode;
	private double meanPower;

	public static final String EVENT_DELIMITER = ";";

	private static final String DATETIME_PATTERN = SCANSUN_DATETIME_PATTERN;
	private static final String EVENT_TYPE = ScansunEventType.toStringHeader();
	private static final String PULSE_DURATION = ScansunPulseDuration
			.toStringHeader();
	private static final String MEAN_POWER_CALIBRATION_MODE = "meanPowerCalibrationMode";
	private static final String MEAN_POWER = "meanPower";

	public void setSite(ScansunSite site) {
		this.site = site;
	}

	public ScansunSite getSite() {
		return site;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public LocalDate getLocalDate() {
		return dateTime.toLocalDate();
	}

	public void setEventType(ScansunEventType eventType) {
		this.eventType = eventType;
	}

	public ScansunEventType getEventType() {
		return eventType;
	}

	public void setAngleParameters(ScansunEventAngleParameters angleParameters) {
		this.angleParameters = angleParameters;
	}

	public double getRadarElevation() {
		return this.angleParameters.getRadarElevation();
	}

	public double getRadarAzimuth() {
		return this.angleParameters.getRadarAzimuth();
	}

	public double getSunElevation() {
		return this.angleParameters.getSunElevation();
	}

	public double getSunAzimuth() {
		return this.angleParameters.getSunAzimuth();
	}

	public double getElevationOffset() {
		return (this.angleParameters.getRadarElevation() - this.angleParameters
				.getSunElevation());
	}

	public double getAzimuthOffset() {
		return (this.angleParameters.getRadarAzimuth() - this.angleParameters
				.getSunAzimuth());
	}

	public void setPulseDuration(ScansunPulseDuration pulseDuration) {
		this.pulseDuration = pulseDuration;
	}

	public ScansunPulseDuration getPulseDuration() {
		return pulseDuration;
	}

	public void setMeanPowerCalibrationMode(
			ScansunMeanPowerCalibrationMode meanPowerCalibrationMode) {
		this.meanPowerCalibrationMode = meanPowerCalibrationMode;
	}

	public ScansunMeanPowerCalibrationMode meanPowerCalibrationMode() {
		return meanPowerCalibrationMode;
	}

	public void setMeanPower(double meanPower) {
		this.meanPower = meanPower;
	}

	public double getMeanPower() {
		return meanPower;
	}

	public static class ScansunEventAngleParameters {
		private double radarElevation;
		private double radarAzimuth;
		private double sunElevation;
		private double sunAzimuth;

		private static final String RADAR_ELEVATION = "radarElevation";
		private static final String RADAR_AZIMUTH = "radarAzimuth";
		private static final String SUN_ELEVAION = "sunElevation";
		private static final String SUN_AZIMUTH = "sunAzimuth";

		public ScansunEventAngleParameters(double radarElevation,
				double radarAzimuth, double sunElevation, double sunAzimuth) {
			this.radarElevation = radarElevation;
			this.radarAzimuth = radarAzimuth;
			this.sunElevation = sunElevation;
			this.sunAzimuth = sunAzimuth;
		}

		public double getRadarElevation() {
			return radarElevation;
		}

		public double getRadarAzimuth() {
			return radarAzimuth;
		}

		public double getSunElevation() {
			return sunElevation;
		}

		public double getSunAzimuth() {
			return sunAzimuth;
		}

		public static String toStringHeader() {
			return toStringHeader(";");
		}

		public static String toStringHeader(String delimiter) {
			StringBuilder header = new StringBuilder();

			header.append(RADAR_ELEVATION + delimiter);
			header.append(RADAR_AZIMUTH + delimiter);
			header.append(SUN_ELEVAION + delimiter);
			header.append(SUN_AZIMUTH);

			return header.toString();
		}

		@Override
		public String toString() {
			return toString(EVENT_DELIMITER);
		}

		public String toString(String delimiter) {
			StringBuilder str = new StringBuilder();

			str.append(radarElevation + delimiter);
			str.append(radarAzimuth + delimiter);
			str.append(sunElevation + delimiter);
			str.append(sunAzimuth);

			return str.toString();
		}

		public static ScansunEventAngleParameters parseEventAngleParameters(
				String[] words) {

			String[] header = toStringHeader(";").split(";");

			if (words.length != header.length) {
				log.printMsg(
						"SCANSUN: parseEventAngleParameters() error: words array wrong length",
						Log.TYPE_ERROR, Log.MODE_VERBOSE);
				return null;
			}

			double radarElevation = Double.parseDouble(words[0]);
			double radarAzimuth = Double.parseDouble(words[1]);
			double sunElevation = Double.parseDouble(words[2]);
			double sunAzimuth = Double.parseDouble(words[3]);

			return new ScansunEventAngleParameters(radarElevation,
					radarAzimuth, sunElevation, sunAzimuth);
		}

	}

	public String toStringHeader() {
		return lineHeader(EVENT_DELIMITER);
	}

	public String toStringHeader(String delimiter) {
		return lineHeader(delimiter);
	}

	@Override
	public String lineHeader(String delimiter) {
		StringBuilder header = new StringBuilder();

		header.append(ScansunSite.toStringHeader(delimiter) + delimiter);
		header.append(DATETIME_PATTERN + delimiter);
		header.append(EVENT_TYPE + delimiter);
		header.append(ScansunEventAngleParameters.toStringHeader(delimiter)
				+ delimiter);
		header.append(PULSE_DURATION + delimiter);
		header.append(MEAN_POWER_CALIBRATION_MODE + delimiter);
		header.append(MEAN_POWER);

		return header.toString();
	}

	@Override
	public String toString() {
		return toString(EVENT_DELIMITER);
	}

	public String toString(String delimiter) {
		StringBuilder eventString = new StringBuilder();

		eventString.append(site.toString(delimiter) + delimiter);
		eventString.append(ScansunUtils.forPattern(DATETIME_PATTERN).print(
				dateTime)
				+ delimiter);
		eventString.append(eventType + delimiter);
		eventString.append(angleParameters.toString(delimiter) + delimiter);
		eventString.append(pulseDuration + delimiter);
		eventString.append(meanPowerCalibrationMode + delimiter);
		eventString.append(meanPower);

		return eventString.toString();
	}

	public static ScansunEvent parseEvent(String line, String delimiter) {

		return LineParseTool.parseLine(line, new ScansunEventFacory(),
				delimiter);
	}

	@Override
	public void parseLine(String[] words) {

		this.setSite(ScansunSite.parseSite(Arrays.copyOfRange(words, 0, 4)));

		String[] header = toStringHeader(EVENT_DELIMITER)
				.split(EVENT_DELIMITER);
		DateTimeFormatter fmt = ScansunUtils.forPattern(header[4]);
		this.setDateTime(fmt.parseDateTime(words[4]));

		this.setEventType(ScansunEventType.parseEventType(words[5]));
		this.setAngleParameters(ScansunEventAngleParameters
				.parseEventAngleParameters(Arrays.copyOfRange(words, 6, 10)));
		this.setPulseDuration(ScansunPulseDuration
				.parsePulseDuration(words[10]));
		this.meanPowerCalibrationMode = ScansunMeanPowerCalibrationMode
				.parseMeanPowerCalibrationMode(words[11]);
		this.meanPower = Double.parseDouble(words[12]);
	}

	public static class ScansunEventFacory implements
			LineParseableFactory<ScansunEvent> {

		@Override
		public ScansunEvent create() {
			return new ScansunEvent();
		}

	}
}