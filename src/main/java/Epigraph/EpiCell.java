/**
 * 
 */
package epigraph;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Pablo Vicente-Munuera
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
	 * 
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
	 * @param id
	 *            identifier
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
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the neighbours
	 */
	public HashSet<Integer> getNeighbours() {
		return neighbours;
	}

	/**
	 * @param neighbours
	 *            the neighbours to set
	 */
	public void setNeighbours(HashSet<Integer> neighbours) {
		this.neighbours = neighbours;
	}

	/**
	 * @return the valid_cell
	 */
	public boolean isValid_cell() {
		return valid_cell && !invalidRegion;
	}

	/**
	 * @param valid_cell
	 *            the valid_cell to set
	 */
	public void setValid_cell(boolean valid_cell) {
		this.valid_cell = valid_cell;
	}

	/**
	 * @return the valid_cell_4
	 */
	public boolean isValid_cell_4() {
		return valid_cell_4;
	}

	/**
	 * @param valid_cell_4
	 *            the valid_cell_4 to set
	 */
	public void setValid_cell_4(boolean valid_cell_4) {
		this.valid_cell_4 = valid_cell_4;
	}

	/**
	 * @return the valid_cell_5
	 */
	public boolean isValid_cell_5() {
		return valid_cell_5;
	}

	/**
	 * @param valid_cell_5
	 *            the valid_cell_5 to set
	 */
	public void setValid_cell_5(boolean valid_cell_5) {
		this.valid_cell_5 = valid_cell_5;
	}

	/**
	 * @return the graphlets
	 */
	public int[] getGraphlets() {
		return graphlets;
	}

	/**
	 * 
	 * @param graphletsWeDontWant
	 *            the graphlets we dont want, will be zeros
	 * @return the graphlets
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
	 *            the graphlets to set
	 */
	public void setGraphlets(int[] graphlets2) {
		this.graphlets = graphlets2;
	}

	
	public int[] getPixelsY() {
		int[] pixels = new int[pixelsY.size()];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = pixelsY.get(i);
		}
		return pixels;
	}

	/**
	 * @param pixelsY the pixelsY to set
	 */
	public void setPixelsY(ArrayList<Integer> pixelsY) {
		this.pixelsY = pixelsY;
	}

	/**
	 * @return the pixelsX
	 */
	public int[] getPixelsX() {
		int[] pixels = new int[pixelsX.size()];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = pixelsX.get(i);
		}
		return pixels;
	}

	/**
	 * @param pixelsX the pixelsX to set
	 */
	public void setPixelsX(ArrayList<Integer> pixelsX) {
		this.pixelsX = pixelsX;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected && valid_cell;
	}

	/**
	 * @param selected the selected to set
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
	 * @param withinTheRange if the cell is within the range of a selected cell
	 */
	public void setWithinTheRange(boolean withinTheRange) {
		this.withinTheRange = withinTheRange;
	}

	/**
	 * @return the invalidRegion
	 */
	public boolean isInvalidRegion() {
		return invalidRegion;
	}

	/**
	 * @param invalidRegion the invalidRegion to set
	 */
	public void setInvalidRegion(boolean invalidRegion) {
		this.invalidRegion = invalidRegion;
	}

	/**
	 * @return the pixels
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
	
	public int searchSelectedPixel(int x1, int y1) {
		for (int x = 0; x < this.pixelsX.size(); x++){
			if (this.pixelsX.get(x) == x1){
				if (this.pixelsY.get(x) == y1){
					this.setSelected(true);
					return 1;
				}
			}
		}
		return -1;
	}

	public int[][] getCentroid() {
		// TODO Auto-generated method stub
		float sumX = 0;
		float sumY = 0;
		for (int i = 0; i < pixelsX.size(); i++) {
			sumX += pixelsX.get(i);
			sumY += pixelsY.get(i);
		}
		
		int centroidX = (int) (sumX / pixelsX.size());
		int centroidY = (int) (sumY / pixelsY.size());
		
		int[][] centroid = {{centroidX, centroidY}};
		return centroid;
	}
}
