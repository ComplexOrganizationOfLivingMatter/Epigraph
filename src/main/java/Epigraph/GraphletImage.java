/**
 * 
 */
package Epigraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ij.ImagePlus;

import ij.plugin.filter.EDM;
import ij.plugin.filter.MaximumFinder;
import ij.plugin.filter.RankFilters;
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
	
	public static int CIRCLE_SHAPE = 0;
	public static int SQUARE_SHAPE = 1;
	
	/**
	 * @param img image
	 */
	public GraphletImage(ImagePlus img) {
		super();
		//TODO: hardcoded variables, when interfaces come, they should be removed
		int radiusOfShape = 3;
		int selectedShape = CIRCLE_SHAPE;
		
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
		
		int percentageOfHexagons = 0;
		int numValid_Cells = 0;
		for (indexEpiCell = 0; indexEpiCell++ < this.cells.size(); indexEpiCell++){
			createNeighbourhood(indexEpiCell, selectedShape, radiusOfShape);
			if (this.cells.get(indexEpiCell).isValid_cell()){
				if (this.cells.get(indexEpiCell).getNeighbours().size() == 6)
					percentageOfHexagons++;
				numValid_Cells++;
			}	
		}
		
		System.out.println(percentageOfHexagons/numValid_Cells);
		//this.raw_img.show();
		//img.show();
		
		
	}
	
	private boolean labelPropagation(int x, int y, int label){
		if (this.raw_img.getChannelProcessor().getPixel(x, y) != 0 && this.l_img.getChannelProcessor().getPixel(x, y) != label + 1){
			this.l_img.getChannelProcessor().set(x, y, label + 1);
			this.cells.get(label).addPixel(x, y);
			//System.out.println("l" + label + ", XY:" + x + " "+ y);
			
			boolean isPerimeter1 = labelPropagation(x - 1, y, label);
			boolean isPerimeter2 = labelPropagation(x + 1, y, label);
			boolean isPerimeter3 = labelPropagation(x, y - 1, label);
			boolean isPerimeter4 = labelPropagation(x, y + 1, label);
			//If some pixel is 
			if (isPerimeter1 || isPerimeter2 || isPerimeter3 || isPerimeter4)
				this.cells.get(label).addPixelToPerimeter(x,y);
			//if it's in the border, then it is a no valid cell
		}else if (this.raw_img.getChannelProcessor().getPixel(x, y) == 0){
			return true;
		}
		//no valid cell
		if (x == 0 || y == 0 || x == this.raw_img.getWidth() || y == this.l_img.getHeight())
			this.cells.get(label).setValid_cell(false);
		return false;
	}
	
	private ImageProcessor generateMask(int shape, int dimensionOfShape, int[] perimeterPixelX, int[] perimeterPixelY){
		//Create the perimeter of the cell
		ImageProcessor img = new ByteProcessor(this.raw_img.getWidth(), this.raw_img.getHeight());
		for (int numPixel = 0; numPixel < perimeterPixelX.length; numPixel++)
			img.set(perimeterPixelX[numPixel], perimeterPixelY[numPixel], 255);
		
		switch (shape) {
		case 0://CIRCLE_SHAPE
			new RankFilters().rank(img, dimensionOfShape, RankFilters.MAX);
			break;
		case 1: //SQUARE_SHAPE
//			for (int i = 0; i < dimensionOfShape*2 - 1; i++)
//				for (int j = 0; j < dimensionOfShape*2 - 1; j++)
//					mask[i][j] = img.getPixel(i, j);
			break;
		}
		
		return img;
	}
	
	private void createNeighbourhood(int idEpiCell, int shape, int dimensionOfShape){
		EpiCell cell = this.cells.get(idEpiCell);
		ImageProcessor imgProc = generateMask(shape, dimensionOfShape, cell.getPerimeterPixelsX(), cell.getPerimeterPixelsY());
		
		HashSet<Integer> neighbours = new HashSet<Integer>();
		for (int x = 0; x < this.l_img.getWidth(); x++){
			for (int y = 0; y < this.l_img.getHeight(); y++){
				if (imgProc.get(x, y) == 255){
					if (this.l_img.getChannelProcessor().get(x, y) != 0 && this.l_img.getChannelProcessor().get(x, y) != idEpiCell + 1)
						neighbours.add(this.l_img.getChannelProcessor().get(x, y));
				}
			}
		}
		//System.out.println(neighbours);
		cell.setNeighbours(neighbours);
	}
}
