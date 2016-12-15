/**
 * 
 */
package Epigraph;

import java.util.ArrayList;
import java.util.List;

import ij.ImagePlus;

import ij.plugin.filter.EDM;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import mpicbg.imglib.algorithm.labeling.AllConnectedComponents;
import util.FindConnectedRegions;


/**
 * @author Pablo Vicente-Munuera
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
		this.cells = new ArrayList<EpiCell>();
		
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
		ImageProcessor imp = new ByteProcessor(img.getChannelProcessor(), true);
		this.raw_img = new ImagePlus("", imp);
		
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
		
		MaximumFinder mxf = new MaximumFinder();
		ByteProcessor btp = mxf.findMaxima(img.getChannelProcessor(), 0.5, MaximumFinder.SINGLE_POINTS, true);
		img.setProcessor(btp);
		img.show();
		this.l_img = new ImagePlus("", img.getChannelProcessor().convertToFloat());
		pixels = img.getChannelProcessor().getIntArray();
		
		int indexEpiCell = 0;
		EpiCell epicell = null;
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++){
				if (pixels[i][j] != 0){
					epicell = new EpiCell(indexEpiCell);
					this.cells.add(epicell);
					labelPropagation(i, j, indexEpiCell);
					epicell.addPixel(i, j);
					indexEpiCell++;
				}
			}
		}
		
		this.l_img.show();
		//this.raw_img.show();
		//img.show();
	}
	
	private boolean labelPropagation(int x, int y, int label){
		if (this.raw_img.getChannelProcessor().getPixel(x, y) != 0 && this.l_img.getChannelProcessor().getPixel(x, y) != label + 1){
			this.l_img.getChannelProcessor().set(x, y, label + 1);
			this.cells.get(label).addPixel(x, y);
			if (x > 0)
				labelPropagation(x - 1, y, label);
			if (x < this.raw_img.getWidth())
				labelPropagation(x + 1, y, label);
			if (y > 0)
				labelPropagation(x, y - 1, label);
			if (y < this.raw_img.getHeight())
				labelPropagation(x, y + 1, label);
			boolean isPerimeter1 = labelPropagation(x - 1, y, label);
			boolean isPerimeter2 = labelPropagation(x + 1, y, label);
			boolean isPerimeter3 = labelPropagation(x, y - 1, label);
			boolean isPerimeter4 = labelPropagation(x, y + 1, label);
			//If some pixel is 
			if (isPerimeter1 || isPerimeter2 || isPerimeter3 || isPerimeter4)
				this.cells.get(label).addPixelToPerimeter(x,y);
			
		}else if (this.raw_img.getChannelProcessor().getPixel(x, y) == 0){
			return true;
		}
		return false;
	}
}
