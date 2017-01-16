package epigraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
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
		this.imageName = new ArrayList<String>();
		this.gddh = new ArrayList<Float>();
		this.gddrv = new ArrayList<Float>();
		this.hexagonsPercentage = new ArrayList<Float>();
		this.R = new ArrayList<Float>();
		this.G = new ArrayList<Float>();
		this.B = new ArrayList<Float>();
	}
	
	
	

	public ExcelClass(String filename,ArrayList<String> imageName, ArrayList<Float> gddh, ArrayList<Float> gddrv,
			ArrayList<Float> hexagonsPercentage, ArrayList<Float> r, ArrayList<Float> g, ArrayList<Float> b) {
		super();
		this.imageName = imageName;
		this.gddh = gddh;
		this.gddrv = gddrv;
		this.hexagonsPercentage = hexagonsPercentage;
		R = r;
		G = g;
		B = b;
	}

	private ArrayList<String> imageName;
	private ArrayList<Float> gddh;
	private ArrayList<Float> gddrv;
	private ArrayList<Float> hexagonsPercentage;
	private ArrayList<Float> R;
	private ArrayList<Float> G;
	private ArrayList<Float> B;

	
	public ArrayList<String> getImageName() {
		return imageName;
	}

	public void setImageName(ArrayList<String> imageName) {
		this.imageName = imageName;
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

	public ArrayList<Object> getRow(int row){
		ArrayList<Object> rowExcel=new ArrayList<Object>();
		rowExcel.add(imageName.get(row));
		rowExcel.add(gddh.get(row));
		rowExcel.add(gddrv.get(row));
		rowExcel.add(hexagonsPercentage.get(row));
		rowExcel.add(R.get(row));
		rowExcel.add(G.get(row));
		rowExcel.add(B.get(row));

		return rowExcel;
	}
	
	
	/*IMPORT DATA FROM EXCEL*/
	
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

			//Start in second row because first row is only for names of heads
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
								this.hexagonsPercentage.add(Float.parseFloat(cell.getStringCellValue().replace(',', '.')));
								break;
							case 2:
								this.gddrv.add(Float.parseFloat(cell.getStringCellValue().replace(',', '.')));
								break;
							case 3:
								this.gddh.add(Float.parseFloat(cell.getStringCellValue().replace(',', '.')));
								break;
							case 4:
								this.R.add(Float.parseFloat(cell.getStringCellValue().replace(',', '.')));
								break;
							case 5:
								this.G.add(Float.parseFloat(cell.getStringCellValue().replace(',', '.')));
								break;
							case 6:
								this.B.add(Float.parseFloat(cell.getStringCellValue().replace(',', '.')));
								break;

							}
							
							
							

						}
					
						//If colors aren't define, default will be 0,0,0 (black) 
						if (cols <= 4 && c == cols){
							
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
	
	
	
	/*EXPORT DATA TO EXCEL*/
	
	
	public void exportData(String fname){
		
		
		//Blank workbook
	    HSSFWorkbook workbook = new HSSFWorkbook();
    
	    
	    //Create a blank sheet
	    HSSFSheet sheet = workbook.createSheet("Graphlets_distance");

	    //This data needs to be written (Object[])
	    Map<String, Object[]> data = new TreeMap<String, Object[]>();
	    data.put("1", new Object[]{"Image name","Hexagons percentage", "GDDRV", "GDDH","R","G","B"});
	    
	    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
	    otherSymbols.setDecimalSeparator(',');
	    DecimalFormat df1 = new DecimalFormat("#0.00", otherSymbols);
	    DecimalFormat df2 = new DecimalFormat("#0.000", otherSymbols);
    
	    
	    
	    for (int i = 0; i < gddh.size(); i++) {
			
	    	Integer j = i+2;
	    	
	    	data.put(j.toString(), new Object[]{imageName.get(i),df1.format(hexagonsPercentage.get(i)),df2.format(gddrv.get(i)),df2.format(gddh.get(i)),df2.format(R.get(i)),df2.format(G.get(i)),df2.format(B.get(i))});
	    	
		}
	    

	    //Iterate over data and write to sheet
	    int rownum = 0;
	    for (Integer keyint=1;keyint<gddh.size()+2;keyint ++) 
	    {
	        //create a row of excelsheet
	        Row row = sheet.createRow(rownum++);

	        //get object array of prerticuler key
	        Object[] objArr = data.get(keyint.toString());

	        int cellnum = 0;

	        for (Object obj : objArr) 
	        {
	            Cell cell = row.createCell(cellnum++);
	            
	            if (obj instanceof Float) 
	            {
	                cell.setCellValue((Float) obj);
	            }
	            else if (obj instanceof String) 
	            {
	                cell.setCellValue((String) obj);
	            }
	            
	        }
	    }
	    try 
	    {
	        //Write the workbook in file system
	        FileOutputStream out = new FileOutputStream(new File(fname));
	        workbook.write(out);
	        out.close();
	    } 
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	
	

}
