/**
 * 
 */
package epigraph;

import java.awt.Color;

/**
 * @author Pablo Vicente-Munuera
 * 
 *         On this class we represent the basic information we aim to have
 *         through the graphlet process.
 */
public class BasicGraphletImage {
	/**
	 * 
	 */
	protected float distanceGDDRV;

	/**
	 * 
	 */
	protected float distanceGDDH;

	/**
	 * 
	 */
	protected float percentageOfHexagons;

	/**
	 * 
	 */
	protected Color color;

	/**
	 * 
	 */
	protected String labelName;

	/**
	 * 
	 */
	public BasicGraphletImage() {
		// TODO Auto-generated constructor stub
		this.color = Color.WHITE;
		this.distanceGDDH = -1;
		this.distanceGDDRV = -1;
		this.labelName = "Wrong name";
		this.percentageOfHexagons = -1;
	}
	
	public BasicGraphletImage(BasicGraphletImage bci){
		this.color = bci.color;
		this.distanceGDDH = bci.distanceGDDH;
		this.distanceGDDRV = bci.distanceGDDRV;
		this.labelName = bci.labelName;
		this.percentageOfHexagons = bci.percentageOfHexagons;
	}

	/**
	 * @param distanceGDDRV
	 *            distance against random voronoi
	 * @param distanceGDDH
	 *            distance against a hexagonal grid
	 * @param percentageOfHexagons
	 */
	public BasicGraphletImage(float distanceGDDRV, float distanceGDDH, float percentageOfHexagons) {
		this.distanceGDDRV = distanceGDDRV;
		this.distanceGDDH = distanceGDDH;
		this.percentageOfHexagons = percentageOfHexagons;
		this.color = Color.BLACK;
		this.labelName = "ReferenceVoronoiNoise";
	}

	/**
	 * @param distanceGDDRV
	 * @param distanceGDDH
	 * @param percentageOfHexagons
	 * @param color
	 * @param labelName
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
	 * @return the distanceGDDRV
	 */
	public float getDistanceGDDRV() {
		return distanceGDDRV;
	}

	/**
	 * @param distanceGDDRV
	 *            the distanceGDDRV to set
	 */
	public void setDistanceGDDRV(float distanceGDDRV) {
		this.distanceGDDRV = distanceGDDRV;
	}

	/**
	 * @return the distanceGDDH
	 */
	public float getDistanceGDDH() {
		return distanceGDDH;
	}

	/**
	 * @param distanceGDDH
	 *            the distanceGDDH to set
	 */
	public void setDistanceGDDH(float distanceGDDH) {
		this.distanceGDDH = distanceGDDH;
	}

	/**
	 * @return the percentageOfHexagons
	 */
	public float getPercentageOfHexagons() {
		return percentageOfHexagons;
	}

	/**
	 * @param percentageOfHexagons
	 *            the percentageOfHexagons to set
	 */
	public void setPercentageOfHexagons(float percentageOfHexagons) {
		this.percentageOfHexagons = percentageOfHexagons;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the labelName
	 */
	public String getLabelName() {
		return labelName;
	}

	/**
	 * @param labelName
	 *            the labelName to set
	 */
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
}
