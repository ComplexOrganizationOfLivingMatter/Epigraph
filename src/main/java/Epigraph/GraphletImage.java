/**
 * 
 */
package Epigraph;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import ij.ImagePlus;
import ij.plugin.filter.MaximumFinder;
import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;


/**
 * 
 * @author Pablo Vicente-Munuera
 *
 */
public class GraphletImage {
	private ImagePlus raw_img;
	private ImagePlus l_img;
	private ArrayList<EpiCell> cells;
	private int[][] adjacencyMatrix;
	private Orca orcaProgram;
	
	public static int CIRCLE_SHAPE = 0;
	public static int SQUARE_SHAPE = 1;

	//Hexagonal reference
	private static BasicGraphlets hexagonRefInt;
	
	//Random voronoi references //TODO: Get out from this class the random voronoi references
	private BasicGraphlets[] randomVoronoiValidCells_4Ref;
	private BasicGraphlets[] randomVoronoiValidCells_5Ref;
	
	// These are the graphlets we won't use on these configurations
	private static int[] totalParcialGraphlets = {8, 14, 22, 23, 36, 37, 38, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72};
	private static int[] totalGraphlets = {49, 50, 62, 63, 64, 54, 55, 68, 69};
	private static int[] basicGraphlets = {16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72};
	private static int[] basicParcialGraphlets = {8, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72};
		
	/**
	 * @param img image
	 */
	public GraphletImage(ImagePlus img) {
		super();
		//TODO: hardcoded variables, when interfaces come, they should be removed
		int radiusOfShape = 3;
		int selectedShape = CIRCLE_SHAPE;
		int modeNumGraphlets = 0;
		
		int[][] hexagonGraphlets = {{6, 18, 9, 6, 54, 54, 6, 2, 0, 12, 24, 12, 6, 6, 0, 162, 162, 81, 18, 36, 18, 18, 0, 0, 48, 24, 48, 36, 36, 72, 36, 0, 0, 0, 0, 0, 0, 0, 0, 6, 12, 6, 6, 12, 3, 12, 12, 12, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 12, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
		this.hexagonRefInt = new BasicGraphlets(hexagonGraphlets);
		
		this.randomVoronoiValidCells_4Ref = new BasicGraphlets[20];
		this.randomVoronoiValidCells_5Ref = new BasicGraphlets[20];
		//TODO: Get out from this class the random voronoi references
		for (int i = 1; i <= 20; i++){
			//System.out.println("graphletsReferences/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			URL fileUrl = Epigraph.class.getResource("graphletsReferences/Basic/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_4Ref[i-1] = new BasicGraphlets(fileUrl.getFile());

			fileUrl = Epigraph.class.getResource("graphletsReferences/Total/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_5Ref[i-1] = new BasicGraphlets(fileUrl.getFile());
		}
		//END TODO
		
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
		//img.show();
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
		
		//int numValidCells = 0;
		for (indexEpiCell = 0; indexEpiCell < this.cells.size(); indexEpiCell++){
			this.cells.get(indexEpiCell).setValid_cell_4(allValidCellsWithinAGivenLength(indexEpiCell, 4));
			this.cells.get(indexEpiCell).setValid_cell_5(allValidCellsWithinAGivenLength(indexEpiCell, 5));
		}
		
		
		int[] graphletsWeDontWant;
		boolean validCells5Graphlets = true;
		switch (modeNumGraphlets) {
		case 0:
			graphletsWeDontWant = totalGraphlets;
			break;
		case 1:
			graphletsWeDontWant = totalParcialGraphlets;
			break;
		case 2:
			graphletsWeDontWant = basicGraphlets;
			validCells5Graphlets = false;
			break;
		case 3:
			graphletsWeDontWant = basicParcialGraphlets;
			validCells5Graphlets = false;
			break;

		default:
			graphletsWeDontWant = totalGraphlets;
			break;
		}
		
		Arrays.sort(graphletsWeDontWant);
		
		ArrayList<Integer[]> graphletsFinal = new ArrayList<Integer[]>();
		Integer[] actualGraphlets;
		for ( EpiCell cell : this.cells) {
			if (cell.isValid_cell_5()){
				actualGraphlets = cell.getGraphletsInteger(graphletsWeDontWant);
				graphletsFinal.add(actualGraphlets);
			}
		}
		
		float distanceGDDH = calculateGDDH(graphletsFinal, this.hexagonRefInt.getGraphletsInteger(graphletsWeDontWant));
		System.out.println(distanceGDDH);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param label
	 * @return
	 */
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
	
	/**
	 * 
	 * @param shape
	 * @param dimensionOfShape
	 * @param perimeterPixelX
	 * @param perimeterPixelY
	 * @return
	 */
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
	
	/**
	 * 
	 * @param idEpiCell
	 * @param shape
	 * @param dimensionOfShape
	 */
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
	 * @param graphletsFinal
	 * @param vectorReferenceInt
	 * @return
	 */
	public float calculateGDD(ArrayList<Integer[]> graphletsFinal, Integer[] vectorReferenceInt){
		ArrayList<Integer[]> distanceReference = new ArrayList<Integer[]>();
		distanceReference.add(vectorReferenceInt);
		ArrayList<HashMap<Integer, Float>> graphletFreqRef = scaleGraphletDists(distanceReference);
		ArrayList<HashMap<Integer, Float>> graphletFreqImage = scaleGraphletDists(graphletsFinal);
		
		float[] orbitDist = new float[this.cells.get(0).getGraphlets().length];
		
		for (int i = 0; i < BasicGraphlets.TOTALGRAPHLETS; i++){
			HashMap<Integer, Float> values1 = graphletFreqRef.get(i);
			HashMap<Integer, Float> values2 = graphletFreqImage.get(i);
			
			//Compute the distance among the orbits
			float sumDistances = (float) 0.0;
			HashSet<Integer> allDegrees = new HashSet<Integer>();
			allDegrees.addAll(values1.keySet());
			allDegrees.addAll(values2.keySet());
			
			for (Integer degree : allDegrees){
				Float score1 = values1.getOrDefault(degree, (float) 0);
				Float score2 = values2.getOrDefault(degree, (float) 0);
				
				sumDistances += Math.pow((score1 - score2), 2);
			}
			orbitDist[i] = (float) ((1/Math.sqrt(2)) * Math.sqrt(sumDistances));
		}
		
		float gdd_distance = mean(orbitDist);
		//gddg_distance
		
		return gdd_distance;
	}
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	private float mean(float[] m) {
		float sum = 0;
		for (int i = 0; i < m.length; i++) {
			sum += m[i];
		}
		return sum / m.length;
	}

	/**
	 * Function to compute the graphlet counts from ndump2 files
	 * @param graphletsImage
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
	private ArrayList<HashMap<Integer,Float>> scaleGraphletDists(ArrayList<Integer[]> signatures){
		ArrayList<HashMap<Integer, Float>> distributions = new ArrayList<HashMap<Integer, Float>>();
		
		HashMap<Integer, Float> graphletsValues;
		Float actualValue;
		for (int numGraphlet = 0; numGraphlet < BasicGraphlets.TOTALGRAPHLETS; numGraphlet++){
			graphletsValues = new HashMap<Integer, Float>();
			
			for (int numNode = 0; numNode < signatures.size(); numNode++){
				actualValue = graphletsValues.putIfAbsent(signatures.get(numNode)[numGraphlet], (float) 1);
				if (actualValue != null){
					graphletsValues.put(signatures.get(numNode)[numGraphlet], actualValue + 1);
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
