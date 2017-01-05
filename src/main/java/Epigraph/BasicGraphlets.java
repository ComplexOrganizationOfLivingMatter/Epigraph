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
	 * 
	 */
	public static final int TOTALGRAPHLETS = 73;

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
		this.orbit = null;
	}
	
	/**
	 * 
	 */
	public BasicGraphlets(int[][] orbit) {
		this.orbit = orbit;
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

			this.orbit = new int[numNodes][TOTALGRAPHLETS];

			// read from filePooped with Scanner class
			Scanner inputStream = new Scanner(file);

			int row = 0;
			int col = 0;
			// hashNext() loops line-by-line
			while (inputStream.hasNextLine()) {
				col = 0;
				// read single line, put in string
				while (inputStream.hasNext() && col < TOTALGRAPHLETS) {
					this.orbit[row][col] = inputStream.nextInt();
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
	public ArrayList<Integer[]> getGraphletsInteger(int[] graphletsWeDontWant) {
		ArrayList<Integer[]> graph = new ArrayList<Integer[]>();
		Integer[] actualGraphlets;
		int graphletIndex = 0;
		for (int numNode = 0; numNode < this.orbit.length; numNode++) {
			actualGraphlets = new Integer[TOTALGRAPHLETS];
			for (int numOrbit = 0; numOrbit < TOTALGRAPHLETS; numOrbit++) {
				if (graphletIndex >= graphletsWeDontWant.length || numOrbit != graphletsWeDontWant[graphletIndex]) {
					actualGraphlets[numOrbit] = this.orbit[numNode][numOrbit];
				} else {
					actualGraphlets[numOrbit] = 0;
					graphletIndex++;
				}
			}
			graph.add(actualGraphlets);
		}
		return graph;
	}

}
