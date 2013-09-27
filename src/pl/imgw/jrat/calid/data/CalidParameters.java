/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidParameters {

	private static final Integer DEFAULT_DIS = new Integer(1000);
	private static final Double DEFAULT_REF = new Double(0.0);
	private static final Double DEFAULT_ELE = new Double(0.0);
	private static final Integer DEFAULT_FREQ = new Integer(1);
	private static final Integer DEFAULT_RANGE = new Integer(200);

	private static final Date DEFAULT_STARTING_DATE;
	private static final Date DEFAULT_ENDING_DATE;

	static {

		Calendar cal = Calendar.getInstance();
		DEFAULT_ENDING_DATE = new Date(cal.getTimeInMillis());
		cal.add(Calendar.MONTH, -3);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		DEFAULT_STARTING_DATE = new Date(cal.getTimeInMillis());

	}

	private Double elevation = null;
	private Integer distance = null;
	private Integer range = null;
	private Double reflectivity = null;
	private Date startRangeDate = null;
	private Date endRangeDate = null;
	private Integer freq = null;

	/**
	 * Empty constructor
	 */
	public CalidParameters() {
	}

	/**
	 * Construct the object with all given parameters
	 * 
	 * @param elevation
	 * @param distance
	 * @param range
	 * @param reflectivity
	 */
	public CalidParameters(Double elevation, Integer distance, Integer range,
			Double reflectivity) {
		this.elevation = elevation;
		this.distance = distance;
		this.range = range;
		this.reflectivity = reflectivity;
	}

	/**
	 * @return the elevation
	 */
	public Double getElevation() {
		return (elevation == null) ? DEFAULT_ELE : elevation;
	}

	public boolean isElevationDefault() {
		return (elevation == null) ? true : false;
	}

	/**
	 * @return the distance
	 */
	public Integer getDistance() {
		return (distance == null) ? DEFAULT_DIS : distance;
	}

	public boolean isDistanceDefault() {
		return (distance == null) ? true : false;
	}

	/**
	 * @return the frequency
	 */
	public Integer getFrequency() {
		return (freq == null) ? DEFAULT_FREQ : freq;
	}

	public boolean isFrequencyDefault() {
		return (freq == null) ? true : false;
	}

	/**
	 * @return the reflectivity
	 */
	public Double getReflectivity() {
		return (reflectivity == null) ? DEFAULT_REF : reflectivity;
	}

	public boolean isReflectivityDefault() {
		return (reflectivity == null) ? true : false;
	}

	/**
	 * @return the date1
	 */
	public Date getStartRangeDate() {
		return (startRangeDate == null) ? DEFAULT_STARTING_DATE
				: startRangeDate;
	}

	/**
	 * @return the date2
	 */
	public Date getEndRangeDate() {
		return (endRangeDate == null) ? DEFAULT_ENDING_DATE : endRangeDate;
	}

	/**
	 * @return
	 */
	public boolean isStartDateDefault() {
		return startRangeDate == null;
	}

	/**
	 * @return
	 */
	public boolean isEndDateDefault() {
		return endRangeDate == null;
	}

	/**
	 * @return
	 */
	public Integer getMaxRange() {
		return (range == null) ? DEFAULT_RANGE : range;
	}

	/**
	 * @return
	 */
	public boolean isMaxRangeDefault() {
		return range == null;
	}

	/**
	 * @param elevation
	 *            the elevation to set
	 */
	protected void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	protected void setDistance(Integer distance) {
		this.distance = distance;
	}

	/**
	 * @param range
	 *            the maximum range to set
	 */
	protected void setMaxRange(Integer range) {
		this.range = range;
	}

	/**
	 * @param reflectivity
	 *            the reflectivity to set
	 */
	protected void setReflectivity(Double reflectivity) {
		this.reflectivity = reflectivity;
	}

	protected void setFreq(int freq) {
		this.freq = freq;
	}

	public void setRangeDates(Date startRangeDate, Date endRangeDate) {
		this.startRangeDate = startRangeDate;
		this.endRangeDate = endRangeDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd/HH:ss");
		StringBuffer msg = new StringBuffer();
		msg.append("\televation=" + getElevation());
		if (isElevationDefault())
			msg.append(" (default)");
		msg.append("\n\tdistance=" + getDistance());
		if (isDistanceDefault())
			msg.append(" (default)");
		msg.append("\n\trange=" + getMaxRange());
		if (isMaxRangeDefault())
			msg.append(" (default)");
		msg.append("\n\treflectivity=" + getReflectivity());
		if (isReflectivityDefault())
			msg.append(" (default)");
		msg.append("\n\tfrequency=" + getFrequency());
		if (isFrequencyDefault())
			msg.append(" (default)");
		msg.append("\n\tstart date=" + format.format(getStartRangeDate()));
		msg.append(", end date=" + format.format(getEndRangeDate()));
		return msg.toString();
	}

}
