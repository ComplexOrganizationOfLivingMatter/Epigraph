/**
 * 
 */
package epigraph;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Model of the table on the main window
 * 
 * @author Pedro Gomez-Galvez
 */
public class JTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames = { "Color", "Label", "Epi-Hexagons", "Epi-Random", "Epi-Voronoi5", "% Hexagons", "Radius", "Shape",
			"Kind", "<html><center>Closest<br>diagram</html>", "<html><center>Confidence<br>score</html>", "✓ Select all" };

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

	public class MyRenderer extends JLabel implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int col) {
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			String s = "";
			if (col == 3) {
				DecimalFormat dFormat = new DecimalFormat("#0.0000");
				Double d = (Double) value;
				s = dFormat.format(d);
				c = renderer.getTableCellRendererComponent(table, s, isSelected, hasFocus, row, col);
				((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
			}
			return c;
		}
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
			return allGraphletImages.get(row).getDistanceGDDV5();
		case 5:
			return allGraphletImages.get(row).getPercentageOfHexagons();
		case 8:
			return listOfModes.get(row);
		case 9:
			double[][] closestDiagrams = allGraphletImages.get(row).getClosestDiagrams();
			return (int) closestDiagrams[0][0];
		case 10:
			double[][] closestDiagrams1 = allGraphletImages.get(row).getClosestDiagrams();
			return closestDiagrams1[0][2];
		case 11:
			return listOfSelected.get(row);
		case 6:
			return allGraphletImages.get(row).getRadiusOfMask();
		case 7:
			if (allGraphletImages.get(row).getShapeOfMask() == GraphletImage.CIRCLE_SHAPE) {
				return "Circle";
			} else if (allGraphletImages.get(row).getShapeOfMask() == GraphletImage.SQUARE_SHAPE) {
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
		if (col != 6 && col != 7 && col != 10 && col != 9)
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
			allGraphletImages.get(row).setDistanceGDDV5((float) value);
			break;
		case 5:
			allGraphletImages.get(row).setPercentageOfHexagons((float) value);
			break;
		case 8:
			listOfModes.set(row, (String) value);
			break;
		case 11:
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
	 * @return the list of selected columns
	 */
	public ArrayList<Boolean> getListOfSelected() {
		return listOfSelected;
	}

	/**
	 * @param listOfSelected
	 *            the list of selected cells
	 */
	public void setListOfVisualizing(ArrayList<Boolean> listOfSelected) {
		this.listOfSelected = listOfSelected;
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
	 * @param newImages new images to add
	 * @param index 
	 */
	public void addImages(int index,ArrayList<BasicGraphletImage> newImages, String graphletsMode) {
		allGraphletImages.add(newImages.get(index));
		listOfSelected.add(true);
		listOfModes.add(graphletsMode);
			
		fireTableDataChanged();
	}

	/**
	 * 
	 * @param newGraphletImage new image to add
	 * @param graphletsMode the setup (MO kind).
	 */
	public void addImage(BasicGraphletImage newGraphletImage, String graphletsMode) {
		allGraphletImages.add(newGraphletImage);
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

	/**
	 * Select or deselect columns to be deleted or visualized
	 * 
	 */
	public void selectAll() {
		if (allSelectedEquals(listOfSelected) == true) {
			if (listOfSelected.get(0) == true) {
				for (int i = 0; i < listOfSelected.size(); i++) {
					listOfSelected.set(i, false);
				}
			} else {
				for (int i = 0; i < listOfSelected.size(); i++) {
					listOfSelected.set(i, true);
				}
			}
		} else {
			for (int i = 0; i < listOfSelected.size(); i++) {
				listOfSelected.set(i, true);
			}
		}

		fireTableDataChanged();
	}

	/**
	 * Select/deselect all the table rows depending on the first value
	 * @param listOfSelected if the rows are selected or deselected
	 * @return if all the rows have the same value as in the first (selected or deselected)
	 */
	public boolean allSelectedEquals(ArrayList<Boolean> listOfSelected) {
		boolean bolVar = listOfSelected.get(0);
		for (int i = 0; i < listOfSelected.size(); i++) {
			if (listOfSelected.get(i) != bolVar) {
				return false;
			}
		}
		return true;
	}

}