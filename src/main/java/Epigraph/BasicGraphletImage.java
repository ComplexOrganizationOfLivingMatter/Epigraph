
package epigraph;

import java.awt.Color;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;

import epigraph.Statistics.StatisticalComparison;
import epigraph.Statistics.Utils;

/**
 * On this class we represent the basic information we aim to have through the
 * graphlet process.
 * 
 * @author Pablo Vicente-Munuera
 */
public class BasicGraphletImage {

	/**
	 * 
	 */
	private static int MAX_CLOSERDIAGRAMS = 3;

	protected float distanceGDDRV;
	protected float distanceGDDV5;
	protected float distanceGDDH;
	protected float percentageOfTriangles;
	protected float percentageOfSquares;
	protected float percentageOfPentagons;
	protected float percentageOfHexagons;
	protected float percentageOfHeptagons;
	protected float percentageOfOctogons;
	protected float percentageOfNonagons;
	protected Color color;
	protected String labelName;
	protected int shapeOfMask;
	protected int radiusOfMask;
	protected boolean selectedCells;
	// 1: Position; 2: Euclidean Distance; 3: Deviation Difference
	protected double[][] closestDiagrams;

	/**
	 * Default constructor. Distances as -1, white color and "Wrong name"
	 */
	public BasicGraphletImage() {
		// TODO Auto-generated constructor stub
		this.color = Color.WHITE;
		this.distanceGDDH = -1;
		this.distanceGDDRV = -1;
		this.labelName = "Wrong name";
		this.percentageOfTriangles = -1;
		this.percentageOfSquares = -1;
		this.percentageOfPentagons = -1;
		this.percentageOfHexagons = -1;
		this.percentageOfHeptagons = -1;
		this.percentageOfOctogons = -1;
		this.percentageOfNonagons = -1;
		this.shapeOfMask = -1;
		this.radiusOfMask = -1;
		this.selectedCells = false;
		this.closestDiagrams = null;
	}

	/**
	 * Constructor of copy
	 * 
	 * @param bci
	 *            new copy of basic graphlet image
	 */
	public BasicGraphletImage(BasicGraphletImage bci) {
		this.color = bci.color;
		this.distanceGDDH = bci.distanceGDDH;
		this.distanceGDDRV = bci.distanceGDDRV;
		this.distanceGDDV5 = bci.distanceGDDV5;
		this.labelName = bci.labelName;
		this.percentageOfTriangles = bci.percentageOfTriangles;
		this.percentageOfSquares = bci.percentageOfSquares;
		this.percentageOfPentagons = bci.percentageOfPentagons;
		this.percentageOfHexagons = bci.percentageOfHexagons;
		this.percentageOfHeptagons = bci.percentageOfHeptagons;
		this.percentageOfOctogons = bci.percentageOfOctogons;
		this.percentageOfNonagons = bci.percentageOfNonagons;
		this.radiusOfMask = bci.radiusOfMask;
		this.shapeOfMask = bci.shapeOfMask;
		this.selectedCells = bci.selectedCells;
		this.closestDiagrams = bci.closestDiagrams.clone();
	}

	/**
	 * Constructing the reference
	 * 
	 * @param distanceGDDRV
	 *            distance against random voronoi
	 * @param distanceGDDV5
	 *            distance against voronoi 5
	 * @param distanceGDDH
	 *            distance against a hexagonal grid
	 * @param percentageOfHexagonsGraphlets
	 *            proportion of hexagons
	 */
	public BasicGraphletImage(float distanceGDDRV, float distanceGDDH, float distanceGDDV5, float percentageOfHexagonsGraphlets) {
		this.distanceGDDRV = distanceGDDRV;
		this.distanceGDDH = distanceGDDH;
		this.distanceGDDV5 = distanceGDDV5;
		this.percentageOfHexagons = percentageOfHexagonsGraphlets;
		this.color = Color.BLACK;
		this.labelName = "ReferenceVoronoiNoise";
		this.shapeOfMask = GraphletImage.CIRCLE_SHAPE;
		this.radiusOfMask = 3;
		this.selectedCells = false;
		this.closestDiagrams = null;
	}

	/**
	 * Constructing from parameters
	 * 
	 * @param distanceGDDRV
	 *            distance against random voronoi
	 * @param distanceGDDV5
	 *            distance against voronoi 5
	 * @param distanceGDDH
	 *            distance against a hexagonal grid
	 * @param percentageOfHexagonsGraphlets
	 *            proportion of hexagons
	 * @param color
	 *            assigned color
	 * @param labelName
	 *            label of image
	 * @param shapeOfMask CIRCLE or SQUARE
	 * @param radiusOfMask radius of shape of mask
	 */
	public BasicGraphletImage(float distanceGDDRV, float distanceGDDH, float distanceGDDV5, float percentageOfHexagonsGraphlets, Color color,
			String labelName, int shapeOfMask, int radiusOfMask) {
		super();
		this.distanceGDDRV = distanceGDDRV;
		this.distanceGDDH = distanceGDDH;
		this.distanceGDDV5 = distanceGDDV5;
		this.percentageOfHexagons = percentageOfHexagonsGraphlets;
		this.color = color;
		this.labelName = labelName;
		this.shapeOfMask = shapeOfMask;
		this.radiusOfMask = radiusOfMask;
		this.selectedCells = false;
		this.closestDiagrams = null;
	}

	/**
	 * @return distanceGDDRV distance against random voronoi
	 */
	public float getDistanceGDDRV() {
		return distanceGDDRV;
	}

	/**
	 * @param distanceGDDRV
	 *            set distance against random voronoi
	 */
	public void setDistanceGDDRV(float distanceGDDRV) {
		this.distanceGDDRV = distanceGDDRV;
	}

	
	/**
	 * @return distanceGDDV5 distance against random voronoi
	 */
	public float getDistanceGDDV5() {
		return distanceGDDV5;
	}

	/**
	 * @param distanceGDDV5
	 *            set distance against random voronoi
	 */
	public void setDistanceGDDV5(float distanceGDDV5) {
		this.distanceGDDV5 = distanceGDDV5;
	}
	
	/**
	 * @return distance against a hexagonal grid
	 */
	public float getDistanceGDDH() {
		return distanceGDDH;
	}

	/**
	 * @param distanceGDDH
	 *            set distance against a hexagonal grid
	 */
	public void setDistanceGDDH(float distanceGDDH) {
		this.distanceGDDH = distanceGDDH;
	}

		
	/**
	 * @return proportion of hexagons
	 */
	public float getPercentageOfHexagons() {
		return percentageOfHexagons;
	}

	/**
	 * @param percentageOfHexagonsGraphlets
	 *            set proportion of hexagons
	 */
	public void setPercentageOfHexagons(float percentageOfHexagonsGraphlets) {
		this.percentageOfHexagons = percentageOfHexagonsGraphlets;
	}


	/**
	 * @return the percentageOfTrianglesGraphlets
	 */
	public final float getPercentageOfTriangles() {
		return percentageOfTriangles;
	}

	/**
	 * @param percentageOfTrianglesGraphlets the percentageOfTrianglesGraphlets to set
	 */
	public final void setPercentageOfTriangles(float percentageOfTrianglesGraphlets) {
		this.percentageOfTriangles = percentageOfTrianglesGraphlets;
	}

	/**
	 * @return the percentageOfSquaresGraphlets
	 */
	public final float getPercentageOfSquares() {
		return percentageOfSquares;
	}

	/**
	 * @param percentageOfSquaresGraphlets the percentageOfSquaresGraphlets to set
	 */
	public final void setPercentageOfSquares(float percentageOfSquaresGraphlets) {
		this.percentageOfSquares = percentageOfSquaresGraphlets;
	}

	/**
	 * @return the percentageOfPentagonsGraphlets
	 */
	public final float getPercentageOfPentagons() {
		return percentageOfPentagons;
	}

	/**
	 * @param percentageOfPentagonsGraphlets the percentageOfPentagonsGraphlets to set
	 */
	public final void setPercentageOfPentagons(float percentageOfPentagonsGraphlets) {
		this.percentageOfPentagons = percentageOfPentagonsGraphlets;
	}

	/**
	 * @return the percentageOfHeptagonsGraphlets
	 */
	public final float getPercentageOfHeptagons() {
		return percentageOfHeptagons;
	}

	/**
	 * @param percentageOfHeptagonsGraphlets the percentageOfHeptagonsGraphlets to set
	 */
	public final void setPercentageOfHeptagons(float percentageOfHeptagonsGraphlets) {
		this.percentageOfHeptagons = percentageOfHeptagonsGraphlets;
	}

	/**
	 * @return the percentageOfOctogonsGraphlets
	 */
	public final float getPercentageOfOctogons() {
		return percentageOfOctogons;
	}

	/**
	 * @param percentageOfOctogonsGraphlets the percentageOfOctogonsGraphlets to set
	 */
	public final void setPercentageOfOctogons(float percentageOfOctogonsGraphlets) {
		this.percentageOfOctogons = percentageOfOctogonsGraphlets;
	}

	/**
	 * @return the percentageOfNonagonsGraphlets
	 */
	public final float getPercentageOfNonagons() {
		return percentageOfNonagons;
	}

	/**
	 * @param percentageOfNonagonsGraphlets the percentageOfNonagonsGraphlets to set
	 */
	public final void setPercentageOfNonagons(float percentageOfNonagonsGraphlets) {
		this.percentageOfNonagons = percentageOfNonagonsGraphlets;
	}

	/**
	 * @return color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            set color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return label of image
	 */
	public String getLabelName() {
		return labelName;
	}

	/**
	 * @param labelName
	 *            set label image
	 */
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	/**
	 * @return the shapeOfMask
	 */
	public int getShapeOfMask() {
		return shapeOfMask;
	}

	/**
	 * @param shapeOfMask the shapeOfMask to set
	 */
	public void setShapeOfMask(int shapeOfMask) {
		this.shapeOfMask = shapeOfMask;
	}

	/**
	 * @return the radiusOfMask
	 */
	public int getRadiusOfMask() {
		return radiusOfMask;
	}

	/**
	 * @param radiusOfMask the radiusOfMask to set
	 */
	public void setRadiusOfMask(int radiusOfMask) {
		this.radiusOfMask = radiusOfMask;
	}
	
	/**
	 * @return the closestDiagrams
	 */
	public final double[][] getClosestDiagrams() {
		return closestDiagrams.clone();
	}

	/**
	 * @param closestDiagrams the closestDiagrams to set
	 */
	public final void setClosestDiagrams(double[][] closestDiagrams) {
		this.closestDiagrams = closestDiagrams;
	}

	/**
	 * @return the selectedCells
	 */
	public boolean isSelectedCells() {
		return selectedCells;
	}

	/**
	 * @param selectedCells the selectedCells to set
	 */
	public void setSelectedCells(boolean selectedCells) {
		this.selectedCells = selectedCells;
	}
	
	/**
	 * 
	 * @throws CloneNotSupportedException
	 */
	public void calculateClosestDiagram(ArrayList<BasicGraphletImage> diagramsData) throws CloneNotSupportedException{

		
		double[] diagramsUsed = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 18, 19, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700};
		double[] statisticalDifferences = new double[diagramsUsed.length];
		double[] statisticalDistances = new double[diagramsUsed.length];
		
		double[] newRow = new double[2];
		
		for (int numDiagram = 0; numDiagram < diagramsUsed.length; numDiagram++){
			
			ArrayList<BasicGraphletImage> originalGroup = filterByDiagram(diagramsData, diagramsUsed[numDiagram]);
			ArrayList<BasicGraphletImage> newGraphletsGroup = new ArrayList<BasicGraphletImage>();
			newGraphletsGroup.add(this);
			newRow = StatisticalComparison.compareGroupsOfImages(originalGroup, newGraphletsGroup);
			statisticalDifferences[numDiagram] = newRow[0];
			statisticalDistances[numDiagram] = newRow[1];
		}
		
		//double[] minValueDistAndPosition = Utils.getMin(statisticalDistances);
		//double[] minValueDiffAndPosition = Utils.getMin(statisticalDifferences);
		
		//They may differ
		
		double[][] orderedDists = Utils.bubbleSorting(statisticalDistances.clone(), diagramsUsed.clone());
		double[][] orderedDiffs = Utils.bubbleSorting(statisticalDistances.clone(), statisticalDifferences.clone());
		
		double[][] closestDiagrams = new double[MAX_CLOSERDIAGRAMS][3];
		for (int numDiagram = 0; numDiagram < MAX_CLOSERDIAGRAMS; numDiagram++) {
			closestDiagrams[numDiagram][0] = orderedDists[1][numDiagram];
			closestDiagrams[numDiagram][1] = orderedDists[0][numDiagram];
			closestDiagrams[numDiagram][2] = orderedDiffs[1][numDiagram];
		}
		
		this.setClosestDiagrams(closestDiagrams);
	}
	


	/**
	 * 
	 * @param allData data to filter
	 * @param numDiagram the number of the diagram we want to
	 * @return all the data that fits with the number of the diagram
	 */
	private ArrayList<BasicGraphletImage> filterByDiagram(ArrayList<BasicGraphletImage> allData, double numDiagram) {
		ArrayList<BasicGraphletImage> actualDiagramData = new ArrayList<BasicGraphletImage>();
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);
		String formattedDiagram = nf.format(numDiagram);
		
		for (BasicGraphletImage basicGraphletImage : allData) {
			if (basicGraphletImage.getLabelName().contains(formattedDiagram))
					actualDiagramData.add(basicGraphletImage);
					
		}
		
		
		return actualDiagramData;
	}
}
