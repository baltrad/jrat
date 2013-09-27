/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import org.apache.commons.math3.special.Erf;

import pl.imgw.jrat.scansun.data.ScansunMeanPowerCalibrationMode;
import pl.imgw.jrat.scansun.data.ScansunPulseDuration;
import pl.imgw.jrat.scansun.data.ScansunRadarParameters;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunCalculator {
	public static final double RADIUS_43 = 8495.0; // km
	public static final double GASEOUS_ATTENUATION = 0.016;
	public static final double Z_0 = 8.4;
	public static final double DELTA_S = 0.57;
	public static final double LIGHT_SPEED = 3.0e8; // km/s

	public static double calculateRange(double elevation, double height) {
		double t1 = RADIUS_43 * Math.sin(Math.toRadians(elevation));

		// double t2 = Math.sqrt(t1 * t1 + 2.0 * ScansunConstants.RADIUS_43
		// * height + height * height);
		double t2 = Math.sqrt(t1 * t1 + 2.0 * RADIUS_43 * height);

		return (t2 - t1);
	}

	public static double calculateRadarConstant(
			ScansunRadarParameters radarParams, ScansunPulseDuration pd) {

		if (radarParams.meanPowerCalibrationMode() == ScansunMeanPowerCalibrationMode.NOT_CALIBRATED) {
			return 1.0;
		}

		double t1n = 2.025 * Math.pow(2.0, 14.0) * Math.log(2)
				* Math.pow(radarParams.getWavelength(), 2.0);
		double t1d = Math.pow(Math.PI, 5.0) * 1.0e-23 * LIGHT_SPEED
				* radarParams.getPower(pd) * radarParams.getBeamwidth()
				* radarParams.getBeamwidth() * radarParams.getPulselength(pd)
				* radarParams.getDielectricFactor();
		double t1 = 10.0 * Math.log10(t1n / t1d);

		double t2 = -2.0 * radarParams.getAntennaGain() + 2.0
				* radarParams.getRadomeloss() + radarParams.getTxloss()
				+ radarParams.getRxloss();

		return t1 + t2;
	}

	public static double calculatePower(double dBZ, double r,
			double radarConstant, double bandwidth) {

		double t1 = 20.0 * Math.log10(r);
		double t2 = 2.0 * GASEOUS_ATTENUATION * r;

		double t3 = 10.0 * Math.log10(bandwidth);

		return (dBZ - t1 - t2 - radarConstant - t3);
	}

	public static double calculateAgas(double sunElevationDeg) {
		double t1 = RADIUS_43 * Math.sin(Math.toRadians(sunElevationDeg));
		double t2 = Math.sqrt(t1 * t1 + 2.0 * Z_0 * RADIUS_43);

		return GASEOUS_ATTENUATION * (t2 - t1);
	}

	public static double calculatePs(double Pf, double sunElevationDeg) {
		return Pf + calculateAgas(sunElevationDeg);
	}

	public static double calculateL0(double deltaR, double deltaS) {
		double d = deltaR / deltaS;

		double t1 = d * d / Math.log(2);
		double t2 = Math.exp(-Math.log(2) / (d * d));

		return (t1 * (1.0 - t2));
	}

	public static double calculateLa(double L0, double deltaR, double deltaA) {
		double d = deltaR / deltaA;
		double t1 = Math.sqrt(Math.PI / Math.log(2));
		double t2 = Erf.erf(Math.sqrt(Math.log(2)) / d);

		return (0.5 * L0) * t1 * d * t2;
	}

	public static double toDecibelScale(double value, double base) {
		return (10.0 * Math.log10(value / base));
	}

	public static double toLinearScale(double valuedB, double base) {
		return base * Math.pow(10.0, valuedB / 10.0);
	}

}
