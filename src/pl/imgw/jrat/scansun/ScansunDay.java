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

    private int dayOfMonth;
    private int month; // 0-11
    private int year;

    private static final int DAY_OF_MONTH_DEFAULT = 1;
    private static final int MONTH_DEFAULT = 0;// 0-11
    private static final int YEAR_DEFAULT = 1970;

    public static final ScansunDay WILDCARD_DAY = new ScansunDay(DAY_OF_MONTH_DEFAULT, MONTH_DEFAULT, YEAR_DEFAULT);

    public ScansunDay(int dayOfMonth, int month, int year) {
	this.dayOfMonth = dayOfMonth;
	this.month = month;
	this.year = year;
    }

    public int getDayOfMonth() {
	return dayOfMonth;
    }

    public int getMonth() {
	return month;
    }

    public int getYear() {
	return year;
    }

    public int getDayOfYear() {
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

	if (this == obj) {
	    return true;
	}

	if ((obj == null) || (obj.getClass() != this.getClass())) {
	    return false;
	}

	if (dayOfMonth == WILDCARD_DAY.dayOfMonth && month == WILDCARD_DAY.month && year == WILDCARD_DAY.year) {
	    return true;
	}

	ScansunDay day = (ScansunDay) obj;

	if (day.dayOfMonth == WILDCARD_DAY.dayOfMonth && day.month == WILDCARD_DAY.month && day.year == WILDCARD_DAY.year) {
	    return true;
	}

	return (dayOfMonth == day.dayOfMonth || (new Integer(dayOfMonth) != null && dayOfMonth == day.dayOfMonth))
		&& (month == day.month || (new Integer(month) != null && month == day.month))
		&& (year == day.year || (new Integer(year) != null && year == day.year));
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 31 * hash + (new Integer(dayOfMonth) == null ? 0 : new Integer(dayOfMonth).hashCode());
	hash = 31 * hash + (new Integer(month) == null ? 0 : new Integer(month).hashCode());
	hash = 31 * hash + (new Integer(year) == null ? 0 : new Integer(year).hashCode());
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
