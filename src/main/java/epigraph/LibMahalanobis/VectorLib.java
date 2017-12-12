/**
 * 
 */
package epigraph.LibMahalanobis;

/**
 * From https://github.com/Navien2/K_Mean_Mahalanobis-Distance
 * 
 * @author nwayyin
 *
 */
public class VectorLib {

	public static void set(double[] vec, double value) {
		for (int j = 0; j < vec.length; j++) {
			vec[j] = value;
		}
	}

	public static double[][] square(double[] vec) {
		int n = vec.length;
		double[][] square = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				square[i][j] = vec[i] * vec[j];
			}
		}
		return square;
	}

	public static double[] addVector(double[] a, double b[], boolean newVec) {
		int len = a.length;

		double[] result = a;
		if (newVec) {
			result = new double[len];
		}
		for (int k = 0; k < len; k++) {
			result[k] = a[k] + b[k];
		}
		return result;
	}

	public static double[] mulScalar(double[] a, double value, boolean genNewVector) {
		int len = a.length;

		double[] result = a;
		if (genNewVector) {
			result = new double[len];
		}
		for (int k = 0; k < len; k++) {
			result[k] = a[k] * value;
		}
		return result;
	}

	public static double[] pow(double[] a, double power) {
		int nrows = a.length;

		double[] c = new double[nrows];
		for (int k = 0; k < nrows; k++) {
			c[k] = Math.pow(a[k], power);
		}
		return c;
	}

	public static double[] subtract(double[] a, double mean[]) {
		int len = a.length;

		double[] c = new double[len];
		for (int k = 0; k < len; k++) {
			c[k] = a[k] - mean[k];
		}
		return c;
	}

	/*
	 * public static double [] exp(double[] a) { int nrows = a.length;
	 * 
	 * double[] c = new double[nrows]; for (int k=0;k<nrows;k++) { c[k] =
	 * Math.exp(a[k]); } return c; }
	 */

	public static double[] mulMatrix(double[] x, double[][] mat) {
		int len = x.length;
		int nrows = mat.length;
		// int ncols = mat[0].length;
		double[] c = new double[len];

		for (int i = 0; i < len; i++) {
			c[i] = 0;
			for (int j = 0; j < nrows; j++) {
				c[i] += x[j] * mat[j][i];
			}
		}
		return c;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////// multi vector [][] *
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// []/////////////////////////////////////////////////////////////////////////////////
	// public static double mulVector(double[] x, double[][] vec) {
	// int len = x.length;
	// //int nrows = vec.length;
	// //int ncols = mat[0].length;
	// double c=0;
	//
	// for(int j=0; j<len; j++) {
	// for ( int i=0;i<len;i++){
	// c += x[j] * vec[j][i];
	// }
	// }
	// return c;
	// }
	//

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static double mulVector(double[] x, double[] vec) {
		int len = x.length;
		// int nrows = vec.length;
		// int ncols = mat[0].length;
		double c = 0;

		for (int j = 0; j < len; j++) {
			c += x[j] * vec[j];
		}
		return c;
	}

	static public Object[] max(double[] vec) {
		double[] max = { vec[0] };
		int[] idx = { 0 };
		for (int j = 0; j < vec.length; j++) {
			if (max[0] < vec[j]) {
				max[0] = vec[j];
				idx[0] = j;
			}
		}
		Object[] out = { max, idx };
		return out;
	}

	static public Object[] min(double[] vec) {
		double[] min = { vec[0] };
		int[] idx = { 0 };
		for (int j = 0; j < vec.length; j++) {
			if (min[0] > vec[j]) {
				min[0] = vec[j];
				idx[0] = j;
			}
		}
		Object[] out = { min, idx };
		return out;
	}

	static public double norm(double[] vec) {
		double sum = 0.0;
		for (int j = 0; j < vec.length; j++) {
			sum += vec[j] * vec[j];
		}
		return Math.sqrt(sum);
	}

	static public void assign(double[] t, double[] src) {
		for (int i = 0; i < src.length; i++) {
			t[i] = src[i];
		}
	}

	static double mulVector(double[] tmp, double[][] D_minus_Cnt2) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	// static double mulVector(double[] tmp, double[][] D_minus_Cnt2) {
	// throw new UnsupportedOperationException("Not supported yet."); //To
	// change body of generated methods, choose Tools | Templates.
	// }

	public static double[][] mulMatrix(double[][] diff, double[][] Sigma_inverse) {

		int aRows = diff.length;
		int aColumns = diff[0].length;
		int bRows = Sigma_inverse.length;
		int bColumns = Sigma_inverse[0].length;

		if (aColumns != bRows) {
			throw new IllegalArgumentException(
					"diff:Rows: " + aColumns + " did not match Sigma_inverse:Columns " + bRows + ".");
		}

		double[][] c = new double[aRows][bColumns];

		for (int i = 0; i < aRows; i++) { // aRow
			for (int j = 0; j < bColumns; j++) { // bColumn
				for (int k = 0; k < aColumns; k++) { // aColumn
					c[i][j] += diff[i][k] * Sigma_inverse[k][j];
				}
			}
		}

		return c;

	}
}
