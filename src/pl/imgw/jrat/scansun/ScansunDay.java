/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */

public class ScansunDay implements Comparable<ScansunDay> {

    private Integer dayOfMonth;
    private Integer month; // 0-11
    private Integer year;

    public ScansunDay(int dayOfMonth, int month, int y) {
	this.dayOfMonth = dayOfMonth;
	this.month = month;
	this.year = y;
    }

    public Integer getDayOfMonth() {
	return dayOfMonth;
    }

    public Integer getMonth() {
	return month;
    }

    public Integer getYear() {
	return year;
    }

    public Integer getDayOfYear() {
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	cal.set(Calendar.MONTH, month);
	cal.set(Calendar.YEAR, year);

	return cal.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public int compareTo(ScansunDay anotherDay) {

	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);

	cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	cal.set(Calendar.MONTH, month);
	cal.set(Calendar.YEAR, year);
	Date date = cal.getTime();

	cal.set(Calendar.DAY_OF_MONTH, anotherDay.getDayOfMonth());
	cal.set(Calendar.MONTH, anotherDay.getMonth());
	cal.set(Calendar.YEAR, anotherDay.getYear());
	Date anotherDate = cal.getTime();

	return date.compareTo(anotherDate);
    }

    @Override
    public final boolean equals(final Object obj) {

	if (this == obj)
	    return true;
	if ((obj == null) || (obj.getClass() != this.getClass()))
	    return false;

	ScansunDay days = (ScansunDay) obj;

	return (dayOfMonth == days.dayOfMonth || (dayOfMonth != null && dayOfMonth.equals(days.dayOfMonth)))
		&& (month == days.month || (month != null && month.equals(days.month)))
		&& (year == days.year || (year != null && year.equals(days.year)));
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 31 * hash + (dayOfMonth == null ? 0 : dayOfMonth.hashCode());
	hash = 31 * hash + (month == null ? 0 : month.hashCode());
	hash = 31 * hash + (year == null ? 0 : year.hashCode());
	return hash;
    }

    @Override
    public String toString() {

	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	cal.set(Calendar.MONTH, month);
	cal.set(Calendar.YEAR, year);

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	return sdf.format(cal.getTime());
    }

}
