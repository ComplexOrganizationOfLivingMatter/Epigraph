/**
 * 
 */
package main.java.Epigraph;

import java.util.ArrayList;

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
	
	/**
	 * @param id
	 * @param neighbours
	 * @param valid_cell
	 * @param valid_cell_4
	 * @param valid_cell_5
	 */
	public EpiCell(int id, ArrayList<EpiCell> neighbours, boolean valid_cell, boolean valid_cell_4,
			boolean valid_cell_5) {
		super();
		this.id = id;
		this.neighbours = neighbours;
		this.valid_cell = valid_cell;
		this.valid_cell_4 = valid_cell_4;
		this.valid_cell_5 = valid_cell_5;
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
}
