/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import org.apache.commons.math3.linear.*;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 * 
 */
public class ScansunPowerFitSolver {

    private RealMatrix X;
    private RealVector y;

    public void addData(double pi, double xi, double yi) {

	double[] v = { xi * xi, yi * yi, xi, yi, 1.0 };

	if (X == null) {
	    X = new Array2DRowRealMatrix(1, 5);
	    X.setRow(0, v);
	} else {
	    double[][] XData = X.getData();
	    int rowDimension = X.getRowDimension();

	    X = new Array2DRowRealMatrix(rowDimension + 1, 5);
	    X.setSubMatrix(XData, 0, 0);
	    X.setRow(rowDimension, v);
	}

	if (y == null)
	    y = new ArrayRealVector();

	y = y.append(pi);

    }

    static class ScansunPowerFitSolution {

	private double ax;
	private double ay;
	private double bx;
	private double by;
	private double c;

	public static final ScansunPowerFitSolution NO_SOLUTION = new ScansunPowerFitSolution();

	ScansunPowerFitSolution() {
	    this.ax = ScansunConstants.NO_VALUE;
	    this.ay = ScansunConstants.NO_VALUE;
	    this.bx = ScansunConstants.NO_VALUE;
	    this.by = ScansunConstants.NO_VALUE;
	    this.c = ScansunConstants.NO_VALUE;
	}

	ScansunPowerFitSolution(double ax, double ay, double bx, double by, double c) {
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
	    return this != NO_SOLUTION ? c - bx * bx / (4.0 * ax) - by * by / (4.0 * ay) : ScansunConstants.NO_VALUE;
	}

	public double calculateSolarFlux(double La, double Ae) {

	    if (this == NO_SOLUTION) {
		return ScansunConstants.NO_VALUE;
	    }

	    double P0dBm = calculateP0dBm();

	    double P0 = 10.0 * Math.pow(10.0, P0dBm / 10.0);
	    double sf = 2.0e13 * P0 / (La * Ae);

	    return (10.0 * Math.log10(sf));
	}

    }

    public ScansunPowerFitSolution solve() {

	QRDecomposition d = new QRDecomposition(X);

	double c = d.getQT().operate(y).getEntry(4) / d.getR().getEntry(4, 4);

	double by = (d.getQT().operate(y).getEntry(3) - d.getR().getEntry(3, 4) * c) / d.getR().getEntry(3, 3);

	double bx = (d.getQT().operate(y).getEntry(2) - d.getR().getEntry(2, 3) * by - d.getR().getEntry(2, 4) * c) / d.getR().getEntry(2, 2);

	double ay = (d.getQT().operate(y).getEntry(1) - d.getR().getEntry(1, 2) * bx - d.getR().getEntry(1, 3) * by - d.getR().getEntry(1, 4) * c)
		/ d.getR().getEntry(1, 1);

	double ax = (d.getQT().operate(y).getEntry(0) - d.getR().getEntry(0, 1) * ay - d.getR().getEntry(0, 2) * bx - d.getR().getEntry(0, 3) * by - d.getR()
		.getEntry(0, 4) * c)
		/ d.getR().getEntry(1, 1);

	return new ScansunPowerFitSolution(ax, ay, bx, by, c);
    }

    public boolean hasDataPoints() {
	return y != null;
    }

}
