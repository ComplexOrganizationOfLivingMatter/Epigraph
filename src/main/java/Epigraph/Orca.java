/**
 * 
 */
package Epigraph;

import java.util.ArrayList;

import net.imglib2.util.ValuePair;

/**
 * @author Pablo Vicente-Munuera
 * 
 *         Adaptation of Orca (Orbit Counting Algorithm): A combinatorial
 *         approach to graphlet counting - by Tomaz Hocevar.
 *
 */
public class Orca {

	private int[][] adjacencyMatrix;
	private int[][] orbit; // orbit[x][o] - how many times does node x
							// participate in
	// orbit o
	private int[][] adj;
	private int[] deg;
	private ArrayList<ValuePair<Integer, Integer>> edges; // list of edges
	private ValuePair<Integer, Integer>[][] inc;

	public Orca(int[][] adjacencyMatrix) {
		super();
		this.adjacencyMatrix = adjacencyMatrix;
		this.orbit = new int[adjacencyMatrix.length][73];
		this.deg = new int[adjacencyMatrix.length];
		this.edges = new ArrayList<ValuePair<Integer, Integer>>();
		this.inc = new ValuePair[adjacencyMatrix.length][adjacencyMatrix.length];
		this.adj = new int[adjacencyMatrix.length][adjacencyMatrix.length];

		int numEdge = 0;
		int[] d = new int[adjacencyMatrix.length];
		for (int i = 0; i < this.adjacencyMatrix[0].length; i++) {
			for (int j = i + 1; j < this.adjacencyMatrix[0].length; j++) {
				if (this.adjacencyMatrix[i][j] != 0) {
					deg[i]++;
					deg[j]++;
					this.edges.add(new ValuePair<Integer, Integer>(i, j));
					adj[i][d[i]] = j;
					inc[i][d[i]] = new ValuePair<Integer, Integer>(j, numEdge);
					adj[j][d[j]] = i;
					inc[j][d[j]] = new ValuePair<Integer, Integer>(i, numEdge);
					d[i]++;
					d[j]++;
					numEdge++;
				}
			}
		}
		System.out.println(numEdge);
	}

	public Orca() {
		// TODO Auto-generated constructor stub
	}

	public void computingCommonNodes() {
		int frac, frac_prev;

		// precompute triangles that span over edges
		int[] tri = new int[edges.size()];
		frac_prev = -1;
		for (int i = 0; i < edges.size(); i++) {
			int x = edges.get(i).getA();
			int y = edges.get(i).getB();

			for (int xi = 0, yi = 0; xi < deg[x] && yi < deg[y];) {
				if (adj[x][xi] == adj[y][yi]) {
					tri[i]++;
					xi++;
					yi++;
				} else if (adj[x][xi] < adj[y][yi]) {
					xi++;
				} else {
					yi++;
				}
			}
		}
	}
}
