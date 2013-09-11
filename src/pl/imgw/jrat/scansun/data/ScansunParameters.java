/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunParameters {

	private static final Double DEFAULT_HMIN = new Double(3.0);
	private static final Double DEFAULT_RMIN = new Double(50.0);
	private static final Double DEFAULT_TF = new Double(0.9);
	private static final Double DEFAULT_AD = new Double(2.0);
	private static final Double DEFAULT_MPWF = new Double(0.75);

	private Double heightMin;
	private Double rangeMin;
	private Double thresholdFraction;
	private Double angleDifference;
	private Double meanPowerWidthFactor;

	public ScansunParameters() {

	}

	public ScansunParameters(Double heightMin, Double rangeMin,
			Double thresholdFraction, Double angleDifference,
			Double meanPowerWidthFactor) {
		this.heightMin = heightMin;
		this.rangeMin = rangeMin;
		this.thresholdFraction = thresholdFraction;
		this.angleDifference = angleDifference;
		this.meanPowerWidthFactor = meanPowerWidthFactor;
	}

	public void setHeightMin(Double heightMin) {
		this.heightMin = heightMin;
	}

	public Double getHeightMin() {
		return (heightMin == null) ? DEFAULT_HMIN : heightMin;
	}

	public void setRangeMin(Double rangeMin) {
		this.rangeMin = rangeMin;
	}

	public Double getRangeMin() {
		return (rangeMin == null) ? DEFAULT_RMIN : rangeMin;
	}

	public void setThresholdFraction(Double thresholdFraction) {
		this.thresholdFraction = thresholdFraction;
	}

	public Double getThresholdFraction() {
		return (thresholdFraction == null) ? DEFAULT_TF : thresholdFraction;
	}

	public void setAngleDifference(Double angleDifference) {
		this.angleDifference = angleDifference;
	}

	public Double getAngleDifference() {
		return (angleDifference == null) ? DEFAULT_AD : angleDifference;
	}

	public void setMeanPowerWidthFactor(Double meanPowerWidthFactor) {
		this.meanPowerWidthFactor = meanPowerWidthFactor;
	}

	public Double getMeanPowerWidthFactor() {
		return (meanPowerWidthFactor == null) ? DEFAULT_MPWF
				: meanPowerWidthFactor;
	}

	public String toString() {
		StringBuffer msg = new StringBuffer();
		msg.append("heightMin=" + getHeightMin());
		msg.append(", rangeMin=" + getRangeMin());
		msg.append(", thresholdFraction=" + getThresholdFraction());
		msg.append(", angleDifference=" + getAngleDifference());
		msg.append(", meanPowerWidthFactor=" + getMeanPowerWidthFactor());
		return msg.toString();

	}
}
