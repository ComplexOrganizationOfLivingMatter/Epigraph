/**
 * 
 */
package main.java.Epigraph;

import java.util.ArrayList;

import ij.ImagePlus;

import ij.plugin.filter.EDM;
import ij.process.ImageProcessor;

/**
 * @author Equipo
 *
 */
public class GraphletImage {
	ImagePlus raw_img;
	ImagePlus l_img;
	ArrayList<EpiCell> cells;
	
	/**
	 * @param img image
	 */
	public GraphletImage(ImagePlus img) {
		super();
		this.raw_img = img;
		EDM edm = new EDM();
		//label image
		img.show();
		edm.toWatershed(img.getChannelProcessor());
	}
}
