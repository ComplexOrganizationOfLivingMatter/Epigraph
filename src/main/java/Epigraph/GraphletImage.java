/**
 * 
 */
package epigraph;

import java.awt.Color;
import java.awt.Point;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;

import epigraph.GUI.CustomElements.CustomCanvas;
import epigraph.GUI.CustomElements.ImageOverlay;
import epigraph.Statistics.StatisticalComparison;
import epigraph.Statistics.Utils;
import fiji.util.gui.OverlayedImageCanvas;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.RankFilters;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.binary.conncomp.FloodFillComponentsLabeling;
import inra.ijpb.label.LabelImages;
import inra.ijpb.morphology.strel.SquareStrel;

/**
 * Calculate polygon distribution, graphlets and other useful information.
 * 
 * @author Pablo Vicente-Munuera
 */
public class GraphletImage extends BasicGraphletImage implements Cloneable {
	

	/**
	 * Nomenclature of the graphlets we'll be using
	 */
	public static String[] KIND_OF_GRAPHLETS = { " 29-motifs"," 17-motifs", " 10-motifs", " 7-motifs" };
	/**
	 * Circle shape of the mask
	 */
	public static int CIRCLE_SHAPE = 0;
	/**
	 * Square shape of the mask
	 */
	public static int SQUARE_SHAPE = 1;

	/**
	 * Number of random voronoi
	 */
	public static final int NUMRANDOMVORONOI = 20;

	// Hexagonal reference
	private BasicGraphlet hexagonRefInt;

	// Random voronoi references
	// TODO: Get out from this class the random voronoi references
	private BasicGraphlet[] randomVoronoiValidCells_4Ref;
	private BasicGraphlet[] randomVoronoiValidCells_5Ref;
	
	private BasicGraphlet[] voronoi5ValidCells_4Ref;
	private BasicGraphlet[] voronoi5ValidCells_5Ref;

	// These are the graphlets we won't use on these configurations
	private static int[] totalParcialGraphlets = { 8, 14, 22, 23, 36, 37, 38, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58,
			62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72 };
	private static int[] totalGraphlets = { 49, 50, 62, 63, 64, 54, 55, 68, 69 };
	private static int[] basicGraphlets = { 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34,
			35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61,
			62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72 };
	private static int[] basicParcialGraphlets = { 8, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
			30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56,
			57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72 };

	public static int MINCELLS = 15;

	private ImagePlus raw_img;
	private ImagePlus l_img;

	private ArrayList<EpiCell> cells;

	private int[][] adjacencyMatrix;
	private Orca orcaProgram;

	private ImagePlus imageWithLabels;

	private ImagePlus neighbourImage;
	private boolean reDoTheComputation;
	private boolean invalidRegionChanged;
	
	// for internal use
	private int[] graphletsWeDontWant;
  boolean validCells5Graphlets;

	private ArrayList<ArrayList<String>> percentagesList;
	private boolean modeNumGraphletsToCheck; 
	
	private float[] orbitDist;
    private float[][] orbitsWeights;

	/**
	 * Constructor
	 * 
	 * @param img
	 *            image
	 */
	public GraphletImage(ImagePlus img) {
		super();
		this.labelName = img.getFileInfo().url;
		this.raw_img = img;

		// Initialize the reference Hexagons and Random Voronoi
		int[][] hexagonGraphlets = { { 6, 18, 9, 6, 54, 54, 6, 2, 0, 12, 24, 12, 6, 6, 0, 162, 162, 81, 18, 36, 18, 18,
				0, 0, 48, 24, 48, 36, 36, 72, 36, 0, 0, 0, 0, 0, 0, 0, 0, 6, 12, 6, 6, 12, 3, 12, 12, 12, 24, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 12, 12, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
		this.hexagonRefInt = new BasicGraphlet(hexagonGraphlets);

		this.randomVoronoiValidCells_4Ref = new BasicGraphlet[NUMRANDOMVORONOI];
		this.randomVoronoiValidCells_5Ref = new BasicGraphlet[NUMRANDOMVORONOI];
		this.voronoi5ValidCells_4Ref = new BasicGraphlet[NUMRANDOMVORONOI];
		this.voronoi5ValidCells_5Ref = new BasicGraphlet[NUMRANDOMVORONOI];
		
		// TODO: Get out from this class the random voronoi references
		for (int i = 1; i <= NUMRANDOMVORONOI; i++) {
			URL fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Basic/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_4Ref[i - 1] = new BasicGraphlet(fileUrl);

			fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Total/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_5Ref[i - 1] = new BasicGraphlet(fileUrl);
			
			fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Basic/voronoi5_" + Integer.toString(i) + ".ndump2");
			this.voronoi5ValidCells_4Ref[i - 1] = new BasicGraphlet(fileUrl);

			fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Total/voronoi5_" + Integer.toString(i) + ".ndump2");
			this.voronoi5ValidCells_5Ref[i - 1] = new BasicGraphlet(fileUrl);
		}
		
		this.invalidRegionChanged = false;
		this.percentageOfSquares=0;
		this.percentageOfPentagons=0;
		this.percentageOfHexagons=0;
		this.percentageOfHeptagons=0;
		this.percentageOfOctogons=0;

		// END TODO
	}
	
	public GraphletImage() {
		super();
		this.labelName = null;
		this.raw_img = null;

		// Initialize the reference Hexagons and Random Voronoi
		int[][] hexagonGraphlets = { { 6, 18, 9, 6, 54, 54, 6, 2, 0, 12, 24, 12, 6, 6, 0, 162, 162, 81, 18, 36, 18, 18,
				0, 0, 48, 24, 48, 36, 36, 72, 36, 0, 0, 0, 0, 0, 0, 0, 0, 6, 12, 6, 6, 12, 3, 12, 12, 12, 24, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 12, 12, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
		this.hexagonRefInt = new BasicGraphlet(hexagonGraphlets);

		this.randomVoronoiValidCells_4Ref = new BasicGraphlet[NUMRANDOMVORONOI];
		this.randomVoronoiValidCells_5Ref = new BasicGraphlet[NUMRANDOMVORONOI];
		this.voronoi5ValidCells_4Ref = new BasicGraphlet[NUMRANDOMVORONOI];
		this.voronoi5ValidCells_5Ref = new BasicGraphlet[NUMRANDOMVORONOI];
		
		// TODO: Get out from this class the random voronoi references
		for (int i = 1; i <= NUMRANDOMVORONOI; i++) {
			URL fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Basic/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_4Ref[i - 1] = new BasicGraphlet(fileUrl);

			fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Total/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_5Ref[i - 1] = new BasicGraphlet(fileUrl);
			
			fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Basic/voronoi5_" + Integer.toString(i) + ".ndump2");
			this.voronoi5ValidCells_4Ref[i - 1] = new BasicGraphlet(fileUrl);

			fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Total/voronoi5_" + Integer.toString(i) + ".ndump2");
			this.voronoi5ValidCells_5Ref[i - 1] = new BasicGraphlet(fileUrl);
		}
		
		this.invalidRegionChanged = false;
		this.percentageOfSquares=0;
		this.percentageOfPentagons=0;
		this.percentageOfHexagons=0;
		this.percentageOfHeptagons=0;
		this.percentageOfOctogons=0;

		// END TODO
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	/**
	 * @return the label image
	 */
	public ImagePlus getLabelledImage() {
		return l_img;
	}

	/**
	 * @param l_img
	 *            the label image to set
	 */
	public void setLabelledImage(ImagePlus l_img) {
		this.l_img = l_img;
	}

	/**
	 * @return the imageWithLabels
	 */
	public ImagePlus getImageWithLabels() {
		return imageWithLabels;
	}

	/**
	 * @param imageWithLabels
	 *            the imageWithLabels to set
	 */
	public void setImageWithLabels(ImagePlus imageWithLabels) {
		this.imageWithLabels = imageWithLabels;
	}

	/**
	 * @return the neighbourImage
	 */
	public ImagePlus getNeighbourImage() {
		return neighbourImage;
	}

	/**
	 * @param neighbourImage
	 *            the neighbourImage to set
	 */
	public void setNeighbourImage(ImagePlus neighbourImage) {
		this.neighbourImage = neighbourImage;
	}

	/**
	 * @return the adjacencyMatrix
	 */
	public int[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	/**
	 * @param adjacencyMatrix the adjacencyMatrix to set
	 */
	public void setAdjacencyMatrix(int[][] adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}
	
	/**
	 * Preprocess image involving binarizing an image, inverting if is the case
	 * and label the image. Furthermore, it add several no valid cells
	 * correspondent to the borders and initialize the adjacency matrix
	 * 
	 * @param img
	 *            image to preprocess
	 * @param connectivity
	 *            kind of connectiviy (4 or 8)
	 * @param progressBar
	 *            to update the progress bar
	 * @throws Exception
	 *             min cells exception
	 */
	public void preprocessImage(ImagePlus img, int connectivity, JProgressBar progressBar) throws Exception {
		/* Preprocessing */

		this.shapeOfMask = -1;
		this.radiusOfMask = -1;
		this.cells = new ArrayList<EpiCell>();

		img = img.flatten();
		img.setProcessor(img.getChannelProcessor().convertToByteProcessor());
		if (!img.getChannelProcessor().isBinary()) {
			// System.out.println("No binary image, improving...");
			img.getChannelProcessor().autoThreshold();
		}

		progressBar.setValue(20);
		int[][] pixels = img.getChannelProcessor().getIntArray();
		int whitePixels = 0;
		int blackPixels = 0;
		for (int i = 0; i < img.getWidth(); i++) {
			progressBar.setValue(20 + i / img.getWidth() * 20);
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

		ImagePlus imgTemp = new ImagePlus("", img.getChannelProcessor());
		// Labelling image
		ByteProcessor btp = LabelImages.createLabelImage(imgTemp.getChannelProcessor());
		imgTemp.setProcessor(btp);
		progressBar.setValue(50);
		// Define connectivity with 8
		FloodFillComponentsLabeling ffcl = new FloodFillComponentsLabeling(connectivity);
		imgTemp.setProcessor(ffcl.computeLabels(imgTemp.getChannelProcessor()));
		this.l_img = new ImagePlus("", imgTemp.getChannelProcessor());

		// get unique labels from labelled imageplus
		int maxValue = (int) imgTemp.getChannelProcessor().getMax() + 1;
		if (maxValue <= MINCELLS) {
			throw new ExecutionException(new Throwable("Your image may be not right. Very few recognized cells"));
		}

		progressBar.setValue(60);

		// get image in a matrix of labels
		int[][] matrixImg = imgTemp.getChannelProcessor().getIntArray();

		// Create epicells
		for (int indexEpiCell = 1; indexEpiCell < maxValue; indexEpiCell++) {
			this.cells.add(new EpiCell(indexEpiCell));
		}
		progressBar.setValue(70);

		// Add pixel to each epicell
		int W = imgTemp.getWidth();
		int H = imgTemp.getHeight();
		int valuePxl;
		for (int indexImgX = 0; indexImgX < W; indexImgX++) {
			progressBar.setValue(70 + indexImgX * 30 / W);
			for (int indexImgY = 0; indexImgY < H; indexImgY++) {
				valuePxl = matrixImg[indexImgX][indexImgY];
				if (valuePxl != 0) {
					this.cells.get(valuePxl - 1).addPixel(indexImgX, indexImgY);
					if (indexImgX == 0 || indexImgX == W - 1 || indexImgY == 0 || indexImgY == H - 1) {
						this.cells.get(valuePxl - 1).setValid_cell(false);
					}
				}
			}
		}

		// Create adjacency matrix from the found cells
		this.adjacencyMatrix = new int[maxValue - 1][maxValue - 1];
	}

	/**
	 * Calculate the polygon distribution of the image given a radius and shape.
	 * Displays it and the neighbours image.
	 * 
	 * @param selectedShape
	 *            the selected shape of the mask
	 * @param radiusOfShape
	 *            the size radius of the mask
	 * @param imgToShow
	 *            if you want to show the neighbour image
	 * @param progressBar
	 *            to update the progress bar
	 * 
	 * @param selectionMode
	 *            are there any ROIs?
	 * 
	 * @param modeNumGraphlets
	 *            number of graphlets used (total, total partial, ...)
	 * 
	 * @param overlayResult
	 *            overlay of the image that we will paint the neighbour image
	 * @return the polygon distribution
	 */
	public ArrayList<ArrayList<String>> testNeighbours(int selectedShape, int radiusOfShape, ImagePlus imgToShow,
			JProgressBar progressBar, boolean selectionMode, int modeNumGraphlets, ImageOverlay overlayResult) {
		double totalPercentageToReach;
		
		if (imgToShow != null)
			totalPercentageToReach = 0.6;
		else
			totalPercentageToReach = 1;

		this.reDoTheComputation = checkReDoComputation(selectedShape, radiusOfShape, selectionMode, modeNumGraphlets);
		
		if (this.reDoTheComputation) {
			
			this.modeNumGraphletsToCheck = modeNumGraphlets < 2;
			
			percentagesList = new ArrayList<ArrayList<String>>();
			// Neighbours
			for (int indexEpiCell = 0; indexEpiCell < this.cells.size(); indexEpiCell++) {
				progressBar.setValue((int) (indexEpiCell * 50 / this.cells.size() / totalPercentageToReach));
				createNeighbourhood(indexEpiCell, selectedShape, radiusOfShape);
			}

			progressBar.setValue((int) (55 / totalPercentageToReach));

			@SuppressWarnings("unused")
			float percentageOfTrianglesGraphlets = 0;
			float percentageOfSquaresGraphlets = 0;
			float percentageOfPentagonsGraphlets = 0;
			this.percentageOfHexagons = 0;
			float percentageOfHeptagonsGraphlets = 0;
			float percentageOfOctogonsGraphlets = 0;
			@SuppressWarnings("unused")
			float percentageOfNonagonsGraphlets = 0;
			@SuppressWarnings("unused")
			float percentageOfDecagonsGraphlets = 0;
			
			@SuppressWarnings("unused")
			float percentageOfTrianglesRoi = 0;
			float percentageOfSquaresRoi = 0;
			float percentageOfPentagonsRoi = 0;
			float percentageOfHexagonsRoi = 0;
			float percentageOfHeptagonsRoi = 0;
			float percentageOfOctogonsRoi = 0;
			@SuppressWarnings("unused")
			float percentageOfNonagonsRoi = 0;
			@SuppressWarnings("unused")
			float percentageOfDecagonsRoi = 0;
			
			int validCells = 0;
			int roiCells=0;
			// int percentageOfHexagonsGraphletsOriginal = 0;
			int[][] actualPixels;

			// Color the image depending the side of the cell
			ColorProcessor colorImgToShow = this.raw_img.getChannelProcessor().convertToColorProcessor();
			Color colorOfCell;
			for (int i = 0; i < this.cells.size(); i++) {
				this.cells.get(i).setValid_cell_4(allValidCellsWithinAGivenLength(i, 4));
				this.cells.get(i).setValid_cell_5(allValidCellsWithinAGivenLength(i, 5));
				colorOfCell = Color.WHITE;
				if (this.cells.get(i).isValid_cell()) {
					//If it is selection mode we check if the cell is selected
					//Otherwise we enter always.
					if ((this.cells.get(i).isValid_cell_5() || (modeNumGraphlets >= 2 && this.cells.get(i).isValid_cell_4()) ) && (!selectionMode || this.cells.get(i).isSelected())) {
						switch (this.cells.get(i).getNeighbours().size()) {
						case 3:
							percentageOfTrianglesGraphlets++;
							percentageOfTrianglesRoi++;
							break;
						case 4:
							percentageOfSquaresGraphlets++;
							percentageOfSquaresRoi++;
							colorOfCell = new Color(255, 101, 6);
							break;
						case 5:
							percentageOfPentagonsGraphlets++;
							percentageOfPentagonsRoi++;
							colorOfCell = new Color(17, 157, 24);
							break;
						case 6:
							percentageOfHexagons++;
							percentageOfHexagonsRoi++;
							colorOfCell = new Color(52, 102, 249);
							break;
						case 7:
							percentageOfHeptagonsGraphlets++;
							percentageOfHeptagonsRoi++;
							colorOfCell = new Color(119, 5, 116);
							break;
						case 8:
							percentageOfOctogonsGraphlets++;
							percentageOfOctogonsRoi++;
							colorOfCell = new Color(18, 107, 121);
							break;
						case 9:
							percentageOfNonagonsGraphlets++;
							percentageOfNonagonsRoi++;
							break;
						case 10:
							percentageOfDecagonsGraphlets++;
							percentageOfDecagonsRoi++;
							break;
						}
						
						roiCells++;
						validCells++;
					} else {
						if (modeNumGraphlets < 2) {
							this.cells.get(i).setWithinTheRange(selectedCellWithinAGivenLength(i, 5));
						} else {
							this.cells.get(i).setWithinTheRange(selectedCellWithinAGivenLength(i, 4));
						}

						if (!selectionMode || this.cells.get(i).isWithinTheRange()) {
							validCells++;

							switch (this.cells.get(i).getNeighbours().size()) {
							case 3:
								percentageOfTrianglesGraphlets++;
								break;
							case 4:
								percentageOfSquaresGraphlets++;
								colorOfCell = new Color(241,206,185);
								
								
								break;
							case 5:
								percentageOfPentagonsGraphlets++;
								colorOfCell = new Color(201,255,211);
								break;
							case 6:
								percentageOfHexagons++;
								colorOfCell = new Color(196,214,255);
								
								break;
							case 7:
								percentageOfHeptagonsGraphlets++;
								colorOfCell = new Color(212,191,217);
								
								break;
							case 8:
								percentageOfOctogonsGraphlets++;
								colorOfCell = new Color(142,176,154);
								
								break;
							case 9:
								percentageOfNonagonsGraphlets++;
								
								break;
							case 10:
								percentageOfDecagonsGraphlets++;
								break;
							}
						} else {
							colorOfCell = new Color(45, 45, 45);
						}
						
					}
				} else if (this.cells.get(i).isInvalidRegion()) {
					colorOfCell = Color.BLACK;

				} else {
					colorOfCell = new Color(45, 45, 45);
				}

				actualPixels = this.cells.get(i).getPixels();
				
				for (int numPixel = 0; numPixel < actualPixels.length; numPixel++) {
					colorImgToShow.set(actualPixels[numPixel][0], actualPixels[numPixel][1], getIntFromColor(colorOfCell.getRed(), colorOfCell.getGreen(), colorOfCell.getBlue(), colorOfCell.getAlpha()));
					
				}
			}

			percentageOfTrianglesGraphlets /= validCells;
			percentageOfSquaresGraphlets /= validCells;
			percentageOfPentagonsGraphlets /= validCells;
			this.percentageOfHexagons /= validCells;
			percentageOfHeptagonsGraphlets /= validCells;
			percentageOfOctogonsGraphlets /= validCells;
			percentageOfNonagonsGraphlets /= validCells;
			percentageOfDecagonsGraphlets /= validCells;
			
			
			float percentageOfHexagonsGraphletsToShow = this.percentageOfHexagons;
			this.percentageOfHexagons = this.percentageOfHexagons * 100;

			ArrayList<String> percentagesListGraphlets = new ArrayList<String>();
			ArrayList<String> percentagesListRoi = new ArrayList<String>();
			
			// IJ.log(percentageOfTrianglesGraphlets + " " + percentageOfSquaresGraphlets + " " +
			// percentageOfPentagonsGraphlets + " " + percentageOfHexagonsGraphletsToShow + " " +
			// percentageOfHeptagonsGraphlets + " " + percentageOfOctogonsGraphlets + " " +
			// percentageOfNonagonsGraphlets + " " + percentageOfDecagonsGraphlets);

			this.neighbourImage = new ImagePlus("", colorImgToShow);

			progressBar.setValue((int) (60 / totalPercentageToReach));

			if (imgToShow != null) {
				overlayResult.setImage(colorImgToShow);
				((OverlayedImageCanvas) imgToShow.getCanvas()).clearOverlay();
				((OverlayedImageCanvas) imgToShow.getCanvas()).addOverlay(overlayResult);
				((CustomCanvas) imgToShow.getCanvas()).setImageOverlay(overlayResult);
			}

			NumberFormat defaultFormat = NumberFormat.getPercentInstance();
			defaultFormat.setMaximumFractionDigits(2);

			percentagesListGraphlets.add(defaultFormat.format(percentageOfSquaresGraphlets));
			percentagesListGraphlets.add(defaultFormat.format(percentageOfPentagonsGraphlets));
			percentagesListGraphlets.add(defaultFormat.format(percentageOfHexagonsGraphletsToShow));
			percentagesListGraphlets.add(defaultFormat.format(percentageOfHeptagonsGraphlets));
			percentagesListGraphlets.add(defaultFormat.format(percentageOfOctogonsGraphlets));
			
			percentagesList.add(percentagesListGraphlets);
			
			if (selectionMode){
				percentageOfTrianglesRoi /= roiCells;
				percentageOfSquaresRoi /= roiCells;
				percentageOfPentagonsRoi /= roiCells;
				percentageOfHexagonsRoi /= roiCells;
				percentageOfHeptagonsRoi /= roiCells;
				percentageOfOctogonsRoi /= roiCells;
				percentageOfNonagonsRoi /= roiCells;
				percentageOfDecagonsRoi /= roiCells;
				
				percentagesListRoi.add(defaultFormat.format(percentageOfSquaresRoi));
				percentagesListRoi.add(defaultFormat.format(percentageOfPentagonsRoi));
				percentagesListRoi.add(defaultFormat.format(percentageOfHexagonsRoi));
				percentagesListRoi.add(defaultFormat.format(percentageOfHeptagonsRoi));
				percentagesListRoi.add(defaultFormat.format(percentageOfOctogonsRoi));
			
				percentagesList.add(percentagesListRoi);
			
			}
			
			/*
			 * Bugfix: This happen when there's no changes in rois and nothing, but you've performed a previous GDD calculation.
			 * When you click again on Calculate graphlets (right after a TestNeighbours).
			 * Instead of redo that GDD computation, it uses somehow the past results resulting in a non-sense and wrong GDD values.
			 */
			this.distanceGDDH = -1;
		}else{
			progressBar.setValue(20);
		}
		
		
		
		return percentagesList;
	}
	/**
	 * Transform RGBA to int
	 * 
	 * @param Red red channel
	 * @param Green green channel
	 * @param Blue blue channel
	 * @param Alpha alpha channel
	 * @return the integer corresponding to the rgba number
	 */
	public int getIntFromColor(int Red, int Green, int Blue, int Alpha){
	    
		int A = (Alpha << 24) & 0xFF000000;
	    int R = (Red << 16) & 0x00FF0000;
	    int G = (Green << 8) & 0x0000FF00;
	    int B = Blue & 0x000000FF;
	    
	    return A | R | G | B;
	}

	/**
	 * Calculate graphlets with the given configuration
	 * 
	 * @param selectedShape
	 *            the selected shape of the mask
	 * @param radiusOfShape
	 *            the size radius of the mask
	 * @param modeNumGraphlets
	 *            number of graphlets used (total, total partial, ...)
	 * @param progressBar
	 *            to update the progress bar
	 * @param selectionMode
	 *            are there any ROIs?
	 * @param overlay
	 *            of the image that we will paint the neighbour image
	 * @return the polygon distribution
	 */
	public ArrayList<ArrayList<String>> runGraphlets(int selectedShape, int radiusOfShape, int modeNumGraphlets,
			JProgressBar progressBar, boolean selectionMode, ImageOverlay overlay) {
		
		
		ArrayList<ArrayList<String>> polDist = testNeighbours(selectedShape, radiusOfShape, null, progressBar, selectionMode,
				modeNumGraphlets, overlay);
		switch (modeNumGraphlets) {
		case 0:
			graphletsWeDontWant = totalGraphlets;
      validCells5Graphlets = true;
			break;
		case 1:
			graphletsWeDontWant = totalParcialGraphlets;
      validCells5Graphlets = true;
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

		
		if (this.reDoTheComputation || this.distanceGDDH == -1) {

			getAdjacencyMatrix(selectionMode);

			this.orcaProgram = new Orca(this.adjacencyMatrix);

			int[][] graphlets = this.orcaProgram.getOrbit();

			progressBar.setValue(65);

			this.orcaProgram = null;

			for (int i = 0; i < graphlets.length; i++) {
				this.cells.get(i).setGraphlets(graphlets[i]);
			}

			progressBar.setValue(70);
		}

		Arrays.sort(graphletsWeDontWant);

		ArrayList<Integer[]> graphletsFinal = new ArrayList<Integer[]>();
		Integer[] actualGraphlets;
		for (EpiCell cell2 : this.cells) {
			progressBar.setValue((75 + cell2.getId() * 5) / this.cells.size());
			if (validCells5Graphlets) {
				if (cell2.isValid_cell_5() && (!selectionMode || cell2.isSelected())) {
					actualGraphlets = cell2.getGraphletsInteger(graphletsWeDontWant);
					graphletsFinal.add(actualGraphlets);
				}
			} else {
				if (cell2.isValid_cell_4() && (!selectionMode || cell2.isSelected())) {
					actualGraphlets = cell2.getGraphletsInteger(graphletsWeDontWant);
					graphletsFinal.add(actualGraphlets);
				}
			}
		}

		progressBar.setValue(80);

		this.distanceGDDH = calculateGDD(graphletsFinal, this.hexagonRefInt.getGraphletsInteger(graphletsWeDontWant));

		orbitsWeights = new float[3][];
		orbitsWeights[0] = getOrbitDist();
		progressBar.setValue(90);

		float[] distanceGDDRVArray = new float[NUMRANDOMVORONOI];
		float[] distanceGDDV5Array = new float[NUMRANDOMVORONOI];
		for (int i = 0; i < NUMRANDOMVORONOI; i++) {
			if (validCells5Graphlets){
				distanceGDDRVArray[i] = calculateGDD(graphletsFinal,
						this.randomVoronoiValidCells_5Ref[i].getGraphletsInteger(graphletsWeDontWant));
				orbitsWeights[1] = getOrbitDist();
				
				distanceGDDV5Array[i] = calculateGDD(graphletsFinal,
						this.voronoi5ValidCells_5Ref[i].getGraphletsInteger(graphletsWeDontWant));
				orbitsWeights[2] = getOrbitDist();
			} else {
				distanceGDDRVArray[i] = calculateGDD(graphletsFinal,
						this.randomVoronoiValidCells_4Ref[i].getGraphletsInteger(graphletsWeDontWant));
				orbitsWeights[1] = getOrbitDist();
				
				distanceGDDV5Array[i] = calculateGDD(graphletsFinal,
						this.voronoi5ValidCells_4Ref[i].getGraphletsInteger(graphletsWeDontWant));
				orbitsWeights[2] = getOrbitDist();
			}
		}
		this.distanceGDDRV = mean(distanceGDDRVArray);
		this.distanceGDDV5 = mean(distanceGDDV5Array);

		// Percentage 100
		progressBar.setValue(100);

		return polDist;
	}

	/**
	 * @param selectionMode
	 */
	public int[][] getAdjacencyMatrix(boolean selectionMode) {
		HashSet<Integer> neighbours;
		ArrayList<Integer> idsROI = new ArrayList<Integer>();
		EpiCell cell;
		for (int idEpiCell = 0; idEpiCell < this.cells.size(); idEpiCell++) {
			cell = this.cells.get(idEpiCell);
			if (cell.isInvalidRegion() == false) {
				if (!selectionMode || cell.isSelected() || cell.isWithinTheRange()) {
					neighbours = cell.getNeighbours();
					idsROI.add(idEpiCell);
					for (int idNeighbour : neighbours) {
						if (this.cells.get(idEpiCell).isValid_cell()
								|| this.cells.get(idNeighbour).isValid_cell()) {
							// Only valid cells' relationships
							this.adjacencyMatrix[idEpiCell][idNeighbour] = 1;
							// this.adjacencyMatrix[idNeighbour][idEpiCell]
							// = 1;
						}
					}
				}
			}
		}
		// TODO: reduced adjacency matrix
		// int[][] adjacencyMatrixReduced = new
		// int[idsROI.size()][idsROI.size()];
		//
		// for (int i = 0; i < idsROI.size(); i++){
		// adjacencyMatrixReduced[i] = this.adjacencyMatrix[idsROI.get(i)];
		// }
		return adjacencyMatrix;
	}

	/**
	 * Generate a mask expanding the pixels with a selected shape and radius
	 * 
	 * @param selectedShape
	 *            the selected shape of the mask
	 * @param radiusOfShape
	 *            the size radius of the mask
	 * @param perimeterPixelX
	 *            pixels in X of the perimeter
	 * @param perimeterPixelY
	 *            pixels in Y of the perimeter
	 * @return
	 */
	private ImageProcessor generateMask(int selectedShape, int radiusOfShape, int[] perimeterPixelX,
			int[] perimeterPixelY) {
		// Create the perimeter of the cell
		ImageProcessor img = new ByteProcessor(this.raw_img.getWidth(), this.raw_img.getHeight());
		for (int numPixel = 0; numPixel < perimeterPixelX.length; numPixel++)
			img.set(perimeterPixelX[numPixel], perimeterPixelY[numPixel], 255);

		switch (selectedShape) {
		case 0:// CIRCLE_SHAPE
			new RankFilters().rank(img, radiusOfShape, RankFilters.MAX);
			break;
		case 1: // SQUARE_SHAPE
			SquareStrel sq = SquareStrel.fromRadius(radiusOfShape);
			img = sq.dilation(img);
			break;
		}

		this.shapeOfMask = selectedShape;
		this.radiusOfMask = radiusOfShape;

		return img;
	}

	/**
	 * Calculate the neighbours of the cell
	 * 
	 * @param idEpiCell
	 *            id of the cell
	 * @param selectedShape
	 *            the selected shape of the mask
	 * @param radiusOfShape
	 *            the size radius of the mask
	 */
	public void createNeighbourhood(int idEpiCell, int selectedShape, int radiusOfShape) {
		EpiCell cell = this.cells.get(idEpiCell);

		ImageProcessor imgProc = generateMask(selectedShape, radiusOfShape, cell.getPixelsX(), cell.getPixelsY());

		HashSet<Integer> neighbours = new HashSet<Integer>();
		int labelNeigh;
		for (int x = 0; x < this.l_img.getWidth(); x++) {
			for (int y = 0; y < this.l_img.getHeight(); y++) {
				if (imgProc.get(x, y) != 0) {
					if (this.l_img.getChannelProcessor().get(x, y) != 0
							&& this.l_img.getChannelProcessor().get(x, y) != idEpiCell + 1) {
						labelNeigh = this.l_img.getChannelProcessor().get(x, y) - 1;
						neighbours.add(labelNeigh);
						if (cell.isInvalidRegion()) {
							this.cells.get(labelNeigh).setValid_cell(false);
						}
					}

				}
			}
		}
		// System.out.println(neighbours);
		cell.setNeighbours(neighbours);

	}

	/**
	 * Find if there is any no valid cells with a given length.
	 * 
	 * @param indexEpiCell
	 *            id of the cell
	 * @param length
	 *            actual length
	 * @return If is any no valid cells it will return false, otherwise true
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
	 * Search selected cells from the actual cell
	 * 
	 * @param indexEpiCell
	 *            id of the cell
	 * @param length
	 *            actual length
	 * @return true if find any selected cells and all valid cells, false
	 *         otherwise.
	 */
	private boolean selectedCellWithinAGivenLength(int indexEpiCell, int length) {
		if (this.cells.get(indexEpiCell).isValid_cell() == false)
			return false;

		if (!this.cells.get(indexEpiCell).isSelected()) {
			if (length > 1) {
				HashSet<Integer> neighbours = this.cells.get(indexEpiCell).getNeighbours();
				Iterator<Integer> itNeigh = neighbours.iterator();
				int neighbourActual = -1;
				while (itNeigh.hasNext()) {
					neighbourActual = itNeigh.next();
					if (selectedCellWithinAGivenLength(neighbourActual, length - 1))
						return true;
				}
			}
			return false;
		}

		return true;
	}

	/**
	 * Adapted from Yaveroglu et. al. Supplementary Information for: "Revealing
	 * the Hidden Language of Complex Networks"
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

		orbitDist = new float[this.cells.get(0).getGraphlets().length];

		for (int i = 0; i < BasicGraphlet.TOTALGRAPHLETS; i++) {
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
	 * Mean of an array of floats
	 * 
	 * @param m
	 *            array with the numbers
	 * @return the mean of the array of floats
	 */
	private float mean(float[] m) {
		float sum = 0;
		for (int i = 0; i < m.length; i++) {
			sum += m[i];
		}
		return sum / m.length;
	}

	/**
	 * Adapted from Yaveroglu et. al. Supplementary Information for: "Revealing
	 * the Hidden Language of Complex Networks"
	 * 
	 * @param signatures
	 *            matrix with the graphlets
	 * @return the distributions of the graphlets
	 */
	private ArrayList<HashMap<Integer, Float>> scaleGraphletDists(ArrayList<Integer[]> signatures) {
		ArrayList<HashMap<Integer, Float>> distributions = new ArrayList<HashMap<Integer, Float>>();

		HashMap<Integer, Float> graphletsValues;
		Float actualValue;
		for (int numGraphlet = 0; numGraphlet < BasicGraphlet.TOTALGRAPHLETS; numGraphlet++) {
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

	/**
	 * Add cell to selected cells
	 * 
	 * @param labelPixel
	 *            number of the label in the image
	 * @return if the label pixel was correct (i.e no border)
	 */
	public int addCellToSelected(int labelPixel) {
		if (labelPixel != 0) {
			this.cells.get(labelPixel - 1).setSelected(true);
			return 1;
		}
		return -1;
	}

	/**
	 * Add cell to invalid region
	 * 
	 * @param labelPixel
	 *            number of the label in the image
	 * @return if the label pixel was correct (i.e no border)
	 */
	public int addCellToInvalidRegion(int labelPixel) {
		if (labelPixel != 0) {
			this.cells.get(labelPixel - 1).setInvalidRegion(true);
			this.cells.get(labelPixel - 1).setValid_cell(false);
			return 1;
		}
		return -1;
	}

	/**
	 * All cells are now valid and the no valid cells are set as default.
	 */
	public void resetInvalidRegion() {
		this.invalidRegionChanged = true;
		for (int i = 0; i < this.cells.size(); i++) {
			this.cells.get(i).setInvalidRegion(false);
			this.cells.get(i).setValid_cell(true);
		}

		resetValidCells();
	}

	/**
	 * No valid cells are found in the boundaries
	 */
	private void resetValidCells() {
		int W = this.raw_img.getWidth();
		int H = this.raw_img.getHeight();
		int[][] matrixImg = this.l_img.getChannelProcessor().getIntArray();
		int valuePxl;
		for (int indexImgX = 0; indexImgX < W; indexImgX++) {
			for (int indexImgY = 0; indexImgY < H; indexImgY++) {
				if (indexImgX == 0 || indexImgX == W - 1 || indexImgY == 0 || indexImgY == H - 1) {
					valuePxl = matrixImg[indexImgX][indexImgY];
					if (valuePxl != 0) {
						this.cells.get(valuePxl - 1).setValid_cell(false);
					}
				}
			}
		}
	}

	/**
	 * Reset selected cells
	 */
	public void resetSelection() {
		for (int i = 0; i < this.cells.size(); i++)
			this.cells.get(i).setSelected(false);
	}

	/**
	 * Get all the pixels of every cell and return the centroid
	 * 
	 * @return the centroids
	 */
	public ArrayList<int[][]> getCentroids() {
		// TODO Auto-generated method stub
		ArrayList<int[][]> centroids = new ArrayList<int[][]>();
		for (int i = 0; i < this.cells.size(); i++) {
			centroids.add(this.cells.get(i).getCentroid());
		}
		return centroids;
	}

	/**
	 * @return graphlets in an array of string
	 */
	public String[][] getFinalGraphlets(boolean selectionMode) {
	  
		String[][] graphlets = new String[this.cells.size()][BasicGraphlet.TOTALGRAPHLETS + 1];
		int cont = 0;
		int numCell = 0;
		for (EpiCell cell : this.cells) {
			graphlets[cont][0] = Integer.toString(cont + 1);
			numCell = 1;
			boolean validCell = false;
			if (validCells5Graphlets) {
        if (cell.isValid_cell_5() && (!selectionMode || cell.isSelected())) {
          validCell = true;
        }
      } else {
        if (cell.isValid_cell_4() && (!selectionMode || cell.isSelected())) {
          validCell = true;
        }
      }
			
			if (validCell) {
  			for (String graphlet : cell.getGraphletsString(graphletsWeDontWant)) {
  				graphlets[cont][numCell] = graphlet;
  				numCell++;
  			}
  			cont++;
		  }
		}
		
		String[][] graphletsFinal = new String[cont][BasicGraphlet.TOTALGRAPHLETS + 1];
		
		for (int i = 0; i < cont; i++) {
		  graphletsFinal[i] = graphlets[i];
    }
		
		return graphletsFinal;
	}
	
	/**
	 * Get the number of cells used in graphlets calculation
	 * @param modeNumGraphlets the setup
	 * @param selectionMode any cells selected?
	 * @param length which Valid cells should we use? Valid cells of 4' or 5'?
	 * @return the number of cells used
	 */
	public int getTotalNumberOfGraphlets(int modeNumGraphlets, boolean selectionMode, int length){
		
		switch (modeNumGraphlets) {
		case 0:
			graphletsWeDontWant = totalGraphlets;
			break;
		case 1:
			graphletsWeDontWant = totalParcialGraphlets;
			break;
		case 2:
			graphletsWeDontWant = basicGraphlets;
			break;
		case 3:
			graphletsWeDontWant = basicParcialGraphlets;
			break;

		default:
			graphletsWeDontWant = totalGraphlets;
			break;
		}

		int totalGraphlets = 0;
		for (EpiCell cell : this.cells) {
			if (length == 4){
				if (cell.isValid_cell_4() && (!selectionMode || cell.isSelected())){
					for (Integer graphlet : cell.getGraphletsInteger(graphletsWeDontWant)) {
						totalGraphlets += graphlet;
					}
				}
			} else {
				if (cell.isValid_cell_5() && (!selectionMode || cell.isSelected())){
					for (Integer graphlet : cell.getGraphletsInteger(graphletsWeDontWant)) {
						totalGraphlets += graphlet;
					}
				}
			}
			
		}
		return totalGraphlets;
	}

	/**
	 * Get all selected cells
	 * @return all the ids of the selected cells
	 */
	public ArrayList<Integer> getAllSelectedCells() {
		ArrayList<Integer> selectedCells = new ArrayList<Integer>();
		for (EpiCell cell : this.cells)
			if (cell.isSelected())
				selectedCells.add(cell.getId());

		return selectedCells;
	}
	
	/**
	 * We check how many valid cells of a given length exists
	 * We should be careful if there is a low number or if there isn't any
	 *  
	 * @param length the max length of the used graphlets
	 * @param selectionMode is any cell selected?
	 * @return the number of valid cells of length
	 */
	public int calculateNumberOfValidCellForGraphlets(int length, boolean selectionMode){
		int numberOfValidCellsOfLength = 0;
		for (EpiCell cell : this.cells){
			if (length == 4){
				if (cell.isValid_cell_4() && (!selectionMode || cell.isSelected()))
					numberOfValidCellsOfLength++;
			} else {
				if (cell.isValid_cell_5() && (!selectionMode || cell.isSelected()))
					numberOfValidCellsOfLength++;
			}
		}
		return numberOfValidCellsOfLength;
	}

	/**
	 * Check if we have to redo the computation of neighbours
	 * @param selectedShape shape of mask
	 * @param radiusOfShape radius of shape of mask
	 * @param selectionMode if exists any roi
	 * @param modeNumGraphlets 
	 * @return if we have to redo the computation of neighbours
	 */
	private boolean checkReDoComputation(int selectedShape, int radiusOfShape, boolean selectionMode, int modeNumGraphlets) {
		boolean reDoTheComputationAux = false;
		if (this.shapeOfMask != selectedShape || this.radiusOfMask != radiusOfShape
				|| this.isSelectedCells() != selectionMode || this.invalidRegionChanged || this.modeNumGraphletsToCheck != (modeNumGraphlets < 2)) {
			reDoTheComputationAux = true;
		}
		
		this.invalidRegionChanged = false;

		if (selectionMode) {
			ArrayList<Integer> previousSelectedCells = this.getAllSelectedCells();

			RoiManager roiManager = RoiManager.getInstance();
			resetSelection();
			// Check if there is any ROI
			if (roiManager != null && selectionMode) {
				for (Roi r : roiManager.getRoisAsArray()) {
					for (Point point : r) {
						int[] pixelInfo = this.getLabelledImage().getPixel(point.x, point.y);
						this.addCellToSelected(pixelInfo[0]);
					}
				}
			}
			ArrayList<Integer> actualSelectedCells = this.getAllSelectedCells();

			if (!previousSelectedCells.equals(actualSelectedCells))
				reDoTheComputationAux = true;
		}

		this.selectedCells = selectionMode;
		return reDoTheComputationAux;
	}

	/**
	 * @return the orbitDist
	 */
	public final float[] getOrbitDist() {
		return orbitDist;
	}

	/**
	 * @return the orbitsWeights
	 */
	public final float[][] getOrbitsWeights() {
		return orbitsWeights;
	}

	/**
	 * @return the raw_img
	 */
	public ImagePlus getRaw_img() {
		return raw_img;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<EpiCell> getCells() {
		return cells;
	}

	/**
	 * 
	 * @param arrayList
	 */
	public void setCells(ArrayList<EpiCell> cells) {
		this.cells = cells;
	}
}
