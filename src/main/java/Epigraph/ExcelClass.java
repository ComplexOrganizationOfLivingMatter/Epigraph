package epigraph;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * This class is used to import and export a xls file with all registered
 * graphlets information
 * 
 * @author Pedro Gomez-Galvez
 */
public class ExcelClass {

	private String fileName;
	private ArrayList<String> imageName;
	private ArrayList<Float> gddh;
	private ArrayList<Float> gddrv;
	private ArrayList<Float> gddv5;
	private ArrayList<Float> R;
	private ArrayList<Float> G;
	private ArrayList<Float> B;
	private ArrayList<String> graphletsMode;
	private ArrayList<Integer> radiusOfMask;
	private ArrayList<String> shapeOfMask;
	private ArrayList<Float> squaresPercentage;
	private ArrayList<Float> pentagonsPercentage;
	private ArrayList<Float> hexagonsPercentage;
	private ArrayList<Float> heptagonsPercentage;
	private ArrayList<Float> octogonsPercentage;
	private ArrayList<String> closestDiagram;
	private ArrayList<Float> distanceDiagram;
	private ArrayList<Float> confidenceDiagram;

	/**
	 * Default constructor
	 */
	public ExcelClass() {
		super();
		this.fileName = "";
		this.imageName = new ArrayList<String>();
		this.gddh = new ArrayList<Float>();
		this.gddrv = new ArrayList<Float>();
		this.gddv5 = new ArrayList<Float>();
		this.R = new ArrayList<Float>();
		this.G = new ArrayList<Float>();
		this.B = new ArrayList<Float>();
		this.graphletsMode = new ArrayList<String>();
		this.shapeOfMask = new ArrayList<String>();
		this.radiusOfMask = new ArrayList<Integer>();
		this.squaresPercentage = new ArrayList<Float>();
		this.pentagonsPercentage = new ArrayList<Float>();
		this.hexagonsPercentage = new ArrayList<Float>();
		this.heptagonsPercentage = new ArrayList<Float>();
		this.octogonsPercentage = new ArrayList<Float>();
		this.closestDiagram = new ArrayList<String>();
		this.distanceDiagram = new ArrayList<Float>();
		this.confidenceDiagram = new ArrayList<Float>();

	}

	/**
	 * Constructor from parameters
	 * 
	 * @param filename
	 *            Name of excel file to import or export
	 * @param imageName
	 *            List of labels of images
	 * @param gddh
	 *            List of graphlet degree distance to hexagons tesselletion
	 * @param gddrv
	 *            List of graphlet degree distance to random voronoi
	 *            tesselletion
	 * @param gddv5
	 *            List of graphlet degree distance to voronoi 5 tesselletion
	 * @param r
	 *            List of red channel value from RGB for each image
	 * @param g
	 *            List of green channel value from RGB for each image
	 * @param b
	 *            List of blue channel value from RGB for each image
	 * @param graphletsMode
	 *            List of chosen mode to calculate graphlets for each image
	 * @param radiusOfMask
	 *            List of radius of the shapes.
	 * @param shapeOfMask
	 *            List of chosen shapes.
	 * @param squaresPercentage
	 *            List of percentages of squares for each image
	 * @param pentagonsPercentage
	 *            List of percentages of pentagons for each image
	 * @param hexagonsPercentage
	 *            List of percentages of hexagons for each image
	 * @param heptagonsPercentage
	 *            List of percentages of heptagons for each image
	 * @param octogonsPercentage
	 *            List of percentages of octogons for each image
	 * @param closestDiagram
	 *            List of closest diagram of each image
	 * @param distanceDiagram
	 *            List of distance of closest diagram of each image
	 * @param confidenceDiagram
	 *            List of confidence of closest diagram of each image
	 */
	public ExcelClass(String filename, ArrayList<String> imageName, ArrayList<Float> gddh, ArrayList<Float> gddrv,
			ArrayList<Float> gddv5, ArrayList<Float> r, ArrayList<Float> g, ArrayList<Float> b,
			ArrayList<String> graphletsMode, ArrayList<Integer> radiusOfMask, ArrayList<String> shapeOfMask,
			ArrayList<Float> squaresPercentage, ArrayList<Float> pentagonsPercentage,
			ArrayList<Float> hexagonsPercentage, ArrayList<Float> heptagonsPercentage,
			ArrayList<Float> octogonsPercentage, ArrayList<String> closestDiagram, ArrayList<Float> distanceDiagram,
			ArrayList<Float> confidenceDiagram) {
		super();
		this.fileName = filename;
		this.imageName = imageName;
		this.gddh = gddh;
		this.gddrv = gddrv;
		this.gddv5 = gddv5;
		this.R = r;
		this.G = g;
		this.B = b;
		this.graphletsMode = graphletsMode;
		this.radiusOfMask = radiusOfMask;
		this.shapeOfMask = shapeOfMask;
		this.squaresPercentage = squaresPercentage;
		this.pentagonsPercentage = pentagonsPercentage;
		this.hexagonsPercentage = hexagonsPercentage;
		this.heptagonsPercentage = heptagonsPercentage;
		this.octogonsPercentage = octogonsPercentage;
		this.closestDiagram = closestDiagram;
		this.distanceDiagram = distanceDiagram;
		this.confidenceDiagram = confidenceDiagram;
	}

	/**
   * @return the fileName
   */
  public final String getFileName() {
    return fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public final void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
	 * 
	 * @return Get list of images names
	 */
	public ArrayList<String> getImageName() {
		return imageName;
	}

	/**
	 * @param imageName
	 *            Set list of image name
	 */
	public void setImageName(ArrayList<String> imageName) {
		this.imageName = imageName;
	}

	/**
	 * 
	 * @return Get list of gddh
	 */
	public ArrayList<Float> getGddh() {
		return gddh;
	}

	/**
	 *
	 * @param gddh
	 *            Set list of gddh
	 */
	public void setGddh(ArrayList<Float> gddh) {
		this.gddh = gddh;
	}

	/**
	 * 
	 * @return Get list of gddrv
	 */
	public ArrayList<Float> getGddrv() {
		return gddrv;
	}

	/**
	 * 
	 * @param gddrv
	 *            Set list of gddrv
	 */
	public void setGddrv(ArrayList<Float> gddrv) {
		this.gddrv = gddrv;
	}

	/**
	 * 
	 * @return Get list of gddv5
	 */
	public ArrayList<Float> getGddv5() {
		return gddv5;
	}

	/**
	 * 
	 * @param gddv5
	 *            Set list of gddv5
	 */
	public void setGddv5(ArrayList<Float> gddv5) {
		this.gddv5 = gddv5;
	}

	/**
	 * @return the squaresPercentage
	 */
	public ArrayList<Float> getSquaresPercentage() {
		return squaresPercentage;
	}

	/**
	 * @param squaresPercentage
	 *            the squaresPercentage to set
	 */
	public void setSquaresPercentage(ArrayList<Float> squaresPercentage) {
		this.squaresPercentage = squaresPercentage;
	}

	/**
	 * @return the pentagonsPercentage
	 */
	public ArrayList<Float> getPentagonsPercentage() {
		return pentagonsPercentage;
	}

	/**
	 * @param pentagonsPercentage
	 *            the pentagonsPercentage to set
	 */
	public void setPentagonsPercentage(ArrayList<Float> pentagonsPercentage) {
		this.pentagonsPercentage = pentagonsPercentage;
	}

	/**
	 *
	 * @return Get list of hexagons percentajes
	 */
	public ArrayList<Float> getHexagonsPercentage() {
		return hexagonsPercentage;
	}

	/**
	 * 
	 * @param hexagonsPercentage
	 *            Set list of hexagons percentajes
	 */
	public void setHexagonsPercentage(ArrayList<Float> hexagonsPercentage) {
		this.hexagonsPercentage = hexagonsPercentage;
	}

	/**
	 * @return the heptagonsPercentage
	 */
	public ArrayList<Float> getHeptagonsPercentage() {
		return heptagonsPercentage;
	}

	/**
	 * @param heptagonsPercentage
	 *            the heptagonsPercentage to set
	 */
	public void setHeptagonsPercentage(ArrayList<Float> heptagonsPercentage) {
		this.heptagonsPercentage = heptagonsPercentage;
	}

	/**
	 * @return the octogonsPercentage
	 */
	public ArrayList<Float> getOctogonsPercentage() {
		return octogonsPercentage;
	}

	/**
	 * @param octogonsPercentage
	 *            the octogonsPercentage to set
	 */
	public void setOctogonsPercentage(ArrayList<Float> octogonsPercentage) {
		this.octogonsPercentage = octogonsPercentage;
	}

	/**
	 * 
	 * @return Get list of channels R (RGB)
	 */
	public ArrayList<Float> getR() {
		return R;
	}

	/**
	 * 
	 * @param r
	 *            Set list of channels R (RGB)
	 */
	public void setR(ArrayList<Float> r) {
		R = r;
	}

	/**
	 * 
	 * @return Get list of channels G (RGB)
	 */
	public ArrayList<Float> getG() {
		return G;
	}

	/**
	 * 
	 * @param g
	 *            Set list of channels G (RGB)
	 */
	public void setG(ArrayList<Float> g) {
		G = g;
	}

	/**
	 * 
	 * @return Get list of channels B (RGB)
	 */
	public ArrayList<Float> getB() {
		return B;
	}

	/**
	 * 
	 * @param b
	 *            Set list of channels B (RGB)
	 */
	public void setB(ArrayList<Float> b) {
		B = b;
	}

	/**
	 * 
	 * @return Get list of graphlets modes used for each image
	 */
	public ArrayList<String> getGraphletsMode() {
		return graphletsMode;
	}

	/**
	 * 
	 * @param graphletsMode
	 *            Set list of graphlets modes
	 */
	public void setGraphletsMode(ArrayList<String> graphletsMode) {
		this.graphletsMode = graphletsMode;
	}

	/**
	 * @return the radiusOfMask
	 */
	public ArrayList<Integer> getRadiusOfMask() {
		return radiusOfMask;
	}

	/**
	 * @param radiusOfMask
	 *            the radiusOfMask to set
	 */
	public void setRadiusOfMask(ArrayList<Integer> radiusOfMask) {
		this.radiusOfMask = radiusOfMask;
	}

	/**
	 * @return the shapeOfMask
	 */
	public ArrayList<String> getShapeOfMask() {
		return shapeOfMask;
	}

	/**
	 * @param shapeOfMask
	 *            the shapeOfMask to set
	 */
	public void setShapeOfMask(ArrayList<String> shapeOfMask) {
		this.shapeOfMask = shapeOfMask;
	}

	/**
	 * @return the closestDiagram
	 */
	public final ArrayList<String> getClosestDiagram() {
		return closestDiagram;
	}

	/**
	 * @param closestDiagram
	 *            the closestDiagram to set
	 */
	public final void setClosestDiagram(ArrayList<String> closestDiagram) {
		this.closestDiagram = closestDiagram;
	}

	/**
	 * @return the distanceDiagram
	 */
	public final ArrayList<Float> getDistanceDiagram() {
		return distanceDiagram;
	}

	/**
	 * @param distanceDiagram
	 *            the distanceDiagram to set
	 */
	public final void setDistanceDiagram(ArrayList<Float> distanceDiagram) {
		this.distanceDiagram = distanceDiagram;
	}

	/**
	 * @return the confidenceDiagram
	 */
	public final ArrayList<Float> getConfidenceDiagram() {
		return confidenceDiagram;
	}

	/**
	 * @param confidenceDiagram
	 *            the confidenceDiagram to set
	 */
	public final void setConfidenceDiagram(ArrayList<Float> confidenceDiagram) {
		this.confidenceDiagram = confidenceDiagram;
	}

	/**
	 * 
	 * @param row
	 *            number of excel class row
	 * @return excel class row with all values
	 */
	public ArrayList<Object> getRow(int row) {
		ArrayList<Object> rowExcel = new ArrayList<Object>();
		rowExcel.add(this.imageName.get(row));
		rowExcel.add(this.gddh.get(row));
		rowExcel.add(this.gddrv.get(row));
		rowExcel.add(this.gddv5.get(row));
		rowExcel.add(this.R.get(row));
		rowExcel.add(this.G.get(row));
		rowExcel.add(this.B.get(row));
		rowExcel.add(this.graphletsMode.get(row));
		rowExcel.add(this.radiusOfMask.get(row));
		rowExcel.add(this.shapeOfMask.get(row));
		rowExcel.add(this.squaresPercentage.get(row));
		rowExcel.add(this.pentagonsPercentage.get(row));
		rowExcel.add(this.hexagonsPercentage.get(row));
		rowExcel.add(this.heptagonsPercentage.get(row));
		rowExcel.add(this.octogonsPercentage.get(row));

		return rowExcel;
	}

	/**
	 * load a xls file previously exported
	 * 
	 * @param path
	 *            Name of file in directory
	 */
	public void importData(InputStream path) {

		try {
			POIFSFileSystem fs = new POIFSFileSystem(path);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell;

			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it
			// doesn't start from first few rows
			for (int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if (tmp > cols)
						cols = tmp;
				}
			}

			// Start in second row because first row is only for names of heads
			for (int r = 1; r < rows; r++) {
				row = sheet.getRow(r);
				if (row != null) {
					for (int c = 0; c <= cols; c++) {
						cell = row.getCell(c);
						if (cell != null) {
							// Your code here

							switch (c) {

							case 0:
								this.imageName.add(cell.getStringCellValue());
								break;

							case 1:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.gddh.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.gddh.add((float) cell.getNumericCellValue());
								}
								break;

							case 2:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.gddrv.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.gddrv.add((float) cell.getNumericCellValue());
								}
								break;

							case 3:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.gddv5.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.gddv5.add((float) cell.getNumericCellValue());
								}
								break;

							case 4:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.R.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.R.add((float) cell.getNumericCellValue());
								}
								break;
							case 5:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.G.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.G.add((float) cell.getNumericCellValue());
								}
								break;
							case 6:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.B.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.B.add((float) cell.getNumericCellValue());
								}
								break;
							case 7:
								this.graphletsMode.add(cell.getStringCellValue());
								break;
							case 8:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.radiusOfMask.add(Integer.parseInt(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.radiusOfMask.add((int) cell.getNumericCellValue());
								}
								break;

							case 9:
								this.shapeOfMask.add(cell.getStringCellValue());
								break;

							case 10:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.squaresPercentage.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.squaresPercentage.add((float) cell.getNumericCellValue());
								}
								break;
							case 11:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.pentagonsPercentage.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.pentagonsPercentage.add((float) cell.getNumericCellValue());
								}
								break;
							case 12:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.hexagonsPercentage.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.hexagonsPercentage.add((float) cell.getNumericCellValue());
								}
								break;
							case 13:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.heptagonsPercentage.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.heptagonsPercentage.add((float) cell.getNumericCellValue());
								}
								break;
							case 14:
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								try {
									this.octogonsPercentage.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.octogonsPercentage.add((float) cell.getNumericCellValue());
								}
								break;

							}

						}

						// If colors aren't define, default will be 0,0,0
						// (black)
						if (cols <= 4 && c == cols) {

							this.R.add((float) 0);
							this.G.add((float) 0);
							this.B.add((float) 0);
						}

					}

				}
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * Import excel from our own file. Use it internally
	 * 
	 * @param file
	 *            the path of the excel to import
	 * @param tableInfo
	 *            table in which it would be save
	 * @param diagramsData
	 *            diagrams information
	 * @return collection of graphlets info from the imported excel
	 * @throws CloneNotSupportedException an exception
	 */
	public ArrayList<BasicGraphletImage> importExcel(InputStream file, JTableModel tableInfo, DiagramsData diagramsData)
			throws CloneNotSupportedException {
		this.importData(file);

		int flat = 0;

		ArrayList<BasicGraphletImage> newImages = new ArrayList<BasicGraphletImage>();

		ArrayList<Object> newRow;
		Color newColor;
		for (int row = 0; row < this.getImageName().size(); row++) {
			newRow = this.getRow(row);
			if (flat == 1) {
				newColor = new Color(Math.round((float) newRow.get(4)), Math.round((float) newRow.get(5)),
						Math.round((float) newRow.get(6)));
			} else if (flat == 2) {
				newColor = new Color((float) newRow.get(4), (float) newRow.get(5), (float) newRow.get(6));
			} else {
				if ((float) newRow.get(4) > 1.0 || (float) newRow.get(5) > 1.0 || (float) newRow.get(6) > 1.0) {
					flat = 1;
					newColor = new Color(Math.round((float) newRow.get(4)), Math.round((float) newRow.get(5)),
							Math.round((float) newRow.get(6)));
				} else if (((float) newRow.get(4) < 1.0 & (float) newRow.get(4) > 1.0)
						|| ((float) newRow.get(5) < 1.0 & (float) newRow.get(5) > 0.0)
						|| ((float) newRow.get(6) < 1.0 & (float) newRow.get(6) > 0.0)) {
					newColor = new Color((float) newRow.get(4), (float) newRow.get(5), (float) newRow.get(6));
					flat = 2;
				} else {
					newColor = new Color((float) newRow.get(4), (float) newRow.get(5), (float) newRow.get(6));

				}

			}

			int shapeColumn;
			String shapeString = (String) newRow.get(9);
			if (shapeString.equals("Circle")) {
				shapeColumn = GraphletImage.CIRCLE_SHAPE;
			} else {
				shapeColumn = GraphletImage.SQUARE_SHAPE;
			}

			BasicGraphletImage newGraphletImage = new BasicGraphletImage();
			newGraphletImage.setDistanceGDDH((float) newRow.get(1));
			newGraphletImage.setDistanceGDDRV((float) newRow.get(2));
			newGraphletImage.setDistanceGDDV5((float) newRow.get(3));
			newGraphletImage.setColor(newColor);
			newGraphletImage.setLabelName((String) newRow.get(0));
			newGraphletImage.setShapeOfMask(shapeColumn);
			newGraphletImage.setRadiusOfMask((int) newRow.get(8));
			newGraphletImage.setPercentageOfSquares((float) newRow.get(10));
			newGraphletImage.setPercentageOfPentagons((float) newRow.get(11));
			newGraphletImage.setPercentageOfHexagons((float) newRow.get(12));
			newGraphletImage.setPercentageOfHexagons((float) newRow.get(12));
			newGraphletImage.setPercentageOfHeptagons((float) newRow.get(13));
			newGraphletImage.setPercentageOfOctogons((float) newRow.get(14));
			
			// Select which motifs			
			int indexString = Arrays.asList(GraphletImage.KIND_OF_GRAPHLETS).indexOf((String) newRow.get(7));
			switch (indexString){
				case 3: newGraphletImage.calculateClosestDiagram(diagramsData.getMo7());
					break;
				case 2: newGraphletImage.calculateClosestDiagram(diagramsData.getMo10());
					break;
				case 1: newGraphletImage.calculateClosestDiagram(diagramsData.getMO17());
					break;
				case 0: newGraphletImage.calculateClosestDiagram(diagramsData.getMo29());
					break;
				default:newGraphletImage.calculateClosestDiagram(diagramsData.getMO17());
					break;
					
			}
			
			newImages.add(newGraphletImage);

			tableInfo.addImage(newGraphletImage, (String) newRow.get(7));

		}
		return newImages;
	}

	/**
	 * 
	 * @param file the excel file
	 * @return the array list of the basic graphlet images without adding it into the table
	 */
	public ArrayList<BasicGraphletImage> importBasicExcel(InputStream file) {
		this.importData(file);

		int flat = 0;

		ArrayList<BasicGraphletImage> newImages = new ArrayList<BasicGraphletImage>();

		ArrayList<Object> newRow;
		Color newColor;
		for (int row = 0; row < this.getImageName().size(); row++) {
			newRow = this.getRow(row);
			if (flat == 1) {
				newColor = new Color(Math.round((float) newRow.get(4)), Math.round((float) newRow.get(5)),
						Math.round((float) newRow.get(6)));
			} else if (flat == 2) {
				newColor = new Color((float) newRow.get(4), (float) newRow.get(5), (float) newRow.get(6));
			} else {
				if ((float) newRow.get(4) > 1.0 || (float) newRow.get(5) > 1.0 || (float) newRow.get(6) > 1.0) {
					flat = 1;
					newColor = new Color(Math.round((float) newRow.get(4)), Math.round((float) newRow.get(5)),
							Math.round((float) newRow.get(6)));
				} else if (((float) newRow.get(4) < 1.0 & (float) newRow.get(4) > 1.0)
						|| ((float) newRow.get(5) < 1.0 & (float) newRow.get(5) > 0.0)
						|| ((float) newRow.get(6) < 1.0 & (float) newRow.get(6) > 0.0)) {
					newColor = new Color((float) newRow.get(4), (float) newRow.get(5), (float) newRow.get(6));
					flat = 2;
				} else {
					newColor = new Color((float) newRow.get(4), (float) newRow.get(5), (float) newRow.get(6));

				}

			}

			int shapeColumn;
			String shapeString = (String) newRow.get(9);
			if (shapeString.equals("Circle")) {
				shapeColumn = GraphletImage.CIRCLE_SHAPE;
			} else {
				shapeColumn = GraphletImage.SQUARE_SHAPE;
			}

			BasicGraphletImage newGraphletImage = new BasicGraphletImage();
			newGraphletImage.setDistanceGDDH((float) newRow.get(1));
			newGraphletImage.setDistanceGDDRV((float) newRow.get(2));
			newGraphletImage.setDistanceGDDV5((float) newRow.get(3));
			newGraphletImage.setColor(newColor);
			newGraphletImage.setLabelName((String) newRow.get(0));
			newGraphletImage.setShapeOfMask(shapeColumn);
			newGraphletImage.setRadiusOfMask((int) newRow.get(8));
			newGraphletImage.setPercentageOfSquares((float) newRow.get(10));
			newGraphletImage.setPercentageOfPentagons((float) newRow.get(11));
			newGraphletImage.setPercentageOfHexagons((float) newRow.get(12));
			newGraphletImage.setPercentageOfHexagons((float) newRow.get(12));
			newGraphletImage.setPercentageOfHeptagons((float) newRow.get(13));
			newGraphletImage.setPercentageOfOctogons((float) newRow.get(14));

			newImages.add(newGraphletImage);

		}
		return newImages;
	}

	/**
	 * Export excelClass to a xls file
	 */
	public void exportData() {

		// Blank workbook
		HSSFWorkbook workbook = new HSSFWorkbook();

		// Create a blank sheet
		HSSFSheet sheet = workbook.createSheet("EpiGraph");

		// This data needs to be written (Object[])
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1",
				new Object[] { "Image name", "Epi-Hexagons", "Epi-Random", "Epi-Voronoi5", "R", "G", "B", "GraphletsMode", "RadiusOfMask",
						"ShapeOfMask", "% squares", "% pentagons", "% hexagons", "% heptagons", "% octogons",
						"Closest diagram", "Distance", "Confidence" });

		NumberFormat df2 = NumberFormat.getInstance();
		df2.setMaximumFractionDigits(3);
		NumberFormat df3 = NumberFormat.getInstance();
		df3.setMaximumFractionDigits(0);

		for (int i = 0; i < gddh.size(); i++) {

			Integer j = i + 2;

			if (pentagonsPercentage.size() != gddh.size()) {
			  data.put(j.toString(),
            new Object[] { imageName.get(i), gddh.get(i), gddrv.get(i), gddv5.get(i), R.get(i),
                G.get(i), B.get(i), this.graphletsMode.get(i), 3,
                "Circle", -1, -1,
                hexagonsPercentage.get(i), -1, -1,
                -1, -1, -1 });
			} else {
        data.put(j.toString(),
            new Object[] { imageName.get(i), gddh.get(i), gddrv.get(i), gddv5.get(i), R.get(i),
                G.get(i), B.get(i), this.graphletsMode.get(i), this.radiusOfMask.get(i),
                this.shapeOfMask.get(i), squaresPercentage.get(i), pentagonsPercentage.get(i),
                hexagonsPercentage.get(i), heptagonsPercentage.get(i), octogonsPercentage.get(i),
                closestDiagram.get(i), distanceDiagram.get(i), confidenceDiagram.get(i) });
			}
		}

		// Iterate over data and write to sheet
		int rownum = 0;
		for (Integer keyint = 1; keyint < gddh.size() + 2; keyint++) {
			// create a row of excelsheet
			Row row = sheet.createRow(rownum++);

			// get object array from key
			Object[] objArr = data.get(keyint.toString());

			int cellnum = 0;

			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);

				if (obj instanceof Float) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue((Float) obj);
				} else if (obj instanceof Integer) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue((Integer) obj);
				} else if (obj instanceof String) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue((String) obj);
				}

			}
		}
		try {
			// Write the workbook in file system
			System.out.println(this.fileName);
			FileOutputStream out = new FileOutputStream(new File(this.fileName));
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
