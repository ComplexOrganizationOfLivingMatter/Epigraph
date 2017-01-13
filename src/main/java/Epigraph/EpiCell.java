/**
 * 
 */
package epigraph;

import java.util.HashSet;
import java.util.stream.IntStream;

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

	private int[] graphlets;
	private int[] pixelsY;
	private int[] pixelsX;

	private boolean selected;

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
		return valid_cell;
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
		return pixelsY;
	}

	/**
	 * @param pixelsY the pixelsY to set
	 */
	public void setPixelsY(int[] pixelsY) {
		this.pixelsY = pixelsY;
	}

	/**
	 * @return the pixelsX
	 */
	public int[] getPixelsX() {
		return pixelsX;
	}

	/**
	 * @param pixelsX the pixelsX to set
	 */
	public void setPixelsX(int[] pixelsX) {
		this.pixelsX = pixelsX;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the pixels
	 */
	public int[][] getPixels() {
		int[][] pixels = new int[pixelsX.length][2];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i][0] = pixelsX[i];
			pixels[i][1] = pixelsY[i];
		}
		return pixels;
	}

	/**
	 * @return the pixels
	 */
	public int[] getCentroid() {
		int[] centroid = new int[2];
		centroid[0] = (int) IntStream.of(pixelsX).average().getAsDouble();
		centroid[1] = (int) IntStream.of(pixelsY).average().getAsDouble();
		return centroid;
	}

	/**
	 * 
	 * @param newPixelX
	 *            pixel to add only coordinate X
	 * @param newPixelY
	 *            coordinate Y
	 */
	public void addPixel(int newPixelX, int newPixelY) {
		int[] newPixelsX;
		int[] newPixelsY;

		if (this.pixelsX == null) {
			newPixelsX = new int[1];
			newPixelsY = new int[1];
			newPixelsX[0] = newPixelX;
			newPixelsY[0] = newPixelY;
		} else {
			newPixelsX = new int[this.pixelsX.length + 1];
			newPixelsY = new int[this.pixelsY.length + 1];
			// Copying the old array into the new one
			for (int i = 0; i < this.pixelsX.length; i++) {
				newPixelsX[i] = this.pixelsX[i];
				newPixelsY[i] = this.pixelsY[i];
			}

			// Adding the new ones
			newPixelsX[this.pixelsX.length] = newPixelX;
			newPixelsY[this.pixelsY.length] = newPixelY;
		}

		this.pixelsX = newPixelsX;
		this.pixelsY = newPixelsY;
	}
	
	public boolean searchSelectedPixel(int x1, int y1) {
		for (int x = 0; x < this.pixelsX.length; x++){
			if (this.pixelsX[x] == x1){
				if (this.pixelsY[x] == y1){
					this.setSelected(this.isSelected());
					return true;
				}
			}
		}
		return false;
	}
}
