/**
 * 
 */
package epigraph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JProgressBar;

import com.google.common.primitives.Ints;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.MaximumFinder;
import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import inra.ijpb.binary.BinaryImages;
import inra.ijpb.binary.conncomp.*;
import inra.ijpb.label.*;
import inra.ijpb.morphology.MinimaAndMaxima3D;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Strel3D;
import inra.ijpb.morphology.strel.SquareStrel;
import net.coobird.thumbnailator.Thumbnails;

/**
 * 
 * @author Pablo Vicente-Munuera
 *
 */
public class GraphletImage extends BasicGraphletImage {

	public static int CIRCLE_SHAPE = 0;
	public static int SQUARE_SHAPE = 1;

	public static final int NUMRANDOMVORONOI = 20;

	// Hexagonal reference
	private BasicGraphlets hexagonRefInt;

	// Random voronoi references
	// TODO: Get out from this class the random voronoi references
	private BasicGraphlets[] randomVoronoiValidCells_4Ref;
	private BasicGraphlets[] randomVoronoiValidCells_5Ref;

	// These are the graphlets we won't use on these configurations
	private static int[] totalParcialGraphlets = { 8, 14, 22, 23, 36, 37, 38, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58,
			62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72 };
	private static int[] totalGraphlets = { 49, 50, 62, 63, 64, 54, 55, 68, 69 };
	private static int[] basicGraphlets = { 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34,
			35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61,
			62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72 };
	private static int[] basicParcialGraphlets = { 8, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
			30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56,
			57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72 };

	private ImagePlus raw_img;
	private ImagePlus l_img;
	private ArrayList<EpiCell> cells;
	private int[][] adjacencyMatrix;
	private Orca orcaProgram;

	/**
	 * @param img
	 *            image
	 */
	public GraphletImage(ImagePlus img) {
		super();
		this.labelName = img.getFileInfo().url;

		int[][] hexagonGraphlets = { { 6, 18, 9, 6, 54, 54, 6, 2, 0, 12, 24, 12, 6, 6, 0, 162, 162, 81, 18, 36, 18, 18,
				0, 0, 48, 24, 48, 36, 36, 72, 36, 0, 0, 0, 0, 0, 0, 0, 0, 6, 12, 6, 6, 12, 3, 12, 12, 12, 24, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 12, 12, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
		this.hexagonRefInt = new BasicGraphlets(hexagonGraphlets);

		this.randomVoronoiValidCells_4Ref = new BasicGraphlets[NUMRANDOMVORONOI];
		this.randomVoronoiValidCells_5Ref = new BasicGraphlets[NUMRANDOMVORONOI];
		// TODO: Get out from this class the random voronoi references
		for (int i = 1; i <= NUMRANDOMVORONOI; i++) {
			// System.out.println("graphletsReferences/randomVoronoi_" +
			// Integer.toString(i) + ".ndump2");
			URL fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Basic/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_4Ref[i - 1] = new BasicGraphlets(fileUrl);

			fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Total/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_5Ref[i - 1] = new BasicGraphlets(fileUrl);
		}

		// END TODO
		
		preprocessImage(img);
	}

	public void preprocessImage(ImagePlus img) {
		/* Preprocessing */
		this.cells = new ArrayList<EpiCell>();

		if (!img.getChannelProcessor().isBinary()) {
			System.out.println("No binary image, improving...");
			img.getChannelProcessor().autoThreshold();
		}

		int[][] pixels = img.getChannelProcessor().getIntArray();
		int whitePixels = 0;
		int blackPixels = 0;
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				if (pixels[i][j] == 0)
					blackPixels++;
				else
					whitePixels++;
			}
		}

		if (blackPixels > whitePixels) {
			img.getChannelProcessor().invert();
		}

		ImageProcessor imp = new ByteProcessor(img.getChannelProcessor(), true);
		this.raw_img = new ImagePlus("", imp);

		// Labelling image
		ByteProcessor btp = LabelImages.createLabelImage(img.getChannelProcessor());
		FloodFillComponentsLabeling ffcl = new FloodFillComponentsLabeling(4);// define
																				// connectivity
		img.setProcessor(ffcl.computeLabels(img.getChannelProcessor()));
		this.l_img = new ImagePlus("", img.getChannelProcessor());

		// get unique labels from labelled imageplus
		int[] labelunique = LabelImages.findAllLabels(img);

		// get image in a matrix of labels
		int[][] matrixImg = img.getChannelProcessor().getIntArray();

		// Create epicells
		for (int indexEpiCell = 1; indexEpiCell < labelunique.length + 1; indexEpiCell++) {
			this.cells.add(new EpiCell(indexEpiCell));
		}

		// Add pixel to each epicell
		int W = img.getWidth();
		int H = img.getHeight();
		int valuePxl;
		for (int indexImgX = 0; indexImgX < H; indexImgX++) {
			for (int indexImgY = 0; indexImgY < W; indexImgY++) {
				valuePxl = matrixImg[indexImgX][indexImgY];
				if (valuePxl != 0) {
					this.cells.get(valuePxl - 1).addPixel(indexImgX, indexImgY);
					if (indexImgX == 0 || indexImgX == H - 1 || indexImgY == 0 || indexImgY == W - 1)
						this.cells.get(valuePxl - 1).setValid_cell(false);
				}
			}
		}

		// Create adjacency matrix from the found cells
		this.adjacencyMatrix = new int[labelunique.length][labelunique.length];
	}

	public ArrayList<String> testNeighbours(ImagePlus img, int selectedShape, int radiusOfShape, ImagePlus imgToShow, JProgressBar progressBar) {
		for (int indexEpiCell = 0; indexEpiCell < this.cells.size(); indexEpiCell++){
			progressBar.setValue(indexEpiCell*40/this.cells.size());
			createNeighbourhood(indexEpiCell, selectedShape, radiusOfShape);
		}

		progressBar.setValue(40);
		
		float percentageOfSquares = 0;
		float percentageOfPentagons = 0;
		this.percentageOfHexagons = 0;
		float percentageOfHeptagons = 0;
		float percentageOfOctogons = 0;
		int validCells = 0;
		// int percentageOfHexagonsOriginal = 0;
		int[][] actualPixels;

		ColorProcessor colorImgToShow = img.getChannelProcessor().convertToColorProcessor();
		Color colorOfCell;
		for (int i = 0; i < this.cells.size(); i++) {
			colorOfCell = Color.WHITE;
			if (this.cells.get(i).isValid_cell()) {
				switch (this.cells.get(i).getNeighbours().size()) {
				case 4:
					percentageOfSquares++;
					colorOfCell = new Color((int) 255, (int) (0.4*255), (int) (0*255));
					break;
				case 5:
					percentageOfPentagons++;
					colorOfCell = Color.green;
					break;
				case 6:
					percentageOfHexagons++;
					colorOfCell = new Color(0, (int) (0.4*255), (int) (1*255));
					break;
				case 7:
					percentageOfHeptagons++;
					colorOfCell = new Color((int) (0.6*255), 0*255, 1*255);
					break;
				case 8:
					percentageOfOctogons++;
					colorOfCell = new Color(0, (int) (0.4*255), (int) (0.6*255));
					break;
				}
				validCells++;
			} else {
				colorOfCell = Color.BLACK;
			}

			actualPixels = this.cells.get(i).getPixels();
			int color;
			for (int numPixel = 0; numPixel < actualPixels.length; numPixel++) {
				color = (int) ((colorOfCell.getRed() & 0xFF) << 16 | (colorOfCell.getGreen() & 0xFF) << 8
						| (colorOfCell.getBlue() & 0xFF));
				colorImgToShow.set(actualPixels[numPixel][0], actualPixels[numPixel][1], color);
			}
		}
		
		progressBar.setValue(60);
		
		percentageOfSquares /= validCells;
		percentageOfPentagons /= validCells;
		this.percentageOfHexagons /= validCells;
		percentageOfHeptagons /= validCells;
		percentageOfOctogons /= validCells;
		
		float percentageOfHexagonsToShow = this.percentageOfHexagons;
		this.percentageOfHexagons = this.percentageOfHexagons * 100;
		
		ArrayList<String> percentajesList = new ArrayList<String>();

		if (imgToShow != null){
			imgToShow.setProcessor(colorImgToShow);
			BufferedImage thumbnail = null;
			try {
				thumbnail = Thumbnails.of(colorImgToShow.getBufferedImage()).height(ImageProcessingWindow.CANVAS_SIZE)
						.width(ImageProcessingWindow.CANVAS_SIZE).asBufferedImage();
				imgToShow.setImage(thumbnail);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			NumberFormat defaultFormat = NumberFormat.getPercentInstance();
			defaultFormat.setMaximumFractionDigits(2);
			
			
			
			percentajesList.add(defaultFormat.format(percentageOfSquares));
			percentajesList.add(defaultFormat.format(percentageOfPentagons));
			percentajesList.add(defaultFormat.format(percentageOfHexagonsToShow));
			percentajesList.add(defaultFormat.format(percentageOfHeptagons));
			percentajesList.add(defaultFormat.format(percentageOfOctogons));

			return percentajesList; /*"Tested polygon distribution: Squares " + defaultFormat.format(percentageOfSquares) + ", Pentagons "
					+ defaultFormat.format(percentageOfPentagons) + ", Hexagons "
					+ defaultFormat.format(this.percentageOfHexagons) + ", Heptagons "
					+ defaultFormat.format(percentageOfHeptagons) + ", Octogons "
					+ defaultFormat.format(percentageOfOctogons);*/
		}
		return percentajesList;
	}

	public void runGraphlets(ImagePlus img, int selectedShape, int radiusOfShape, int modeNumGraphlets, JProgressBar progressBar) {
		if (this.percentageOfHexagons == -1){
			testNeighbours(img, selectedShape, radiusOfShape, null, progressBar);
		}
		
		progressBar.setValue(70);

		this.orcaProgram = new Orca(this.adjacencyMatrix);

		int[][] graphlets = this.orcaProgram.getOrbit();
		
		progressBar.setValue(75);
		
		this.orcaProgram = null;
		
		for (int i = 0; i < graphlets.length; i++) {
			this.cells.get(i).setGraphlets(graphlets[i]);
		}
		
		// int numValidCells = 0;
		for (int indexEpiCell = 0; indexEpiCell < this.cells.size(); indexEpiCell++) {
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
		for (EpiCell cell : this.cells) {
			if (cell.isValid_cell_5()) {
				actualGraphlets = cell.getGraphletsInteger(graphletsWeDontWant);
				graphletsFinal.add(actualGraphlets);
			}
		}
		
		//Percentage 70
		progressBar.setValue(80);

		this.distanceGDDH = calculateGDD(graphletsFinal, this.hexagonRefInt.getGraphletsInteger(graphletsWeDontWant));

		//Percentage 85
		progressBar.setValue(90);
		
		float[] distanceGDDRVArray = new float[NUMRANDOMVORONOI];
		for (int i = 0; i < NUMRANDOMVORONOI; i++) {
			if (validCells5Graphlets)
				distanceGDDRVArray[i] = calculateGDD(graphletsFinal,
						this.randomVoronoiValidCells_5Ref[i].getGraphletsInteger(graphletsWeDontWant));
			else
				distanceGDDRVArray[i] = calculateGDD(graphletsFinal,
						this.randomVoronoiValidCells_4Ref[i].getGraphletsInteger(graphletsWeDontWant));

		}
		this.distanceGDDRV = mean(distanceGDDRVArray);
		
		//Percentage 100
		progressBar.setValue(100);
	}

	/**
	 * 
	 * @param shape
	 * @param dimensionOfShape
	 * @param perimeterPixelX
	 * @param perimeterPixelY
	 * @return
	 */
	private ImageProcessor generateMask(int shape, int dimensionOfShape, int[] perimeterPixelX, int[] perimeterPixelY) {
		// Create the perimeter of the cell
		ImageProcessor img = new ByteProcessor(this.raw_img.getWidth(), this.raw_img.getHeight());
		for (int numPixel = 0; numPixel < perimeterPixelX.length; numPixel++)
			img.set(perimeterPixelX[numPixel], perimeterPixelY[numPixel], 255);

		switch (shape) {
		case 0:// CIRCLE_SHAPE
			new RankFilters().rank(img, dimensionOfShape, RankFilters.MAX);
			break;
		case 1: // SQUARE_SHAPE
			SquareStrel sq = SquareStrel.fromRadius(dimensionOfShape);
			img = sq.dilation(img);
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
	private void createNeighbourhood(int idEpiCell, int shape, int dimensionOfShape) {
		EpiCell cell = this.cells.get(idEpiCell);
		ImageProcessor imgProc = generateMask(shape, dimensionOfShape, cell.getPixelsX(), cell.getPixelsY());
		
		HashSet<Integer> neighbours = new HashSet<Integer>();
		int labelNeigh;
		for (int x = 0; x < this.l_img.getWidth(); x++) {
			for (int y = 0; y < this.l_img.getHeight(); y++) {
				if (imgProc.get(x, y) == 255) {
					if (this.l_img.getChannelProcessor().get(x, y) != 0
							&& this.l_img.getChannelProcessor().get(x, y) != idEpiCell + 1) {
						labelNeigh = this.l_img.getChannelProcessor().get(x, y) - 1;
						neighbours.add(labelNeigh);
						if (this.cells.get(idEpiCell).isValid_cell() || this.cells.get(labelNeigh).isValid_cell()) { // Only
																														// valid
																														// cells'
																														// relationships
							this.adjacencyMatrix[idEpiCell][labelNeigh] = 1;
							this.adjacencyMatrix[labelNeigh][idEpiCell] = 1;
						}
					}

				}
			}
		}
		// System.out.println(neighbours);
		cell.setNeighbours(neighbours);
	}

	/**
	 * 
	 * @param indexEpiCell
	 * @param length
	 * @return
	 */
	private boolean allValidCellsWithinAGivenLength(int indexEpiCell, int length) {
		if (this.cells.get(indexEpiCell).isValid_cell()) {
			if (length > 1) {
				HashSet<Integer> neighbours = this.cells.get(indexEpiCell).getNeighbours();
				Iterator<Integer> itNeigh = neighbours.iterator();
				int neighbourActual = -1;
				while (itNeigh.hasNext()) {
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
	 *            graphlets of the image
	 * @param distanceReference
	 *            graphlets of the reference image
	 * @return the distance calculated between the two params
	 */
	public float calculateGDD(ArrayList<Integer[]> graphletsFinal, ArrayList<Integer[]> distanceReference) {
		ArrayList<HashMap<Integer, Float>> graphletFreqRef = scaleGraphletDists(distanceReference);
		ArrayList<HashMap<Integer, Float>> graphletFreqImage = scaleGraphletDists(graphletsFinal);

		float[] orbitDist = new float[this.cells.get(0).getGraphlets().length];

		for (int i = 0; i < BasicGraphlets.TOTALGRAPHLETS; i++) {
			HashMap<Integer, Float> values1 = graphletFreqRef.get(i);
			HashMap<Integer, Float> values2 = graphletFreqImage.get(i);

			// Compute the distance among the orbits
			float sumDistances = (float) 0.0;
			HashSet<Integer> allDegrees = new HashSet<Integer>();
			allDegrees.addAll(values1.keySet());
			allDegrees.addAll(values2.keySet());

			for (Integer degree : allDegrees) {
				Float score1 = values1.getOrDefault(degree, (float) 0);
				Float score2 = values2.getOrDefault(degree, (float) 0);

				sumDistances += Math.pow((score1 - score2), 2);
			}
			orbitDist[i] = (float) ((1 / Math.sqrt(2)) * Math.sqrt(sumDistances));
		}

		float gdd_distance = mean(orbitDist);
		// gddg_distance

		return gdd_distance;
	}

	/**
	 * 
	 * @param m
	 *            array with the numbers
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
	 * 
	 * @param signatures
	 *            matrix with the graphlets
	 * @return
	 */
	private ArrayList<HashMap<Integer, Float>> scaleGraphletDists(ArrayList<Integer[]> signatures) {
		ArrayList<HashMap<Integer, Float>> distributions = new ArrayList<HashMap<Integer, Float>>();

		HashMap<Integer, Float> graphletsValues;
		Float actualValue;
		for (int numGraphlet = 0; numGraphlet < BasicGraphlets.TOTALGRAPHLETS; numGraphlet++) {
			graphletsValues = new HashMap<Integer, Float>();

			for (int numNode = 0; numNode < signatures.size(); numNode++) {
				actualValue = graphletsValues.putIfAbsent(signatures.get(numNode)[numGraphlet], (float) 1);
				if (actualValue != null) {
					graphletsValues.put(signatures.get(numNode)[numGraphlet], actualValue + 1);
				}
			}
			graphletsValues.remove(0);

			// Scale the distribution values for GDD agreement
			float total = 0;
			float valueGraph;
			for (int key : graphletsValues.keySet()) {
				valueGraph = graphletsValues.get(key);
				graphletsValues.replace(key, valueGraph / key);
				total += valueGraph / key;
			}

			// Normalize the distributions
			for (int key : graphletsValues.keySet()) {
				valueGraph = graphletsValues.get(key);
				graphletsValues.replace(key, valueGraph / total);
			}

			distributions.add(graphletsValues);
		}

		return distributions;
	}

	public int addCellToSelected(int x, int y) {
		int pixelsIsSelected;
		for (int numCell = 0; numCell < this.cells.size(); numCell++) {
			pixelsIsSelected = this.cells.get(numCell).searchSelectedPixel(x, y);
			if (pixelsIsSelected != -1) {
				return pixelsIsSelected;
			}
		}
		return -1;
	}
}
