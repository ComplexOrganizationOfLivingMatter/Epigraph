/**
 * 
 */
package epigraph.Statistics;

/**
 * From https://github.com/Navien2/K_Mean_Mahalanobis-Distance
 * 
 * @author nawin
 */

public class InvMat {

	public static double[][] Adjoint(double[][] m) {

		int n = m.length;

		double cof[][] = new double[n][n];

		int ii, jj, ia, ja;
		double det;

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				ia = 0;
				ja = 0;

				double reduceM[][] = new double[n - 1][n - 1];

				for (ii = 0; ii < n; ii++) {
					for (jj = 0; jj < n; jj++) {

						if ((ii != i) && (jj != j)) {
							reduceM[ia][ja] = m[ii][jj];
							ja++;
						}

					}
					if ((ii != i) && (jj != j)) {
						ia++;
					}
					ja = 0;
				}

				det = Det.deter(reduceM);

				cof[i][j] = (double) Math.pow(-1, i + j) * det;

			}

		m = Transpose(cof);

		return m;
	}

	// *****************************************************************************//
	public static double[][] Transpose(double[][] a) {

		int n = a.length;

		double m[][] = new double[n][n];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				m[i][j] = a[j][i];
			}

		return m;

	}
	// ***************************************************************************//

	public static double[][] Inverse(double[][] a) {
		// Formula used to Calculate Inverse:
		// inv(A) = 1/det(A) * adj(A)

		int n = a.length;

		double m[][] = new double[n][n];
		double mm[][] = Adjoint(a);

		double det = Det.deter(a);
		double dd = 0;

		if (det == 0) {
			System.out.println("Determinant Equals 0, Not Invertible.");

		} else {
			// System.out.println(" Deter value is = "+ det);
			dd = 1 / det;

		}

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				m[i][j] = dd * mm[i][j];
			}
		return m;
	}

	// ****************************************************************************//

	static void printMatrix(double[][] m) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < m.length; i++) {
			double[] row = m[i];
			sb.append("[");
			for (int j = 0; j < row.length; j++) {
				sb.append(" ");
				sb.append(row[j]);
			}
			sb.append(" ]\n");
		}
		sb.deleteCharAt(sb.length() - 1);

		System.out.println(sb.toString());
	}

}
