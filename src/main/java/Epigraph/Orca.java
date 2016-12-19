/**
 * 
 */
package Epigraph;

/**
 * @author Pablo Vicente-Munuera
 * 
 * Adaptation of Orca (Orbit Counting Algorithm):
 * A combinatorial approach to graphlet counting - by Tomaz Hocevar.
 *
 */
public class Orca {
	
	int[][] adjacencyMatrix;
	int[][] orbit; // orbit[x][o] - how many times does node x participate in orbit o

	public Orca(int[][] adjacencyMatrix) {
		super();
		this.adjacencyMatrix = adjacencyMatrix;
		this.orbit = new int[adjacencyMatrix.length][73];
	}
}
