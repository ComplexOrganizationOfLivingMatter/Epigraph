/**
 * 
 */
package epigraph;

import java.util.ArrayList;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

import epigraph.Statistics.Utils;

/**
 * 
 * Here we want to compare an image with a CVTn diagram. If the image is an
 * outlier of the diagramas, it will be outside of the CVTn, while inlier
 * images, would be within the CVTn.
 * 
 * @author Pablo Vicente-Munuera, Pedro Gomez-Galvez
 *
 */
public final class StatisticalComparison {

	static int MAX_DIMENSIONS = 4;

	/**
	 * We're not using it
	 */
	private StatisticalComparison() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * We compute if exists a difference between the original group and the new
	 * image, taking into account the 4 coordiantes. This will be a problem of
	 * outlier detection within a point cloud
	 * 
	 * @param originalGroup
	 *            group to compare with
	 * @param newGroup
	 *            new group added to the original one
	 * @return standard deviation and distances between mean of each group group
	 *         by dimension
	 */
	public static double[] compareGroupsOfImages(ArrayList<BasicGraphletImage> originalGroup,
			ArrayList<BasicGraphletImage> newGroup) {

		double[][] originalData = create4DMatrix(originalGroup);
		ArrayList<BasicGraphletImage> originalPlusNewGroup = new ArrayList<BasicGraphletImage>();

		originalPlusNewGroup.addAll(originalGroup);
		originalPlusNewGroup.addAll(newGroup);
		double[][] originalPlusNewData = create4DMatrix(originalPlusNewGroup);

		MultivariateSummaryStatistics originalStats = getStats(originalData);
		MultivariateSummaryStatistics originalPlusNewStats = getStats(originalPlusNewData);

		double[] stdDevs = originalStats.getStandardDeviation();
		double[] stdDevsNewData = originalPlusNewStats.getStandardDeviation();

		EuclideanDistance euDis = new EuclideanDistance();
		double distanceBetweenMeans = euDis.compute(originalStats.getMean(), originalPlusNewStats.getMean());

		double[] stdDiff = new double[stdDevs.length];

		for (int numDimension = 0; numDimension < stdDevs.length; numDimension++) {
			stdDiff[numDimension] = stdDevsNewData[numDimension] / stdDevs[numDimension];
		}

		double[] stdDiffAndDistance = { Utils.getMean(stdDiff), distanceBetweenMeans };
		return stdDiffAndDistance;
	}

	/**
	 * Get all the statistics from a multivariate dataset
	 * 
	 * @param matrix4D
	 *            data to get the statistics
	 * @return the multivariate summary statistics
	 */
	public static MultivariateSummaryStatistics getStats(double[][] matrix4D) {
		MultivariateSummaryStatistics statsOriginal = new MultivariateSummaryStatistics(MAX_DIMENSIONS, false);
		double[] newRow = new double[MAX_DIMENSIONS];
		for (int numImg = 0; numImg < matrix4D[0].length; numImg++) {
			newRow[0] = matrix4D[0][numImg];
			newRow[1] = matrix4D[1][numImg];
			newRow[2] = matrix4D[2][numImg];
			newRow[3] = matrix4D[3][numImg];
			statsOriginal.addValue(newRow);
		}

		return statsOriginal;
	}

	/**
	 * @param groupOfImages
	 *            arraylist of group of images
	 * @return the group of images with the 4 dimension we want as double[][]
	 */
	private static double[][] create4DMatrix(ArrayList<BasicGraphletImage> groupOfImages) {

		double[][] originalData = new double[4][groupOfImages.size()];

		for (int numRow = 0; numRow < originalData[0].length; numRow++) {
			originalData[0][numRow] = groupOfImages.get(numRow).getDistanceGDDH();
			originalData[1][numRow] = groupOfImages.get(numRow).getDistanceGDDRV();
			originalData[2][numRow] = groupOfImages.get(numRow).getDistanceGDDV5();
			originalData[3][numRow] = groupOfImages.get(numRow).getPercentageOfHexagons() / 100; // Normalization
		}

		return originalData;
	}
}
