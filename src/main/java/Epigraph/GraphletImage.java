/**
 * 
 */
package Epigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	int[][] adjacencyMatrix;
	Orca orcaProgram;
	
	public static int CIRCLE_SHAPE = 0;
	public static int SQUARE_SHAPE = 1;
	
	public static int TOTALGRAPHLETS = 73;
	
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
		//Create adjacency matrix from the found cells
		this.adjacencyMatrix = new int[indexEpiCell][indexEpiCell];
		
		
		this.l_img.show();
		
		for (indexEpiCell = 0; indexEpiCell < this.cells.size(); indexEpiCell++)
			createNeighbourhood(indexEpiCell, selectedShape, radiusOfShape);
		
		this.orcaProgram = new Orca(this.adjacencyMatrix);
		
		int[][] graphlets = this.orcaProgram.getOrbit();
		int percentageOfHexagons = 0, percentageOfHexagonsOriginal = 0;
		for (int i = 0; i < graphlets.length; i++){
			this.cells.get(i).setGraphlets(graphlets[i]);
			if (graphlets[i][0] == 6){
				percentageOfHexagons++;
			}
			if (this.cells.get(i).getNeighbours().size() == 6 && this.cells.get(i).isValid_cell()){
				percentageOfHexagonsOriginal++;
			}
		}
		System.out.println(percentageOfHexagons);
		System.out.println(percentageOfHexagonsOriginal);
		this.orcaProgram = null;
		
		int numValidCells = 0;
		for (indexEpiCell = 0; indexEpiCell < this.cells.size(); indexEpiCell++){
			if (allValidCellsWithinAGivenLength(indexEpiCell, 4)){
				this.cells.get(indexEpiCell).setValid_cell_4(true);
				numValidCells++;
			}else{
				this.cells.get(indexEpiCell).setValid_cell_4(false);
			}
			this.cells.get(indexEpiCell).setValid_cell_5(allValidCellsWithinAGivenLength(indexEpiCell, 5));
		}
		
		System.out.println(numValidCells);
		
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
			//If some pixel is at the perimeter
			if (isPerimeter1 || isPerimeter2 || isPerimeter3 || isPerimeter4)
				this.cells.get(label).addPixelToPerimeter(x,y);
			//if it's in the border, then it is a no valid cell
		}else if (this.raw_img.getChannelProcessor().getPixel(x, y) == 0){
			return true;
		}
		//no valid cell
		if (x == 0 || y == 0 || y == this.l_img.getWidth() - 1 || x == this.l_img.getHeight() - 1)
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
		int labelNeigh;
		for (int x = 0; x < this.l_img.getWidth(); x++){
			for (int y = 0; y < this.l_img.getHeight(); y++){
				if (imgProc.get(x, y) == 255){
					if (this.l_img.getChannelProcessor().get(x, y) != 0 && this.l_img.getChannelProcessor().get(x, y) != idEpiCell + 1){
						labelNeigh = this.l_img.getChannelProcessor().get(x, y) - 1;
						neighbours.add(labelNeigh);
						if (this.cells.get(idEpiCell).isValid_cell() ||  this.cells.get(labelNeigh).isValid_cell()){ //Only valid cells' relationships
							this.adjacencyMatrix[idEpiCell][labelNeigh] = 1;
							this.adjacencyMatrix[labelNeigh][idEpiCell] = 1;
						}
					}
						
				}
			}
		}
		//System.out.println(neighbours);
		cell.setNeighbours(neighbours);
	}
	
	/**
	 * 
	 * @param indexEpiCell
	 * @param length
	 * @return
	 */
	private boolean allValidCellsWithinAGivenLength(int indexEpiCell, int length){
		if (this.cells.get(indexEpiCell).isValid_cell()){
			if (length > 1){
				HashSet<Integer> neighbours = this.cells.get(indexEpiCell).getNeighbours();
				Iterator<Integer> itNeigh = neighbours.iterator();
				int neighbourActual = -1;
				while (itNeigh.hasNext()){
					neighbourActual = itNeigh.next();
					if (allValidCellsWithinAGivenLength(neighbourActual, length - 1) == false)
						return false;
				}
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public float distanceGDDH(){
		int[][] hexagonReference = {{6, 18, 9, 6, 54, 54, 6, 2, 0, 12, 24, 12, 6, 6, 0, 162, 162, 81, 18, 36, 18, 18, 0, 0, 48, 24, 48, 36, 36, 72, 36, 0, 0, 0, 0, 0, 0, 0, 0, 6, 12, 6, 6, 12, 3, 12, 12, 12, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 12, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
		
		ArrayList<HashMap<Integer, Float>> graphletFreqRef = scaleGraphletDists(hexagonReference);
		ArrayList<HashMap<Integer, Float>> graphletFreqImage = scaleGraphletDists(this.orcaProgram.getOrbit());
		
		float[] orbitDist = new float[this.cells.get(0).getGraphlets().length];
		
		for (int i = 0; i < TOTALGRAPHLETS; i++){
			HashMap<Integer, Float> values1 = graphletFreqRef.get(i);
			HashMap<Integer, Float> values2 = graphletFreqImage.get(i);
			
			//Compute the distance among the orbits
			float sumDistances = (float) 0.0;
			Set<Integer> allDegrees = values1.keySet();
			allDegrees.addAll(values2.keySet());
			
			for (Integer degree : allDegrees){
				
			}
		}
		
		return orbitDist;
	}
	
	/**
	 * Function to compute the graphlet counts from ndump2 files
	 * @return
	 */
	private float[] getGraphletFrequence(int[][] graphletsImage){
		int[] orbits = {2, 3, 5, 7, 8, 9, 12, 14, 17, 18, 23, 25, 27, 33, 34, 35, 39, 44, 45, 50, 52, 55, 56, 61, 62, 65, 69, 70, 72};
		int[] weights = {1, 3, 2, 1, 4, 1, 2, 4, 1, 1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 5};
		
		float[] graphletCounts = new float[orbits.length];
		
		int orbit;
		for (int i = 0; i < orbits.length; i++){
			orbit = orbits[i];
			int sumCount = 0;
			for (int j = 0; j < this.cells.size(); j++){
				sumCount += graphletsImage[j][orbit];
			}
			graphletCounts[i] = (float) sumCount / (float) weights[i];
		}
		
		return graphletCounts;
	}
	
	/**
	 * 
	 * @param signatures
	 * @return
	 */
	private ArrayList<HashMap<Integer,Float>> scaleGraphletDists(int[][] signatures){
		ArrayList<HashMap<Integer, Float>> distributions = new ArrayList<HashMap<Integer, Float>>();
		
		HashMap<Integer, Float> graphletsValues;
		Float actualValue;
		for (int numGraphlet = 0; numGraphlet < TOTALGRAPHLETS; numGraphlet++){
			graphletsValues = new HashMap<Integer, Float>();
			
			for (int numNode = 0; numNode < signatures.length; numNode++){
				actualValue = graphletsValues.putIfAbsent(signatures[numNode][numGraphlet], (float) 1);
				if (actualValue != null){
					graphletsValues.put(signatures[numNode][numGraphlet], actualValue + 1);
				}
			}
			graphletsValues.remove(0);
			
			//Scale the distribution values for GDD agreement
			float total = 0;
			float valueGraph;
			for (int key : graphletsValues.keySet()) {
				valueGraph = graphletsValues.get(key);
				graphletsValues.replace(key, valueGraph/key);
				total += valueGraph/key;
			}
			
			//Normalize the distributions
			for (int key : graphletsValues.keySet()) {
				valueGraph = graphletsValues.get(key);
				graphletsValues.replace(key, valueGraph/total);
			}
			
			distributions.add(graphletsValues);
		}
		
		return distributions;
	}
}
