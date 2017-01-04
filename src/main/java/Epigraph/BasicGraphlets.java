/**
 * 
 */
package Epigraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Pablo Vicente-Munuera
 *
 */
public class BasicGraphlets {

	/**
	 * This will only be seen by this class and the classes whose inherit from
	 * this one.
	 * 
	 * orbit[x][o] - how many times does node x
	 */
	protected int[][] orbit;

	/**
	 * 
	 */
	public BasicGraphlets() {
		orbit = null;
	}
	/**
	 * 
	 */
	public BasicGraphlets(String fileName) {
		ArrayList<String> data = readNdump(fileName);
		orbit = new int[data.size()][73];
		
		String row = "";
		for (int i = 0; i < data.size(); i++){
			row = 
			for (int j = 0; j < )
		}
	}

	/**
	 * 
	 * @param fileName
	 */
	private ArrayList<String> readNdump(String fileName) {
		// File class needed to turn stringName to actual file
		File file = new File(fileName);
		ArrayList<String> wholeFileData = new ArrayList<String>();

		try {
			// read from filePooped with Scanner class
			Scanner inputStream = new Scanner(file);
			// hashNext() loops line-by-line
			while (inputStream.hasNext()) {
				// read single line, put in string
				wholeFileData.add(inputStream.next());
			}
			// after loop, close scanner
			inputStream.close();

			return wholeFileData;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
