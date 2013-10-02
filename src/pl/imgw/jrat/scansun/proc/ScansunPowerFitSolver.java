/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import pl.imgw.jrat.scansun.data.ScansunPowerFitSolution;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public abstract class ScansunPowerFitSolver {
	protected RealMatrix X;
	protected RealVector y;

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

		if (y == null) {
			y = new ArrayRealVector();
		}
		y = y.append(pi);

	}

	public abstract ScansunPowerFitSolution solve();

	public boolean hasDataPoints() {
		return y != null;
	}

}
