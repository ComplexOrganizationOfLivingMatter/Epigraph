package epigraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import ij.IJ;
import ij.ImagePlus;
import javax.swing.ScrollPaneConstants;

/**
 * @author Pedro Gomez-Galvez, Pablo Vicente-Munuera
 * 
 *         TableDemo is just like SimpleTableDemo, except that it uses a custom
 *         TableModel.
 */
public class MainWindow extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTableModel tableInfo;
	private JScrollPane scrollPane;
	private JTable table;
	private JPanel panel;
	private JButton btnVisualize;
	private JButton btnOpenButton;

	/**
	 * 
	 */
	public MainWindow() {
		super(new GridLayout(1, 0));

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel);

		// Create buttons
		btnVisualize = new JButton("Visualize");
		btnVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				VisualizingWindow visualizingWindow = new VisualizingWindow(tableInfo);
				visualizingWindow.setVisible(true);
			}
		});
		btnVisualize.setBounds(495, 255, 93, 29);

		btnOpenButton = new JButton("Open");
		btnOpenButton.setBounds(37, 255, 71, 29);
		btnOpenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ImagePlus raw_img = IJ.openImage();
							if (raw_img != null) {
								IJ.log("Initializing...");
								ImageProcessingWindow imageProcessing = new ImageProcessingWindow(raw_img, tableInfo);
								imageProcessing.pack();
							} else {
								JOptionPane.showMessageDialog(panel.getParent(),
										"You must introduce a valid image or set of images.");
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		panel.setLayout(null);

		// Create table and scroll pane
		this.tableInfo = new JTableModel();
		table = new JTable(tableInfo);
		table.setRowSelectionAllowed(false);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
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
		scrollPane.setBounds(15, 27, 598, 215);
		// scrollPane.setPreferredSize(new Dimension(400, 200));

		panel.add(scrollPane);
		panel.add(btnOpenButton);
		panel.add(btnVisualize);

		JButton btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExcelClass excelclass = new ExcelClass();
				ArrayList<String> arrayNames = new ArrayList<String>();
				ArrayList<Float> arrayHexagons = new ArrayList<Float>();
				ArrayList<Float> arrayGDDH = new ArrayList<Float>();
				ArrayList<Float> arraGDDRV = new ArrayList<Float>();
				ArrayList<Float> arrayR = new ArrayList<Float>();
				ArrayList<Float> arrayG = new ArrayList<Float>();
				ArrayList<Float> arrayB = new ArrayList<Float>();

				for (BasicGraphletImage graphletImg : tableInfo.getAllGraphletImages()) {

					arrayNames.add(graphletImg.getLabelName());
					arrayHexagons.add(graphletImg.getPercentageOfHexagons());
					arrayGDDH.add(graphletImg.getDistanceGDDH());
					arraGDDRV.add(graphletImg.getDistanceGDDH());
					arrayR.add((float) graphletImg.getColor().getRed());
					arrayG.add((float) graphletImg.getColor().getGreen());
					arrayB.add((float) graphletImg.getColor().getBlue());

				}
				excelclass.setR(arrayR);
				excelclass.setG(arrayG);
				excelclass.setB(arrayB);
				excelclass.setImageName(arrayNames);
				excelclass.setGddh(arrayGDDH);
				excelclass.setGddrv(arraGDDRV);
				excelclass.setHexagonsPercentage(arrayHexagons);

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

					// DO something with filename
					excelclass.exportData(filename);
				}
			}
		});

		btnExport.setBounds(369, 255, 87, 29);
		panel.add(btnExport);

		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

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
							tableInfo.addImage(
									new BasicGraphletImage((float) excelclass.getRow(row).get(2),
											(float) excelclass.getRow(row).get(1),
											(float) excelclass.getRow(row).get(3),
											new Color(Math.round((float) excelclass.getRow(row).get(4)),
													Math.round((float) excelclass.getRow(row).get(5)), Math
															.round((float) excelclass.getRow(row).get(6))),
											(String) excelclass.getRow(row).get(0)),
									(String) excelclass.getRow(row).get(7));
						} else if (flat == 2) {
							tableInfo.addImage(
									new BasicGraphletImage((float) excelclass.getRow(row).get(2),
											(float) excelclass.getRow(row).get(1),
											(float) excelclass.getRow(row).get(3),
											new Color((float) excelclass.getRow(row).get(4),
													(float) excelclass.getRow(row).get(5), (float) excelclass
															.getRow(row).get(6)),
											(String) excelclass.getRow(row).get(0)),
									(String) excelclass.getRow(row).get(7));
						} else {
							if ((float) excelclass.getRow(row).get(4) > 1.0
									|| (float) excelclass.getRow(row).get(5) > 1.0
									|| (float) excelclass.getRow(row).get(6) > 1.0) {
								flat = 1;
								tableInfo.addImage(
										new BasicGraphletImage((float) excelclass.getRow(row).get(2),
												(float) excelclass.getRow(row).get(1),
												(float) excelclass.getRow(row).get(3),
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
												(float) excelclass.getRow(row).get(1),
												(float) excelclass.getRow(row).get(3),
												new Color((float) excelclass.getRow(row).get(4),
														(float) excelclass.getRow(row).get(5), (float) excelclass
																.getRow(row).get(6)),
												(String) excelclass.getRow(row).get(0)),
										(String) excelclass.getRow(row).get(7));
								flat = 2;
							} else {
								tableInfo.addImage(
										new BasicGraphletImage((float) excelclass.getRow(row).get(2),
												(float) excelclass.getRow(row).get(1),
												(float) excelclass.getRow(row).get(3),
												new Color((float) excelclass.getRow(row).get(4),
														(float) excelclass.getRow(row).get(5), (float) excelclass
																.getRow(row).get(6)),
												(String) excelclass.getRow(row).get(0)),
										(String) excelclass.getRow(row).get(7));
							}
						}

					}

				} else {
					System.out.println("No Selection ");
				}

			}
		});
		btnImport.setBounds(160, 255, 81, 29);
		panel.add(btnImport);

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public void createAndShowGUI() {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("Epigraph");

		// Create and set up the content pane.
		MainWindow newContentPane = new MainWindow();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setSize(650, 350);
		frame.setVisible(true);
	}
}
