/**
 * 
 */
package epigraph;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Pablo Vicente-Munuera
 *
 *Define all cell properties. Each cell image will be an Epicell
 *
 */
public class EpiCell {
	private int id;

	private HashSet<Integer> neighbours;
	private boolean valid_cell;
	private boolean valid_cell_4;
	private boolean valid_cell_5;
	private boolean invalidRegion;

	private int[] graphlets;
	private ArrayList<Integer> pixelsY;
	private ArrayList<Integer> pixelsX;

	private boolean selected;
	private boolean withinTheRange;

	/**
	 * Constructor by default
	 */
	public EpiCell() {
		super();
		this.id = -1;
		this.neighbours = null;
		this.valid_cell = true;
		this.valid_cell_4 = false;
		this.valid_cell_5 = false;
		this.graphlets = null;
		this.pixelsY = null;
		this.pixelsX = null;
		this.selected = false;
		this.invalidRegion = false;
		this.pixelsY = new ArrayList<Integer>();
		this.pixelsX = new ArrayList<Integer>();
	}

	/**
	 * Constructor from an id, the rest by just initialize
	 * 
	 * @param id
	 *  identifier (label)
	 */
	public EpiCell(int id) {
		super();
		this.id = id;
		this.neighbours = null;
		this.valid_cell = true;
		this.valid_cell_4 = false;
		this.valid_cell_5 = false;
		this.graphlets = null;
		this.pixelsY = null;
		this.pixelsX = null;
		this.selected = false;
		this.invalidRegion = false;
		this.pixelsY = new ArrayList<Integer>();
		this.pixelsX = new ArrayList<Integer>();
	}

	/**
	 * Constructor with parameters
	 * 
	 * @param id
	 *            identifier
	 * @param neighbours
	 *            neighbors of the cells
	 * @param valid_cell
	 *            valid cells to compute
	 * @param valid_cell_4
	 *            valid cells in a 4-length path
	 * @param valid_cell_5
	 *            valid cells in a 5-length path
	 */
	public EpiCell(int id, HashSet<Integer> neighbours, boolean valid_cell, boolean valid_cell_4,
			boolean valid_cell_5) {
		super();
		this.id = id;
		this.neighbours = neighbours;
		this.valid_cell = valid_cell;
		this.valid_cell_4 = valid_cell_4;
		this.valid_cell_5 = valid_cell_5;
		this.graphlets = null;
		this.pixelsY = null;
		this.pixelsX = null;
		this.selected = false;
		this.invalidRegion = false;
		this.pixelsY = new ArrayList<Integer>();
		this.pixelsX = new ArrayList<Integer>();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *  set id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return 
	 * set of neighbours
	 */
	public HashSet<Integer> getNeighbours() {
		return neighbours;
	}

	/**
	 * @param neighbours
	 * neighbours to set
	 */
	public void setNeighbours(HashSet<Integer> neighbours) {
		this.neighbours = neighbours;
	}

	/**
	 * @return 
	 * if cell is valid
	 */
	public boolean isValid_cell() {
		return valid_cell && !invalidRegion;
	}

	/**
	 * @param valid_cell
	 * set valid cell property
	 */
	public void setValid_cell(boolean valid_cell) {
		this.valid_cell = valid_cell;
	}

	/**
	 * @return if a cell is a valid cell 4'
	 */
	public boolean isValid_cell_4() {
		return valid_cell_4;
	}

	/**
	 * @param valid_cell_4
	 * set the property valid cell 4' in an cell
	 */
	public void setValid_cell_4(boolean valid_cell_4) {
		this.valid_cell_4 = valid_cell_4;
	}

	/**
	 * @return if a cell is a valid cell 5'
	 */
	public boolean isValid_cell_5() {
		return valid_cell_5;
	}

	/**
	 * @param valid_cell_5
	 * set the property valid cell 5' in an cell	 
	 */
	public void setValid_cell_5(boolean valid_cell_5) {
		this.valid_cell_5 = valid_cell_5;
	}

	/**
	 * @return graphlets int values
	 */
	public int[] getGraphlets() {
		return graphlets;
	}

	/**
	 * @return graphlets int values
	 */
	public String[] getGraphletsString() {
		String[] graphletStr = new String[BasicGraphlets.TOTALGRAPHLETS];

		for (int i = 0; i < this.graphlets.length; i++) {
			graphletStr[i] = Integer.toString(graphlets[i]);
		}

		return graphletStr;
	}

	/**
	 * 
	 * @param graphletsWeDontWant
	 *            the graphlets we dont want, will be zeros
	 * @return filtered graphlets
	 */
	public Integer[] getGraphletsInteger(int[] graphletsWeDontWant) {
		Integer[] graph = new Integer[graphlets.length];
		int graphletIndex = 0;
		for (int i = 0; i < graphlets.length; i++) {
			if (graphletIndex >= graphletsWeDontWant.length || i != graphletsWeDontWant[graphletIndex]) {
				graph[i] = graphlets[i];
			} else {
				graph[i] = 0;
				graphletIndex++;
			}

		}
		return graph;
	}

	/**
	 * @param graphlets2
	 *            set the graphlets values 
	 */
	public void setGraphlets(int[] graphlets2) {
		this.graphlets = graphlets2;
	}

	/**
	 * 
	 * @return
	 * coordinates Y from all cell pixels
	 */
	public int[] getPixelsY() {
		int[] pixels = new int[pixelsY.size()];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = pixelsY.get(i);
		}
		return pixels;
	}

	/**
	 * @param pixelsY
	 *   set coordinates Y of all pixels in cell
	 */
	public void setPixelsY(ArrayList<Integer> pixelsY) {
		this.pixelsY = pixelsY;
	}

	/**
	 * 
	 * @return
	 * coordinates X from all cell pixels
	 */
	public int[] getPixelsX() {
		int[] pixels = new int[pixelsX.size()];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = pixelsX.get(i);
		}
		return pixels;
	}

	/**
	 * @param pixelsX
	 *   set coordinates X of all pixels in cell
	 */
	public void setPixelsX(ArrayList<Integer> pixelsX) {
		this.pixelsX = pixelsX;
	}

	/**
	 * @return if cell is selected or not
	 */
	public boolean isSelected() {
		return selected && valid_cell;
	}

	/**
	 * @param selected
	 * set selected property of cell
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return whether or not the cell is within the range of a selected cell
	 */
	public boolean isWithinTheRange() {
		return withinTheRange;
	}

	/**
	 * @param withinTheRange
	 *    set withinTheRange property
	 */
	public void setWithinTheRange(boolean withinTheRange) {
		this.withinTheRange = withinTheRange;
	}

	/**
	 * @return 
	 * if cell is into invalid region
	 * 
	 */
	public boolean isInvalidRegion() {
		return invalidRegion;
	}

	/**
	 * @param invalidRegion
	 * set property invalid region
	 */
	public void setInvalidRegion(boolean invalidRegion) {
		this.invalidRegion = invalidRegion;
	}

	/**
	 * @return all pixels into the cell
	 */
	public int[][] getPixels() {
		int[][] pixels = new int[pixelsX.size()][2];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i][0] = pixelsX.get(i);
			pixels[i][1] = pixelsY.get(i);
		}
		return pixels;
	}

	/**
	 * 
	 * @param newPixelX
	 *            pixel to add only coordinate X
	 * @param newPixelY
	 *            coordinate Y
	 */
	public void addPixel(int newPixelX, int newPixelY) {
		this.pixelsX.add(newPixelX);
		this.pixelsY.add(newPixelY);
	}

	/**
	 * If the pixels from the parameters, belongs to this cell
	 * 
	 * @param x1
	 *            pixel X
	 * @param y1
	 *            pixel Y
	 * @return if the pixels are on this cell
	 */
	public int searchSelectedPixel(int x1, int y1) {
		for (int x = 0; x < this.pixelsX.size(); x++) {
			if (this.pixelsX.get(x) == x1) {
				if (this.pixelsY.get(x) == y1) {
					this.setSelected(true);
					return 1;
				}
			}
		}
		return -1;
	}

	/**
	 * Get the centroid of the cell
	 * 
	 * @return pixel representing the centroid of the cell
	 */
	public int[][] getCentroid() {
		float sumX = 0;
		float sumY = 0;
		for (int i = 0; i < pixelsX.size(); i++) {
			sumX += pixelsX.get(i);
			sumY += pixelsY.get(i);
		}

		int centroidX = (int) (sumX / pixelsX.size());
		int centroidY = (int) (sumY / pixelsY.size());

		int[][] centroid = { { centroidX, centroidY } };
		return centroid;
	}
}
