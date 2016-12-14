/**
 * 
 */
package main.java.Epigraph;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.stream.IntStream;


/**
 * @author Equipo
 *
 */
public class EpiCell {
	private int id;
	
	private ArrayList<EpiCell> neighbours;
	private boolean valid_cell;
	private boolean valid_cell_4;
	private boolean valid_cell_5;

	private ArrayList<Integer> graphlets;
	private int[] pixelsY;
	private int[] pixelsX;
	
	
	public EpiCell() {
		super();
		this.id = -1;
		this.neighbours = null;
		this.valid_cell = false;
		this.valid_cell_4 = false;
		this.valid_cell_5 = false;
		this.graphlets = null;
		int[] pixelsY = null;
		int[] pixelsX = null;
	}
	
	/**
	 * @param id
	 */
	public EpiCell(int id) {
		super();
		this.id = id;
		this.neighbours = null;
		this.valid_cell = false;
		this.valid_cell_4 = false;
		this.valid_cell_5 = false;
		this.graphlets = null;
		int[] pixelsY = null;
		int[] pixelsX = null;
	}
	
	/**
	 * @param id identifier
	 * @param neighbours neighbors of the cells
	 * @param valid_cell valid cells to compute
	 * @param valid_cell_4 valid cells in a 4-length path
	 * @param valid_cell_5 valid cells in a 5-length path
	 */
	public EpiCell(int id, ArrayList<EpiCell> neighbours, boolean valid_cell, boolean valid_cell_4,
			boolean valid_cell_5) {
		super();
		this.id = id;
		this.neighbours = neighbours;
		this.valid_cell = valid_cell;
		this.valid_cell_4 = valid_cell_4;
		this.valid_cell_5 = valid_cell_5;
		this.graphlets = null;
		int[] pixelsY = null;
		int[] pixelsX = null;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the neighbours
	 */
	public ArrayList<EpiCell> getNeighbours() {
		return neighbours;
	}

	/**
	 * @param neighbours the neighbours to set
	 */
	public void setNeighbours(ArrayList<EpiCell> neighbours) {
		this.neighbours = neighbours;
	}

	/**
	 * @return the valid_cell
	 */
	public boolean isValid_cell() {
		return valid_cell;
	}

	/**
	 * @param valid_cell the valid_cell to set
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
	 * @param valid_cell_4 the valid_cell_4 to set
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
	 * @param valid_cell_5 the valid_cell_5 to set
	 */
	public void setValid_cell_5(boolean valid_cell_5) {
		this.valid_cell_5 = valid_cell_5;
	}

	/**
	 * @return the graphlets
	 */
	public ArrayList<Integer> getGraphlets() {
		return graphlets;
	}

	/**
	 * @param graphlets the graphlets to set
	 */
	public void setGraphlets(ArrayList<Integer> graphlets) {
		this.graphlets = graphlets;
	}

	/**
	 * @return the pixels
	 */
	public int[][] getPixels() {
		int[][] pixels = new int[pixelsX.length][2];
		for (int i = 0; i < pixels.length; i++){
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
	 * @param pixels the pixels to set
	 */
	public void setPixels(int[][] pixels) {
		for (int i = 0; i < pixels.length; i++){
			this.pixelsX[i] = pixels[i][0];
			this.pixelsY[i] = pixels[i][1];
		}
	}
	
	/**
	 * @param pixels the pixels to set
	 */
	public void addPixel(int[] newPixel) {
		int[] newPixelsX;
		int[] newPixelsY;
		
		if (this.pixelsX == null){
			newPixelsX = new int[1];
			newPixelsY = new int[1];
		} else {
			newPixelsX = new int[this.pixelsX.length+1];
			newPixelsY = new int[this.pixelsY.length+1];
		}
		
		
		//Copying the old array into the new one
		for (int i = 0; i < this.pixelsX.length; i++){
			newPixelsX[i] = this.pixelsX[i];
			newPixelsY[i] = this.pixelsY[i];
		}
		//Adding the new ones
		newPixelsX[this.pixelsX.length] = newPixel[0];
		newPixelsY[this.pixelsY.length] = newPixel[1];
		
		this.pixelsX = newPixelsX;
		this.pixelsY = newPixelsY;
	}
}
