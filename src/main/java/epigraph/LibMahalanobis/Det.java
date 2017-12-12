/**
 * 
 */
package epigraph.LibMahalanobis;

/**
 * From https://github.com/Navien2/K_Mean_Mahalanobis-Distance
 * 
 * @author nwayyin
 */
public class Det {

	public static double deter(double[][] m) {
		int n = m.length;
		if (n == 1) {
			return m[0][0];
		} else {
			double det = 0;
			for (int j = 0; j < n; j++) {
				det += Math.pow(-1, j) * m[0][j] * deter(minor(m, 0, j));
			}
			return det;
		}
	}

	/**
	 * Computing the minor of the matrix m without the i-th row and the j-th
	 * column
	 * 
	 * @param m
	 *            input matrix
	 * @param i
	 *            removing the i-th row of m
	 * @param j
	 *            removing the j-th column of m
	 * @return minor of m
	 */
	private static double[][] minor(final double[][] m, final int i, final int j) {
		int n = m.length;
		double[][] minor = new double[n - 1][n - 1];
		// index for minor matrix position:
		int r = 0, s = 0;
		for (int k = 0; k < n; k++) {
			double[] row = m[k];
			if (k != i) {
				for (int l = 0; l < row.length; l++) {
					if (l != j) {
						minor[r][s++] = row[l];
					}
				}
				r++;
				s = 0;
			}
		}
		return minor;
	}

}
