package epigraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import ij.IJ;
import ij.ImagePlus;

/**
 * The main window with all its functionality. It will have a table for all of
 * the processed images and 4 buttons: Open: Open image processing window;
 * Import: XLS to the table; Export: Table to XLS; Visualize: Open the
 * visualization window
 * 
 * @author Pedro Gomez-Galvez, Pablo Vicente-Munuera
 */
public class MainWindow extends JFrame {
	/**
	 * Default serial. Useless
	 */
	private static final long serialVersionUID = 1L;
	public JTableModel tableInfo;
	private JScrollPane scrollPane;
	private JTable table;
	private JPanel panel;
	private JButton btnVisualize;
	private JButton btnOpenButton;
	private JFrame fatherWindow;
	private JButton btnExport;
	private JButton btnImport;

	/**
	 * Constructor by default. Setup all the windows and creates the panel. It
	 * initialize all the GUI items as well.
	 */
	public MainWindow() {
		//3 and 1
		String name = UIManager.getInstalledLookAndFeels()[3].getClassName();
        try {
			UIManager.setLookAndFeel(name);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UIManager.put("Panel.background", Color.WHITE);
		UIManager.put("Slider.background", Color.WHITE);
		fatherWindow = this;
		setMinimumSize(new Dimension(800, 600));
		setTitle("Epigraph");
		// Not close Fiji when Epigraph is closed
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Main panel
		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setLayout(null);
		// Associate this panel to the window
		getContentPane().add(panel);

		initGUIItems();

	}

	/**
	 * Initialize the gui items and set up properly within the window
	 */
	private void initGUIItems() {
		btnVisualize = new JButton("Visualize");
		btnVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					VisualizingWindow visualizingWindow = new VisualizingWindow(fatherWindow, tableInfo);
					visualizingWindow.setVisible(true);
				} catch (Exception e) {
					String msg = String.format("Unexpected problem: %s", e.getCause().toString());
					JOptionPane.showMessageDialog(panel.getParent(), msg, "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		btnOpenButton = new JButton("Open");
		btnOpenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						initImageProcessingWindow();
					}
				});
			}
		});

		// Create table and scroll pane
		this.tableInfo = new JTableModel();
		table = new JTable(tableInfo);
		table.setRowSelectionAllowed(false);
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new JColorEditor());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(2).setMaxWidth(47);
		table.getColumnModel().getColumn(2).setMinWidth(47);
		table.getColumnModel().getColumn(3).setMaxWidth(55);
		table.getColumnModel().getColumn(3).setMinWidth(55);
		table.getColumnModel().getColumn(4).setMinWidth(80);
		table.getColumnModel().getColumn(4).setMaxWidth(80);
		table.getColumnModel().getColumn(5).setMaxWidth(150);
		table.getColumnModel().getColumn(5).setMinWidth(150);
		table.getColumnModel().getColumn(6).setMaxWidth(70);
		table.getColumnModel().getColumn(6).setMinWidth(70);

		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		btnExport = new JButton("Export table");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportTableToXLS();
			}
		});

		btnImport = new JButton("Import table");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importXLSToTable();
			}
		});

		btnImport.setBounds(240, 496, 105, 29);
		btnExport.setBounds(431, 496, 105, 29);
		btnVisualize.setBounds(607, 496, 93, 29);
		scrollPane.setBounds(15, 27, 765, 425);
		btnOpenButton.setBounds(92, 496, 71, 29);

		panel.add(scrollPane);
		panel.add(btnOpenButton);
		panel.add(btnVisualize);
		panel.add(btnImport);
		panel.add(btnExport);
	}

	/**
	 * Create the image processing window. However, restrictions are applied
	 * with the selected image. It cannot exceed 3000 neither in width nor
	 * height.
	 */
	public void initImageProcessingWindow() {
		try {
			ImagePlus raw_img = IJ.openImage();
			if (raw_img != null) {
				if (raw_img.getHeight() < 3000 || raw_img.getWidth() < 3000) {
					ImageProcessingWindow imageProcessing = new ImageProcessingWindow(raw_img, tableInfo);
					imageProcessing.pack();
				} else {
					JOptionPane.showMessageDialog(panel.getParent(),
							"Max. width or height is 3000px. Please, resize it.");
				}

			} else {
				JOptionPane.showMessageDialog(panel.getParent(), "You must introduce a valid image or set of images.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Export all the information inside the table to a .xls file
	 */
	public void exportTableToXLS() {
		ArrayList<String> arrayNames = new ArrayList<String>();
		ArrayList<Float> arrayHexagons = new ArrayList<Float>();
		ArrayList<Float> arrayGDDH = new ArrayList<Float>();
		ArrayList<Float> arrayGDDRV = new ArrayList<Float>();
		ArrayList<Float> arrayR = new ArrayList<Float>();
		ArrayList<Float> arrayG = new ArrayList<Float>();
		ArrayList<Float> arrayB = new ArrayList<Float>();
		ArrayList<String> arrayMode = new ArrayList<String>();

		int cont = 0;
		for (BasicGraphletImage graphletImg : tableInfo.getAllGraphletImages()) {

			arrayNames.add(graphletImg.getLabelName());
			arrayHexagons.add(graphletImg.getPercentageOfHexagons());
			arrayGDDH.add(graphletImg.getDistanceGDDH());
			arrayGDDRV.add(graphletImg.getDistanceGDDRV());
			arrayR.add((float) graphletImg.getColor().getRed());
			arrayG.add((float) graphletImg.getColor().getGreen());
			arrayB.add((float) graphletImg.getColor().getBlue());
			arrayMode.add(tableInfo.getListOfModes().get(cont));
			cont++;
		}

		JFrame parentFrame = new JFrame();
		JFileChooser fileChooser = new JFileChooser();
		// set it to be a save dialog
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		// set a default filename (this is where you default extension
		// first comes in)
		fileChooser.setSelectedFile(new File("myfile.xls"));
		// Set an extension filter, so the user sees other XML files
		fileChooser.setFileFilter(new FileNameExtensionFilter("XLS files", "xls"));

		fileChooser.setAcceptAllFileFilterUsed(false);

		int userSelection = fileChooser.showSaveDialog(parentFrame);
		if (userSelection == JFileChooser.APPROVE_OPTION) {

			String filename = fileChooser.getSelectedFile().toString();
			if (!filename.endsWith(".xls"))
				filename += ".xls";

			ExcelClass excelclass = new ExcelClass(filename, arrayNames, arrayGDDH, arrayGDDRV, arrayHexagons, arrayR,
					arrayG, arrayB, arrayMode);
			excelclass.exportData();
		}
	}

	/**
	 * Import information from an .XLS file to the table
	 */
	public void importXLSToTable() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Import");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xls");
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + chooser.getSelectedFile().getPath());

			ExcelClass excelclass = new ExcelClass();
			excelclass.importData(chooser.getSelectedFile().getPath());
			int flat = 0;
			for (int row = 0; row < excelclass.getImageName().size(); row++) {

				if (flat == 1) {
					tableInfo
							.addImage(
									new BasicGraphletImage((float) excelclass.getRow(row).get(2),
											(float) excelclass.getRow(row).get(1),
											(float) excelclass.getRow(row).get(3),
											new Color(Math.round((float) excelclass.getRow(row).get(4)),
													Math.round((float) excelclass.getRow(row).get(5)), Math
															.round((float) excelclass.getRow(row).get(6))),
											(String) excelclass.getRow(row).get(0)),
									(String) excelclass.getRow(row).get(7));

				} else if (flat == 2) {
					tableInfo
							.addImage(
									new BasicGraphletImage((float) excelclass.getRow(row).get(2),
											(float) excelclass.getRow(row).get(1),
											(float) excelclass.getRow(row).get(3),
											new Color((float) excelclass.getRow(row).get(4),
													(float) excelclass.getRow(row).get(5), (float) excelclass
															.getRow(row).get(6)),
											(String) excelclass.getRow(row).get(0)),
									(String) excelclass.getRow(row).get(7));
				} else {
					if ((float) excelclass.getRow(row).get(4) > 1.0 || (float) excelclass.getRow(row).get(5) > 1.0
							|| (float) excelclass.getRow(row).get(6) > 1.0) {
						flat = 1;
						tableInfo.addImage(
								new BasicGraphletImage((float) excelclass.getRow(row).get(2),
										(float) excelclass.getRow(row).get(1), (float) excelclass.getRow(row).get(3),
										new Color(Math.round((float) excelclass.getRow(row).get(4)),
												Math.round((float) excelclass.getRow(row).get(5)), Math
														.round((float) excelclass.getRow(row).get(6))),
										(String) excelclass.getRow(row).get(0)),
								(String) excelclass.getRow(row).get(7));
					} else if (((float) excelclass.getRow(row).get(4) < 1.0
							& (float) excelclass.getRow(row).get(4) > 1.0)
							|| ((float) excelclass.getRow(row).get(5) < 1.0
									& (float) excelclass.getRow(row).get(5) > 0.0)
							|| ((float) excelclass.getRow(row).get(6) < 1.0
									& (float) excelclass.getRow(row).get(6) > 0.0)) {

						tableInfo.addImage(
								new BasicGraphletImage((float) excelclass.getRow(row).get(2),
										(float) excelclass.getRow(row).get(1), (float) excelclass.getRow(row).get(3),
										new Color((float) excelclass.getRow(row).get(4),
												(float) excelclass.getRow(row).get(5),
												(float) excelclass.getRow(row).get(6)),
										(String) excelclass.getRow(row).get(0)),
								(String) excelclass.getRow(row).get(7));
						flat = 2;
					} else {

						tableInfo.addImage(
								new BasicGraphletImage((float) excelclass.getRow(row).get(2),
										(float) excelclass.getRow(row).get(1), (float) excelclass.getRow(row).get(3),
										new Color((float) excelclass.getRow(row).get(4),
												(float) excelclass.getRow(row).get(5),
												(float) excelclass.getRow(row).get(6)),
										(String) excelclass.getRow(row).get(0)),
								(String) excelclass.getRow(row).get(7));

					}
				}

			}

		}
	}
}
