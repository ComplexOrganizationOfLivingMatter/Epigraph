/**
 * 
 */
package epigraph;

import java.util.ArrayList;
import java.util.Arrays;

import epigraph.LibMahalanobis.InvMat;
import epigraph.LibMahalanobis.MatrixLib;
import epigraph.LibMahalanobis.Utils;
import epigraph.LibMahalanobis.VectorLib;


/**
 * 
 * Here we want to compare an image with a CVTn diagram. 
 * If the image is an outlier of the diagramas, it will be outside of the CVTn,
 * while inlier images, would be within the CVTn.
 * 
 * @author Pablo Vicente-Munuera, Pedro Gomez-Galvez
 *
 */
final class StatisticalComparison {
	
	static int MAX_DIMENSIONS = 4;

	/**
	 * We're not using it
	 */
	private StatisticalComparison() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param originalGroup
	 * @param newGroup
	 * @return
	 */
	public static int compareGroupsOfImages(ArrayList<BasicGraphletImage> originalGroup, ArrayList<BasicGraphletImage> newGroup){
		
		//Create 4D matrix
		double[][] originalData = create4DMatrix(originalGroup);
		ArrayList<BasicGraphletImage> originalPlusNewGroup = new ArrayList<BasicGraphletImage>();
		
		originalPlusNewGroup.addAll(originalGroup);
		originalPlusNewGroup.addAll(newGroup);
		double[][] originalPlusNewData = create4DMatrix(originalPlusNewGroup);
		
		double[] stdDevs = Utils.getStdDev(originalData);
		double[] stdDevsNewData = Utils.getStdDev(originalPlusNewData);
		
		
		
		return 0;
	}

	/**
	 * @param groupOfImages
	 * @return
	 */
	private static double[][] create4DMatrix(ArrayList<BasicGraphletImage> groupOfImages) {
		
		double[][] originalData = new double[4][groupOfImages.size()];
		
		for (int numRow = 1; numRow < originalData[1].length; numRow++){
			originalData[1][numRow] = groupOfImages.get(numRow).getDistanceGDDH();
			originalData[2][numRow] = groupOfImages.get(numRow).getDistanceGDDRV();
			originalData[3][numRow] = groupOfImages.get(numRow).getDistanceGDDV5();
			originalData[4][numRow] = groupOfImages.get(numRow).getPercentageOfHexagonsGraphlets();
		}
		
		return originalData;
	}
}
