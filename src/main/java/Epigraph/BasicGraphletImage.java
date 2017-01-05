/**
 * 
 */
package Epigraph;

/**
 * @author Pablo Vicente-Munuera
 * On this class we will represent only the reference images.
 * This means, from this images, we'll only have the distances,
 * just in case the user wants a reference in the graphics.
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
	public BasicGraphletImage() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param distanceGDDRV
	 * @param distanceGDDH
	 */
	public BasicGraphletImage(float distanceGDDRV, float distanceGDDH) {
		this.distanceGDDRV = distanceGDDRV;
		this.distanceGDDH = distanceGDDH;
	}

	/**
	 * @return the distanceGDDRV
	 */
	public float getDistanceGDDRV() {
		return distanceGDDRV;
	}

	/**
	 * @param distanceGDDRV the distanceGDDRV to set
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
	 * @param distanceGDDH the distanceGDDH to set
	 */
	public void setDistanceGDDH(float distanceGDDH) {
		this.distanceGDDH = distanceGDDH;
	}
}
