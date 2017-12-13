/**
 * 
 */
package epigraph.Statistics;

/**
 * From https://github.com/Navien2/K_Mean_Mahalanobis-Distance
 * 
 * @author nwayyin
 *
 */
public class MatrixLib {

	public static void set(double[][] mat, double value) {
		for (int j = 0; j < mat.length; j++) {
			VectorLib.set(mat[j], value);
		}
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @param genResultMat
	 */
	public static void addMat(double[][] a, double[][] b, boolean genResultMat) {
		int rows = a.length;
		int cols = a[0].length;
		double[][] result = a;
		if (genResultMat) {
			result = new double[rows][cols];
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result[i][j] = a[i][j] + b[i][j];
			}
		}
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[][] subtractMat(double[][] a, double[][] b) {
		int rows = a.length;
		int cols = a[0].length;
		double[][] result = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result[i][j] = a[i][j] - b[i][j];
			}
		}
		return result;
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	public static double[][] copyMatrix(double[][] a) {
		double[][] b = new double[a.length][];
		for (int i = 0; i < a.length; i++) {
			double[] aMatrix = a[i];
			int aLength = aMatrix.length;
			b[i] = new double[aLength];
			System.arraycopy(aMatrix, 0, b[i], 0, aLength);
		}
		return b;
	}

	/**
	 * 
	 * @param a
	 * @param v
	 * @param newMat
	 * @return
	 */
	public static double[][] mulScalar(double[][] a, double v, boolean newMat) {
		int rows = a.length;
		int cols = a[0].length;
		double[][] result = a;
		if (newMat) {
			result = new double[rows][cols];
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result[i][j] = a[i][j] * v;
			}
		}
		return result;
	}

	////////////////////////////////////////////////////////
	//////// transpose one dimention/////////////////////////
	/**
	 * 
	 * @param a
	 * @return
	 */
	public static double[][] Transpose1(double[] a) {

		int r = a.length;
		// int cols = a[0].length;

		double mT[][] = new double[r][1];

		for (int i = 0; i < r; i++)

		{
			mT[i][0] = a[i];
		}

		return mT;

	}

	///////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param a
	 * @return
	 */
	public static double[][] Transpose(double[][] a) {

		int rows = a.length;
		int cols = a[0].length;

		double m[][] = new double[cols][rows];

		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++) {
				m[i][j] = a[j][i];
			}

		return m;

	}
	/////////////////////////////////////////////////////////////////////

	public static double[] sum(double[][] mat, int dim) {
		int nrows = mat.length;
		int ncols = mat[0].length;

		double[] res = null;
		if (dim == 1) {
			res = new double[ncols];
			for (int i = 0; i < ncols; i++) {
				res[i] = 0;
				for (int j = 0; j < nrows; j++) {
					res[i] += mat[j][i];
				}
			}
		} else if (dim == 2) {
			res = new double[nrows];
			for (int i = 0; i < nrows; i++) {
				res[i] = 0;
				for (int j = 0; j < ncols; j++) {
					res[i] += mat[i][j];
				}
			}
		}
		return res;
	}

	///////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param mat
	 * @return
	 */
	static public double norm(double[][] mat) {
		double sum = 0.0;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				sum += mat[i][j];
			}
		}
		return Math.sqrt(sum);
	}

	////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param t
	 * @param src
	 */

	static public void assign(double[][] t, double[][] src) {
		for (int i = 0; i < src.length; i++) {
			for (int j = 0; j < src[0].length; j++) {
				t[i][j] = src[i][j];
			}
		}
	}
}
