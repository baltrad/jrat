/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import static pl.imgw.jrat.scansun.data.ScansunConstants.NO_VALUE;
import static pl.imgw.jrat.scansun.data.ScansunConstants.COMMENT;

import java.text.DecimalFormat;

import pl.imgw.jrat.scansun.proc.ScansunCalculator;

;
/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunPowerFitSolution {
	private double ax;
	private double ay;
	private double bx;
	private double by;
	private double c;

	private static final String AX = "ax";
	private static final String AY = "ay";
	private static final String BX = "bx";
	private static final String BY = "by";
	private static final String C = "c";

	public static final ScansunPowerFitSolution NO_SOLUTION = new ScansunPowerFitSolution();

	public ScansunPowerFitSolution() {
		this.ax = NO_VALUE;
		this.ay = NO_VALUE;
		this.bx = NO_VALUE;
		this.by = NO_VALUE;
		this.c = NO_VALUE;
	}

	public ScansunPowerFitSolution(double ax, double ay, double bx, double by,
			double c) {
		this.ax = ax;
		this.ay = ay;
		this.bx = bx;
		this.by = by;
		this.c = c;
	}

	public double getAx() {
		return ax;
	}

	public double getAy() {
		return ay;
	}

	public double getBx() {
		return bx;
	}

	public double getBy() {
		return by;
	}

	public double getC() {
		return c;
	}

	public double calculateP0dBm() {
		return this != NO_SOLUTION ? c - bx * bx / (4.0 * ax) - by * by
				/ (4.0 * ay) : NO_VALUE;
	}

	public double calculateSolarFluxDecibel(double La, double Ae) {

		if (this == NO_SOLUTION) {
			return NO_VALUE;
		}

		double P0dBm = calculateP0dBm();
		double P0 = ScansunCalculator.toLinearScale(P0dBm, 1.0);
		// TODO double P0 = 10.0 * Math.pow(10.0, P0dBm / 10.0);

		double sf = 2.0e13 * P0 / (La * Ae);

		return ScansunCalculator.toDecibelScale(sf, 1.0);
	}

	public static String toStringHeader(String delimiter) {
		StringBuilder header = new StringBuilder();

		header.append(AX + delimiter);
		header.append(AY + delimiter);
		header.append(BX + delimiter);
		header.append(BY + delimiter);
		header.append(C);

		return header.toString();
	}

	public String toString(String delimiter, DecimalFormat df) {
		StringBuilder result = new StringBuilder();

		result.append(df.format(ax) + delimiter);
		result.append(df.format(ay) + delimiter);
		result.append(df.format(bx) + delimiter);
		result.append(df.format(by) + delimiter);
		result.append(df.format(c));

		return result.toString();
	}

}
