package Epigraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelClass {

	/**
	 * 
	 */
	public ExcelClass() {
		super();
		this.gddh = new ArrayList<Float>();
		this.gddrv = new ArrayList<Float>();
		this.hexagonsPercentage = new ArrayList<Float>();
		this.R = new ArrayList<Float>();
		this.G = new ArrayList<Float>();
		this.B = new ArrayList<Float>();
	}

	/**
	 * @param filename
	 */
	public ExcelClass(String filename) {
		super();
		this.filename = filename;
	}

	public ExcelClass(String filename, ArrayList<Float> gddh, ArrayList<Float> gddrv,
			ArrayList<Float> hexagonsPercentage, ArrayList<Float> r, ArrayList<Float> g, ArrayList<Float> b) {
		super();
		this.filename = filename;
		this.gddh = gddh;
		this.gddrv = gddrv;
		this.hexagonsPercentage = hexagonsPercentage;
		R = r;
		G = g;
		B = b;
	}

	private String filename;
	private ArrayList<Float> gddh;
	private ArrayList<Float> gddrv;
	private ArrayList<Float> hexagonsPercentage;
	private ArrayList<Float> R;
	private ArrayList<Float> G;
	private ArrayList<Float> B;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ArrayList<Float> getGddh() {
		return gddh;
	}

	public void setGddh(ArrayList<Float> gddh) {
		this.gddh = gddh;
	}

	public ArrayList<Float> getGddrv() {
		return gddrv;
	}

	public void setGddrv(ArrayList<Float> gddrv) {
		this.gddrv = gddrv;
	}

	public ArrayList<Float> getHexagonsPercentage() {
		return hexagonsPercentage;
	}

	public void setHexagonsPercentage(ArrayList<Float> hexagonsPercentage) {
		this.hexagonsPercentage = hexagonsPercentage;
	}

	public ArrayList<Float> getR() {
		return R;
	}

	public void setR(ArrayList<Float> r) {
		R = r;
	}

	public ArrayList<Float> getG() {
		return G;
	}

	public void setG(ArrayList<Float> g) {
		G = g;
	}

	public ArrayList<Float> getB() {
		return B;
	}

	public void setB(ArrayList<Float> b) {
		B = b;
	}

	public void importData(FileInputStream filename) {

		try {
			POIFSFileSystem fs = new POIFSFileSystem(filename);
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

			for (int r = 0; r < rows; r++) {
				row = sheet.getRow(r);
				if (row != null) {
					for (int c = 0; c < cols; c++) {
						cell = row.getCell(c);
						if (cell != null) {
							// Your code here

							switch (c) {

							case 0:
								this.gddh.add((float) cell.getNumericCellValue());
								break;
							case 1:
								this.gddrv.add((float) cell.getNumericCellValue());
								break;
							case 2:
								this.hexagonsPercentage.add((float) cell.getNumericCellValue());
								break;
							case 3:
								this.R.add((float) cell.getNumericCellValue());
								break;
							case 4:
								this.G.add((float) cell.getNumericCellValue());
								break;
							case 5:
								this.B.add((float) cell.getNumericCellValue());
								break;

							}

						}
					}
				}
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}

}
