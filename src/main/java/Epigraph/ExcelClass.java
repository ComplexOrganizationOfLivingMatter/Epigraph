package epigraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
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
	private ArrayList<Float> hexagonsPercentage;
	private ArrayList<Float> R;
	private ArrayList<Float> G;
	private ArrayList<Float> B;
	private ArrayList<String> graphletsMode;
	private ArrayList<Integer> radiusOfMask;
	private ArrayList<String> shapeOfMask; 

	/**
	 * Default constructor
	 */
	public ExcelClass() {
		super();
		this.fileName = "";
		this.imageName = new ArrayList<String>();
		this.gddh = new ArrayList<Float>();
		this.gddrv = new ArrayList<Float>();
		this.hexagonsPercentage = new ArrayList<Float>();
		this.R = new ArrayList<Float>();
		this.G = new ArrayList<Float>();
		this.B = new ArrayList<Float>();
		this.graphletsMode = new ArrayList<String>();
		this.shapeOfMask = new ArrayList<String>();
		this.radiusOfMask = new ArrayList<Integer>();
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
	 * @param hexagonsPercentage
	 *            List of percentajes of hexagons for each image
	 * @param r
	 *            List of red channel value from RGB for each image
	 * @param g
	 *            List of green channel value from RGB for each image
	 * @param b
	 *            List of blue channel value from RGB for each image
	 * @param graphletsMode
	 *            List of chosen mode to calculate graphlets for each image
	 */
	public ExcelClass(String filename, ArrayList<String> imageName, ArrayList<Float> gddh, ArrayList<Float> gddrv,
			ArrayList<Float> hexagonsPercentage, ArrayList<Float> r, ArrayList<Float> g, ArrayList<Float> b,
			ArrayList<String> graphletsMode, ArrayList<Integer> radiusOfMask, ArrayList<String> shapeOfMask) {
		super();
		this.fileName = filename;
		this.imageName = imageName;
		this.gddh = gddh;
		this.gddrv = gddrv;
		this.hexagonsPercentage = hexagonsPercentage;
		this.R = r;
		this.G = g;
		this.B = b;
		this.graphletsMode = graphletsMode;
		this.radiusOfMask = radiusOfMask;
		this.shapeOfMask = shapeOfMask;
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
	 * @param radiusOfMask the radiusOfMask to set
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
	 * @param shapeOfMask the shapeOfMask to set
	 */
	public void setShapeOfMask(ArrayList<String> shapeOfMask) {
		this.shapeOfMask = shapeOfMask;
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
		rowExcel.add(this.hexagonsPercentage.get(row));
		rowExcel.add(this.R.get(row));
		rowExcel.add(this.G.get(row));
		rowExcel.add(this.B.get(row));
		rowExcel.add(this.graphletsMode.get(row));
		rowExcel.add(this.radiusOfMask.get(row));
		rowExcel.add(this.shapeOfMask.get(row));

		return rowExcel;
	}

	/**
	 * load a xls file previously exported
	 * 
	 * @param filename
	 *            Name of file in directory
	 */
	public void importData(String filename) {

		try {
			FileInputStream path = new FileInputStream(filename);
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
								this.hexagonsPercentage
										.add(Float.parseFloat(cell.getStringCellValue().replace(',', '.')));
								break;
							case 2:
								this.gddrv.add(Float.parseFloat(cell.getStringCellValue().replace(',', '.')));
								break;
							case 3:
								this.gddh.add(Float.parseFloat(cell.getStringCellValue().replace(',', '.')));
								break;
							case 4:
								try {
									this.R.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.R.add((float) cell.getNumericCellValue());
								}
								break;
							case 5:
								try {
									this.G.add(Float.parseFloat(cell.getStringCellValue()));
								} catch (java.lang.IllegalStateException e) {
									// TODO: handle exception
									this.G.add((float) cell.getNumericCellValue());
								}
								break;
							case 6:
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
	 * Export excelClass to a xls file
	 */
	public void exportData() {

		// Blank workbook
		HSSFWorkbook workbook = new HSSFWorkbook();

		// Create a blank sheet
		HSSFSheet sheet = workbook.createSheet("Graphlets_distance");

		// This data needs to be written (Object[])
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1",
				new Object[] { "Image name", "Hexagons percentage", "GDDRV", "GDDH", "R", "G", "B", "GraphletsMode", "RadiusOfMask", "ShapeOfMask" });

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator(',');
		DecimalFormat df1 = new DecimalFormat("#0.00", otherSymbols);
		DecimalFormat df2 = new DecimalFormat("#0.000", otherSymbols);
		DecimalFormat df3 = new DecimalFormat("#0", otherSymbols);

		for (int i = 0; i < gddh.size(); i++) {

			Integer j = i + 2;

			data.put(j.toString(),
					new Object[] { imageName.get(i), df1.format(hexagonsPercentage.get(i)), df2.format(gddrv.get(i)),
							df2.format(gddh.get(i)), df3.format(R.get(i)), df3.format(G.get(i)), df3.format(B.get(i)),
							this.graphletsMode.get(i), this.radiusOfMask.get(i), this.shapeOfMask.get(i) });

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
					cell.setCellValue((Float) obj);
				}else if(obj instanceof Integer){
					cell.setCellValue((Integer) obj);
				}else if(obj instanceof String) {		
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
