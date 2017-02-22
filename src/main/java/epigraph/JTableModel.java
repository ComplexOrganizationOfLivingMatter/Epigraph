/**
 * 
 */
package epigraph;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * Model of the table on the main window
 * 
 * @author Pedro Gomez-Galvez
 */
class JTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames = { "Color", "Label", "GDDH", "GDDRV", "% Hexagons", "Radius", "Shape", "Kind", "Selected" };

	private ArrayList<BasicGraphletImage> allGraphletImages;
	private ArrayList<Boolean> listOfSelected;
	private ArrayList<String> listOfModes;

	/**
	 * Construct by default
	 */
	public JTableModel() {
		super();
		allGraphletImages = new ArrayList<BasicGraphletImage>();
		listOfSelected = new ArrayList<Boolean>();
		listOfModes = new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return allGraphletImages.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
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
		case 7:
			return listOfModes.get(row);
		case 8:
			return listOfSelected.get(row);
		case 5:
			return allGraphletImages.get(row).getRadiusOfMask();
		case 6:
			if (allGraphletImages.get(row).getShapeOfMask() == GraphletImage.CIRCLE_SHAPE){
				return "Circle";
			} else if (allGraphletImages.get(row).getShapeOfMask() == GraphletImage.SQUARE_SHAPE){
				return "Square";
			}
		}
		return null;
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box. (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int col) {
		if (col != 5 && col != 6)
			return true;
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 * int, int)
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
		case 7:
			listOfModes.set(row, (String) value);
			break;
		case 8:
			listOfSelected.set(row, (Boolean) value);
			break;
		}
		// Updating table
		fireTableCellUpdated(row, col);
	}

	/**
	 * @return the allGraphletImages
	 */
	public ArrayList<BasicGraphletImage> getAllGraphletImages() {
		return allGraphletImages;
	}

	/**
	 * @param allGraphletImages
	 *            the allGraphletImages to set
	 */
	public void setAllGraphletImages(ArrayList<BasicGraphletImage> allGraphletImages) {
		this.allGraphletImages = allGraphletImages;
	}

	/**
	 * @return the listOfVisualizing
	 */
	public ArrayList<Boolean> getListOfVisualizing() {
		return listOfSelected;
	}

	/**
	 * @param listOfVisualizing
	 *            the listOfVisualizing to set
	 */
	public void setListOfVisualizing(ArrayList<Boolean> listOfVisualizing) {
		this.listOfSelected = listOfVisualizing;
	}

	/**
	 * @return the listOfModes
	 */
	public ArrayList<String> getListOfModes() {
		return listOfModes;
	}

	/**
	 * @param listOfModes
	 *            the listOfModes to set
	 */
	public void setListOfModes(ArrayList<String> listOfModes) {
		this.listOfModes = listOfModes;
	}

	/**
	 * 
	 * @param newImages
	 */
	public void addImages(ArrayList<BasicGraphletImage> newImages) {
		allGraphletImages.addAll(newImages);

		for (int i = 0; i < newImages.size(); i++) {
			listOfSelected.add(true);
		}
		fireTableDataChanged();
	}

	/**
	 * 
	 * @param newImage
	 * @param graphletsMode
	 */
	public void addImage(BasicGraphletImage newImage, String graphletsMode) {
		allGraphletImages.add(new BasicGraphletImage(newImage));
		listOfSelected.add(true);
		listOfModes.add(graphletsMode);

		fireTableDataChanged();
	}

	/**
	 * Remove row when selected
	 */
	public void deleteRow() {
		int cont = 0;
		while (cont < listOfSelected.size()) {
			if (listOfSelected.get(cont)) {
				allGraphletImages.remove(cont);
				listOfSelected.remove(cont);
				listOfModes.remove(cont);
				fireTableRowsDeleted(cont, cont);
			} else {
				cont++;
			}

		}
	}

}