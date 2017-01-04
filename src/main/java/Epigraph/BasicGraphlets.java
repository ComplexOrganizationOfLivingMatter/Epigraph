/**
 * 
 */
package Epigraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

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
		Stack<String> data = readNdump(fileName);
		orbit = new int[data.size()][73];
		
		String row = "";
		String orbitString = "";
		int orbitNum = 0;
		int numRow = 0;
		while (data.isEmpty()){
			row = data.pop();
			orbitNum = 0;
			orbitString = "";
			for (int j = 0; j < row.length(); j++){
				if (row.substring(j, j+1) != " "){
					if (orbitString != ""){
						orbit[numRow][orbitNum] = Integer.parseInt(orbitString);
						orbitNum++;
					}
				} else {
					orbitString += row.substring(j, j+1);
				}
			}
			numRow++;
		}
	}

	/**
	 * 
	 * @param fileName
	 */
	private Stack<String> readNdump(String fileName) {
		// File class needed to turn stringName to actual file
		File file = new File(fileName);
		Stack<String> wholeFileData = new Stack<String>();

		try {
			// read from filePooped with Scanner class
			Scanner inputStream = new Scanner(file);
			// hashNext() loops line-by-line
			while (inputStream.hasNext()) {
				// read single line, put in string
				wholeFileData.push(inputStream.next());
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
