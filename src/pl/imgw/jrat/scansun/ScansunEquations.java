/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import org.apache.commons.math3.special.Erf;

import pl.imgw.jrat.scansun.ScansunEvent.PulseDuration;

import static pl.imgw.jrat.scansun.ScansunConstants.*;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 * 
 */
public class ScansunEquations {

    private ScansunEquations() {
    }

    public static double calculateRadarConstant(RadarParsedParameters p, PulseDuration pd) {

	if (!p.areParametersProper()) {
	    return 1.0;
	}

	double t1n = 2.025 * Math.pow(2.0, 14.0) * Math.log(2) * Math.pow(p.getWavelength(), 2.0);
	double t1d = Math.pow(Math.PI, 5.0) * 1.0e-23 * LIGHT_SPEED * p.getPower().get(pd) * p.getBeamwidth() * p.getBeamwidth() * p.getPulselength().get(pd)
		* p.getDielectricFactor();
	double t1 = 10.0 * Math.log10(t1n / t1d);

	double t2 = -2.0 * p.getAntennaGain() + 2.0 * p.getRadomeloss() + p.getTxloss() + p.getRxloss();

	return t1 + t2;
    }

    public static double calculateRange(double sunElevationDeg, double height) {
	double t1 = RADIUS_43 * Math.sin(Math.toRadians(sunElevationDeg));

	// double t2 = Math.sqrt(t1 * t1 + 2.0 * ScansunConstants.RADIUS_43
	// * height + height * height);
	double t2 = Math.sqrt(t1 * t1 + 2.0 * RADIUS_43 * height);

	return (t2 - t1);
    }

    public static double calculatePower(double dBZ, double r, double radarConstant, double bandwidth) {

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

}
