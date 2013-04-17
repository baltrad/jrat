/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.*;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 * 
 */

public class ScansunPowerFit {

	private RealMatrix X;
	private RealVector y;

	private double ax;
	private double ay;
	private double bx;
	private double by;
	private double c;

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
		/*
		 * if (y == null) { y = new Array2DRowRealMatrix(1, 1); y.setEntry(0, 0,
		 * pi); } else { double[][] yData = y.getData(); int rowDimension =
		 * y.getRowDimension();
		 * 
		 * y = new Array2DRowRealMatrix(rowDimension + 1, 1);
		 * X.setSubMatrix(yData, 0, 0); y.setEntry(rowDimension, 0, pi); }
		 */

		if (y == null)
			y = new ArrayRealVector();

		y = y.append(pi);

	}

	public void calculate() {

		QRDecomposition d = new QRDecomposition(X);

		c = d.getQT().operate(y).getEntry(4) / d.getR().getEntry(4, 4);

		by = (d.getQT().operate(y).getEntry(3) - d.getR().getEntry(3, 4) * c)
				/ d.getR().getEntry(3, 3);

		bx = (d.getQT().operate(y).getEntry(2) - d.getR().getEntry(2, 3) * by - d
				.getR().getEntry(2, 4) * c)
				/ d.getR().getEntry(2, 2);

		ay = (d.getQT().operate(y).getEntry(1) - d.getR().getEntry(1, 2) * bx
				- d.getR().getEntry(1, 3) * by - d.getR().getEntry(1, 4) * c)
				/ d.getR().getEntry(1, 1);

		ax = (d.getQT().operate(y).getEntry(0) - d.getR().getEntry(0, 1) * ay
				- d.getR().getEntry(0, 2) * bx - d.getR().getEntry(0, 3) * by - d
				.getR().getEntry(0, 4) * c)
				/ d.getR().getEntry(1, 1);

		/*
		 * c = d.getQT().multiply((new Array2DRowRealMatrix(y.toArray())))
		 * .getEntry(4, 0) / d.getR().getEntry(4, 4);
		 * 
		 * by = (d.getQT().multiply((new Array2DRowRealMatrix(y.toArray())))
		 * .getEntry(3, 0) - d.getR().getEntry(3, 4) * c) / d.getR().getEntry(3,
		 * 3);
		 * 
		 * bx = (d.getQT().multiply((new Array2DRowRealMatrix(y.toArray())))
		 * .getEntry(2, 0) - d.getR().getEntry(2, 3) * by - d.getR().getEntry(2,
		 * 4) * c) / d.getR().getEntry(2, 2);
		 * 
		 * ay = (d.getQT().multiply((new Array2DRowRealMatrix(y.toArray())))
		 * .getEntry(1, 0) - d.getR().getEntry(1, 2) * bx - d.getR().getEntry(1,
		 * 3) * by - d .getR().getEntry(1, 4) * c) / d.getR().getEntry(1, 1);
		 * 
		 * ax = (d.getQT().multiply((new Array2DRowRealMatrix(y.toArray())))
		 * .getEntry(0, 0) - d.getR().getEntry(0, 1) ay - d.getR().getEntry(0,
		 * 2) bx - d.getR().getEntry(0, 3) * by - d.getR().getEntry(0, 4) * c) /
		 * d.getR().getEntry(0, 0);
		 */

		/*
		 * c = d.getQT().multiply(y).getEntry(4, 0) / d.getR().getEntry(4, 4);
		 * 
		 * by = (d.getQT().multiply(y).getEntry(3, 0) - d.getR().getEntry(3, 4)
		 * c) / d.getR().getEntry(3, 3);
		 * 
		 * bx = (d.getQT().multiply(y).getEntry(2, 0) - d.getR().getEntry(2, 3)
		 * by - d.getR().getEntry(2, 4) * c) / d.getR().getEntry(2, 2);
		 * 
		 * ay = (d.getQT().multiply(y).getEntry(1, 0) - d.getR().getEntry(1, 2)
		 * bx - d.getR().getEntry(1, 3) * by - d.getR().getEntry(1, 4) c) /
		 * d.getR().getEntry(1, 1);
		 * 
		 * ax = (d.getQT().multiply(y).getEntry(0, 0) - d.getR().getEntry(0, 1)
		 * ay - d.getR().getEntry(0, 2) * bx - d.getR().getEntry(0, 3) by -
		 * d.getR().getEntry(0, 4) * c) / d.getR().getEntry(0, 0);
		 */
	}

	public List<Double> toArray() {
		List<Double> coefficients = new ArrayList<Double>();
		coefficients.add(ax);
		coefficients.add(ay);
		coefficients.add(bx);
		coefficients.add(by);
		coefficients.add(c);

		return coefficients;
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

}
