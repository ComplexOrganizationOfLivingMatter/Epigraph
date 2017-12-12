/**
 * 
 */
package epigraph;

import java.util.ArrayList;
import java.util.Arrays;

import epigraph.LibMahalanobis.InvMat;
import epigraph.LibMahalanobis.MatrixLib;
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
		 
		//MAD approach Median and Median Absolute Deviation Method (MAD)
		return 0;
	}
}
