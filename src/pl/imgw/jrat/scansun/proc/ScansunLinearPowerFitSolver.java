/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.proc;

import org.apache.commons.math3.linear.QRDecomposition;

import pl.imgw.jrat.scansun.data.ScansunPowerFitSolution;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunLinearPowerFitSolver extends ScansunPowerFitSolver {

	@Override
	public ScansunPowerFitSolution solve() {
		QRDecomposition d = new QRDecomposition(X);

		double c = d.getQT().operate(y).getEntry(4) / d.getR().getEntry(4, 4);

		double by = (d.getQT().operate(y).getEntry(3) - d.getR().getEntry(3, 4)
				* c)
				/ d.getR().getEntry(3, 3);

		double bx = (d.getQT().operate(y).getEntry(2) - d.getR().getEntry(2, 3)
				* by - d.getR().getEntry(2, 4) * c)
				/ d.getR().getEntry(2, 2);

		double ay = (d.getQT().operate(y).getEntry(1) - d.getR().getEntry(1, 2)
				* bx - d.getR().getEntry(1, 3) * by - d.getR().getEntry(1, 4)
				* c)
				/ d.getR().getEntry(1, 1);

		double ax = (d.getQT().operate(y).getEntry(0) - d.getR().getEntry(0, 1)
				* ay - d.getR().getEntry(0, 2) * bx - d.getR().getEntry(0, 3)
				* by - d.getR().getEntry(0, 4) * c)
				/ d.getR().getEntry(1, 1);

		return new ScansunPowerFitSolution(ax, ay, bx, by, c);
	}

}
