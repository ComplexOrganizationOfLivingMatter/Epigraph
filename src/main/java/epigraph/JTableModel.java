/**
 * 
 */
package epigraph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author Pedro Gomez-Galvez
 *
 */
class JTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames = { "Color", "Label", "GDDH", "GDDRV", "% Hexagons", "Visualizing" };

	private ArrayList<GraphletImage> allGraphletImages;
	private ArrayList<Boolean> listOfVisualizing;
	
	/**
	 * 
	 */
	public JTableModel() {
		super();
		allGraphletImages = new ArrayList<GraphletImage>();
		listOfVisualizing = new ArrayList<Boolean>();
	}

	/**
	 * 
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return allGraphletImages.size();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return allGraphletImages.get(row).getColor();
		case 1:
			return allGraphletImages.get(row).getLabelName();
		case 2:
			return allGraphletImages.get(row).getDistanceGDDH();
		case 3:
			return allGraphletImages.get(row).getDistanceGDDRV();
		case 4:
			return allGraphletImages.get(row).getPercentageOfHexagons();
		case 5:
			return listOfVisualizing.get(row);
		}
		return null;
	}

	/**
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/**
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return true;
	}

	/**
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		switch (col) {
		case 0:
			allGraphletImages.get(row).setColor((Color) value);
			break;
		case 1:
			allGraphletImages.get(row).setLabelName((String) value);
			break;
		case 2:
			allGraphletImages.get(row).setDistanceGDDH((float) value);
			break;
		case 3:
			allGraphletImages.get(row).setDistanceGDDRV((float) value);
			break;
		case 4:
			allGraphletImages.get(row).setPercentageOfHexagons((float) value);
			break;
		case 5:
			listOfVisualizing.set(row, (Boolean) value);
			break;
		}
		//Updating table
		fireTableCellUpdated(row, col);
	}

	/**
	 * @return the allGraphletImages
	 */
	public ArrayList<GraphletImage> getAllGraphletImages() {
		return allGraphletImages;
	}

	/**
	 * @param allGraphletImages
	 *            the allGraphletImages to set
	 */
	public void setAllGraphletImages(ArrayList<GraphletImage> allGraphletImages) {
		this.allGraphletImages = allGraphletImages;
	}

	/**
	 * 
	 * @param newImages
	 */
	public void addImages(ArrayList<GraphletImage> newImages) {
		allGraphletImages.addAll(newImages);
		
		for (int i = 0; i < newImages.size(); i++){
			listOfVisualizing.add(true);
		}
		fireTableDataChanged();
	}
	
	public void addImage(GraphletImage newImage){
		allGraphletImages.add(newImage);
		listOfVisualizing.add(true);

		fireTableDataChanged();
	}

}