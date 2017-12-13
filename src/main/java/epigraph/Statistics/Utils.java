/**
 * 
 */
package epigraph.Statistics;

import java.util.Arrays;

/**
 * Several statistical methods taken from the Internet
 * 
 * @author Pablo Vicente-Munuera
 *
 */
final public class Utils {

	/**
	 * Taken from
	 * https://stackoverflow.com/questions/7988486/how-do-you-calculate-the-variance-median-and-standard-deviation-in-c-or-java
	 * Mr. White's answer
	 * 
	 * @param data
	 * @return
	 */
	public static double getMean(double[] data) {
		double sum = 0.0;
		for (double a : data)
			sum += a;
		return sum / data.length;
	}

	/**
	 * @param data
	 * @return
	 */
	public static double[] getMin(double[] data) {
		double minValue = Integer.MAX_VALUE;
		int position = -1;
		int actualPosition = 0;
		for (double a : data) {
			if (a < minValue) {
				minValue = a;
				position = actualPosition;
			}
			actualPosition++;
		}

		double[] minAndPosition = { minValue, position };

		return minAndPosition;
	}

	/**
	 * Taken from
	 * https://stackoverflow.com/questions/7988486/how-do-you-calculate-the-variance-median-and-standard-deviation-in-c-or-java
	 * Mr. White's answer
	 * 
	 * @param data
	 * @return
	 */
	public static double getVariance(double[] data) {
		double mean = getMean(data);
		double temp = 0;
		for (double a : data)
			temp += (a - mean) * (a - mean);
		return temp / (data.length - 1);
	}

	/**
	 * Taken from
	 * https://stackoverflow.com/questions/7988486/how-do-you-calculate-the-variance-median-and-standard-deviation-in-c-or-java
	 * Mr. White's answer
	 * 
	 * @param data
	 * @return
	 */
	public static double getStdDev(double[] data) {
		return Math.sqrt(getVariance(data));
	}

	/**
	 * Taken from
	 * https://stackoverflow.com/questions/7988486/how-do-you-calculate-the-variance-median-and-standard-deviation-in-c-or-java
	 * Mr. White's answer
	 * 
	 * @param data
	 * @return
	 */
	public static double median(double[] data) {
		Arrays.sort(data);

		if (data.length % 2 == 0) {
			return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
		}
		return data[data.length / 2];
	}

	/**
	 * The mean of a 2-dim array
	 * 
	 * @param data
	 * @return
	 */
	public static double[] getMean(double[][] data) {
		int noRows = data.length;
		int noCols = data[0].length;

		double[] mean = new double[noCols];
		for (int c = 0; c < noCols; c++) {
			mean[c] = 0;
			for (int r = 0; r < noRows; r++)
				mean[c] += data[r][c];

			mean[c] /= noRows;
		}

		return mean;
	}

	/**
	 * Return the covariance value of two values from a given mean
	 */
	public static double getCovariance(double x, double y, double mean) {
		return (x - mean) * (y - mean);
	}

	/**
	 * Return the covariance matrix of data. Each column of the given data
	 * represents a random variable
	 */
	static public double[][] getCovarianceMatrix(double[][] data) {

		double[] mean = getMean(data);

		int noRows = data.length;
		int noCols = data[0].length;

		final double[][] covarianceMatrix = new double[noCols][noCols];

		for (int r = 0; r < noCols; r++) {
			for (int c = r; c < noCols; c++) {

				double sum = 0;
				for (int i = 0; i < noRows; i++)
					sum += getCovariance(data[i][r], data[i][c], mean[c]);

				covarianceMatrix[r][c] = covarianceMatrix[c][r] = sum / (noRows - 1);
			}
		}

		return covarianceMatrix;
	}

	/**
	 * https://github.com/Navien2/K_Mean_Mahalanobis-Distance/blob/master/src/com/navien/kmeanmahalanobis/Utils.java
	 * Malanobis by
	 * 
	 * @param a
	 * @param b
	 * @param Sigma
	 * @return
	 * @throws Exception
	 */
	public static double mahalanobisDistance(double[] a, double[] b, double[][] Sigma) {

		double[][] Sigma_inverse = InvMat.Inverse(Sigma);

		double[][] diff = new double[1][a.length];

		for (int i = 0; i < a.length - 1; i++) {
			diff[0][i] = a[i] - b[i];
		}

		double result[][] = VectorLib.mulMatrix(diff, Sigma_inverse);

		result = VectorLib.mulMatrix(result, MatrixLib.Transpose(diff));
		return Math.sqrt(result[0][0]);
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static double[] getStdDev(double[][] data) {

		double[] stdDevs = new double[data.length];

		for (int numCol = 0; numCol < data.length; numCol++) {
			stdDevs[numCol] = getStdDev(data[numCol]);
		}

		return stdDevs;
	}

	/**
	 * Taken from http://www.java2novice.com/java-sorting-algorithms/bubble-sort/
	 * @param array
	 * @return
	 */
	public static double[][] bubbleSorting(double array[], double[] diagramsUsed) {
		int n = array.length;
		int k;
		for (int m = n; m >= 0; m--) {
			for (int i = 0; i < n - 1; i++) {
				k = i + 1;
				if (array[i] > array[k]) {
					swapNumbers(i, k, array);
					swapNumbers(i, k, diagramsUsed);
				}
			}

		}
		double[][] arrayAndPositions = {array, diagramsUsed};
		return arrayAndPositions;
	}

	/**
	 * 
	 * @param i
	 * @param j
	 * @param array
	 */
	private static void swapNumbers(int i, int j, double[] array) {
		double temp;
		temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

}