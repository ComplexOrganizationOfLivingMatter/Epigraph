/**
 * 
 */
package epigraph;

import java.awt.Color;
import java.awt.Point;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;

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
public class GraphletImage extends BasicGraphletImage {

	/**
	 * Nomenclature of the graphlets we'll be using
	 */
	public static String[] KIND_OF_GRAPHLETS = { "26 Motifs","17 Motifs", "9 Motifs", "7 Motifs" };
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

	public static int MINCELLS = 15;

	private ImagePlus raw_img;
	private ImagePlus l_img;

	private ArrayList<EpiCell> cells;
	private int[][] adjacencyMatrix;
	private Orca orcaProgram;

	private ImagePlus imageWithLabels;

	private ImagePlus neighbourImage;
	private ArrayList<String> percentagesList;
	private boolean reDoTheComputation;
	private boolean invalidRegionChanged;

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
		// TODO: Get out from this class the random voronoi references
		for (int i = 1; i <= NUMRANDOMVORONOI; i++) {
			URL fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Basic/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_4Ref[i - 1] = new BasicGraphlet(fileUrl);

			fileUrl = Epigraph.class.getResource(
					"/epigraph/graphletsReferences/Total/randomVoronoi_" + Integer.toString(i) + ".ndump2");
			this.randomVoronoiValidCells_5Ref[i - 1] = new BasicGraphlet(fileUrl);
		}
		
		this.invalidRegionChanged = false;

		// END TODO
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
	public ArrayList<String> testNeighbours(int selectedShape, int radiusOfShape, ImagePlus imgToShow,
			JProgressBar progressBar, boolean selectionMode, int modeNumGraphlets, ImageOverlay overlayResult) {
		double totalPercentageToReach;
		if (imgToShow != null)
			totalPercentageToReach = 0.6;
		else
			totalPercentageToReach = 1;

		this.reDoTheComputation = checkReDoComputation(selectedShape, radiusOfShape, selectionMode);

		if (this.reDoTheComputation) {
			// Neighbours
			for (int indexEpiCell = 0; indexEpiCell < this.cells.size(); indexEpiCell++) {
				progressBar.setValue((int) (indexEpiCell * 50 / this.cells.size() / totalPercentageToReach));
				createNeighbourhood(indexEpiCell, selectedShape, radiusOfShape);
			}

			progressBar.setValue((int) (55 / totalPercentageToReach));

			@SuppressWarnings("unused")
			float percentageOfTriangles = 0;
			float percentageOfSquares = 0;
			float percentageOfPentagons = 0;
			this.percentageOfHexagons = 0;
			float percentageOfHeptagons = 0;
			float percentageOfOctogons = 0;
			@SuppressWarnings("unused")
			float percentageOfNonagons = 0;
			@SuppressWarnings("unused")
			float percentageOfDecagons = 0;
			int validCells = 0;
			// int percentageOfHexagonsOriginal = 0;
			int[][] actualPixels;

			// Color the image depending the side of the cell
			ColorProcessor colorImgToShow = this.raw_img.getChannelProcessor().convertToColorProcessor();
			Color colorOfCell;
			int color;
			for (int i = 0; i < this.cells.size(); i++) {
				colorOfCell = Color.WHITE;
				if (this.cells.get(i).isValid_cell()) {
					if (!selectionMode || this.cells.get(i).isSelected()) {
						switch (this.cells.get(i).getNeighbours().size()) {
						case 3:
							percentageOfTriangles++;
							break;
						case 4:
							percentageOfSquares++;
							colorOfCell = new Color((int) 255, (int) 101, (int) 6);
							break;
						case 5:
							percentageOfPentagons++;
							colorOfCell = new Color((int) 17, (int) 157, (int) 24);
							break;
						case 6:
							percentageOfHexagons++;
							colorOfCell = new Color(52, (int) 102, (int) 249);
							break;
						case 7:
							percentageOfHeptagons++;
							colorOfCell = new Color((int) 119, 5, 116);
							break;
						case 8:
							percentageOfOctogons++;
							colorOfCell = new Color(18, (int) 107, (int) 121);
							break;
						case 9:
							percentageOfNonagons++;
							break;
						case 10:
							percentageOfDecagons++;
							break;
						}

						validCells++;
					} else if (selectionMode) { // Some cells are selected
						if (modeNumGraphlets < 2) {
							this.cells.get(i).setWithinTheRange(selectedCellWithinAGivenLength(i, 5));
						} else {
							this.cells.get(i).setWithinTheRange(selectedCellWithinAGivenLength(i, 4));
						}

						if (this.cells.get(i).isWithinTheRange()) {
							colorOfCell = Color.GRAY;
							validCells++;

							switch (this.cells.get(i).getNeighbours().size()) {
							case 3:
								percentageOfTriangles++;
								break;
							case 4:
								percentageOfSquares++;
								break;
							case 5:
								percentageOfPentagons++;
								break;
							case 6:
								percentageOfHexagons++;
								break;
							case 7:
								percentageOfHeptagons++;
								break;
							case 8:
								percentageOfOctogons++;
								break;
							case 9:
								percentageOfNonagons++;
								break;
							case 10:
								percentageOfDecagons++;
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
				color = (int) ((colorOfCell.getRed() & 0xFF) << 16 | (colorOfCell.getGreen() & 0xFF) << 8
						| (colorOfCell.getBlue() & 0xFF));
				for (int numPixel = 0; numPixel < actualPixels.length; numPixel++) {
					colorImgToShow.set(actualPixels[numPixel][0], actualPixels[numPixel][1], color);
				}
			}

			percentageOfTriangles /= validCells;
			percentageOfSquares /= validCells;
			percentageOfPentagons /= validCells;
			this.percentageOfHexagons /= validCells;
			percentageOfHeptagons /= validCells;
			percentageOfOctogons /= validCells;
			percentageOfNonagons /= validCells;
			percentageOfDecagons /= validCells;

			float percentageOfHexagonsToShow = this.percentageOfHexagons;
			this.percentageOfHexagons = this.percentageOfHexagons * 100;

			percentagesList = new ArrayList<String>();

			// IJ.log(percentageOfTriangles + " " + percentageOfSquares + " " +
			// percentageOfPentagons + " " + percentageOfHexagonsToShow + " " +
			// percentageOfHeptagons + " " + percentageOfOctogons + " " +
			// percentageOfNonagons + " " + percentageOfDecagons);

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

			percentagesList.add(defaultFormat.format(percentageOfSquares));
			percentagesList.add(defaultFormat.format(percentageOfPentagons));
			percentagesList.add(defaultFormat.format(percentageOfHexagonsToShow));
			percentagesList.add(defaultFormat.format(percentageOfHeptagons));
			percentagesList.add(defaultFormat.format(percentageOfOctogons));
		}

		return percentagesList;
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
	public ArrayList<String> runGraphlets(int selectedShape, int radiusOfShape, int modeNumGraphlets,
			JProgressBar progressBar, boolean selectionMode, ImageOverlay overlay) {
		ArrayList<String> polDist = testNeighbours(selectedShape, radiusOfShape, null, progressBar, selectionMode,
				modeNumGraphlets, overlay);

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

		if (this.reDoTheComputation || this.distanceGDDH == -1) {
			for (int indexEpiCell = 0; indexEpiCell < this.cells.size(); indexEpiCell++) {
				this.cells.get(indexEpiCell).setValid_cell_4(allValidCellsWithinAGivenLength(indexEpiCell, 4));
				this.cells.get(indexEpiCell).setValid_cell_5(allValidCellsWithinAGivenLength(indexEpiCell, 5));
			}

			// Adjacency matrix
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

		// Percentage 100
		progressBar.setValue(100);

		return polDist;
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
	private void createNeighbourhood(int idEpiCell, int selectedShape, int radiusOfShape) {
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

		float[] orbitDist = new float[this.cells.get(0).getGraphlets().length];

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
	public String[][] getGraphlets() {
		String[][] graphlets = new String[this.cells.size()][BasicGraphlet.TOTALGRAPHLETS + 1];
		int cont = 0;
		int numCell = 0;
		for (EpiCell cell : this.cells) {
			graphlets[cont][0] = Integer.toString(cont + 1);
			numCell = 1;
			for (String graphlet : cell.getGraphletsString()) {
				graphlets[cont][numCell] = graphlet;
				numCell++;
			}
			cont++;
		}
		return graphlets;
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
	 * Check if we have to redo the computation of neighbours
	 * @param selectedShape shape of mask
	 * @param radiusOfShape radius of shape of mask
	 * @param selectionMode if exists any roi
	 * @return if we have to redo the computation of neighbours
	 */
	private boolean checkReDoComputation(int selectedShape, int radiusOfShape, boolean selectionMode) {
		boolean reDoTheComputation = false;
		if (this.shapeOfMask != selectedShape || this.radiusOfMask != radiusOfShape
				|| this.isSelectedCells() != selectionMode || this.invalidRegionChanged) {
			reDoTheComputation = true;
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
				reDoTheComputation = true;
		}

		this.selectedCells = selectionMode;
		return reDoTheComputation;
	}
}
