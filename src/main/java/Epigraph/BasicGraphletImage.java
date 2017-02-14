
package epigraph;

import java.awt.Color;

/**
 * On this class we represent the basic information we aim to have through the
 * graphlet process.
 * 
 * @author Pablo Vicente-Munuera
 */
public class BasicGraphletImage {

	protected float distanceGDDRV;
	protected float distanceGDDH;
	protected float percentageOfHexagons;
	protected Color color;
	protected String labelName;

	/**
	 * Default constructor. Distances as -1, white color and "Wrong name"
	 */
	public BasicGraphletImage() {
		// TODO Auto-generated constructor stub
		this.color = Color.WHITE;
		this.distanceGDDH = -1;
		this.distanceGDDRV = -1;
		this.labelName = "Wrong name";
		this.percentageOfHexagons = -1;
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
		this.percentageOfHexagons = bci.percentageOfHexagons;
	}

	/**
	 * Constructing the reference
	 * 
	 * @param distanceGDDRV
	 *            distance against random voronoi
	 * @param distanceGDDH
	 *            distance against a hexagonal grid
	 * @param percentageOfHexagons
	 *            proportion of hexagons
	 */
	public BasicGraphletImage(float distanceGDDRV, float distanceGDDH, float percentageOfHexagons) {
		this.distanceGDDRV = distanceGDDRV;
		this.distanceGDDH = distanceGDDH;
		this.percentageOfHexagons = percentageOfHexagons;
		this.color = Color.BLACK;
		this.labelName = "ReferenceVoronoiNoise";
	}

	/**
	 * Constructing from parameters
	 * 
	 * @param distanceGDDRV
	 *            distance against random voronoi
	 * @param distanceGDDH
	 *            distance against a hexagonal grid
	 * @param percentageOfHexagons
	 *            proportion of hexagons
	 * @param color
	 *            assigned color
	 * @param labelName
	 *            label of image
	 */
	public BasicGraphletImage(float distanceGDDRV, float distanceGDDH, float percentageOfHexagons, Color color,
			String labelName) {
		super();
		this.distanceGDDRV = distanceGDDRV;
		this.distanceGDDH = distanceGDDH;
		this.percentageOfHexagons = percentageOfHexagons;
		this.color = color;
		this.labelName = labelName;
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
	public float getPercentageOfHexagons() {
		return percentageOfHexagons;
	}

	/**
	 * @param percentageOfHexagons
	 *            set proportion of hexagons
	 */
	public void setPercentageOfHexagons(float percentageOfHexagons) {
		this.percentageOfHexagons = percentageOfHexagons;
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
}
