/**
 * 
 */
package epigraph;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Pablo Vicente Munuera, Pedro Gomez Galvez
 *
 */
final public class DiagramsData {
	private ArrayList<BasicGraphletImage> mo17;
	private ArrayList<BasicGraphletImage> mo7;
	private ArrayList<BasicGraphletImage> mo9;
	private ArrayList<BasicGraphletImage> mo29;
	
	public DiagramsData() throws CloneNotSupportedException{
		//MO 17
		ExcelClass excelclass = new ExcelClass();
		
		InputStream file = this.getClass().getResourceAsStream("/epigraph/voronoiNoiseReference/allDiagrams/17Motifs_CVTn_GDDs_06_12_2017.xls"); //GetPath not working
		
		mo17 = excelclass.importBasicExcel(file);
		
//		//Mo29
//		excelclass = new ExcelClass();
//		
//		file = this.getClass().getResourceAsStream("/epigraph/voronoiNoiseReference/allDiagrams/17Motifs_CVTn_GDDs_06_12_2017.xls"); //GetPath not working
//		
//		mo29 = excelclass.importBasicExcel(file);
//		
//		//Mo9
//		excelclass = new ExcelClass();
//		
//		file = this.getClass().getResourceAsStream("/epigraph/voronoiNoiseReference/allDiagrams/17Motifs_CVTn_GDDs_06_12_2017.xls"); //GetPath not working
//		
//		mo9 = excelclass.importBasicExcel(file);
//		
//		//Mo7
//		excelclass = new ExcelClass();
//		
//		file = this.getClass().getResourceAsStream("/epigraph/voronoiNoiseReference/allDiagrams/17Motifs_CVTn_GDDs_06_12_2017.xls"); //GetPath not working
//		
//		mo7 = excelclass.importBasicExcel(file);
		
		
	}

	/**
	 * @return the motifs17
	 */
	public final ArrayList<BasicGraphletImage> getMO17() {
		return mo17;
	}

	/**
	 * @return the mo7
	 */
	public final ArrayList<BasicGraphletImage> getMo7() {
		return mo7;
	}

	/**
	 * @return the mo10
	 */
	public final ArrayList<BasicGraphletImage> getMo9() {
		return mo9;
	}

	/**
	 * @return the mo26
	 */
	public final ArrayList<BasicGraphletImage> getMo29() {
		return mo29;
	}

	
}
