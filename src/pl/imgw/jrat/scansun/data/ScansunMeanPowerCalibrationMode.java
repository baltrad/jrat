package pl.imgw.jrat.scansun.data;

import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

public enum ScansunMeanPowerCalibrationMode {
	CALIBRATED, NOT_CALIBRATED;

	private static Log log = LogManager.getLogger();

	public String toString() {
		return name().toUpperCase();
	}

	public static String toStringHeader() {
		return "MeanPowerCalibrationMode";
	}

	public static ScansunMeanPowerCalibrationMode parseMeanPowerCalibrationMode(
			String word) {
		if (word.equalsIgnoreCase(NOT_CALIBRATED.name())) {
			return NOT_CALIBRATED;
		} else if (word.equalsIgnoreCase(CALIBRATED.name())) {
			return CALIBRATED;
		} else {
			log.printMsg(
					"SCANSUN: ScansunMeanPowerCalibrationMode.parseMeanPowerCalibrationMode() error: word is not a ScansunMeanPowerCalibrationMode",
					Log.TYPE_ERROR, Log.MODE_VERBOSE);
			return null;
		}
	}

}