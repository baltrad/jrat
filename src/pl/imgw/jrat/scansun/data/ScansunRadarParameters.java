/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import java.util.HashMap;
import java.util.Map;

import pl.imgw.jrat.scansun.proc.ScansunCalculator;

import static pl.imgw.jrat.scansun.data.ScansunMeanPowerCalibrationMode.CALIBRATED;
import static pl.imgw.jrat.scansun.data.ScansunMeanPowerCalibrationMode.NOT_CALIBRATED;
import static pl.imgw.jrat.scansun.data.ScansunConstants.DELTA_S;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunRadarParameters {

	private Double wavelength;
	private static final Double DEFAULT_WAVELENGTH = new Double(5.3);
	private boolean isWavelengthDefault = true;

	private Double beamwidth;
	private static final Double DEFAULT_BEAMWIDTH = new Double(1.0);
	private boolean isBeamwidthDefault = true;

	private Double dielectricFactor;
	private static final Double DEFAULT_DIELECTRIC_FACTOR = new Double(0.93);
	private boolean isDielectricFactorDefault = true;

	private Double antennaGain;
	private static final Double DEFAULT_ANTENNA_GAIN = new Double(44.0);
	private boolean isAntennaGainDefault = true;

	private Double radomeloss;
	private static final Double DEFAULT_RADOMELOSS = new Double(0.2);
	private boolean isRadomelossDefault = true;

	private Double txloss;
	private static final Double DEFAULT_TXLOSS = new Double(1.0);
	private boolean isTxlossDefault = true;

	private Double rxloss;
	private static final Double DEFAULT_RXLOSS = new Double(2.0);
	private boolean isRxlossDefault = true;

	private Map<ScansunPulseDuration, Double> power;
	private static final Map<ScansunPulseDuration, Double> DEFAULT_POWER = new HashMap<ScansunPulseDuration, Double>();
	static {
		DEFAULT_POWER.put(ScansunPulseDuration.SHORT, 250.0);
		DEFAULT_POWER.put(ScansunPulseDuration.LONG, 250.0);
	}
	private boolean isPowerDefault = true;

	private Map<ScansunPulseDuration, Double> pulselength;
	private static final Map<ScansunPulseDuration, Double> DEFAULT_PULSELENGTH = new HashMap<ScansunPulseDuration, Double>();
	static {
		DEFAULT_PULSELENGTH.put(ScansunPulseDuration.SHORT, 1.0);
		DEFAULT_PULSELENGTH.put(ScansunPulseDuration.LONG, 2.0);
	}
	private boolean isPulselengthDefault = true;

	private Map<ScansunPulseDuration, Double> bandwidth;
	private static final Map<ScansunPulseDuration, Double> DEFAULT_BANDWIDTH = new HashMap<ScansunPulseDuration, Double>();
	static {
		DEFAULT_BANDWIDTH.put(ScansunPulseDuration.SHORT, 1.5);
		DEFAULT_BANDWIDTH.put(ScansunPulseDuration.LONG, 0.6);
	}
	private boolean isBandwidthDefault = true;

	// private ScansunMeanPowerCalibrationMode meanPowerCalibrationMode =
	// NOT_CALIBRATED;

	public void setWavelength(Double wavelength) {
		this.wavelength = wavelength;
		this.isWavelengthDefault = false;
	}

	public Double getWavelength() {
		return (wavelength == null) ? DEFAULT_WAVELENGTH : wavelength;
	}

	public void setBeamwidth(Double beamwidth) {
		this.isBeamwidthDefault = false;
		this.beamwidth = beamwidth;
	}

	public Double getBeamwidth() {
		return (beamwidth == null) ? DEFAULT_BEAMWIDTH : beamwidth;
	}

	public void setDielectricFactor(Double dielectricFactor) {
		this.isDielectricFactorDefault = false;
		this.dielectricFactor = dielectricFactor;
	}

	public Double getDielectricFactor() {
		return (dielectricFactor == null) ? DEFAULT_DIELECTRIC_FACTOR
				: dielectricFactor;
	}

	public void setAntennaGain(Double antennaGain) {
		this.isAntennaGainDefault = false;
		this.antennaGain = antennaGain;
	}

	public Double getAntennaGain() {
		return (antennaGain == null) ? DEFAULT_ANTENNA_GAIN : antennaGain;
	}

	public void setRadomeloss(Double radomeloss) {
		this.isRadomelossDefault = false;
		this.radomeloss = radomeloss;
	}

	public Double getRadomeloss() {
		return (radomeloss == null) ? DEFAULT_RADOMELOSS : radomeloss;
	}

	public void setTxloss(Double txloss) {
		this.isTxlossDefault = false;
		this.txloss = txloss;
	}

	public Double getTxloss() {
		return (txloss == null) ? DEFAULT_TXLOSS : txloss;
	}

	public void setRxloss(Double rxloss) {
		this.isRxlossDefault = false;
		this.rxloss = rxloss;
	}

	public Double getRxloss() {
		return (rxloss == null) ? DEFAULT_RXLOSS : rxloss;
	}

	public void setPower(Map<ScansunPulseDuration, Double> power) {
		this.isPowerDefault = false;
		this.power = power;
	}

	public Double getPower(ScansunPulseDuration pulseDuration) {
		return (power == null) ? DEFAULT_POWER.get(pulseDuration) : power
				.get(pulseDuration);
	}

	public void setPulselength(Map<ScansunPulseDuration, Double> pulselength) {
		this.isPulselengthDefault = false;
		this.pulselength = pulselength;
	}

	public Double getPulselength(ScansunPulseDuration pulseDuration) {
		return (pulselength == null) ? DEFAULT_PULSELENGTH.get(pulseDuration)
				: pulselength.get(pulseDuration);
	}

	public void setBandwidth(Map<ScansunPulseDuration, Double> bandwidth) {
		this.isBandwidthDefault = false;
		this.bandwidth = bandwidth;
	}

	public Double getBandwidth(ScansunPulseDuration pd) {
		return (bandwidth == null) ? DEFAULT_BANDWIDTH.get(pd) : bandwidth
				.get(pd);
	}

	public ScansunMeanPowerCalibrationMode meanPowerCalibrationMode() {

		return (isWavelengthDefault && isBeamwidthDefault
				&& isDielectricFactorDefault && isAntennaGainDefault
				&& isRadomelossDefault && isTxlossDefault && isRxlossDefault
				&& isPowerDefault && isPulselengthDefault && isBandwidthDefault) ? NOT_CALIBRATED
				: CALIBRATED;

	}

	public double calculateLa() {
		double deltaR = Math.toRadians(beamwidth);
		double deltaS = Math.toRadians(DELTA_S);
		double L0 = ScansunCalculator.calculateL0(deltaR, deltaS);

		double deltaA = Math.toRadians(1.0);
		return ScansunCalculator.calculateLa(L0, deltaR, deltaA);
	}

	public double calculateAe() {
		double lambda = wavelength / 100.0;
		double GdBi = antennaGain;

		double G = Math.pow(10.0, GdBi / 10.0);
		return (lambda * lambda * G / (4.0 * Math.PI));
	}

}