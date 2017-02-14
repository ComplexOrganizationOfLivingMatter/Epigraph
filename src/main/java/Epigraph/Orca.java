/**
 * 
 */
package epigraph;

import java.util.ArrayList;

import net.imglib2.util.ValuePair;

/**
 * Adaptation of Orca (Orbit Counting Algorithm): A combinatorial approach to
 * graphlet counting - by Tomaz Hocevar.
 * 
 * @author Pablo Vicente-Munuera
 */
public class Orca extends BasicGraphlet {

	private int[][] adjacencyMatrix;
	/** inherited from BasicGraphlets **/
	// private int[][] orbit; // orbit[x][o] - how many times does node x

	// participate in
	// orbit o
	private int[][] adj;
	private int[] deg;
	private ArrayList<ValuePair<Integer, Integer>> edges; // list of edges
	private ArrayList<ArrayList<ValuePair<Integer, Integer>>> inc;
	private int[][] common2;
	private int[][][] common3;
	private int[] tri;
	private int[] C5;

	/**
	 * 
	 * @param adjacencyMatrix
	 */
	public Orca(int[][] adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
		this.orbit = new int[this.adjacencyMatrix[0].length][TOTALGRAPHLETS];
		this.deg = new int[this.adjacencyMatrix[0].length];
		this.edges = new ArrayList<ValuePair<Integer, Integer>>();
		this.inc = new ArrayList<ArrayList<ValuePair<Integer, Integer>>>();
		this.adj = new int[this.adjacencyMatrix[0].length][this.adjacencyMatrix[0].length];
		this.common2 = new int[this.adjacencyMatrix[0].length][this.adjacencyMatrix[0].length];
		this.common3 = new int[this.adjacencyMatrix[0].length][this.adjacencyMatrix[0].length][this.adjacencyMatrix[0].length];

		int numEdge = 0;
		int[] d = new int[this.adjacencyMatrix[0].length];

		for (int i = 0; i < this.adjacencyMatrix[0].length; i++)
			inc.add(new ArrayList<ValuePair<Integer, Integer>>());

		for (int i = 0; i < this.adjacencyMatrix[0].length; i++) {
			for (int j = i + 1; j < this.adjacencyMatrix[0].length; j++) {
				if (this.adjacencyMatrix[i][j] != 0) {
					deg[i]++;
					deg[j]++;
					this.edges.add(new ValuePair<Integer, Integer>(i, j));
					adj[i][d[i]] = j;
					adj[j][d[j]] = i;
					inc.get(i).add(d[i], new ValuePair<Integer, Integer>(j, numEdge));
					inc.get(j).add(d[j], new ValuePair<Integer, Integer>(i, numEdge));
					d[i]++;
					d[j]++;
					numEdge++;
				}
			}
		}

		// There were a sorting on previous code (Tomaz Hocevar's code).
		// However, seems to be not necessary.

		this.computingCommonNodes();
		this.countingFullGraphlets();
		this.buildingEquationSystems();
	}

	/**
	 * 
	 */
	public Orca() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the orbit
	 */
	public int[][] getOrbit() {
		return orbit;
	}

	/**
	 * Precompute common nodes and precompute triangles that span over edges
	 */
	public void computingCommonNodes() {

		for (int x = 0; x < this.adjacencyMatrix[0].length; x++) {
			for (int n1 = 0; n1 < deg[x]; n1++) {
				int a = adj[x][n1];
				for (int n2 = n1 + 1; n2 < deg[x]; n2++) {
					int b = adj[x][n2];
					common2[a][b]++;
					common2[b][a]++;
					for (int n3 = n2 + 1; n3 < deg[x]; n3++) {
						int c = adj[x][n3];
						int st = isAdjacent(a, b) + isAdjacent(a, c) + isAdjacent(b, c);
						if (st < 2)
							continue;
						common3[a][b][c]++;
						common3[c][b][a]++;
						common3[a][c][b]++;
						common3[b][c][a]++;
						common3[b][a][c]++;
						common3[c][a][b]++;
					}
				}
			}
		}

		// Precompute triangles that span over edges
		tri = new int[edges.size()];
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

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private int isAdjacent(int a, int b) {
		return this.adjacencyMatrix[a][b];
	}

	/**
	 * 
	 */
	private void countingFullGraphlets() {
		this.C5 = new int[this.adjacencyMatrix[0].length];
		int[] neigh = new int[this.adjacencyMatrix[0].length];
		int[] neigh2 = new int[this.adjacencyMatrix[0].length];
		int nn;
		int nn2;

		for (int x = 0; x < this.adjacencyMatrix[0].length; x++) {
			for (int nx = 0; nx < this.deg[x]; nx++) {
				int y = this.adj[x][nx];
				if (y >= x)
					break;
				nn = 0;
				for (int ny = 0; ny < this.deg[y]; ny++) {
					int z = this.adj[y][ny];
					if (z >= y)
						break;
					if (isAdjacent(x, z) == 1) {
						neigh[nn++] = z;
					}
				}
				for (int i = 0; i < nn; i++) {
					int z = neigh[i];
					nn2 = 0;
					for (int j = i + 1; j < nn; j++) {
						int zz = neigh[j];
						if (isAdjacent(z, zz) == 1) {
							neigh2[nn2++] = zz;
						}
					}
					for (int i2 = 0; i2 < nn2; i2++) {
						int zz = neigh2[i2];
						for (int j2 = i2 + 1; j2 < nn2; j2++) {
							int zzz = neigh2[j2];
							if (isAdjacent(zz, zzz) == 1) {
								this.C5[x]++;
								this.C5[y]++;
								this.C5[z]++;
								this.C5[zz]++;
								this.C5[zzz]++;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	private void buildingEquationSystems() {
		int[] common_x = new int[this.adjacencyMatrix[0].length];
		int[] common_x_list = new int[this.adjacencyMatrix[0].length];
		int[] common_a = new int[this.adjacencyMatrix[0].length];
		int[] common_a_list = new int[this.adjacencyMatrix[0].length];
		int ncx = 0;
		int nca = 0;

		// set up a system of equations relating orbit counts
		for (int x = 0; x < this.adjacencyMatrix[0].length; x++) {

			for (int i = 0; i < ncx; i++)
				common_x[common_x_list[i]] = 0;
			ncx = 0;

			// smaller graphlets
			orbit[x][0] = deg[x];
			for (int nx1 = 0; nx1 < deg[x]; nx1++) {
				int a = adj[x][nx1];
				for (int nx2 = nx1 + 1; nx2 < deg[x]; nx2++) {
					int b = adj[x][nx2];
					if (isAdjacent(a, b) == 1)
						orbit[x][3]++;
					else
						orbit[x][2]++;
				}
				for (int na = 0; na < deg[a]; na++) {
					int b = adj[a][na];
					if (b != x && isAdjacent(x, b) == 0) {
						orbit[x][1]++;
						if (common_x[b] == 0)
							common_x_list[ncx++] = b;
						common_x[b]++;
					}
				}
			}

			int f_71 = 0, f_70 = 0, f_67 = 0, f_66 = 0, f_58 = 0, f_57 = 0; // 14
			int f_69 = 0, f_68 = 0, f_64 = 0, f_61 = 0, f_60 = 0, f_55 = 0, f_48 = 0, f_42 = 0, f_41 = 0; // 13
			int f_65 = 0, f_63 = 0, f_59 = 0, f_54 = 0, f_47 = 0, f_46 = 0, f_40 = 0; // 12
			int f_62 = 0, f_53 = 0, f_51 = 0, f_50 = 0, f_49 = 0, f_38 = 0, f_37 = 0, f_36 = 0; // 8
			int f_44 = 0, f_33 = 0, f_30 = 0, f_26 = 0; // 11
			int f_52 = 0, f_43 = 0, f_32 = 0, f_29 = 0, f_25 = 0; // 10
			int f_56 = 0, f_45 = 0, f_39 = 0, f_31 = 0, f_28 = 0, f_24 = 0; // 9
			int f_35 = 0, f_34 = 0, f_27 = 0, f_18 = 0, f_16 = 0, f_15 = 0; // 4
			int f_17 = 0; // 5
			int f_22 = 0, f_20 = 0, f_19 = 0; // 6
			int f_23 = 0, f_21 = 0; // 7

			for (int nx1 = 0; nx1 < deg[x]; nx1++) {
				int a = inc.get(x).get(nx1).getA(), xa = inc.get(x).get(nx1).getB();

				for (int i = 0; i < nca; i++)
					common_a[common_a_list[i]] = 0;
				nca = 0;
				for (int na = 0; na < deg[a]; na++) {
					int b = adj[a][na];
					for (int nb = 0; nb < deg[b]; nb++) {
						int c = adj[b][nb];
						if (c == a || isAdjacent(a, c) == 1)
							continue;
						if (common_a[c] == 0)
							common_a_list[nca++] = c; // is the same in c++?
						common_a[c]++;
					}
				}

				// x = orbit-14 (tetrahedron)
				for (int nx2 = nx1 + 1; nx2 < deg[x]; nx2++) {
					int b = inc.get(x).get(nx2).getA(), xb = inc.get(x).get(nx2).getB();

					if (isAdjacent(a, b) == 0)
						continue;

					for (int nx3 = nx2 + 1; nx3 < deg[x]; nx3++) {
						int c = inc.get(x).get(nx3).getA(), xc = inc.get(x).get(nx3).getB();
						if (isAdjacent(a, c) == 0 || isAdjacent(b, c) == 0)
							continue;
						orbit[x][14]++;
						f_70 += this.common3[a][b][c] - 1;
						f_71 += (tri[xa] > 2 && tri[xb] > 2) ? (common3[x][a][b]) - 1 : 0;
						f_71 += (tri[xa] > 2 && tri[xc] > 2) ? (common3[x][a][c]) - 1 : 0;
						f_71 += (tri[xb] > 2 && tri[xc] > 2) ? (common3[x][b][c]) - 1 : 0;
						f_67 += tri[xa] - 2 + tri[xb] - 2 + tri[xc] - 2;
						f_66 += common2[a][b] - 2;
						f_66 += common2[a][c] - 2;
						f_66 += common2[b][c] - 2;
						f_58 += deg[x] - 3;
						f_57 += deg[a] - 3 + deg[b] - 3 + deg[c] - 3;
					}
				}

				// x = orbit-13 (diamond)
				for (int nx2 = 0; nx2 < deg[x]; nx2++) {
					int b = inc.get(x).get(nx2).getA(), xb = inc.get(x).get(nx2).getB();
					if (isAdjacent(a, b) == 0)
						continue;
					for (int nx3 = nx2 + 1; nx3 < deg[x]; nx3++) {
						int c = inc.get(x).get(nx3).getA(), xc = inc.get(x).get(nx3).getB();
						if (isAdjacent(a, c) == 0 || isAdjacent(b, c) == 1)
							continue;
						orbit[x][13]++;
						f_69 += (tri[xb] > 1 && tri[xc] > 1) ? (common3[x][b][c] - 1) : 0;
						f_68 += common3[a][b][c] - 1;
						f_64 += common2[b][c] - 2;
						f_61 += tri[xb] - 1 + tri[xc] - 1;
						f_60 += common2[a][b] - 1;
						f_60 += common2[a][c] - 1;
						f_55 += tri[xa] - 2;
						f_48 += deg[b] - 2 + deg[c] - 2;
						f_42 += deg[x] - 3;
						f_41 += deg[a] - 3;
					}
				}

				// x = orbit-12 (diamond)
				for (int nx2 = nx1 + 1; nx2 < deg[x]; nx2++) {
					int b = inc.get(x).get(nx2).getA();
					if (isAdjacent(a, b) == 0)
						continue;
					for (int na = 0; na < deg[a]; na++) {
						int c = inc.get(a).get(na).getA(), ac = inc.get(a).get(na).getB();
						if (c == x || isAdjacent(x, c) == 1 || isAdjacent(b, c) == 0) {
							continue;
						}
						orbit[x][12]++;
						f_65 += (tri[ac] > 1) ? common3[a][b][c] : 0;
						f_63 += common_x[c] - 2;
						f_59 += tri[ac] - 1 + common2[b][c] - 1;
						f_54 += common2[a][b] - 2;
						f_47 += deg[x] - 2;
						f_46 += deg[c] - 2;
						f_40 += deg[a] - 3 + deg[b] - 3;
					}
				}

				// x = orbit-8 (cycle)
				for (int nx2 = nx1 + 1; nx2 < deg[x]; nx2++) {
					int b = inc.get(x).get(nx2).getA(), xb = inc.get(x).get(nx2).getB();
					if (isAdjacent(a, b) == 1)
						continue;
					for (int na = 0; na < deg[a]; na++) {
						int c = inc.get(a).get(na).getA(), ac = inc.get(a).get(na).getB();
						if (c == x || isAdjacent(x, c) == 1 || isAdjacent(b, c) == 0) {
							continue;
						}
						orbit[x][8]++;
						f_62 += (tri[ac] > 0) ? common3[a][b][c] : 0;
						f_53 += tri[xa] + tri[xb];
						f_51 += tri[ac] + common2[c][b];
						f_50 += common_x[c] - 2;
						f_49 += common_a[b] - 2;
						f_38 += deg[x] - 2;
						f_37 += deg[a] - 2 + deg[b] - 2;
						f_36 += deg[c] - 2;
					}
				}

				// x = orbit-11 (paw)
				for (int nx2 = nx1 + 1; nx2 < deg[x]; nx2++) {
					int b = inc.get(x).get(nx2).getA();
					if (isAdjacent(a, b) == 0)
						continue;
					for (int nx3 = 0; nx3 < deg[x]; nx3++) {
						int c = inc.get(x).get(nx3).getA(), xc = inc.get(x).get(nx3).getB();
						if (c == a || c == b || isAdjacent(a, c) == 1 || isAdjacent(b, c) == 1)
							continue;
						orbit[x][11]++;
						f_44 += tri[xc];
						f_33 += deg[x] - 3;
						f_30 += deg[c] - 1;
						f_26 += deg[a] - 2 + deg[b] - 2;
					}
				}

				// x = orbit-10 (paw)
				for (int nx2 = 0; nx2 < deg[x]; nx2++) {
					int b = inc.get(x).get(nx2).getA();
					if (isAdjacent(a, b) == 0)
						continue;
					for (int nb = 0; nb < deg[b]; nb++) {
						int c = inc.get(b).get(nb).getA(), bc = inc.get(b).get(nb).getB();
						if (c == x || c == a || isAdjacent(a, c) == 1 || isAdjacent(x, c) == 1)
							continue;
						orbit[x][10]++;
						f_52 += common_a[c] - 1;
						f_43 += tri[bc];
						f_32 += deg[b] - 3;
						f_29 += deg[c] - 1;
						f_25 += deg[a] - 2;
					}
				}

				// x = orbit-9 (paw)
				for (int na1 = 0; na1 < deg[a]; na1++) {
					int b = inc.get(a).get(na1).getA(), ab = inc.get(a).get(na1).getB();
					if (b == x || isAdjacent(x, b) == 1)
						continue;
					for (int na2 = na1 + 1; na2 < deg[a]; na2++) {
						int c = inc.get(a).get(na2).getA(), ac = inc.get(a).get(na2).getB();
						if (c == x || isAdjacent(b, c) == 0 || isAdjacent(x, c) == 1)
							continue;
						orbit[x][9]++;
						f_56 += (tri[ab] > 1 && tri[ac] > 1) ? common3[a][b][c] : 0;
						f_45 += common2[b][c] - 1;
						f_39 += tri[ab] - 1 + tri[ac] - 1;
						f_31 += deg[a] - 3;
						f_28 += deg[x] - 1;
						f_24 += deg[b] - 2 + deg[c] - 2;
					}
				}

				// x = orbit-4 (path)
				for (int na = 0; na < deg[a]; na++) {
					int b = inc.get(a).get(na).getA();
					if (b == x || isAdjacent(x, b) == 1)
						continue;
					for (int nb = 0; nb < deg[b]; nb++) {
						int c = inc.get(b).get(nb).getA(), bc = inc.get(b).get(nb).getB();
						if (c == a || isAdjacent(a, c) == 1 || isAdjacent(x, c) == 1)
							continue;
						orbit[x][4]++;
						f_35 += common_a[c] - 1;
						f_34 += common_x[c];
						f_27 += tri[bc];
						f_18 += deg[b] - 2;
						f_16 += deg[x] - 1;
						f_15 += deg[c] - 1;
					}
				}

				// x = orbit-5 (path)
				for (int nx2 = 0; nx2 < deg[x]; nx2++) {
					int b = inc.get(x).get(nx2).getA();
					if (b == a || isAdjacent(a, b) == 1)
						continue;
					for (int nb = 0; nb < deg[b]; nb++) {
						int c = inc.get(b).get(nb).getA();
						if (c == x || isAdjacent(a, c) == 1 || isAdjacent(x, c) == 1)
							continue;
						orbit[x][5]++;
						f_17 += deg[a] - 1;
					}
				}

				// x = orbit-6 (claw)
				for (int na1 = 0; na1 < deg[a]; na1++) {
					int b = inc.get(a).get(na1).getA();
					if (b == x || isAdjacent(x, b) == 1)
						continue;
					for (int na2 = na1 + 1; na2 < deg[a]; na2++) {
						int c = inc.get(a).get(na2).getA();
						if (c == x || isAdjacent(x, c) == 1 || isAdjacent(b, c) == 1)
							continue;
						orbit[x][6]++;
						f_22 += deg[a] - 3;
						f_20 += deg[x] - 1;
						f_19 += deg[b] - 1 + deg[c] - 1;
					}
				}

				// x = orbit-7 (claw)
				for (int nx2 = nx1 + 1; nx2 < deg[x]; nx2++) {
					int b = inc.get(x).get(nx2).getA();
					if (isAdjacent(a, b) == 1)
						continue;
					for (int nx3 = nx2 + 1; nx3 < deg[x]; nx3++) {
						int c = inc.get(x).get(nx3).getA();
						if (isAdjacent(a, c) == 1 || isAdjacent(b, c) == 1)
							continue;
						orbit[x][7]++;
						f_23 += deg[x] - 3;
						f_21 += deg[a] - 1 + deg[b] - 1 + deg[c] - 1;
					}
				}
			}

			// solve equations
			orbit[x][72] = this.C5[x];
			orbit[x][71] = (f_71 - 12 * orbit[x][72]) / 2;
			orbit[x][70] = (f_70 - 4 * orbit[x][72]);
			orbit[x][69] = (f_69 - 2 * orbit[x][71]) / 4;
			orbit[x][68] = (f_68 - 2 * orbit[x][71]);
			orbit[x][67] = (f_67 - 12 * orbit[x][72] - 4 * orbit[x][71]);
			orbit[x][66] = (f_66 - 12 * orbit[x][72] - 2 * orbit[x][71] - 3 * orbit[x][70]);
			orbit[x][65] = (f_65 - 3 * orbit[x][70]) / 2;
			orbit[x][64] = (f_64 - 2 * orbit[x][71] - 4 * orbit[x][69] - 1 * orbit[x][68]);
			orbit[x][63] = (f_63 - 3 * orbit[x][70] - 2 * orbit[x][68]);
			orbit[x][62] = (f_62 - 1 * orbit[x][68]) / 2;
			orbit[x][61] = (f_61 - 4 * orbit[x][71] - 8 * orbit[x][69] - 2 * orbit[x][67]) / 2;
			orbit[x][60] = (f_60 - 4 * orbit[x][71] - 2 * orbit[x][68] - 2 * orbit[x][67]);
			orbit[x][59] = (f_59 - 6 * orbit[x][70] - 2 * orbit[x][68] - 4 * orbit[x][65]);
			orbit[x][58] = (f_58 - 4 * orbit[x][72] - 2 * orbit[x][71] - 1 * orbit[x][67]);
			orbit[x][57] = (f_57 - 12 * orbit[x][72] - 4 * orbit[x][71] - 3 * orbit[x][70] - 1 * orbit[x][67]
					- 2 * orbit[x][66]);
			orbit[x][56] = (f_56 - 2 * orbit[x][65]) / 3;
			orbit[x][55] = (f_55 - 2 * orbit[x][71] - 2 * orbit[x][67]) / 3;
			orbit[x][54] = (f_54 - 3 * orbit[x][70] - 1 * orbit[x][66] - 2 * orbit[x][65]) / 2;
			orbit[x][53] = (f_53 - 2 * orbit[x][68] - 2 * orbit[x][64] - 2 * orbit[x][63]);
			orbit[x][52] = (f_52 - 2 * orbit[x][66] - 2 * orbit[x][64] - 1 * orbit[x][59]) / 2;
			orbit[x][51] = (f_51 - 2 * orbit[x][68] - 2 * orbit[x][63] - 4 * orbit[x][62]);
			orbit[x][50] = (f_50 - 1 * orbit[x][68] - 2 * orbit[x][63]) / 3;
			orbit[x][49] = (f_49 - 1 * orbit[x][68] - 1 * orbit[x][64] - 2 * orbit[x][62]) / 2;
			orbit[x][48] = (f_48 - 4 * orbit[x][71] - 8 * orbit[x][69] - 2 * orbit[x][68] - 2 * orbit[x][67]
					- 2 * orbit[x][64] - 2 * orbit[x][61] - 1 * orbit[x][60]);
			orbit[x][47] = (f_47 - 3 * orbit[x][70] - 2 * orbit[x][68] - 1 * orbit[x][66] - 1 * orbit[x][63]
					- 1 * orbit[x][60]);
			orbit[x][46] = (f_46 - 3 * orbit[x][70] - 2 * orbit[x][68] - 2 * orbit[x][65] - 1 * orbit[x][63]
					- 1 * orbit[x][59]);
			orbit[x][45] = (f_45 - 2 * orbit[x][65] - 2 * orbit[x][62] - 3 * orbit[x][56]);
			orbit[x][44] = (f_44 - 1 * orbit[x][67] - 2 * orbit[x][61]) / 4;
			orbit[x][43] = (f_43 - 2 * orbit[x][66] - 1 * orbit[x][60] - 1 * orbit[x][59]) / 2;
			orbit[x][42] = (f_42 - 2 * orbit[x][71] - 4 * orbit[x][69] - 2 * orbit[x][67] - 2 * orbit[x][61]
					- 3 * orbit[x][55]);
			orbit[x][41] = (f_41 - 2 * orbit[x][71] - 1 * orbit[x][68] - 2 * orbit[x][67] - 1 * orbit[x][60]
					- 3 * orbit[x][55]);
			orbit[x][40] = (f_40 - 6 * orbit[x][70] - 2 * orbit[x][68] - 2 * orbit[x][66] - 4 * orbit[x][65]
					- 1 * orbit[x][60] - 1 * orbit[x][59] - 4 * orbit[x][54]);
			orbit[x][39] = (f_39 - 4 * orbit[x][65] - 1 * orbit[x][59] - 6 * orbit[x][56]) / 2;
			orbit[x][38] = (f_38 - 1 * orbit[x][68] - 1 * orbit[x][64] - 2 * orbit[x][63] - 1 * orbit[x][53]
					- 3 * orbit[x][50]);
			orbit[x][37] = (f_37 - 2 * orbit[x][68] - 2 * orbit[x][64] - 2 * orbit[x][63] - 4 * orbit[x][62]
					- 1 * orbit[x][53] - 1 * orbit[x][51] - 4 * orbit[x][49]);
			orbit[x][36] = (f_36 - 1 * orbit[x][68] - 2 * orbit[x][63] - 2 * orbit[x][62] - 1 * orbit[x][51]
					- 3 * orbit[x][50]);
			orbit[x][35] = (f_35 - 1 * orbit[x][59] - 2 * orbit[x][52] - 2 * orbit[x][45]) / 2;
			orbit[x][34] = (f_34 - 1 * orbit[x][59] - 2 * orbit[x][52] - 1 * orbit[x][51]) / 2;
			orbit[x][33] = (f_33 - 1 * orbit[x][67] - 2 * orbit[x][61] - 3 * orbit[x][58] - 4 * orbit[x][44]
					- 2 * orbit[x][42]) / 2;
			orbit[x][32] = (f_32 - 2 * orbit[x][66] - 1 * orbit[x][60] - 1 * orbit[x][59] - 2 * orbit[x][57]
					- 2 * orbit[x][43] - 2 * orbit[x][41] - 1 * orbit[x][40]) / 2;
			orbit[x][31] = (f_31 - 2 * orbit[x][65] - 1 * orbit[x][59] - 3 * orbit[x][56] - 1 * orbit[x][43]
					- 2 * orbit[x][39]);
			orbit[x][30] = (f_30 - 1 * orbit[x][67] - 1 * orbit[x][63] - 2 * orbit[x][61] - 1 * orbit[x][53]
					- 4 * orbit[x][44]);
			orbit[x][29] = (f_29 - 2 * orbit[x][66] - 2 * orbit[x][64] - 1 * orbit[x][60] - 1 * orbit[x][59]
					- 1 * orbit[x][53] - 2 * orbit[x][52] - 2 * orbit[x][43]);
			orbit[x][28] = (f_28 - 2 * orbit[x][65] - 2 * orbit[x][62] - 1 * orbit[x][59] - 1 * orbit[x][51]
					- 1 * orbit[x][43]);
			orbit[x][27] = (f_27 - 1 * orbit[x][59] - 1 * orbit[x][51] - 2 * orbit[x][45]) / 2;
			orbit[x][26] = (f_26 - 2 * orbit[x][67] - 2 * orbit[x][63] - 2 * orbit[x][61] - 6 * orbit[x][58]
					- 1 * orbit[x][53] - 2 * orbit[x][47] - 2 * orbit[x][42]);
			orbit[x][25] = (f_25 - 2 * orbit[x][66] - 2 * orbit[x][64] - 1 * orbit[x][59] - 2 * orbit[x][57]
					- 2 * orbit[x][52] - 1 * orbit[x][48] - 1 * orbit[x][40]) / 2;
			orbit[x][24] = (f_24 - 4 * orbit[x][65] - 4 * orbit[x][62] - 1 * orbit[x][59] - 6 * orbit[x][56]
					- 1 * orbit[x][51] - 2 * orbit[x][45] - 2 * orbit[x][39]);
			orbit[x][23] = (f_23 - 1 * orbit[x][55] - 1 * orbit[x][42] - 2 * orbit[x][33]) / 4;
			orbit[x][22] = (f_22 - 2 * orbit[x][54] - 1 * orbit[x][40] - 1 * orbit[x][39] - 1 * orbit[x][32]
					- 2 * orbit[x][31]) / 3;
			orbit[x][21] = (f_21 - 3 * orbit[x][55] - 3 * orbit[x][50] - 2 * orbit[x][42] - 2 * orbit[x][38]
					- 2 * orbit[x][33]);
			orbit[x][20] = (f_20 - 2 * orbit[x][54] - 2 * orbit[x][49] - 1 * orbit[x][40] - 1 * orbit[x][37]
					- 1 * orbit[x][32]);
			orbit[x][19] = (f_19 - 4 * orbit[x][54] - 4 * orbit[x][49] - 1 * orbit[x][40] - 2 * orbit[x][39]
					- 1 * orbit[x][37] - 2 * orbit[x][35] - 2 * orbit[x][31]);
			orbit[x][18] = (f_18 - 1 * orbit[x][59] - 1 * orbit[x][51] - 2 * orbit[x][46] - 2 * orbit[x][45]
					- 2 * orbit[x][36] - 2 * orbit[x][27] - 1 * orbit[x][24]) / 2;
			orbit[x][17] = (f_17 - 1 * orbit[x][60] - 1 * orbit[x][53] - 1 * orbit[x][51] - 1 * orbit[x][48]
					- 1 * orbit[x][37] - 2 * orbit[x][34] - 2 * orbit[x][30]) / 2;
			orbit[x][16] = (f_16 - 1 * orbit[x][59] - 2 * orbit[x][52] - 1 * orbit[x][51] - 2 * orbit[x][46]
					- 2 * orbit[x][36] - 2 * orbit[x][34] - 1 * orbit[x][29]);
			orbit[x][15] = (f_15 - 1 * orbit[x][59] - 2 * orbit[x][52] - 1 * orbit[x][51] - 2 * orbit[x][45]
					- 2 * orbit[x][35] - 2 * orbit[x][34] - 2 * orbit[x][27]);
		}
	}
}
