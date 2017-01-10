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
	private List<Boolean> listOfVisualizing;

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return allGraphletImages.size() + 1;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return null;
//		switch (column) {
//		case 0:
//			return obj.getId();
//		case 1:
//			return obj.getName();
//		case 2:
//			return obj.getNumber();
//		case 3:
//			return obj.isYes();
//		default:
//			return null;
//		}
//		return data[row][col];
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
		case 0: allGraphletImages.get(row).setColor((Color) value);
			break;
		case 1: allGraphletImages.get(row).setLabelName((String) value);
			break;
		case 2: allGraphletImages.get(row).setDistanceGDDH((float) value);
			break;
		case 3: allGraphletImages.get(row).setDistanceGDDRV((float) value);
			break;
		case 4: allGraphletImages.get(row).setPercentageOfHexagons((float) value);
			break;
		case 5: listOfVisualizing.set(row, (Boolean) value);
			break;
		}
		//allGraphletImages.get(row).setColor(color);
		//data[row][col] = value;
		fireTableCellUpdated(row, col);
	}

	/**
	 * @return the allGraphletImages
	 */
	public ArrayList<GraphletImage> getAllGraphletImages() {
		return allGraphletImages;
	}

	/**
	 * @param allGraphletImages the allGraphletImages to set
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
	}

}