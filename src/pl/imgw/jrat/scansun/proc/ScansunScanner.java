/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.ScanContainer;
import pl.imgw.jrat.data.rainbow.RainbowScanArray;
import pl.imgw.jrat.scansun.data.ScansunEvent;
import pl.imgw.jrat.scansun.data.ScansunEvent.ScansunEventAngleParameters;
import pl.imgw.jrat.scansun.data.ScansunEventType;
import pl.imgw.jrat.scansun.data.ScansunMeanPowerCalibrationMode;
import pl.imgw.jrat.scansun.data.ScansunParameters;
import pl.imgw.jrat.scansun.data.ScansunRadarParameters;
import pl.imgw.jrat.scansun.data.ScansunPulseDuration;
import pl.imgw.jrat.scansun.data.ScansunScanResult;
import pl.imgw.jrat.scansun.data.ScansunSite;
import pl.imgw.jrat.scansun.spa.ScansunSolarPositionCalculator;
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
public class ScansunScanner {

	private static Log log = LogManager.getLogger();

	public static final double ELEVATION_MIN = 1.0;

	public static ScansunScanResult scan(PolarData volume,
			ScansunParameters params,
			Map<ScansunSite, ScansunRadarParameters> radarParams) {
		ScansunScanResult result = new ScansunScanResult();

		ScansunSite site = ScansunSite.forName(volume.getSiteName());
		DateTime volumeDateTime = new DateTime(volume.getTimeSec());
		DateTimeFormatter fmt = ScansunUtils
				.forPattern(SCANSUN_DATETIME_PATTERN);

		log.printMsg("SCANSUN: Scanning volume: site=" + site.getSiteName()
				+ " date=" + fmt.print(volumeDateTime), Log.TYPE_NORMAL,
				Log.MODE_VERBOSE);

		for (ScanContainer scan : volume.getAllScans()) {

			if (scan.getElevation() < ELEVATION_MIN)
				continue;

			int rays = scan.getNRays();
			int bins = scan.getNBins();

			int binMin = calculateBinMin(scan.getRScale(), scan.getElevation(),
					params.getHeightMin(), params.getRangeMin());

			if (binMin > bins)
				continue;

			for (int rayNumber = 0; rayNumber < rays; rayNumber++) {

				double rayAzimuth = ((RainbowScanArray) scan.getArray())
						.getAzimuth(rayNumber);

				int deltaSeconds = (int) Math.floor(rayNumber
						/ (scan.getRPM() * 60.0));
				DateTime rayDateTime = (new DateTime(scan.getStartTime()))
						.secondOfMinute().addToCopy(deltaSeconds);

				ScansunSolarPositionCalculator solarPositionCalculator = new ScansunSolarPositionCalculator(
						scan.getCoordinates().x, scan.getCoordinates().y,
						volume.getHeight());
				double sunElevation = solarPositionCalculator
						.calculateSunElevation(rayDateTime);
				double sunAzimuth = solarPositionCalculator
						.calculateSunAzimuth(rayDateTime);

				double elevationDifference = scan.getElevation() - sunElevation;
				double azimuthDifference = rayAzimuth - sunAzimuth;

				if (!areAntennaAndSunSligned(elevationDifference,
						azimuthDifference, params.getAngleDifference()))
					continue;

				ScansunPulseDuration pulseDuration = scan.getRScale() < 750.0 ? ScansunPulseDuration.SHORT
						: ScansunPulseDuration.LONG;

				ScansunEventAngleParameters eventAngleParams = new ScansunEventAngleParameters(
						scan.getElevation(), rayAzimuth, sunElevation,
						sunAzimuth);

				ScansunEventType eventType = ScansunEventType.NON_SOLAR;
				ScansunMeanPowerCalibrationMode meanPowerCalibrationMode = radarParams
						.get(site).meanPowerCalibrationMode();
				double meanPower = 0.0;

				if (isReflectivityIncreasing(scan, rayNumber, binMin)
						&& isEchoNarrow(scan, rayNumber, binMin)) {

					double[] powerByBin = calculatePowerByBin(scan, rayNumber,
							binMin, radarParams.get(site));

					meanPower = calculateMeanSolarPower(powerByBin);

					int binCount = calculateBinCount(scan, rayNumber, binMin,
							meanPower, powerByBin,
							params.getMeanPowerWidthFactor());

					StringBuilder msg = new StringBuilder();
					if (isBinCountAboveThreshold(bins, binMin, binCount,
							params.getThresholdFraction())) {
						msg.append("Solar ray found: ");
						eventType = ScansunEventType.SOLAR_RAY;
					} else {
						msg.append("Event found: ");
						eventType = ScansunEventType.NON_SOLAR;
						meanPower = 0.0;
					}

					msg.append("date=" + fmt.print(rayDateTime));
					msg.append(" elevation = " + scan.getElevation()
							+ " (sun elevation=" + sunElevation + ")");
					msg.append(" azimuth = " + rayAzimuth + " (sun azimuth"
							+ sunAzimuth + ")");

					log.printMsg(msg.toString(), Log.TYPE_NORMAL,
							Log.MODE_VERBOSE);
				}

				ScansunEvent event = new ScansunEvent();
				event.setSite(site);
				event.setDateTime(rayDateTime);
				event.setEventType(eventType);
				event.setAngleParameters(eventAngleParams);
				event.setPulseDuration(pulseDuration);
				event.setMeanPowerCalibrationMode(meanPowerCalibrationMode);
				event.setMeanPower(meanPower);

				result.add(event);
			}
		}

		return result;
	}

	private static int calculateBinMin(double rScale, double elevation,
			double heightMin, double rangeMin) {
		double rangeStep = rScale / 1000.0;

		double range = ScansunCalculator.calculateRange(elevation, heightMin);

		int binMin = (int) Math.floor(range / rangeStep);

		if (rangeMin > binMin * rangeStep)
			binMin = (int) Math.floor(rangeMin / rangeStep);

		return binMin;
	}

	private static boolean areAntennaAndSunSligned(double deltaElevation,
			double deltaAzimuth, double angleDifference) {
		return Math.sqrt(deltaElevation * deltaElevation + deltaAzimuth
				* deltaAzimuth) < angleDifference;
	}

	private static boolean isReflectivityIncreasing(ScanContainer scan,
			int rayNumber, int binMin) {

		int bins = scan.getNBins();

		/*
		 * try { String filename = "test.dat"; FileWriter fout = new
		 * FileWriter(filename);
		 * 
		 * for (int i = binMin; i < scan.getNBins(); i++) fout.write(i + " " +
		 * scan.getArray().getPoint(i, rayNumber) + "\n"); fout.close(); } catch
		 * (IOException e) { System.out.println("ERROR!!!"); }
		 */
		int dBin = 20;

		if (scan.getArray().getPoint(binMin, rayNumber) > scan.getOffset()) {
			if (scan.getArray().getPoint(bins - dBin, rayNumber) > scan
					.getOffset()) {
				if (scan.getArray().getPoint(binMin, rayNumber) < scan
						.getArray().getPoint(bins - dBin, rayNumber))
					return true;
			}
		}

		return false;
	}

	public static boolean isEchoNarrow(ScanContainer scan, int rayNumber,
			int binMin) {

		int rays = scan.getNRays();
		int bins = scan.getNBins();

		int rayNb = 2;
		int rayNumberPrevious = (rayNumber - rayNb + rays) % rays;
		int rayNumberNext = (rayNumber + rayNb) % rays;

		for (int binNumber = binMin; binNumber < bins; binNumber++) {
			double dBZprevious = scan.getArray().getPoint(binNumber,
					rayNumberPrevious);
			double dBZ = scan.getArray().getPoint(binNumber, rayNumber);
			double dBZnext = scan.getArray().getPoint(binNumber, rayNumberNext);

			boolean b1 = Math.abs(dBZprevious - scan.getOffset()) > 0.1 ? true
					: false;
			boolean b2 = dBZ > scan.getOffset() ? true : false;
			boolean b3 = Math.abs(dBZnext - scan.getOffset()) > 0.1 ? true
					: false;

			if (b1 && b2 && b3)
				return false;
			/*
			 * if (dBZ > scan.getOffset() && Math.abs(dBZprevious -
			 * scan.getOffset()) > 0.1 && Math.abs(dBZnext - scan.getOffset()) >
			 * 0.1) return false;
			 */
		}

		return true;
	}

	private static double[] calculatePowerByBin(ScanContainer scan,
			int rayNumber, int binMin, ScansunRadarParameters radarParams) {

		int bins = scan.getNBins();

		double[] powers = new double[bins - binMin];

		double radarConstant, bandwidth;

		for (int i = 0; i < powers.length; i++) {
			int binNumber = i + binMin;
			double r = binNumber * (scan.getRScale() / 1000.0);
			double dBZ = scan.getArray().getPoint(binNumber, rayNumber);

			ScansunPulseDuration pd = scan.getRScale() < 750.0 ? ScansunPulseDuration.SHORT
					: ScansunPulseDuration.LONG;
			radarConstant = ScansunCalculator.calculateRadarConstant(
					radarParams, pd);
			bandwidth = radarParams.getBandwidth(pd);

			powers[i] = ScansunCalculator.calculatePower(dBZ, r, radarConstant,
					bandwidth);
		}

		return powers;
	}

	private static double calculateMeanSolarPower(double[] powers) {

		double sum = 0.0;
		for (int i = 0; i < powers.length; i++) {
			sum += powers[i];
		}

		return (sum / powers.length);
	}

	private static int calculateBinCount(ScanContainer scan, int rayNumber,
			int binMin, double meanPower, double[] powerByBin,
			double meanPowerWidthFactor) {

		int count = 0;

		for (int i = 0; i < powerByBin.length; i++) {
			int binNumber = i + binMin;
			double dBZ = scan.getArray().getPoint(binNumber, rayNumber);

			if (dBZ > scan.getOffset()) {
				if (Math.abs((powerByBin[i] - meanPower) / meanPower) < meanPowerWidthFactor)
					count++;
			}
		}

		return count;
	}

	private static boolean isBinCountAboveThreshold(int bins, int binMin,
			int binCount, double thresholdFraction) {
		return binCount > thresholdFraction * (bins - binMin);
	}

}