/**
 * 
 */
package Epigraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author Pablo Vicente-Munuera
 *
 */
public class BasicGraphlets {

	/**
	 * 
	 */
	public static int TOTALGRAPHLETS = 73;

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
		// File class needed to turn stringName to actual file
		File file = new File(fileName);

		try {
			// count lines
			Scanner countLines = new Scanner(file);

			int numNodes = 0;
			while (countLines.hasNextLine()) {
				countLines.nextLine();
				numNodes++;
			}
			countLines.close();

			orbit = new int[numNodes][TOTALGRAPHLETS];

			// read from filePooped with Scanner class
			Scanner inputStream = new Scanner(file);

			int row = 0;
			int col = 0;
			// hashNext() loops line-by-line
			while (inputStream.hasNextLine()) {
				col = 0;
				// read single line, put in string
				while (inputStream.hasNext() && col < TOTALGRAPHLETS) {
					orbit[row][col] = inputStream.nextInt();
					col++;
				}
				inputStream.nextLine();
				row++;
			}
			// after loop, close scanner
			inputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the graphlets
	 */
	public Integer[][] getGraphletsInteger(int[] graphletsWeDontWant) {
		Integer[][] graph = new Integer[orbit.length][TOTALGRAPHLETS];
		int graphletIndex = 0;
		for (int numNode = 0; numNode < orbit.length; numNode++) {
			for (int numOrbit = 0; numOrbit < TOTALGRAPHLETS; numOrbit++) {
				if (graphletIndex >= graphletsWeDontWant.length || numOrbit != graphletsWeDontWant[graphletIndex]) {
					graph[numNode][numOrbit] = orbit[numNode][numOrbit];
				} else {
					graph[numNode][numOrbit] = 0;
					graphletIndex++;
				}

			}
		}
		return graph;
	}

}
