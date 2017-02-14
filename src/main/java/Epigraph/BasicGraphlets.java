/**
 * 
 */
package epigraph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Pablo Vicente-Munuera
 * 
 * This class read referenced graphlets information and is heritage of a more complex class: GraphletImage
 *
 */
public class BasicGraphlets {

	/**
	 * Max number of graphlets 
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
	 * Basic constructor
	 */
	public BasicGraphlets() {
		this.orbit = null;
	}

	/**
	 * Constructor from graphlets
	 * @param orbit
	 *            the graphlets themselves
	 */
	public BasicGraphlets(int[][] orbit) {
		this.orbit = orbit;
	}

	/**
	 * Constructing reference Voronoi Noise Scale from text file
	 * @param fileName
	 * where we'll get the graphlets
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
	 * Get an array of graphlets as Integer 
	 * @param graphletsWeDontWant
	 * the graphlets we don't want will appear as 0s
	 * @return  
	 * the graphlets we'll use
	 */
	public ArrayList<Integer[]> getGraphletsInteger(int[] graphletsWeDontWant) {
		ArrayList<Integer[]> graph = new ArrayList<Integer[]>();
		Integer[] actualGraphlets;
		int graphletIndex = 0;
		//Go through all the nodes
		for (int numNode = 0; numNode < this.orbit.length; numNode++) {
			actualGraphlets = new Integer[TOTALGRAPHLETS];
			graphletIndex = 0;
			//Go through the orbits of the node
			for (int numOrbit = 0; numOrbit < TOTALGRAPHLETS; numOrbit++) {
				//If we want the orbit of the node, we write it
				if (graphletIndex >= graphletsWeDontWant.length || numOrbit != graphletsWeDontWant[graphletIndex]) {
					actualGraphlets[numOrbit] = this.orbit[numNode][numOrbit];
				} else { //We don't want this orbit
					actualGraphlets[numOrbit] = 0;
					graphletIndex++;
				}
			}
			graph.add(actualGraphlets);
		}
		return graph;
	}

}
