
package epigraph;

import java.awt.Color;
import java.util.ArrayList;

/**
 * On this class we represent the basic information we aim to have through the
 * graphlet process.
 * 
 * @author Pablo Vicente-Munuera
 */
public class BasicGraphletImage {

	protected float distanceGDDRV;
	protected float distanceGDDV5;
	protected float distanceGDDH;
	protected float percentageOfHexagonsGraphlets;
	protected Color color;
	protected String labelName;
	protected int shapeOfMask;
	protected int radiusOfMask;
	protected boolean selectedCells;
	
	protected ArrayList<Float> polygonDistGraphletCells;
	protected ArrayList<Float> polygonDistRoiCells;

	/**
	 * Default constructor. Distances as -1, white color and "Wrong name"
	 */
	public BasicGraphletImage() {
		// TODO Auto-generated constructor stub
		this.color = Color.WHITE;
		this.distanceGDDH = -1;
		this.distanceGDDRV = -1;
		this.labelName = "Wrong name";
		this.percentageOfHexagonsGraphlets = -1;
		this.shapeOfMask = -1;
		this.radiusOfMask = -1;
		this.selectedCells = false;
		this.polygonDistGraphletCells = null;
		this.polygonDistRoiCells = null;
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
		this.labelName = bci.labelName;
		this.percentageOfHexagonsGraphlets = bci.percentageOfHexagonsGraphlets;
		this.radiusOfMask = bci.radiusOfMask;
		this.shapeOfMask = bci.shapeOfMask;
		this.selectedCells = bci.selectedCells;
		this.polygonDistGraphletCells = bci.polygonDistGraphletCells;
		this.polygonDistRoiCells = bci.polygonDistRoiCells;
	}

	/**
	 * Constructing the reference
	 * 
	 * @param distanceGDDRV
	 *            distance against random voronoi
	 * @param distanceGDDH
	 *            distance against a hexagonal grid
	 * @param percentageOfHexagonsGraphlets
	 *            proportion of hexagons
	 */
	public BasicGraphletImage(float distanceGDDRV, float distanceGDDH, float percentageOfHexagonsGraphlets, ArrayList<Float> polygonDistGraphletCells,ArrayList<Float> polygonDistRoiCells) {
		this.distanceGDDRV = distanceGDDRV;
		this.distanceGDDH = distanceGDDH;
		this.percentageOfHexagonsGraphlets = percentageOfHexagonsGraphlets;
		this.color = Color.BLACK;
		this.labelName = "ReferenceVoronoiNoise";
		this.shapeOfMask = GraphletImage.CIRCLE_SHAPE;
		this.radiusOfMask = 3;
		this.selectedCells = false;
		this.polygonDistGraphletCells = polygonDistGraphletCells;
		this.polygonDistRoiCells = polygonDistRoiCells;
	}

	/**
	 * Constructing from parameters
	 * 
	 * @param distanceGDDRV
	 *            distance against random voronoi
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
	public BasicGraphletImage(float distanceGDDRV, float distanceGDDH, float percentageOfHexagonsGraphlets, Color color,
			String labelName, int shapeOfMask, int radiusOfMask, ArrayList<Float> polygonDistGraphletCells,ArrayList<Float> polygonDistRoiCells) {
		super();
		this.distanceGDDRV = distanceGDDRV;
		this.distanceGDDH = distanceGDDH;
		this.percentageOfHexagonsGraphlets = percentageOfHexagonsGraphlets;
		this.color = color;
		this.labelName = labelName;
		this.shapeOfMask = shapeOfMask;
		this.radiusOfMask = radiusOfMask;
		this.selectedCells = false;
		this.polygonDistGraphletCells = polygonDistGraphletCells;
		this.polygonDistRoiCells = polygonDistRoiCells;
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
	public float getPercentageOfHexagonsGraphlets() {
		return percentageOfHexagonsGraphlets;
	}

	/**
	 * @param percentageOfHexagonsGraphlets
	 *            set proportion of hexagons
	 */
	public void setPercentageOfHexagonsGraphlets(float percentageOfHexagonsGraphlets) {
		this.percentageOfHexagonsGraphlets = percentageOfHexagonsGraphlets;
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
	 * @return the polygonDistGraphletCells
	 */
	public ArrayList<Float> getPolygonDistGraphletCells() {
		return polygonDistGraphletCells;
	}

	/**
	 * @param polygonDistGraphletCells the polygonDistGraphletCells to set
	 */
	public void setPolygonDistGraphletCells(ArrayList<Float> polygonDistGraphletCells) {
		this.polygonDistGraphletCells = polygonDistGraphletCells;
	}
	
	
	/**
	 * @return the polygonDistRoiCells
	 */
	public ArrayList<Float> getPolygonDistRoiCells() {
		return polygonDistRoiCells;
	}

	/**
	 * @param polygonDistRoiCells the polygonDistRoiCells to set
	 */
	public void setPolygonDistRoiCells(ArrayList<Float> polygonDistRoiCells) {
		this.polygonDistRoiCells = polygonDistRoiCells;
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
}
