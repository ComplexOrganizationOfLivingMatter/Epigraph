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
		EDM edm = new EDM();
		
		if (!img.getChannelProcessor().isBinary()){
			System.out.println("No binary image, improving...");
			img.getChannelProcessor().autoThreshold();
			img.show();
		}
		
		int[][] pixels = img.getChannelProcessor().getIntArray();
		int whitePixels = 0;
		int blackPixels = 0;
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++){
				if (pixels[i][j] == 0)
					blackPixels++;
				else
					whitePixels++;
			}
		}
		if (blackPixels > whitePixels){
			img.getChannelProcessor().invert();
			img.show();
		}
		
		this.raw_img = img;
		
		edm.run(img.getChannelProcessor());
		edm.toWatershed(img.getChannelProcessor());
		img.show();
		this.l_img = img;
		
		pixels = img.getChannelProcessor().getIntArray();
		
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++){
				System.out.println(pixels[i][j]);
			}
		}
		img.show();
	}
}
