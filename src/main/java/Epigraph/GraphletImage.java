/**
 * 
 */
package main.java.Epigraph;

import java.util.ArrayList;
import java.util.List;

import ij.ImagePlus;

import ij.plugin.filter.EDM;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
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
		cells = new ArrayList<EpiCell>();
		
		if (!img.getChannelProcessor().isBinary()){
			System.out.println("No binary image, improving...");
			img.getChannelProcessor().autoThreshold();
			img.show();
		}
		
		int[][] pixels = img.getChannelProcessor().getIntArray();
		int whitePixels = 0;
		int blackPixels = 0;
		for (int i = 0; i < img.getWidth(); i++) {
			if (pixels[i][0] == 0)
				blackPixels++;
			else
				whitePixels++;
		}
		
		if (blackPixels > whitePixels){
			img.getChannelProcessor().invert();
			img.show();
		}
		
		//Add a frame
		for (int i = 0; i < img.getWidth(); i++){
			img.getChannelProcessor().set(i, 0, 0);
			img.getChannelProcessor().set(i, img.getHeight() - 1, 0);
		}
		
		for (int i = 0; i < img.getHeight(); i++){
			img.getChannelProcessor().set(0, i, 0);
			img.getChannelProcessor().set(img.getWidth() - 1, i, 0);
		}
		img.show();
		this.raw_img = img;
		
		MaximumFinder mxf = new MaximumFinder();
		ByteProcessor btp = mxf.findMaxima(img.getChannelProcessor(), 0.5, MaximumFinder.SINGLE_POINTS, true);
		img.setProcessor(btp);
		img.show();
		this.l_img = img;
		
		pixels = img.getChannelProcessor().getIntArray();
		ArrayList<Integer> numCells = new ArrayList<Integer>();
		
		int indexEpiCell;
		EpiCell epicell = null;
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++){
				if (pixels[i][j] != 0){
					indexEpiCell = numCells.indexOf(pixels[i][j]);
					if (indexEpiCell != -1){
						epicell = cells.get(indexEpiCell);
					}else{
						numCells.add(pixels[i][j]);
						epicell = new EpiCell(pixels[i][j]);
						cells.add(epicell);
					}
					
					epicell.addPixel(i, j);
					if (i == 0 || j == 0 || j == (img.getHeight() - 1) || i == (img.getWidth() - 1)){
						epicell.setValid_cell(false);
					} else {
						epicell.setValid_cell(true);
					}
				}
			}
		}
		
		
		img.show();
	}
}
