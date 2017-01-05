/**
 * 
 */
package epigraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

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
	 * @param orbit
	 *            the graphlets themselves
	 */
	public BasicGraphlets(int[][] orbit) {
		this.orbit = orbit;
	}

	/**
	 * 
	 * @param fileName
	 *            where we'll get the graphlets
	 */
	public BasicGraphlets(URL fileName) {
		// File class needed to turn stringName to actual file
		try {
			// count lines
			Scanner countLines = new Scanner(fileName.openStream());

			int numNodes = 0;
			while (countLines.hasNextLine()) {
				countLines.nextLine();
				numNodes++;
			}
			countLines.close();

			this.orbit = new int[numNodes][TOTALGRAPHLETS];

			// read from filePooped with Scanner class
			Scanner inputStream =  new Scanner(fileName.openStream());

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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param graphletsWeDontWant
	 *            the graphlets we don't want will appear as 0s
	 * @return
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
