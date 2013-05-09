/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.HashMap;
import java.util.Map;

import pl.imgw.jrat.scansun.ScansunEvent.PulseDuration;
import pl.imgw.jrat.scansun.ScansunPowerFitSolver.ScansunPowerFitSolution;

/**
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class RadarParsedParameters {
    private double wavelength;
    private double dielectricFactor;
    private double antennaGain;
    private double radomeloss;
    private double beamwidth;
    private Map<PulseDuration, Double> power;
    private Map<PulseDuration, Double> pulselength;
    private double txloss;
    private double rxloss;
    private Map<PulseDuration, Double> bandwidth;

    public static final RadarParsedParameters DEFAULT_PARAMETERS = createDefaultParameters();

    private static RadarParsedParameters createDefaultParameters() {
	RadarParsedParameters p = new RadarParsedParameters();

	HashMap<PulseDuration, Double> unitMap = new HashMap<PulseDuration, Double>();
	unitMap.put(PulseDuration.SHORT, 1.0);
	unitMap.put(PulseDuration.LONG, 1.0);

	p.setWavelength(1.0);
	p.setBeamwidth(1.0);
	p.setDielectricFactor(1.0);
	p.setAntennaGain(1.0);
	p.setPower(unitMap);
	p.setPulselength(unitMap);
	p.setRadomeloss(1.0);
	p.setTxloss(1.0);
	p.setRxloss(1.0);
	p.setBandwidth(unitMap);

	return p;
    }

    public RadarParsedParameters() {
    }

    public double getWavelength() {
	return wavelength;
    }

    public void setWavelength(double wavelength) {
	this.wavelength = wavelength;
    }

    public double getDielectricFactor() {
	return dielectricFactor;
    }

    public void setDielectricFactor(double dielectricFactor) {
	this.dielectricFactor = dielectricFactor;
    }

    public double getAntennaGain() {
	return antennaGain;
    }

    public void setAntennaGain(double antennaGain) {
	this.antennaGain = antennaGain;
    }

    public double getRadomeloss() {
	return radomeloss;
    }

    public void setRadomeloss(double radomeloss) {
	this.radomeloss = radomeloss;
    }

    public double getBeamwidth() {
	return beamwidth;
    }

    public void setBeamwidth(double beamwidth) {
	this.beamwidth = beamwidth;
    }

    public Map<PulseDuration, Double> getPower() {
	return power;
    }

    public void setPower(Map<PulseDuration, Double> power) {
	this.power = power;
    }

    public Map<PulseDuration, Double> getPulselength() {
	return pulselength;
    }

    public void setPulselength(Map<PulseDuration, Double> pulselength) {
	this.pulselength = pulselength;
    }

    public double getTxloss() {
	return txloss;
    }

    public void setTxloss(double txloss) {
	this.txloss = txloss;
    }

    public double getRxloss() {
	return rxloss;
    }

    public void setRxloss(double rxloss) {
	this.rxloss = rxloss;
    }

    public Map<PulseDuration, Double> getBandwidth() {
	return bandwidth;
    }

    public void setBandwidth(Map<PulseDuration, Double> bandwidth) {
	this.bandwidth = bandwidth;
    }

    public boolean areParametersProper() {
	return this != DEFAULT_PARAMETERS;
    }

    public double calculateLa() {
	double deltaR = Math.toRadians(beamwidth);

	double deltaS = Math.toRadians(ScansunConstants.DELTA_S);
	double L0 = ScansunEquations.calculateL0(deltaR, deltaS);

	double deltaA = Math.toRadians(1.0);
	return ScansunEquations.calculateLa(L0, deltaR, deltaA);
    }

    public double calculateAe() {
	double lambda = wavelength / 100.0;
	double GdBi = antennaGain;

	double G = Math.pow(10.0, GdBi / 10.0);
	return (lambda * lambda * G / (4.0 * Math.PI));
    }

}